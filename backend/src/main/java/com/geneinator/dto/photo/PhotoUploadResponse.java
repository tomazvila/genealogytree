package com.geneinator.dto.photo;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PhotoUploadResponse {
    private UUID photoId;
    private String originalUrl;
    private String processingStatus;
    private String message;
}
