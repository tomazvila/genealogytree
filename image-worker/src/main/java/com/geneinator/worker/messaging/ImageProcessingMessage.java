package com.geneinator.worker.messaging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImageProcessingMessage {
    private UUID photoId;
    private String originalPath;
    private UUID uploadedBy;
}
