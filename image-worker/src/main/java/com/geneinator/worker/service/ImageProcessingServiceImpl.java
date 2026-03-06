package com.geneinator.worker.service;

import com.geneinator.worker.messaging.ImageProcessingMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {

    private final ThumbnailService thumbnailService;
    private final ExifService exifService;

    @Value("${app.storage.base-path}")
    private String basePath;

    @Override
    public ImageProcessingResult processImage(ImageProcessingMessage message) {
        log.info("Processing image: {}", message.getPhotoId());

        // Validate path for traversal attacks
        Path originalPath = validateAndResolvePath(message.getOriginalPath());

        // Validate file exists
        if (!Files.exists(originalPath)) {
            throw new IllegalArgumentException("Original file not found: " + originalPath);
        }

        // Step 1: Extract EXIF metadata
        Map<String, Object> exifData = exifService.extractMetadata(originalPath);

        // Step 2: Generate thumbnails
        Map<String, String> thumbnails = thumbnailService.generateThumbnails(originalPath, message.getPhotoId());

        log.info("Successfully processed image: {}", message.getPhotoId());

        return ImageProcessingResult.builder()
                .photoId(message.getPhotoId())
                .status("COMPLETED")
                .thumbnailSmall(thumbnails.get("small"))
                .thumbnailMedium(thumbnails.get("medium"))
                .thumbnailLarge(thumbnails.get("large"))
                .exifData(exifData)
                .build();
    }

    /**
     * Validates the input path and ensures it stays within the base storage directory.
     * Prevents path traversal attacks.
     */
    private Path validateAndResolvePath(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            throw new SecurityException("Path traversal attempt detected: empty path");
        }

        // Decode URL-encoded characters to catch encoded traversal attempts
        String decodedPath = java.net.URLDecoder.decode(relativePath, java.nio.charset.StandardCharsets.UTF_8);

        // Check for obvious traversal patterns before normalization
        if (decodedPath.contains("..") || decodedPath.startsWith("/")) {
            throw new SecurityException("Path traversal attempt detected: " + relativePath);
        }

        Path basePathResolved = Paths.get(basePath).toAbsolutePath().normalize();
        Path resolvedPath = basePathResolved.resolve(decodedPath).normalize();

        // Ensure the resolved path is still within the base path
        if (!resolvedPath.startsWith(basePathResolved)) {
            throw new SecurityException("Path traversal attempt detected: " + relativePath);
        }

        return resolvedPath;
    }
}
