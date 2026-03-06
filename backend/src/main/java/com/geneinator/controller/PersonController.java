package com.geneinator.controller;

import com.geneinator.dto.event.EventDto;
import com.geneinator.dto.person.PersonCreateRequest;
import com.geneinator.dto.person.PersonDto;
import com.geneinator.dto.person.PersonMergeRequest;
import com.geneinator.dto.person.PersonUpdateRequest;
import com.geneinator.dto.person.RelativeDto;
import com.geneinator.dto.photo.PhotoDto;
import com.geneinator.entity.User;
import com.geneinator.service.EventService;
import com.geneinator.service.PersonService;
import com.geneinator.service.PhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;
    private final PhotoService photoService;
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Page<PersonDto>> findAll(Pageable pageable) {
        Page<PersonDto> persons = personService.findAll(pageable);
        return ResponseEntity.ok(persons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PersonDto> findById(@PathVariable UUID id) {
        PersonDto person = personService.findById(id);
        return ResponseEntity.ok(person);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<PersonDto>> search(@RequestParam("q") String query, Pageable pageable) {
        Page<PersonDto> results = personService.search(query, pageable);
        return ResponseEntity.ok(results);
    }

    @PostMapping
    public ResponseEntity<PersonDto> create(
            @Valid @RequestBody PersonCreateRequest request,
            @AuthenticationPrincipal User user) {
        UUID userId = user != null ? user.getId() : UUID.randomUUID(); // Fallback for tests
        PersonDto created = personService.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody PersonUpdateRequest request) {
        PersonDto updated = personService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        personService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/relatives")
    public ResponseEntity<List<RelativeDto>> findRelatives(@PathVariable UUID id) {
        List<RelativeDto> relatives = personService.findRelatives(id);
        return ResponseEntity.ok(relatives);
    }

    @GetMapping("/{id}/photos")
    public ResponseEntity<Page<PhotoDto>> findPhotos(@PathVariable UUID id, Pageable pageable) {
        Page<PhotoDto> photos = photoService.findByPersonId(id, pageable);
        return ResponseEntity.ok(photos);
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<Page<EventDto>> findEvents(@PathVariable UUID id, Pageable pageable) {
        Page<EventDto> events = eventService.findByPersonId(id, pageable);
        return ResponseEntity.ok(events);
    }

    @PostMapping("/merge")
    public ResponseEntity<PersonDto> merge(
            @Valid @RequestBody PersonMergeRequest request,
            @AuthenticationPrincipal User user) {
        UUID userId = user != null ? user.getId() : null;
        PersonDto merged = personService.merge(request, userId);
        return ResponseEntity.ok(merged);
    }
}
