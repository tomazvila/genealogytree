package com.geneinator.dto.person;

import com.geneinator.dto.common.ApproximateDateDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PersonDto {
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
}
