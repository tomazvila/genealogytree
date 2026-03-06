package com.geneinator.dto.settings;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class SystemSettingsDto {
    private Boolean spouseFamilyVisible;
    private Integer maxRelationshipHops;
    private Boolean includeMarriageConnections;
}
