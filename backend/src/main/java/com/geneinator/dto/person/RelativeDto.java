package com.geneinator.dto.person;

import com.geneinator.dto.common.ApproximateDateDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * DTO representing a relative of a person, including the relationship type.
 * Contains person information plus the type of relationship from the perspective
 * of the person being queried.
 */
@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class RelativeDto {
    private UUID id;
    private String fullName;
    private ApproximateDateDto birthDate;
    private ApproximateDateDto deathDate;
    private String gender;
    private String biography;
    private Map<String, String> contactInfo;
    private String locationBirth;
    private String locationDeath;
    private String locationBurial;
    private UUID treeId;
    private String primaryPhotoUrl;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * The relationship type from the queried person's perspective.
     * Values: PARENT, CHILD, SPOUSE
     */
    private String relationshipType;
}
