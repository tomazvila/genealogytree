package com.geneinator.service;

import com.geneinator.dto.photo.PhotoDto;
import com.geneinator.dto.photo.PhotoUploadResponse;
import com.geneinator.entity.Person;
import com.geneinator.entity.PersonPhoto;
import com.geneinator.entity.Photo;
import com.geneinator.entity.Photo.ProcessingStatus;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.messaging.ImageMessagePublisher;
import com.geneinator.messaging.ImageProcessingMessage;
import com.geneinator.repository.PersonPhotoRepository;
import com.geneinator.repository.PersonRepository;
import com.geneinator.repository.PhotoRepository;
import com.geneinator.service.impl.PhotoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PhotoService")
class PhotoServiceTest {

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PersonPhotoRepository personPhotoRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private ImageMessagePublisher imageMessagePublisher;

    private PhotoService photoService;

    @BeforeEach
    void setUp() {
        photoService = new PhotoServiceImpl(photoRepository, personRepository, personPhotoRepository, storageService, imageMessagePublisher);
    }

    @Nested
    @DisplayName("upload")
    class Upload {

        @Test
        @DisplayName("should upload photo and return pending status")
        void shouldUploadPhotoAndReturnPendingStatus() throws IOException {
            // Given
            UUID uploadedBy = UUID.randomUUID();
            MultipartFile file = new MockMultipartFile(
                    "photo",
                    "test-photo.jpg",
                    "image/jpeg",
                    "test image content".getBytes()
            );

            when(storageService.store(any(MultipartFile.class), eq("originals")))
                    .thenReturn("originals/abc123.jpg");
            when(storageService.getUrl("originals/abc123.jpg"))
                    .thenReturn("/api/storage/originals/abc123.jpg");
            when(photoRepository.save(any(Photo.class))).thenAnswer(inv -> {
                Photo photo = inv.getArgument(0);
                return photo;
            });

            // When
            PhotoUploadResponse result = photoService.upload(file, uploadedBy);

            // Then
            assertThat(result.getProcessingStatus()).isEqualTo("PENDING");
            assertThat(result.getOriginalUrl()).isEqualTo("/api/storage/originals/abc123.jpg");
            assertThat(result.getMessage()).contains("uploaded");
            verify(photoRepository).save(any(Photo.class));
            verify(imageMessagePublisher).publishImageForProcessing(any(ImageProcessingMessage.class));
        }

        @Test
        @DisplayName("should throw exception when storage fails")
        void shouldThrowExceptionWhenStorageFails() throws IOException {
            // Given
            UUID uploadedBy = UUID.randomUUID();
            MultipartFile file = new MockMultipartFile(
                    "photo",
                    "test-photo.jpg",
                    "image/jpeg",
                    "test image content".getBytes()
            );

            when(storageService.store(any(MultipartFile.class), eq("originals")))
                    .thenThrow(new IOException("Storage failure"));

            // When/Then
            assertThatThrownBy(() -> photoService.upload(file, uploadedBy))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Failed to store photo");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return photo when found")
        void shouldReturnPhotoWhenFound() {
            // Given
            UUID photoId = UUID.randomUUID();
            Photo photo = Photo.builder()
                    .originalPath("originals/test.jpg")
                    .processingStatus(ProcessingStatus.COMPLETED)
                    .thumbnailSmall("thumbnails/small.jpg")
                    .thumbnailMedium("thumbnails/medium.jpg")
                    .thumbnailLarge("thumbnails/large.jpg")
                    .uploadedBy(UUID.randomUUID())
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(storageService.getUrl("originals/test.jpg"))
                    .thenReturn("/api/storage/originals/test.jpg");
            when(storageService.getUrl("thumbnails/small.jpg"))
                    .thenReturn("/api/storage/thumbnails/small.jpg");
            when(storageService.getUrl("thumbnails/medium.jpg"))
                    .thenReturn("/api/storage/thumbnails/medium.jpg");
            when(storageService.getUrl("thumbnails/large.jpg"))
                    .thenReturn("/api/storage/thumbnails/large.jpg");

            // When
            PhotoDto result = photoService.findById(photoId);

            // Then
            assertThat(result.getProcessingStatus()).isEqualTo("COMPLETED");
            assertThat(result.getOriginalUrl()).isEqualTo("/api/storage/originals/test.jpg");
            assertThat(result.getThumbnailSmallUrl()).isEqualTo("/api/storage/thumbnails/small.jpg");
        }

