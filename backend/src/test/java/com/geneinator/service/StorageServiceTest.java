package com.geneinator.service;

import com.geneinator.service.impl.LocalStorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

@DisplayName("StorageService")
class StorageServiceTest {

    @TempDir
    Path tempDir;

    private StorageService storageService;

    @BeforeEach
    void setUp() {
        storageService = new LocalStorageServiceImpl(tempDir.toString(), "/api/storage");
    }

    @Nested
    @DisplayName("store")
    class Store {

        @Test
        @DisplayName("should store file and return path")
        void shouldStoreFileAndReturnPath() throws IOException {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "photo",
                    "test-photo.jpg",
                    "image/jpeg",
                    "test image content".getBytes()
            );

            // When
            String storedPath = storageService.store(file, "originals");

            // Then
            assertThat(storedPath).startsWith("originals/");
            assertThat(storedPath).endsWith(".jpg");

            // Verify file exists on disk
            Path fullPath = tempDir.resolve(storedPath);
            assertThat(Files.exists(fullPath)).isTrue();
            assertThat(Files.readAllBytes(fullPath)).isEqualTo("test image content".getBytes());
        }

        @Test
        @DisplayName("should create directory structure based on date")
        void shouldCreateDirectoryStructure() throws IOException {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "photo",
                    "test-photo.png",
                    "image/png",
                    "png content".getBytes()
            );

            // When
            String storedPath = storageService.store(file, "originals");

            // Then
            // Path should be: originals/{year}/{month}/{uuid}.ext
            String[] parts = storedPath.split("/");
            assertThat(parts).hasSizeGreaterThanOrEqualTo(4);
            assertThat(parts[0]).isEqualTo("originals");
        }

        @Test
        @DisplayName("should generate unique filename using UUID")
        void shouldGenerateUniqueFilename() throws IOException {
            // Given
            MockMultipartFile file1 = new MockMultipartFile(
                    "photo",
                    "same-name.jpg",
                    "image/jpeg",
                    "content 1".getBytes()
            );
            MockMultipartFile file2 = new MockMultipartFile(
                    "photo",
                    "same-name.jpg",
                    "image/jpeg",
                    "content 2".getBytes()
            );

            // When
            String path1 = storageService.store(file1, "originals");
            String path2 = storageService.store(file2, "originals");

            // Then
            assertThat(path1).isNotEqualTo(path2);
        }

        @Test
        @DisplayName("should preserve file extension")
        void shouldPreserveFileExtension() throws IOException {
            // Given
            MockMultipartFile jpegFile = new MockMultipartFile("photo", "test.jpeg", "image/jpeg", "content".getBytes());
            MockMultipartFile pngFile = new MockMultipartFile("photo", "test.png", "image/png", "content".getBytes());
            MockMultipartFile webpFile = new MockMultipartFile("photo", "test.webp", "image/webp", "content".getBytes());

            // When/Then
            assertThat(storageService.store(jpegFile, "originals")).endsWith(".jpeg");
            assertThat(storageService.store(pngFile, "originals")).endsWith(".png");
            assertThat(storageService.store(webpFile, "originals")).endsWith(".webp");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete file from storage")
        void shouldDeleteFile() throws IOException {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "photo",
                    "to-delete.jpg",
                    "image/jpeg",
                    "delete me".getBytes()
            );
            String storedPath = storageService.store(file, "originals");
            Path fullPath = tempDir.resolve(storedPath);
            assertThat(Files.exists(fullPath)).isTrue();

            // When
            storageService.delete(storedPath);

            // Then
            assertThat(Files.exists(fullPath)).isFalse();
        }

        @Test
        @DisplayName("should not throw when file does not exist")
        void shouldNotThrowWhenFileDoesNotExist() {
            // When/Then
            assertThatCode(() -> storageService.delete("nonexistent/path/file.jpg"))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("getUrl")
    class GetUrl {

        @Test
        @DisplayName("should return URL for stored path")
        void shouldReturnUrlForStoredPath() {
            // When
            String url = storageService.getUrl("originals/2024/01/abc123.jpg");

            // Then
            assertThat(url).isEqualTo("/api/storage/originals/2024/01/abc123.jpg");
        }

        @Test
        @DisplayName("should return null for null path")
        void shouldReturnNullForNullPath() {
            // When
            String url = storageService.getUrl(null);

            // Then
            assertThat(url).isNull();
        }
    }
}
