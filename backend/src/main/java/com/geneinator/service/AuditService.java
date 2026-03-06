package com.geneinator.service;

import com.geneinator.dto.audit.AuditLogDto;
import com.geneinator.entity.AuditLog.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public interface AuditService {

    void log(AuditAction action, String entityType, UUID entityId,
             Map<String, Object> oldValue, Map<String, Object> newValue,
             UUID userId, String ipAddress);

    Page<AuditLogDto> findAll(Pageable pageable);

    Page<AuditLogDto> findByUserId(UUID userId, Pageable pageable);

    Page<AuditLogDto> findByEntity(String entityType, UUID entityId, Pageable pageable);

    Page<AuditLogDto> findByDateRange(Instant start, Instant end, Pageable pageable);
}
