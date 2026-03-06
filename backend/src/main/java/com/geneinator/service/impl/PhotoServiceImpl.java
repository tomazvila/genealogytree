package com.geneinator.service.impl;

import com.geneinator.dto.common.ApproximateDateDto;
import com.geneinator.dto.photo.PhotoDto;
import com.geneinator.dto.photo.PhotoUploadResponse;
import com.geneinator.entity.ApproximateDate;
import com.geneinator.entity.Person;
import com.geneinator.entity.PersonPhoto;
import com.geneinator.entity.PersonPhoto.PersonPhotoId;
import com.geneinator.entity.Photo;
import com.geneinator.entity.Photo.ProcessingStatus;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.messaging.ImageMessagePublisher;
import com.geneinator.messaging.ImageProcessingMessage;
import com.geneinator.repository.PersonPhotoRepository;
import com.geneinator.repository.PersonRepository;
import com.geneinator.repository.PhotoRepository;
import com.geneinator.service.PhotoService;
import com.geneinator.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;
    private final PersonRepository personRepository;
    private final PersonPhotoRepository personPhotoRepository;
    private final StorageService storageService;
    private final ImageMessagePublisher imageMessagePublisher;

    @Override
    @Transactional
    public PhotoUploadResponse upload(MultipartFile file, UUID uploadedBy) {
        log.info("Uploading photo: {} by user: {}", file.getOriginalFilename(), uploadedBy);

        String storedPath;
        try {
            storedPath = storageService.store(file, "originals");
        } catch (IOException e) {
            log.error("Failed to store photo", e);
            throw new RuntimeException("Failed to store photo: " + e.getMessage(), e);
        }

        Photo photo = Photo.builder()
                .originalPath(storedPath)
                .processingStatus(ProcessingStatus.PENDING)
                .uploadedBy(uploadedBy)
                .build();

        Photo saved = photoRepository.save(photo);
        log.info("Photo uploaded with id: {}", saved.getId());

        // Publish message for async processing
        ImageProcessingMessage message = ImageProcessingMessage.builder()
                .photoId(saved.getId())
                .originalPath(storedPath)
                .uploadedBy(uploadedBy)
                .build();
        imageMessagePublisher.publishImageForProcessing(message);

        return PhotoUploadResponse.builder()
                .photoId(saved.getId())
                .originalUrl(storageService.getUrl(storedPath))
                .processingStatus(ProcessingStatus.PENDING.name())
                .message("Photo uploaded successfully and queued for processing")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PhotoDto findById(UUID id) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + id));
        return toDto(photo);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PhotoDto> findByPersonId(UUID personId, Pageable pageable) {
        return photoRepository.findByPersonId(personId, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PhotoDto> findByUploadedBy(UUID uploadedBy, Pageable pageable) {
        return photoRepository.findByUploadedBy(uploadedBy, pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public void linkToPersons(UUID photoId, List<UUID> personIds, UUID primaryPersonId) {
        log.info("Linking photo {} to persons: {}, primary: {}", photoId, personIds, primaryPersonId);

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

        photo.getPersons().clear();

        // If setting a primary person, atomically clear their existing primary photo first
        // This prevents race conditions where concurrent requests could create duplicate primaries
        if (primaryPersonId != null) {
            personPhotoRepository.clearPrimaryForPerson(primaryPersonId);
        }

        for (UUID personId : personIds) {
            Person person = personRepository.findById(personId)
                    .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + personId));

            PersonPhoto personPhoto = PersonPhoto.builder()
                    .id(new PersonPhotoId(personId, photoId))
                    .person(person)
                    .photo(photo)
                    .isPrimary(personId.equals(primaryPersonId))
                    .build();

            photo.getPersons().add(personPhoto);
        }

        photoRepository.save(photo);
        log.info("Photo {} linked to {} persons", photoId, personIds.size());
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting photo: {}", id);

        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + id));

        try {
            storageService.delete(photo.getOriginalPath());
            if (photo.getThumbnailSmall() != null) {
                storageService.delete(photo.getThumbnailSmall());
            }
            if (photo.getThumbnailMedium() != null) {
                storageService.delete(photo.getThumbnailMedium());
            }
            if (photo.getThumbnailLarge() != null) {
                storageService.delete(photo.getThumbnailLarge());
            }
        } catch (IOException e) {
            log.warn("Failed to delete some photo files: {}", e.getMessage());
        }

        photoRepository.delete(photo);
        log.info("Photo deleted: {}", id);
    }

    @Override
    @Transactional
    public void updateProcessingStatus(UUID id, String status, String thumbnailSmall,
                                        String thumbnailMedium, String thumbnailLarge) {
        log.info("Updating processing status for photo {}: {}", id, status);

        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + id));

        photo.setProcessingStatus(ProcessingStatus.valueOf(status));
        photo.setThumbnailSmall(thumbnailSmall);
        photo.setThumbnailMedium(thumbnailMedium);
        photo.setThumbnailLarge(thumbnailLarge);

        photoRepository.save(photo);
        log.info("Photo {} processing status updated to {}", id, status);
    }

    @Override
    @Transactional
    public int reprocessPendingPhotos() {
        log.info("Re-queuing photos with PENDING or FAILED status");

        List<Photo> pendingPhotos = photoRepository.findByProcessingStatusIn(
                List.of(ProcessingStatus.PENDING, ProcessingStatus.FAILED)
        );

        for (Photo photo : pendingPhotos) {
            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photo.getId())
                    .originalPath(photo.getOriginalPath())
                    .uploadedBy(photo.getUploadedBy())
                    .build();
            imageMessagePublisher.publishImageForProcessing(message);
            log.info("Re-queued photo {} for processing", photo.getId());
        }

        log.info("Re-queued {} photos for processing", pendingPhotos.size());
        return pendingPhotos.size();
    }

    @Override
    @Transactional
    public void setAsPrimary(UUID photoId, UUID personId) {
        log.info("Setting photo {} as primary for person {}", photoId, personId);

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new ResourceNotFoundException("Photo not found with id: " + photoId));

        Person person = personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + personId));

        // Atomically clear isPrimary on all photos for this person using direct UPDATE
        // This prevents race conditions where concurrent requests could both set isPrimary=true
        personPhotoRepository.clearPrimaryForPerson(personId);

        // Check if the photo is already linked to the person
        PersonPhotoId linkId = new PersonPhotoId(personId, photoId);
        if (personPhotoRepository.existsById(linkId)) {
            // Atomically set this photo as primary using direct UPDATE
            personPhotoRepository.setAsPrimary(personId, photoId);
        } else {
            // Photo is not yet linked to person, create the link with isPrimary=true
            PersonPhoto newLink = PersonPhoto.builder()
                    .id(linkId)
                    .person(person)
                    .photo(photo)
                    .isPrimary(true)
                    .build();
            personPhotoRepository.save(newLink);
        }

        log.info("Photo {} set as primary for person {}", photoId, personId);
    }

    private PhotoDto toDto(Photo photo) {
        List<UUID> personIds = Collections.emptyList();
        if (photo.getPersons() != null && !photo.getPersons().isEmpty()) {
            personIds = photo.getPersons().stream()
                    .map(pp -> pp.getPerson().getId())
                    .collect(Collectors.toList());
        }

        return PhotoDto.builder()
                .id(photo.getId())
                .originalUrl(storageService.getUrl(photo.getOriginalPath()))
                .thumbnailSmallUrl(storageService.getUrl(photo.getThumbnailSmall()))
                .thumbnailMediumUrl(storageService.getUrl(photo.getThumbnailMedium()))
                .thumbnailLargeUrl(storageService.getUrl(photo.getThumbnailLarge()))
                .caption(photo.getCaption())
                .dateTaken(toDto(photo.getDateTaken()))
                .location(photo.getLocation())
                .processingStatus(photo.getProcessingStatus().name())
                .personIds(personIds)
                .exifData(photo.getExifData())
                .createdAt(photo.getCreatedAt())
                .build();
    }

    private ApproximateDateDto toDto(ApproximateDate date) {
        if (date == null) return null;
        return ApproximateDateDto.builder()
                .year(date.getYear())
                .month(date.getMonth())
                .day(date.getDay())
                .isApproximate(date.getIsApproximate())
                .dateText(date.getDateText())
                .build();
    }
}
