package com.geneinator.service;

import com.geneinator.dto.photo.PhotoDto;
import com.geneinator.dto.photo.PhotoUploadResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PhotoService {

    PhotoUploadResponse upload(MultipartFile file, UUID uploadedBy);

    PhotoDto findById(UUID id);

    Page<PhotoDto> findByPersonId(UUID personId, Pageable pageable);

    Page<PhotoDto> findByUploadedBy(UUID uploadedBy, Pageable pageable);

    void linkToPersons(UUID photoId, List<UUID> personIds, UUID primaryPersonId);

    void delete(UUID id);

    void updateProcessingStatus(UUID id, String status, String thumbnailSmall,
                                 String thumbnailMedium, String thumbnailLarge);

    /**
     * Re-queue all photos with PENDING or FAILED status for processing.
     * @return the number of photos re-queued
     */
    int reprocessPendingPhotos();

    /**
     * Set a photo as the primary profile photo for a person.
     * @param photoId the photo ID
     * @param personId the person ID
     */
    void setAsPrimary(UUID photoId, UUID personId);
}
