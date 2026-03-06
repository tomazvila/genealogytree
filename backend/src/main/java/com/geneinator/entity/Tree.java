package com.geneinator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "trees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tree extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_person_id")
    private Person rootPerson;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "is_mergeable")
    private Boolean isMergeable;

    @OneToMany(mappedBy = "tree")
    @Builder.Default
    private Set<Person> persons = new HashSet<>();
}
