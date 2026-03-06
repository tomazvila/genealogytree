package com.geneinator.dto.relationship;

import com.geneinator.dto.common.ApproximateDateDto;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class RelationshipCreateRequest {

    @NotNull(message = "Person from ID is required")
    private UUID personFromId;

    @NotNull(message = "Person to ID is required")
    private UUID personToId;

    @NotNull(message = "Relationship type is required")
    private String relationshipType;

    private ApproximateDateDto startDate;
    private ApproximateDateDto endDate;
    private Boolean isDivorced;
}
