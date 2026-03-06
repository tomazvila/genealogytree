package com.geneinator.worker.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ThumbnailServiceImpl implements ThumbnailService {

    @Value("${app.storage.base-path}")
    private String basePath;

    @Value("${app.storage.thumbnails-dir}")
    private String thumbnailsDir;

    @Value("${app.processing.thumbnail-sizes.small}")
    private int smallSize;

    @Value("${app.processing.thumbnail-sizes.medium}")
    private int mediumSize;

    @Value("${app.processing.thumbnail-sizes.large}")
    private int largeSize;

    @Override
    public Map<String, String> generateThumbnails(Path originalPath, UUID photoId) {
        log.info("Generating thumbnails for photo: {} from: {}", photoId, originalPath);

        if (!Files.exists(originalPath)) {
            throw new IllegalArgumentException("Original file not found: " + originalPath);
        }

        Map<String, String> thumbnails = new HashMap<>();
        BufferedImage original = null;

        try {
            original = ImageIO.read(originalPath.toFile());
            if (original == null) {
                throw new IllegalArgumentException("Could not read image: " + originalPath);
            }

            // Generate small thumbnail
            Path smallPath = generateAndSaveThumbnail(original, smallSize, photoId, "small");
            thumbnails.put("small", getRelativePath(smallPath));

            // Generate medium thumbnail
            Path mediumPath = generateAndSaveThumbnail(original, mediumSize, photoId, "medium");
            thumbnails.put("medium", getRelativePath(mediumPath));

            // Generate large thumbnail
            Path largePath = generateAndSaveThumbnail(original, largeSize, photoId, "large");
            thumbnails.put("large", getRelativePath(largePath));

            log.info("Generated {} thumbnails for photo: {}", thumbnails.size(), photoId);
            return thumbnails;

        } catch (IOException e) {
            log.error("Failed to generate thumbnails for photo: {}", photoId, e);
            throw new RuntimeException("Failed to generate thumbnails", e);
        } finally {
            // Release native resources to prevent memory leaks
            if (original != null) {
                original.flush();
            }
        }
    }

    @Override
    public Path generateThumbnail(Path originalPath, int size, String suffix) {
        log.debug("Generating {} thumbnail ({}px) from: {}", suffix, size, originalPath);

        if (!Files.exists(originalPath)) {
            throw new IllegalArgumentException("Original file not found: " + originalPath);
        }

        BufferedImage original = null;
        try {
            original = ImageIO.read(originalPath.toFile());
            if (original == null) {
                throw new IllegalArgumentException("Could not read image: " + originalPath);
            }

            // Generate random filename for standalone thumbnail
            UUID tempId = UUID.randomUUID();
            return generateAndSaveThumbnail(original, size, tempId, suffix);

        } catch (IOException e) {
            log.error("Failed to generate thumbnail", e);
            throw new RuntimeException("Failed to generate thumbnail", e);
        } finally {
            // Release native resources to prevent memory leaks
            if (original != null) {
                original.flush();
            }
        }
    }

    private Path generateAndSaveThumbnail(BufferedImage original, int targetSize, UUID photoId, String sizeLabel) throws IOException {
        // Calculate new dimensions maintaining aspect ratio
        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        // Don't upscale - if image is smaller than target, use original size
        int maxDimension = Math.max(originalWidth, originalHeight);
        int effectiveTargetSize = Math.min(targetSize, maxDimension);

        int newWidth, newHeight;
        if (originalWidth > originalHeight) {
            // Landscape
            newWidth = effectiveTargetSize;
            newHeight = (int) Math.round((double) originalHeight / originalWidth * effectiveTargetSize);
        } else {
            // Portrait or square
            newHeight = effectiveTargetSize;
            newWidth = (int) Math.round((double) originalWidth / originalHeight * effectiveTargetSize);
        }

        // Create scaled image
        BufferedImage scaled = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaled.createGraphics();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.drawImage(original, 0, 0, newWidth, newHeight, null);
        } finally {
            g2d.dispose();
        }

        // Determine output path
        Path outputDir = Paths.get(basePath, thumbnailsDir, sizeLabel);
        Files.createDirectories(outputDir);

        String filename = photoId.toString() + ".jpg";
        Path outputPath = outputDir.resolve(filename);

        try {
            // Write image
            ImageIO.write(scaled, "jpg", outputPath.toFile());
            log.debug("Saved {} thumbnail to: {}", sizeLabel, outputPath);
            return outputPath;
        } finally {
            // Release native resources for scaled image
            scaled.flush();
        }
    }

    private String getRelativePath(Path fullPath) {
        Path basePathObj = Paths.get(basePath);
        return basePathObj.relativize(fullPath).toString();
    }
}
