package com.geneinator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/storage")
@RequiredArgsConstructor
@Slf4j
public class StorageController {

    private final StorageFileService storageFileService;

    @GetMapping("/{*path}")
    public ResponseEntity<Resource> getFile(@PathVariable String path) {
        // Remove leading slash if present (Spring includes it with {*path})
        if (path != null && path.startsWith("/")) {
            path = path.substring(1);
        }
        log.debug("Serving file: {}", path);

        Resource resource = storageFileService.loadAsResource(path);

        if (resource == null || !resource.exists()) {
            log.debug("File not found: {}", path);
            return ResponseEntity.notFound().build();
        }

        String contentType = storageFileService.getContentType(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CACHE_CONTROL, "max-age=86400")
                .body(resource);
    }

    @Service
    @Slf4j
    public static class StorageFileService {

        private final Path basePath;

        public StorageFileService(@Value("${app.storage.base-path:./data/geneinator}") String basePath) {
            this.basePath = Paths.get(basePath).toAbsolutePath().normalize();
        }

        public Resource loadAsResource(String path) {
            try {
                Path filePath = basePath.resolve(path).normalize();

                // Security check: ensure the path is within base path
                if (!filePath.startsWith(basePath)) {
                    log.warn("Path traversal attempt detected: {}", path);
                    return null;
                }

                if (!Files.exists(filePath)) {
                    return null;
                }

                Resource resource = new UrlResource(filePath.toUri());
                if (resource.exists() && resource.isReadable()) {
                    return resource;
                }
                return null;
            } catch (MalformedURLException e) {
                log.error("Failed to load file: {}", path, e);
                return null;
            }
        }

        public String getContentType(String path) {
            try {
                Path filePath = basePath.resolve(path).normalize();
                return Files.probeContentType(filePath);
            } catch (Exception e) {
                log.debug("Could not determine content type for: {}", path);
                return null;
            }
        }
    }
}
