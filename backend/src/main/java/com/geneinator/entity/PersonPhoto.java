package com.geneinator.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "person_photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonPhoto {

    @EmbeddedId
    private PersonPhotoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("personId")
    @JoinColumn(name = "person_id")
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("photoId")
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class PersonPhotoId implements java.io.Serializable {
        @Column(name = "person_id")
        private UUID personId;

        @Column(name = "photo_id")
        private UUID photoId;
    }
}
