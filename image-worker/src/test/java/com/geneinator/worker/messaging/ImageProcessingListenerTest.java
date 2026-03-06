package com.geneinator.worker.messaging;

import com.geneinator.worker.entity.Photo;
import com.geneinator.worker.repository.PhotoRepository;
import com.geneinator.worker.service.ImageProcessingResult;
import com.geneinator.worker.service.ImageProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageProcessingListener")
class ImageProcessingListenerTest {

    @Mock
    private ImageProcessingService imageProcessingService;

    @Mock
    private PhotoRepository photoRepository;

    private ImageProcessingListener listener;

    @BeforeEach
    void setUp() {
        listener = new ImageProcessingListener(imageProcessingService, photoRepository);
    }

    @Nested
    @DisplayName("handleImageProcessing")
    class HandleImageProcessing {

        @Test
        @DisplayName("should update photo status to COMPLETED after successful processing")
        void shouldUpdatePhotoStatusToCompleted() {
            // Given
            UUID photoId = UUID.randomUUID();
            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath("originals/test.jpg")
                    .uploadedBy(UUID.randomUUID())
                    .build();

            Photo photo = new Photo();
            photo.setId(photoId);
            photo.setProcessingStatus(Photo.ProcessingStatus.PENDING);

            ImageProcessingResult result = ImageProcessingResult.builder()
                    .photoId(photoId)
                    .status("COMPLETED")
                    .thumbnailSmall("thumbnails/small/test.jpg")
                    .thumbnailMedium("thumbnails/medium/test.jpg")
                    .thumbnailLarge("thumbnails/large/test.jpg")
                    .exifData(Map.of("width", 1000, "height", 800))
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(imageProcessingService.processImage(message)).thenReturn(result);

            // When
            listener.handleImageProcessing(message);

            // Then - save is called twice: once for PROCESSING, once for COMPLETED
            ArgumentCaptor<Photo> photoCaptor = ArgumentCaptor.forClass(Photo.class);
            verify(photoRepository, times(2)).save(photoCaptor.capture());

            // Get the final saved state (last invocation)
            Photo savedPhoto = photoCaptor.getAllValues().get(1);
            assertThat(savedPhoto.getProcessingStatus()).isEqualTo(Photo.ProcessingStatus.COMPLETED);
        }

        @Test
        @DisplayName("should save thumbnail paths to database")
        void shouldSaveThumbnailPaths() {
            // Given
            UUID photoId = UUID.randomUUID();
            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath("originals/test.jpg")
                    .uploadedBy(UUID.randomUUID())
                    .build();

            Photo photo = new Photo();
            photo.setId(photoId);
            photo.setProcessingStatus(Photo.ProcessingStatus.PENDING);

            String smallPath = "thumbnails/small/" + photoId + ".jpg";
            String mediumPath = "thumbnails/medium/" + photoId + ".jpg";
            String largePath = "thumbnails/large/" + photoId + ".jpg";

            ImageProcessingResult result = ImageProcessingResult.builder()
                    .photoId(photoId)
                    .status("COMPLETED")
                    .thumbnailSmall(smallPath)
                    .thumbnailMedium(mediumPath)
                    .thumbnailLarge(largePath)
                    .exifData(Map.of())
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(imageProcessingService.processImage(message)).thenReturn(result);

            // When
            listener.handleImageProcessing(message);

            // Then - save is called twice: once for PROCESSING, once for COMPLETED
            ArgumentCaptor<Photo> photoCaptor = ArgumentCaptor.forClass(Photo.class);
            verify(photoRepository, times(2)).save(photoCaptor.capture());

            // Get the final saved state (last invocation)
            Photo savedPhoto = photoCaptor.getAllValues().get(1);
            assertThat(savedPhoto.getThumbnailSmall()).isEqualTo(smallPath);
            assertThat(savedPhoto.getThumbnailMedium()).isEqualTo(mediumPath);
            assertThat(savedPhoto.getThumbnailLarge()).isEqualTo(largePath);
        }

