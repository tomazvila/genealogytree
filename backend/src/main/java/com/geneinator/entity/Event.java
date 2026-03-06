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
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "year", column = @Column(name = "event_year")),
            @AttributeOverride(name = "month", column = @Column(name = "event_month")),
            @AttributeOverride(name = "day", column = @Column(name = "event_day")),
            @AttributeOverride(name = "isApproximate", column = @Column(name = "event_is_approximate")),
            @AttributeOverride(name = "dateText", column = @Column(name = "event_date_text"))
    })
    private ApproximateDate eventDate;

    private String location;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "privacy_settings", columnDefinition = "jsonb")
    private Map<String, Object> privacySettings;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<EventParticipant> participants = new HashSet<>();

    public enum EventType {
        WEDDING, GRADUATION, MILITARY_SERVICE, BIRTH, DEATH, BAPTISM, OTHER
    }
}
