package com.geneinator.controller;

import com.geneinator.dto.photo.PhotoDto;
import com.geneinator.dto.photo.PhotoUploadResponse;
import com.geneinator.entity.User;
import com.geneinator.service.PhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @GetMapping
    public ResponseEntity<Page<PhotoDto>> getMyPhotos(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable) {
        UUID uploadedBy = user != null ? user.getId() : UUID.randomUUID();
        Page<PhotoDto> photos = photoService.findByUploadedBy(uploadedBy, pageable);
        return ResponseEntity.ok(photos);
    }

    @PostMapping("/upload")
    public ResponseEntity<PhotoUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {
        UUID uploadedBy = user != null ? user.getId() : UUID.randomUUID();
        PhotoUploadResponse response = photoService.upload(file, uploadedBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PhotoDto> findById(@PathVariable UUID id) {
        PhotoDto photo = photoService.findById(id);
        return ResponseEntity.ok(photo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        photoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/persons")
    public ResponseEntity<Void> linkToPersons(
            @PathVariable UUID id,
            @RequestBody List<UUID> personIds,
            @RequestParam(required = false) UUID primaryPersonId) {
        photoService.linkToPersons(id, personIds, primaryPersonId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/set-primary")
    public ResponseEntity<Void> setAsPrimary(
            @PathVariable UUID id,
            @RequestParam UUID personId) {
        photoService.setAsPrimary(id, personId);
        return ResponseEntity.noContent().build();
    }
}
