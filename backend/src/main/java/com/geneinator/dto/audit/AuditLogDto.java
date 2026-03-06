package com.geneinator.dto.audit;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class AuditLogDto {
    private UUID id;
    private UUID userId;
    private String userUsername;
    private String action;
    private String entityType;
    private UUID entityId;
    private Map<String, Object> oldValue;
    private Map<String, Object> newValue;
    private Instant timestamp;
    private String ipAddress;
}
