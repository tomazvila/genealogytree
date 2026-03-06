package com.geneinator.service.impl;

import com.geneinator.dto.audit.AuditLogDto;
import com.geneinator.entity.AuditLog;
import com.geneinator.entity.AuditLog.AuditAction;
import com.geneinator.repository.AuditLogRepository;
import com.geneinator.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void log(AuditAction action, String entityType, UUID entityId,
                    Map<String, Object> oldValue, Map<String, Object> newValue,
                    UUID userId, String ipAddress) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .oldValue(oldValue)
                .newValue(newValue)
                .userId(userId)
                .ipAddress(ipAddress)
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit log created: {} on {} {}", action, entityType, entityId);
    }

    @Override
    public Page<AuditLogDto> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<AuditLogDto> findByUserId(UUID userId, Pageable pageable) {
        return auditLogRepository.findByUserId(userId, pageable).map(this::toDto);
    }

    @Override
    public Page<AuditLogDto> findByEntity(String entityType, UUID entityId, Pageable pageable) {
        return auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId, pageable).map(this::toDto);
    }

    @Override
    public Page<AuditLogDto> findByDateRange(Instant start, Instant end, Pageable pageable) {
        return auditLogRepository.findByTimestampBetween(start, end, pageable).map(this::toDto);
    }

    private AuditLogDto toDto(AuditLog auditLog) {
        return AuditLogDto.builder()
                .id(auditLog.getId())
                .userId(auditLog.getUserId())
                .action(auditLog.getAction().name())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .timestamp(auditLog.getTimestamp())
                .ipAddress(auditLog.getIpAddress())
                .build();
    }
}
