package com.geneinator.dto.person;

import com.geneinator.dto.common.ApproximateDateDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PersonCreateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotNull(message = "Birth date is required")
    private ApproximateDateDto birthDate;

    private ApproximateDateDto deathDate;
    private String gender;
    private String biography;
    private Map<String, String> contactInfo;
    private String locationBirth;
    private String locationDeath;
    private String locationBurial;
    private UUID treeId;
    private Map<String, Object> privacySettings;
}
