package com.geneinator.worker.service;

import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class ImageProcessingResult {

    private UUID photoId;
    private String status;
    private String thumbnailSmall;
    private String thumbnailMedium;
    private String thumbnailLarge;
    private Map<String, Object> exifData;
}
