package com.geneinator.controller;

import com.geneinator.dto.relationship.RelationshipCreateRequest;
import com.geneinator.dto.relationship.RelationshipDto;
import com.geneinator.service.RelationshipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/relationships")
@RequiredArgsConstructor
public class RelationshipController {

    private final RelationshipService relationshipService;

    @PostMapping
    public ResponseEntity<RelationshipDto> create(@Valid @RequestBody RelationshipCreateRequest request) {
        RelationshipDto created = relationshipService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        relationshipService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<RelationshipDto>> findByPerson(@PathVariable UUID personId) {
        List<RelationshipDto> relationships = relationshipService.findByPersonId(personId);
        return ResponseEntity.ok(relationships);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> areRelated(
            @RequestParam UUID personId1,
            @RequestParam UUID personId2,
            @RequestParam(defaultValue = "3") int maxHops) {
        boolean related = relationshipService.areRelated(personId1, personId2, maxHops);
        return ResponseEntity.ok(Map.of("related", related));
    }
}
