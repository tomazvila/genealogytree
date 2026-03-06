package com.geneinator.dto.photo;

import com.geneinator.dto.common.ApproximateDateDto;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class PhotoDto {
    private UUID id;
    private String originalUrl;
    private String thumbnailSmallUrl;
    private String thumbnailMediumUrl;
    private String thumbnailLargeUrl;
    private String caption;
    private ApproximateDateDto dateTaken;
    private String location;
    private String processingStatus;
    private List<UUID> personIds;
    private Map<String, Object> exifData;
    private Instant createdAt;
}
