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
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DisplayName("ThumbnailService")
class ThumbnailServiceTest {

    @TempDir
    Path tempDir;

    private ThumbnailServiceImpl thumbnailService;
    private Path testImagePath;

    @BeforeEach
    void setUp() throws IOException {
        thumbnailService = new ThumbnailServiceImpl();

        // Configure via reflection since @Value won't work in unit test
        setField(thumbnailService, "basePath", tempDir.toString());
        setField(thumbnailService, "thumbnailsDir", "thumbnails");
        setField(thumbnailService, "smallSize", 150);
        setField(thumbnailService, "mediumSize", 400);
        setField(thumbnailService, "largeSize", 800);

        // Create test directories
        Files.createDirectories(tempDir.resolve("thumbnails/small"));
        Files.createDirectories(tempDir.resolve("thumbnails/medium"));
        Files.createDirectories(tempDir.resolve("thumbnails/large"));

        // Create a test image (1000x800 pixels)
        testImagePath = createTestImage(1000, 800, "test-image.jpg");
    }

    private Path createTestImage(int width, int height, String filename) throws IOException {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.WHITE);
        g.drawString("Test Image " + width + "x" + height, 50, 50);
        g.dispose();

        Path imagePath = tempDir.resolve(filename);
        ImageIO.write(image, "jpg", imagePath.toFile());
        return imagePath;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    @Nested
    @DisplayName("generateThumbnails")
    class GenerateThumbnails {

        @Test
        @DisplayName("should generate small, medium, and large thumbnails")
        void shouldGenerateAllThumbnails() {
            // Given
            UUID photoId = UUID.randomUUID();

            // When
            Map<String, String> thumbnails = thumbnailService.generateThumbnails(testImagePath, photoId);

            // Then
            assertThat(thumbnails).containsKeys("small", "medium", "large");
            assertThat(thumbnails.get("small")).contains("thumbnails/small/");
            assertThat(thumbnails.get("medium")).contains("thumbnails/medium/");
            assertThat(thumbnails.get("large")).contains("thumbnails/large/");

            // Verify files exist
            Path smallPath = tempDir.resolve(thumbnails.get("small"));
            Path mediumPath = tempDir.resolve(thumbnails.get("medium"));
            Path largePath = tempDir.resolve(thumbnails.get("large"));

            assertThat(Files.exists(smallPath)).isTrue();
            assertThat(Files.exists(mediumPath)).isTrue();
            assertThat(Files.exists(largePath)).isTrue();
        }

        @Test
        @DisplayName("should create thumbnails with correct dimensions")
        void shouldCreateCorrectDimensions() throws IOException {
            // Given
            UUID photoId = UUID.randomUUID();

            // When
            Map<String, String> thumbnails = thumbnailService.generateThumbnails(testImagePath, photoId);

            // Then - verify dimensions
            BufferedImage small = ImageIO.read(tempDir.resolve(thumbnails.get("small")).toFile());
            BufferedImage medium = ImageIO.read(tempDir.resolve(thumbnails.get("medium")).toFile());
            BufferedImage large = ImageIO.read(tempDir.resolve(thumbnails.get("large")).toFile());

            // Small thumbnail should be 150px on longest side
            assertThat(Math.max(small.getWidth(), small.getHeight())).isEqualTo(150);

            // Medium thumbnail should be 400px on longest side
            assertThat(Math.max(medium.getWidth(), medium.getHeight())).isEqualTo(400);

            // Large thumbnail should be 800px on longest side
            assertThat(Math.max(large.getWidth(), large.getHeight())).isEqualTo(800);
        }

        @Test
        @DisplayName("should maintain aspect ratio")
        void shouldMaintainAspectRatio() throws IOException {
            // Given - original is 1000x800 (5:4 ratio)
            UUID photoId = UUID.randomUUID();

            // When
            Map<String, String> thumbnails = thumbnailService.generateThumbnails(testImagePath, photoId);

            // Then - verify aspect ratio is preserved
            BufferedImage small = ImageIO.read(tempDir.resolve(thumbnails.get("small")).toFile());

            // Original ratio: 1000/800 = 1.25
            // Small should be 150x120 (or close to it)
            double originalRatio = 1000.0 / 800.0;
            double thumbnailRatio = (double) small.getWidth() / small.getHeight();

            assertThat(thumbnailRatio).isCloseTo(originalRatio, within(0.01));
        }

        @Test
        @DisplayName("should handle portrait images")
        void shouldHandlePortraitImages() throws IOException {
            // Given - portrait image (600x900)
            Path portraitImage = createTestImage(600, 900, "portrait.jpg");
            UUID photoId = UUID.randomUUID();

            // When
            Map<String, String> thumbnails = thumbnailService.generateThumbnails(portraitImage, photoId);

            // Then
            BufferedImage small = ImageIO.read(tempDir.resolve(thumbnails.get("small")).toFile());

            // For portrait, height should be 150 (longest side)
            assertThat(small.getHeight()).isEqualTo(150);
            // Width should be scaled proportionally (600/900 * 150 = 100)
            assertThat(small.getWidth()).isEqualTo(100);
        }

        @Test
        @DisplayName("should not upscale small images")
        void shouldNotUpscaleSmallImages() throws IOException {
            // Given - small image (100x80)
            Path smallImage = createTestImage(100, 80, "small-image.jpg");
            UUID photoId = UUID.randomUUID();

            // When
            Map<String, String> thumbnails = thumbnailService.generateThumbnails(smallImage, photoId);

            // Then - large thumbnail should not exceed original size
            BufferedImage large = ImageIO.read(tempDir.resolve(thumbnails.get("large")).toFile());
            assertThat(large.getWidth()).isLessThanOrEqualTo(100);
            assertThat(large.getHeight()).isLessThanOrEqualTo(80);
        }

        @Test
        @DisplayName("should use photoId in filename")
        void shouldUsePhotoIdInFilename() {
            // Given
            UUID photoId = UUID.randomUUID();

            // When
            Map<String, String> thumbnails = thumbnailService.generateThumbnails(testImagePath, photoId);

            // Then
            assertThat(thumbnails.get("small")).contains(photoId.toString());
            assertThat(thumbnails.get("medium")).contains(photoId.toString());
            assertThat(thumbnails.get("large")).contains(photoId.toString());
        }
    }

    @Nested
    @DisplayName("generateThumbnail")
    class GenerateThumbnail {

        @Test
        @DisplayName("should generate single thumbnail with specified size")
        void shouldGenerateSingleThumbnail() throws IOException {
            // When
            Path thumbnail = thumbnailService.generateThumbnail(testImagePath, 200, "custom");

            // Then
            assertThat(Files.exists(thumbnail)).isTrue();
            BufferedImage img = ImageIO.read(thumbnail.toFile());
            assertThat(Math.max(img.getWidth(), img.getHeight())).isEqualTo(200);
        }

        @Test
        @DisplayName("should throw exception for non-existent file")
        void shouldThrowExceptionForNonExistentFile() {
            // Given
            Path nonExistent = tempDir.resolve("non-existent.jpg");

            // When/Then
            assertThatThrownBy(() -> thumbnailService.generateThumbnail(nonExistent, 150, "test"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("not found");
        }
    }
}
