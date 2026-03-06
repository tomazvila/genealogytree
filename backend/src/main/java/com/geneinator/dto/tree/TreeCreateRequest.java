package com.geneinator.dto.tree;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class TreeCreateRequest {

    @NotBlank(message = "Tree name is required")
    private String name;

    private String description;
    private UUID rootPersonId;
    private Boolean isMergeable;
}
