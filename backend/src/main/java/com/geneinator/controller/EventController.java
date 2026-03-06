package com.geneinator.controller;

import com.geneinator.dto.event.EventCreateRequest;
import com.geneinator.dto.event.EventDto;
import com.geneinator.dto.event.EventUpdateRequest;
import com.geneinator.entity.User;
import com.geneinator.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Page<EventDto>> findAll(Pageable pageable) {
        Page<EventDto> events = eventService.findAll(pageable);
        return ResponseEntity.ok(events);
    }

    @PostMapping
    public ResponseEntity<EventDto> create(
            @Valid @RequestBody EventCreateRequest request,
            @AuthenticationPrincipal User user) {
        UUID createdBy = user != null ? user.getId() : UUID.randomUUID();
        EventDto created = eventService.create(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody EventUpdateRequest request) {
        EventDto updated = eventService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