        @Test
        @DisplayName("should throw exception when photo not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            UUID photoId = UUID.randomUUID();
            when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> photoService.findById(photoId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByPersonId")
    class FindByPersonId {

        @Test
        @DisplayName("should return photos for a person")
        void shouldReturnPhotosForPerson() {
            // Given
            UUID personId = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Photo photo = Photo.builder()
                    .originalPath("originals/test.jpg")
                    .processingStatus(ProcessingStatus.COMPLETED)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            when(photoRepository.findByPersonId(personId, pageable))
                    .thenReturn(new PageImpl<>(List.of(photo)));
            when(storageService.getUrl("originals/test.jpg"))
                    .thenReturn("/api/storage/originals/test.jpg");

            // When
            Page<PhotoDto> result = photoService.findByPersonId(personId, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("findByUploadedBy")
    class FindByUploadedBy {

        @Test
        @DisplayName("should return photos uploaded by user")
        void shouldReturnPhotosUploadedByUser() {
            // Given
            UUID uploadedBy = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            Photo photo = Photo.builder()
                    .originalPath("originals/test.jpg")
                    .processingStatus(ProcessingStatus.COMPLETED)
                    .uploadedBy(uploadedBy)
                    .build();

            when(photoRepository.findByUploadedBy(uploadedBy, pageable))
                    .thenReturn(new PageImpl<>(List.of(photo)));
            when(storageService.getUrl("originals/test.jpg"))
                    .thenReturn("/api/storage/originals/test.jpg");

            // When
            Page<PhotoDto> result = photoService.findByUploadedBy(uploadedBy, pageable);

            // Then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getOriginalUrl()).isEqualTo("/api/storage/originals/test.jpg");
        }

        @Test
        @DisplayName("should return empty page when user has no photos")
        void shouldReturnEmptyPageWhenNoPhotos() {
            // Given
            UUID uploadedBy = UUID.randomUUID();
            Pageable pageable = PageRequest.of(0, 10);

            when(photoRepository.findByUploadedBy(uploadedBy, pageable))
                    .thenReturn(Page.empty());

            // When
            Page<PhotoDto> result = photoService.findByUploadedBy(uploadedBy, pageable);

            // Then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("linkToPersons")
    class LinkToPersons {

        @Test
        @DisplayName("should link photo to persons with primary person and clear existing primaries")
        void shouldLinkPhotoToPersonsWithPrimaryPerson() {
            // Given
            UUID photoId = UUID.randomUUID();
            UUID person1Id = UUID.randomUUID();
            UUID person2Id = UUID.randomUUID();

            Photo photo = Photo.builder()
                    .originalPath("originals/test.jpg")
                    .processingStatus(ProcessingStatus.COMPLETED)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            Person person1 = Person.builder()
                    .fullName("Person 1")
                    .createdBy(UUID.randomUUID())
                    .build();
            Person person2 = Person.builder()
                    .fullName("Person 2")
                    .createdBy(UUID.randomUUID())
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(personRepository.findById(person1Id)).thenReturn(Optional.of(person1));
            when(personRepository.findById(person2Id)).thenReturn(Optional.of(person2));
            when(photoRepository.save(any(Photo.class))).thenAnswer(inv -> inv.getArgument(0));
            when(personPhotoRepository.clearPrimaryForPerson(person1Id)).thenReturn(1);

            // When
            photoService.linkToPersons(photoId, List.of(person1Id, person2Id), person1Id);

            // Then - verify atomic clear is called before setting new primary
            verify(personPhotoRepository).clearPrimaryForPerson(person1Id);
            verify(photoRepository).save(any(Photo.class));
        }

        @Test
        @DisplayName("should throw exception when photo not found")
        void shouldThrowExceptionWhenPhotoNotFound() {
            // Given
            UUID photoId = UUID.randomUUID();
            when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> photoService.linkToPersons(photoId, List.of(UUID.randomUUID()), null))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete photo and its files")
        void shouldDeletePhotoAndFiles() throws IOException {
            // Given
            UUID photoId = UUID.randomUUID();
            Photo photo = Photo.builder()
                    .originalPath("originals/test.jpg")
                    .thumbnailSmall("thumbnails/small.jpg")
                    .thumbnailMedium("thumbnails/medium.jpg")
                    .thumbnailLarge("thumbnails/large.jpg")
                    .processingStatus(ProcessingStatus.COMPLETED)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));

            // When
            photoService.delete(photoId);

            // Then
            verify(storageService).delete("originals/test.jpg");
            verify(storageService).delete("thumbnails/small.jpg");
            verify(storageService).delete("thumbnails/medium.jpg");
            verify(storageService).delete("thumbnails/large.jpg");
            verify(photoRepository).delete(photo);
        }

        @Test
        @DisplayName("should throw exception when photo not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            UUID photoId = UUID.randomUUID();
            when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> photoService.delete(photoId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateProcessingStatus")
    class UpdateProcessingStatus {

        @Test
        @DisplayName("should update processing status and thumbnails")
        void shouldUpdateProcessingStatusAndThumbnails() {
            // Given
            UUID photoId = UUID.randomUUID();
            Photo photo = Photo.builder()
                    .originalPath("originals/test.jpg")
                    .processingStatus(ProcessingStatus.PENDING)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(photoRepository.save(any(Photo.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            photoService.updateProcessingStatus(
                    photoId,
                    "COMPLETED",
                    "thumbnails/small.jpg",
                    "thumbnails/medium.jpg",
                    "thumbnails/large.jpg"
            );

            // Then
            verify(photoRepository).save(argThat(p ->
                    p.getProcessingStatus() == ProcessingStatus.COMPLETED &&
                    "thumbnails/small.jpg".equals(p.getThumbnailSmall()) &&
                    "thumbnails/medium.jpg".equals(p.getThumbnailMedium()) &&
                    "thumbnails/large.jpg".equals(p.getThumbnailLarge())
            ));
        }

        @Test
        @DisplayName("should throw exception when photo not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            UUID photoId = UUID.randomUUID();
            when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> photoService.updateProcessingStatus(photoId, "COMPLETED", null, null, null))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("setAsPrimary")
    class SetAsPrimary {

        @Test
        @DisplayName("should use atomic database updates to set primary photo")
        void shouldUseAtomicDatabaseUpdates() {
            // Given
            UUID photoId = UUID.randomUUID();
            UUID personId = UUID.randomUUID();

            Photo photo = Photo.builder()
                    .originalPath("originals/test.jpg")
                    .processingStatus(ProcessingStatus.COMPLETED)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            Person person = Person.builder()
                    .fullName("Test Person")
                    .createdBy(UUID.randomUUID())
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(personRepository.findById(personId)).thenReturn(Optional.of(person));
            when(personPhotoRepository.existsById(any())).thenReturn(true);
            when(personPhotoRepository.clearPrimaryForPerson(personId)).thenReturn(1);
            when(personPhotoRepository.setAsPrimary(personId, photoId)).thenReturn(1);

            // When
            photoService.setAsPrimary(photoId, personId);

            // Then - verify atomic updates are used (not in-memory manipulation)
            verify(personPhotoRepository).clearPrimaryForPerson(personId);
            verify(personPhotoRepository).setAsPrimary(personId, photoId);
        }

        @Test
        @DisplayName("should create link when photo not linked to person")
        void shouldCreateLinkWhenNotLinked() {
            // Given
            UUID photoId = UUID.randomUUID();
            UUID personId = UUID.randomUUID();

            Photo photo = Photo.builder()
                    .originalPath("originals/test.jpg")
                    .processingStatus(ProcessingStatus.COMPLETED)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            Person person = Person.builder()
                    .fullName("Test Person")
                    .createdBy(UUID.randomUUID())
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(personRepository.findById(personId)).thenReturn(Optional.of(person));
            when(personPhotoRepository.existsById(any())).thenReturn(false);
            when(personPhotoRepository.clearPrimaryForPerson(personId)).thenReturn(0);
            when(personPhotoRepository.save(any(PersonPhoto.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            photoService.setAsPrimary(photoId, personId);

            // Then
            verify(personPhotoRepository).clearPrimaryForPerson(personId);
            verify(personPhotoRepository).save(any(PersonPhoto.class));
        }

        @Test
        @DisplayName("should throw exception when photo not found")
        void shouldThrowExceptionWhenPhotoNotFound() {
            // Given
            UUID photoId = UUID.randomUUID();
            UUID personId = UUID.randomUUID();

            when(photoRepository.findById(photoId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> photoService.setAsPrimary(photoId, personId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("should throw exception when person not found")
        void shouldThrowExceptionWhenPersonNotFound() {
            // Given
            UUID photoId = UUID.randomUUID();
            UUID personId = UUID.randomUUID();

            Photo photo = Photo.builder()
                    .originalPath("originals/test.jpg")
                    .processingStatus(ProcessingStatus.COMPLETED)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(personRepository.findById(personId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> photoService.setAsPrimary(photoId, personId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
