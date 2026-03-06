package com.geneinator.service;

import com.geneinator.dto.event.EventCreateRequest;
import com.geneinator.dto.event.EventDto;
import com.geneinator.dto.event.EventUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface EventService {

    EventDto findById(UUID id);

    Page<EventDto> findAll(Pageable pageable);

    Page<EventDto> findByPersonId(UUID personId, Pageable pageable);

    EventDto create(EventCreateRequest request, UUID createdBy);

    EventDto update(UUID id, EventUpdateRequest request);

    void delete(UUID id);
}
