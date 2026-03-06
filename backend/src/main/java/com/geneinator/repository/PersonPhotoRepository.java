package com.geneinator.repository;

import com.geneinator.entity.PersonPhoto;
import com.geneinator.entity.PersonPhoto.PersonPhotoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PersonPhotoRepository extends JpaRepository<PersonPhoto, PersonPhotoId> {

    /**
     * Atomically clears isPrimary flag for all photos of a person.
     * Uses direct UPDATE to avoid race conditions with in-memory manipulation.
     */
    @Modifying
    @Query("UPDATE PersonPhoto pp SET pp.isPrimary = false WHERE pp.person.id = :personId")
    int clearPrimaryForPerson(@Param("personId") UUID personId);

    /**
     * Atomically sets a specific photo as primary for a person.
     * Uses direct UPDATE to avoid race conditions.
     */
    @Modifying
    @Query("UPDATE PersonPhoto pp SET pp.isPrimary = true WHERE pp.person.id = :personId AND pp.photo.id = :photoId")
    int setAsPrimary(@Param("personId") UUID personId, @Param("photoId") UUID photoId);

    /**
     * Checks if a photo is linked to a person.
     */
    boolean existsById(PersonPhotoId id);
}
