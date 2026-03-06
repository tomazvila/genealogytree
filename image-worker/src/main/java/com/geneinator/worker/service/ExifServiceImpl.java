package com.geneinator.worker.service;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class ExifServiceImpl implements ExifService {

    @Override
    public Map<String, Object> extractMetadata(Path imagePath) {
        log.info("Extracting metadata from: {}", imagePath);

        if (!Files.exists(imagePath)) {
            throw new IllegalArgumentException("Image file not found: " + imagePath);
        }

        Map<String, Object> metadata = new HashMap<>();

        try {
            // Get basic file info
            metadata.put("fileSize", Files.size(imagePath));

            // Try to determine MIME type
            String mimeType = Files.probeContentType(imagePath);
            if (mimeType == null || !mimeType.startsWith("image")) {
                // Try to read as image to verify
                BufferedImage image = ImageIO.read(imagePath.toFile());
                if (image == null) {
                    throw new IllegalArgumentException("File is not a valid image: " + imagePath);
                }
                metadata.put("width", image.getWidth());
                metadata.put("height", image.getHeight());
                metadata.put("mimeType", "image/unknown");
            } else {
                metadata.put("mimeType", mimeType);

                // Read image dimensions
                BufferedImage image = ImageIO.read(imagePath.toFile());
                if (image != null) {
                    metadata.put("width", image.getWidth());
                    metadata.put("height", image.getHeight());
                }
            }

            // Extract EXIF metadata using metadata-extractor
            try {
                Metadata exifMetadata = ImageMetadataReader.readMetadata(imagePath.toFile());
                extractExifData(exifMetadata, metadata);
            } catch (ImageProcessingException e) {
                log.warn("Could not extract EXIF metadata from: {}", imagePath, e);
                // Continue without EXIF - not all images have it
            }

            log.info("Extracted {} metadata fields from: {}", metadata.size(), imagePath);
            return metadata;

        } catch (IOException e) {
            log.error("Failed to read metadata from: {}", imagePath, e);
            throw new RuntimeException("Failed to extract metadata", e);
        }
    }

    private void extractExifData(Metadata metadata, Map<String, Object> result) {
        // Extract date taken
        ExifSubIFDDirectory exifSubIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (exifSubIFD != null) {
            Date dateOriginal = exifSubIFD.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            if (dateOriginal != null) {
                LocalDateTime dateTaken = dateOriginal.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                result.put("dateTaken", dateTaken.toString());
            }
        }

        // Extract camera make and model
        ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (exifIFD0 != null) {
            String make = exifIFD0.getString(ExifIFD0Directory.TAG_MAKE);
            String model = exifIFD0.getString(ExifIFD0Directory.TAG_MODEL);
            if (make != null) {
                result.put("cameraMake", make);
            }
            if (model != null) {
                result.put("cameraModel", model);
            }

            // Orientation
            Integer orientation = exifIFD0.getInteger(ExifIFD0Directory.TAG_ORIENTATION);
            if (orientation != null) {
                result.put("orientation", orientation);
            }
        }

        // Extract GPS coordinates
        GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
        if (gpsDirectory != null && gpsDirectory.getGeoLocation() != null) {
            result.put("latitude", gpsDirectory.getGeoLocation().getLatitude());
            result.put("longitude", gpsDirectory.getGeoLocation().getLongitude());
        }

        // Extract image dimensions from JPEG directory if not already set
        JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);
        if (jpegDirectory != null) {
            if (!result.containsKey("width")) {
                Integer width = jpegDirectory.getInteger(JpegDirectory.TAG_IMAGE_WIDTH);
                if (width != null) {
                    result.put("width", width);
                }
            }
            if (!result.containsKey("height")) {
                Integer height = jpegDirectory.getInteger(JpegDirectory.TAG_IMAGE_HEIGHT);
                if (height != null) {
                    result.put("height", height);
                }
            }
        }
    }
}
