package com.geneinator.worker.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;
import java.util.UUID;

/**
 * Simplified Photo entity for the image-worker.
 * Only includes fields that the worker needs to update after processing.
 * Maps to the same 'photos' table as the backend entity.
 */
@Entity
@Table(name = "photos")
@Getter
@Setter
@NoArgsConstructor
public class Photo {

    @Id
    private UUID id;

    @Column(name = "thumbnail_small")
    private String thumbnailSmall;

    @Column(name = "thumbnail_medium")
    private String thumbnailMedium;

    @Column(name = "thumbnail_large")
    private String thumbnailLarge;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exif_data", columnDefinition = "jsonb")
    private Map<String, Object> exifData;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false)
    private ProcessingStatus processingStatus;

    public enum ProcessingStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }
}
