package com.geneinator.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "relationships")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Relationship extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_from_id", nullable = false)
    private Person personFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_to_id", nullable = false)
    private Person personTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "relationship_type", nullable = false)
    private RelationshipType relationshipType;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "year", column = @Column(name = "start_year")),
            @AttributeOverride(name = "month", column = @Column(name = "start_month")),
            @AttributeOverride(name = "day", column = @Column(name = "start_day")),
            @AttributeOverride(name = "isApproximate", column = @Column(name = "start_is_approximate")),
            @AttributeOverride(name = "dateText", column = @Column(name = "start_date_text"))
    })
    private ApproximateDate startDate;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "year", column = @Column(name = "end_year")),
            @AttributeOverride(name = "month", column = @Column(name = "end_month")),
            @AttributeOverride(name = "day", column = @Column(name = "end_day")),
            @AttributeOverride(name = "isApproximate", column = @Column(name = "end_is_approximate")),
            @AttributeOverride(name = "dateText", column = @Column(name = "end_date_text"))
    })
    private ApproximateDate endDate;

    @Column(name = "is_divorced")
    private Boolean isDivorced;

    public enum RelationshipType {
        PARENT, CHILD, SPOUSE, SIBLING
    }
}
