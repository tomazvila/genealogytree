package com.geneinator.dto.tree;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class TreeDto {
    private UUID id;
    private String name;
    private String description;
    private UUID rootPersonId;
    private String rootPersonName;
    private int personCount;
    private Boolean isMergeable;
    private UUID createdBy;
    private Instant createdAt;
}
