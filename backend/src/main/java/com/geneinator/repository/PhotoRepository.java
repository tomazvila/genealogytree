package com.geneinator.repository;

import com.geneinator.entity.Photo;
import com.geneinator.entity.Photo.ProcessingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, UUID> {

    List<Photo> findByProcessingStatus(ProcessingStatus status);

    List<Photo> findByProcessingStatusIn(List<ProcessingStatus> statuses);

    Page<Photo> findByUploadedBy(UUID uploadedBy, Pageable pageable);

    @Query("SELECT p FROM Photo p JOIN p.persons pp WHERE pp.person.id = :personId")
    Page<Photo> findByPersonId(@Param("personId") UUID personId, Pageable pageable);

    @Query(value = "SELECT p.* FROM photos p JOIN person_photos pp ON p.id = pp.photo_id WHERE pp.person_id = :personId AND pp.is_primary = true LIMIT 1", nativeQuery = true)
    Photo findPrimaryPhotoByPersonId(@Param("personId") UUID personId);
}
