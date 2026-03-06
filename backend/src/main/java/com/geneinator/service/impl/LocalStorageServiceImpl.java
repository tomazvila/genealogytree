package com.geneinator.service.impl;

import com.geneinator.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Slf4j
public class LocalStorageServiceImpl implements StorageService {

    private final Path basePath;
    private final String baseUrl;

    public LocalStorageServiceImpl(
            @Value("${app.storage.base-path:./data/geneinator}") String basePath,
            @Value("${app.storage.base-url:/api/storage}") String baseUrl) {
        this.basePath = Paths.get(basePath).toAbsolutePath().normalize();
        this.baseUrl = baseUrl;

        try {
            Files.createDirectories(this.basePath);
            log.info("Storage initialized at: {}", this.basePath);
        } catch (IOException e) {
            log.error("Failed to create storage directory", e);
            throw new RuntimeException("Failed to initialize storage", e);
        }
    }

    @Override
    public String store(MultipartFile file, String directory) throws IOException {
        log.debug("Storing file: {} to directory: {}", file.getOriginalFilename(), directory);

        // Create path structure: {directory}/{year}/{month}/{uuid}.{extension}
        LocalDate now = LocalDate.now();
        String extension = getFileExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + extension;

        String relativePath = String.format("%s/%d/%02d/%s",
                directory,
                now.getYear(),
                now.getMonthValue(),
                filename);

        Path targetPath = basePath.resolve(relativePath);

        // Create parent directories if they don't exist
        Files.createDirectories(targetPath.getParent());

        // Copy file to storage
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        log.info("File stored at: {}", relativePath);
        return relativePath;
    }

    @Override
    public void delete(String path) throws IOException {
        if (path == null || path.isBlank()) {
            return;
        }

        Path targetPath = basePath.resolve(path);

        if (Files.exists(targetPath)) {
            Files.delete(targetPath);
            log.info("File deleted: {}", path);
        } else {
            log.debug("File not found for deletion: {}", path);
        }
    }

    @Override
    public String getUrl(String path) {
        if (path == null || path.isBlank()) {
            return null;
        }
        return baseUrl + "/" + path;
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
