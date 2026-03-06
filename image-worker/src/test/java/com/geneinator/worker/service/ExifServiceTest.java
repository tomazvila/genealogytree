package com.geneinator.worker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ExifService")
class ExifServiceTest {

    @TempDir
    Path tempDir;

    private ExifServiceImpl exifService;

    @BeforeEach
    void setUp() {
        exifService = new ExifServiceImpl();
    }

    private Path createTestImage(String filename) throws IOException {
        BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, 800, 600);
        g.dispose();

        Path imagePath = tempDir.resolve(filename);
        ImageIO.write(image, "jpg", imagePath.toFile());
        return imagePath;
    }

    @Nested
    @DisplayName("extractMetadata")
    class ExtractMetadata {

        @Test
        @DisplayName("should return map with metadata fields")
        void shouldReturnMetadataMap() throws IOException {
            // Given
            Path imagePath = createTestImage("test-image.jpg");

            // When
            Map<String, Object> metadata = exifService.extractMetadata(imagePath);

            // Then
            assertThat(metadata).isNotNull();
        }

        @Test
        @DisplayName("should include image dimensions")
        void shouldIncludeImageDimensions() throws IOException {
            // Given
            Path imagePath = createTestImage("test-image.jpg");

            // When
            Map<String, Object> metadata = exifService.extractMetadata(imagePath);

            // Then
            assertThat(metadata).containsKey("width");
            assertThat(metadata).containsKey("height");
            assertThat(metadata.get("width")).isEqualTo(800);
            assertThat(metadata.get("height")).isEqualTo(600);
        }

        @Test
        @DisplayName("should include file size")
        void shouldIncludeFileSize() throws IOException {
            // Given
            Path imagePath = createTestImage("test-image.jpg");
            long expectedSize = Files.size(imagePath);

            // When
            Map<String, Object> metadata = exifService.extractMetadata(imagePath);

            // Then
            assertThat(metadata).containsKey("fileSize");
            assertThat(metadata.get("fileSize")).isEqualTo(expectedSize);
        }

        @Test
        @DisplayName("should handle image without EXIF data gracefully")
        void shouldHandleNoExifData() throws IOException {
            // Given - programmatically created image has no EXIF
            Path imagePath = createTestImage("no-exif.jpg");

            // When
            Map<String, Object> metadata = exifService.extractMetadata(imagePath);

            // Then - should not throw, should return basic metadata
            assertThat(metadata).isNotNull();
            assertThat(metadata).containsKey("width");
            assertThat(metadata).containsKey("height");
            // EXIF-specific fields may be null or absent
            assertThat(metadata.get("dateTaken")).isNull();
        }

        @Test
        @DisplayName("should throw exception for non-existent file")
        void shouldThrowExceptionForNonExistentFile() {
            // Given
            Path nonExistent = tempDir.resolve("non-existent.jpg");

            // When/Then
            assertThatThrownBy(() -> exifService.extractMetadata(nonExistent))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("should throw exception for non-image file")
        void shouldThrowExceptionForNonImageFile() throws IOException {
            // Given
            Path textFile = tempDir.resolve("text.txt");
            Files.writeString(textFile, "This is not an image");

            // When/Then
            assertThatThrownBy(() -> exifService.extractMetadata(textFile))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not a valid image");
        }

        @Test
        @DisplayName("should include mime type")
        void shouldIncludeMimeType() throws IOException {
            // Given
            Path imagePath = createTestImage("test-image.jpg");

            // When
            Map<String, Object> metadata = exifService.extractMetadata(imagePath);

            // Then
            assertThat(metadata).containsKey("mimeType");
            assertThat((String) metadata.get("mimeType")).contains("image");
        }
    }
}
