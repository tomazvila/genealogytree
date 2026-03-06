package com.geneinator.repository;

import com.geneinator.entity.Event;
import com.geneinator.entity.Event.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    Page<Event> findByEventType(EventType eventType, Pageable pageable);

    @Query("SELECT e FROM Event e JOIN e.participants ep WHERE ep.person.id = :personId")
    Page<Event> findByPersonId(@Param("personId") UUID personId, Pageable pageable);

    Page<Event> findByCreatedBy(UUID createdBy, Pageable pageable);
}
