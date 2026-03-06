package com.geneinator.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Photo extends BaseEntity {

    @Column(name = "original_path", nullable = false)
    private String originalPath;

    @Column(name = "thumbnail_small")
    private String thumbnailSmall;

    @Column(name = "thumbnail_medium")
    private String thumbnailMedium;

    @Column(name = "thumbnail_large")
    private String thumbnailLarge;

    private String caption;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "year", column = @Column(name = "taken_year")),
            @AttributeOverride(name = "month", column = @Column(name = "taken_month")),
            @AttributeOverride(name = "day", column = @Column(name = "taken_day")),
            @AttributeOverride(name = "isApproximate", column = @Column(name = "taken_is_approximate")),
            @AttributeOverride(name = "dateText", column = @Column(name = "taken_date_text"))
    })
    private ApproximateDate dateTaken;

    private String location;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "exif_data", columnDefinition = "jsonb")
    private Map<String, Object> exifData;

    @Enumerated(EnumType.STRING)
    @Column(name = "processing_status", nullable = false)
    private ProcessingStatus processingStatus;

    @Column(name = "uploaded_by", nullable = false)
    private UUID uploadedBy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "privacy_settings", columnDefinition = "jsonb")
    private Map<String, Object> privacySettings;

    @OneToMany(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PersonPhoto> persons = new HashSet<>();

    public enum ProcessingStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }
}
