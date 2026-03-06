package com.geneinator.controller;

import com.geneinator.dto.audit.AuditLogDto;
import com.geneinator.dto.settings.SystemSettingsDto;
import com.geneinator.dto.user.UserDto;
import com.geneinator.entity.User.UserStatus;
import com.geneinator.service.AuditService;
import com.geneinator.service.PhotoService;
import com.geneinator.service.SettingsService;
import com.geneinator.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final AuditService auditService;
    private final SettingsService settingsService;
    private final PhotoService photoService;

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> findAllUsers(Pageable pageable) {
        Page<UserDto> users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/users/{id}/approve")
    public ResponseEntity<Void> approveUser(@PathVariable UUID id) {
        userService.approveUser(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable UUID id) {
        userService.suspendUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<Page<AuditLogDto>> findAuditLogs(Pageable pageable) {
        Page<AuditLogDto> logs = auditService.findAll(pageable);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        // Get pending approvals count
        Page<UserDto> pendingUsers = userService.findByStatus(
                UserStatus.PENDING_APPROVAL,
                PageRequest.of(0, 1)
        );

        return ResponseEntity.ok(Map.of(
                "pendingApprovals", pendingUsers.getTotalElements()
        ));
    }

    @GetMapping("/settings")
    public ResponseEntity<SystemSettingsDto> getSettings() {
        SystemSettingsDto settings = settingsService.getSettings();
        return ResponseEntity.ok(settings);
    }

    @PutMapping("/settings")
    public ResponseEntity<SystemSettingsDto> updateSettings(@RequestBody SystemSettingsDto settings) {
        SystemSettingsDto updated = settingsService.updateSettings(settings);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/photos/reprocess")
    public ResponseEntity<Map<String, Object>> reprocessPendingPhotos() {
        int count = photoService.reprocessPendingPhotos();
        return ResponseEntity.ok(Map.of(
                "message", "Re-queued photos for processing",
                "count", count
        ));
    }
}
