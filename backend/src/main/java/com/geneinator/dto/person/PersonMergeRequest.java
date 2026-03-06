package com.geneinator.dto.person;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonMergeRequest {

    @NotNull(message = "Primary person ID is required")
    private UUID primaryPersonId;

    @NotNull(message = "Secondary person ID is required")
    private UUID secondaryPersonId;

    @Builder.Default
    private Boolean mergeBiography = false;
}
