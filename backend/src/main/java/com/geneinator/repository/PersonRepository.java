package com.geneinator.repository;

import com.geneinator.entity.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonRepository extends JpaRepository<Person, UUID> {

    Page<Person> findByTreeId(UUID treeId, Pageable pageable);

    List<Person> findByTreeId(UUID treeId);

    @Query("SELECT p FROM Person p WHERE LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Person> searchByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM Person p WHERE p.birthDate.year BETWEEN :startYear AND :endYear")
    Page<Person> findByBirthYearBetween(
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear,
            Pageable pageable);

    @Query("SELECT p FROM Person p WHERE " +
            "LOWER(p.locationBirth) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
            "LOWER(p.locationDeath) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
            "LOWER(p.locationBurial) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<Person> searchByLocation(@Param("location") String location, Pageable pageable);

    @Query("SELECT p FROM Person p WHERE " +
            "(:name IS NULL OR LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:birthYearFrom IS NULL OR p.birthDate.year >= :birthYearFrom) AND " +
            "(:birthYearTo IS NULL OR p.birthDate.year <= :birthYearTo) AND " +
            "(:location IS NULL OR (" +
            "LOWER(p.locationBirth) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
            "LOWER(p.locationDeath) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
            "LOWER(p.locationBurial) LIKE LOWER(CONCAT('%', :location, '%'))))")
    Page<Person> searchAdvanced(
            @Param("name") String name,
            @Param("birthYearFrom") Integer birthYearFrom,
            @Param("birthYearTo") Integer birthYearTo,
            @Param("location") String location,
            Pageable pageable);

    List<Person> findByCreatedBy(UUID createdBy);

    @Query("SELECT DISTINCT p FROM Person p " +
            "LEFT JOIN FETCH p.relationshipsFrom rf " +
            "LEFT JOIN FETCH p.relationshipsTo rt " +
            "WHERE p.id = :id")
    Person findByIdWithRelationships(@Param("id") UUID id);
}
