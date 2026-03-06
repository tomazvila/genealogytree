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
@Table(name = "persons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person extends BaseEntity {

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "year", column = @Column(name = "birth_year")),
            @AttributeOverride(name = "month", column = @Column(name = "birth_month")),
            @AttributeOverride(name = "day", column = @Column(name = "birth_day")),
            @AttributeOverride(name = "isApproximate", column = @Column(name = "birth_is_approximate")),
            @AttributeOverride(name = "dateText", column = @Column(name = "birth_date_text"))
    })
    private ApproximateDate birthDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "year", column = @Column(name = "death_year")),
            @AttributeOverride(name = "month", column = @Column(name = "death_month")),
            @AttributeOverride(name = "day", column = @Column(name = "death_day")),
            @AttributeOverride(name = "isApproximate", column = @Column(name = "death_is_approximate")),
            @AttributeOverride(name = "dateText", column = @Column(name = "death_date_text"))
    })
    private ApproximateDate deathDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String biography;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contact_info", columnDefinition = "jsonb")
    private Map<String, String> contactInfo;

    @Column(name = "location_birth")
    private String locationBirth;

    @Column(name = "location_death")
    private String locationDeath;

    @Column(name = "location_burial")
    private String locationBurial;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "privacy_settings", columnDefinition = "jsonb")
    private Map<String, Object> privacySettings;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tree_id")
    private Tree tree;

    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<PersonPhoto> photos = new HashSet<>();

    @OneToMany(mappedBy = "personFrom", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Relationship> relationshipsFrom = new HashSet<>();

    @OneToMany(mappedBy = "personTo", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Relationship> relationshipsTo = new HashSet<>();

    public enum Gender {
        MALE, FEMALE, OTHER, UNKNOWN
    }
}
