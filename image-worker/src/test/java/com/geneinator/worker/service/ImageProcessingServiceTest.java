package com.geneinator.worker.service;

import com.geneinator.worker.messaging.ImageProcessingMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ImageProcessingService")
class ImageProcessingServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private ThumbnailService thumbnailService;

    @Mock
    private ExifService exifService;

    private ImageProcessingServiceImpl imageProcessingService;

    @BeforeEach
    void setUp() throws Exception {
        imageProcessingService = new ImageProcessingServiceImpl(thumbnailService, exifService);

        // Set basePath via reflection
        var field = imageProcessingService.getClass().getDeclaredField("basePath");
        field.setAccessible(true);
        field.set(imageProcessingService, tempDir.toString());

        // Create originals directory and test image
        Files.createDirectories(tempDir.resolve("originals"));
    }

    private Path createTestImage(String relativePath) throws IOException {
        BufferedImage image = new BufferedImage(1000, 800, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 1000, 800);
        g.dispose();

        Path imagePath = tempDir.resolve(relativePath);
        Files.createDirectories(imagePath.getParent());
        ImageIO.write(image, "jpg", imagePath.toFile());
        return imagePath;
    }

    @Nested
    @DisplayName("processImage")
    class ProcessImage {

        @Test
        @DisplayName("should extract EXIF metadata")
        void shouldExtractExifMetadata() throws IOException {
            // Given
            UUID photoId = UUID.randomUUID();
            String originalPath = "originals/test.jpg";
            createTestImage(originalPath);

            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath(originalPath)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            when(exifService.extractMetadata(any(Path.class)))
                    .thenReturn(Map.of("width", 1000, "height", 800));
            when(thumbnailService.generateThumbnails(any(Path.class), eq(photoId)))
                    .thenReturn(Map.of(
                            "small", "thumbnails/small/" + photoId + ".jpg",
                            "medium", "thumbnails/medium/" + photoId + ".jpg",
                            "large", "thumbnails/large/" + photoId + ".jpg"
                    ));

            // When
            ImageProcessingResult result = imageProcessingService.processImage(message);

            // Then
            verify(exifService).extractMetadata(any(Path.class));
            assertThat(result.getExifData()).containsEntry("width", 1000);
            assertThat(result.getExifData()).containsEntry("height", 800);
        }

        @Test
        @DisplayName("should generate thumbnails")
        void shouldGenerateThumbnails() throws IOException {
            // Given
            UUID photoId = UUID.randomUUID();
            String originalPath = "originals/test.jpg";
            createTestImage(originalPath);

            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath(originalPath)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            Map<String, String> thumbnails = Map.of(
                    "small", "thumbnails/small/" + photoId + ".jpg",
                    "medium", "thumbnails/medium/" + photoId + ".jpg",
                    "large", "thumbnails/large/" + photoId + ".jpg"
            );

            when(exifService.extractMetadata(any(Path.class)))
                    .thenReturn(Map.of());
            when(thumbnailService.generateThumbnails(any(Path.class), eq(photoId)))
                    .thenReturn(thumbnails);

            // When
            ImageProcessingResult result = imageProcessingService.processImage(message);

            // Then
            verify(thumbnailService).generateThumbnails(any(Path.class), eq(photoId));
            assertThat(result.getThumbnailSmall()).isEqualTo(thumbnails.get("small"));
            assertThat(result.getThumbnailMedium()).isEqualTo(thumbnails.get("medium"));
            assertThat(result.getThumbnailLarge()).isEqualTo(thumbnails.get("large"));
        }

        @Test
        @DisplayName("should return complete processing result")
        void shouldReturnCompleteResult() throws IOException {
            // Given
            UUID photoId = UUID.randomUUID();
            String originalPath = "originals/test.jpg";
            createTestImage(originalPath);

            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath(originalPath)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            when(exifService.extractMetadata(any(Path.class)))
                    .thenReturn(Map.of("dateTaken", "2024-01-15"));
            when(thumbnailService.generateThumbnails(any(Path.class), eq(photoId)))
                    .thenReturn(Map.of(
                            "small", "thumbnails/small/img.jpg",
                            "medium", "thumbnails/medium/img.jpg",
                            "large", "thumbnails/large/img.jpg"
                    ));

            // When
            ImageProcessingResult result = imageProcessingService.processImage(message);

            // Then
            assertThat(result.getPhotoId()).isEqualTo(photoId);
            assertThat(result.getStatus()).isEqualTo("COMPLETED");
            assertThat(result.getThumbnailSmall()).isNotNull();
            assertThat(result.getThumbnailMedium()).isNotNull();
            assertThat(result.getThumbnailLarge()).isNotNull();
            assertThat(result.getExifData()).isNotNull();
        }

        @Test
        @DisplayName("should throw exception when original file not found")
        void shouldThrowExceptionWhenFileNotFound() {
            // Given
            UUID photoId = UUID.randomUUID();
            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath("originals/non-existent.jpg")
                    .uploadedBy(UUID.randomUUID())
                    .build();

            // When/Then
            assertThatThrownBy(() -> imageProcessingService.processImage(message))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("should handle thumbnail generation failure gracefully")
        void shouldHandleThumbnailFailure() throws IOException {
            // Given
            UUID photoId = UUID.randomUUID();
            String originalPath = "originals/test.jpg";
            createTestImage(originalPath);

            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath(originalPath)
                    .uploadedBy(UUID.randomUUID())
                    .build();

            when(exifService.extractMetadata(any(Path.class)))
                    .thenReturn(Map.of());
            when(thumbnailService.generateThumbnails(any(Path.class), eq(photoId)))
                    .thenThrow(new RuntimeException("Thumbnail generation failed"));

            // When/Then
            assertThatThrownBy(() -> imageProcessingService.processImage(message))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Thumbnail generation failed");
        }

        @Test
        @DisplayName("should reject path traversal attempts with ../")
        void shouldRejectPathTraversalWithDotDot() {
            // Given
            UUID photoId = UUID.randomUUID();
            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath("../../../etc/passwd")
                    .uploadedBy(UUID.randomUUID())
                    .build();

            // When/Then
            assertThatThrownBy(() -> imageProcessingService.processImage(message))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Path traversal");
        }

        @Test
        @DisplayName("should reject path traversal attempts with absolute path")
        void shouldRejectAbsolutePathTraversal() {
            // Given
            UUID photoId = UUID.randomUUID();
            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath("/etc/passwd")
                    .uploadedBy(UUID.randomUUID())
                    .build();

            // When/Then
            assertThatThrownBy(() -> imageProcessingService.processImage(message))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Path traversal");
        }

        @Test
        @DisplayName("should reject path with encoded traversal")
        void shouldRejectEncodedPathTraversal() {
            // Given
            UUID photoId = UUID.randomUUID();
            ImageProcessingMessage message = ImageProcessingMessage.builder()
                    .photoId(photoId)
                    .originalPath("originals/..%2F..%2Fetc/passwd")
                    .uploadedBy(UUID.randomUUID())
                    .build();

            // When/Then
            assertThatThrownBy(() -> imageProcessingService.processImage(message))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Path traversal");
        }
    }
}
