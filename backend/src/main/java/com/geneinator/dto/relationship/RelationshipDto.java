package com.geneinator.dto.relationship;

import com.geneinator.dto.common.ApproximateDateDto;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class RelationshipDto {
    private UUID id;
    private UUID personFromId;
    private String personFromName;
    private UUID personToId;
    private String personToName;
    private String relationshipType;
    private ApproximateDateDto startDate;
    private ApproximateDateDto endDate;
    private Boolean isDivorced;
}