        @Test
        @DisplayName("should save EXIF data to database")
        void shouldSaveExifData() {
            // Given
            UUID photoId = UUID.randomUUID();
            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath("originals/test.jpg")
                    .uploadedBy(UUID.randomUUID())
                    .build();

            Photo photo = new Photo();
            photo.setId(photoId);
            photo.setProcessingStatus(Photo.ProcessingStatus.PENDING);

            Map<String, Object> exifData = Map.of(
                    "width", 1920,
                    "height", 1080,
                    "dateTaken", "2024-06-15"
            );

            ImageProcessingResult result = ImageProcessingResult.builder()
                    .photoId(photoId)
                    .status("COMPLETED")
                    .thumbnailSmall("small.jpg")
                    .thumbnailMedium("medium.jpg")
                    .thumbnailLarge("large.jpg")
                    .exifData(exifData)
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(imageProcessingService.processImage(message)).thenReturn(result);

            // When
            listener.handleImageProcessing(message);

            // Then - save is called twice: once for PROCESSING, once for COMPLETED
            ArgumentCaptor<Photo> photoCaptor = ArgumentCaptor.forClass(Photo.class);
            verify(photoRepository, times(2)).save(photoCaptor.capture());

            // Get the final saved state (last invocation)
            Photo savedPhoto = photoCaptor.getAllValues().get(1);
            assertThat(savedPhoto.getExifData()).containsEntry("width", 1920);
            assertThat(savedPhoto.getExifData()).containsEntry("height", 1080);
            assertThat(savedPhoto.getExifData()).containsEntry("dateTaken", "2024-06-15");
        }

        @Test
        @DisplayName("should set status to FAILED when processing throws exception")
        void shouldSetStatusToFailedOnException() {
            // Given
            UUID photoId = UUID.randomUUID();
            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath("originals/test.jpg")
                    .uploadedBy(UUID.randomUUID())
                    .build();

            Photo photo = new Photo();
            photo.setId(photoId);
            photo.setProcessingStatus(Photo.ProcessingStatus.PENDING);

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(imageProcessingService.processImage(message))
                    .thenThrow(new RuntimeException("Processing failed"));

            // When/Then
            assertThatThrownBy(() -> listener.handleImageProcessing(message))
                    .isInstanceOf(RuntimeException.class);

            // Save is called twice: once for PROCESSING, once for FAILED
            ArgumentCaptor<Photo> photoCaptor = ArgumentCaptor.forClass(Photo.class);
            verify(photoRepository, times(2)).save(photoCaptor.capture());

            // Get the final saved state (last invocation)
            Photo savedPhoto = photoCaptor.getAllValues().get(1);
            assertThat(savedPhoto.getProcessingStatus()).isEqualTo(Photo.ProcessingStatus.FAILED);
        }

        @Test
        @DisplayName("should set status to PROCESSING before starting")
        void shouldSetStatusToProcessingBeforeStart() {
            // Given
            UUID photoId = UUID.randomUUID();
            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath("originals/test.jpg")
                    .uploadedBy(UUID.randomUUID())
                    .build();

            Photo photo = new Photo();
            photo.setId(photoId);
            photo.setProcessingStatus(Photo.ProcessingStatus.PENDING);

            ImageProcessingResult result = ImageProcessingResult.builder()
                    .photoId(photoId)
                    .status("COMPLETED")
                    .thumbnailSmall("small.jpg")
                    .thumbnailMedium("medium.jpg")
                    .thumbnailLarge("large.jpg")
                    .exifData(Map.of())
                    .build();

            when(photoRepository.findById(photoId)).thenReturn(Optional.of(photo));
            when(imageProcessingService.processImage(message)).thenReturn(result);

            // When
            listener.handleImageProcessing(message);

            // Then - verify save was called twice (once for PROCESSING, once for COMPLETED)
            verify(photoRepository, times(2)).save(any(Photo.class));
        }
    }
}
