package com.geneinator.dto.person;

import com.geneinator.dto.common.ApproximateDateDto;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PersonUpdateRequest {
    private String fullName;
    private ApproximateDateDto birthDate;
    private ApproximateDateDto deathDate;
    private String gender;
    private String biography;
    private Map<String, String> contactInfo;
    private String locationBirth;
    private String locationDeath;
    private String locationBurial;
    private Map<String, Object> privacySettings;
}
