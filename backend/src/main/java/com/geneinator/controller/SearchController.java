package com.geneinator.controller;

import com.geneinator.dto.person.PersonDto;
import com.geneinator.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final PersonService personService;

    @GetMapping
    public ResponseEntity<Page<PersonDto>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer birthYearFrom,
            @RequestParam(required = false) Integer birthYearTo,
            @RequestParam(required = false) String location,
            Pageable pageable) {

        // Validate that at least one search criteria is provided
        if (name == null && birthYearFrom == null && birthYearTo == null && location == null) {
            return ResponseEntity.badRequest().build();
        }

        // Determine which search method to use based on provided criteria
        boolean hasName = name != null && !name.isBlank();
        boolean hasBirthYear = birthYearFrom != null || birthYearTo != null;
        boolean hasLocation = location != null && !location.isBlank();

        // Multiple criteria - use advanced search
        int criteriaCount = (hasName ? 1 : 0) + (hasBirthYear ? 1 : 0) + (hasLocation ? 1 : 0);

        if (criteriaCount > 1) {
            Page<PersonDto> results = personService.searchAdvanced(
                    name, birthYearFrom, birthYearTo, location, pageable);
            return ResponseEntity.ok(results);
        }

        // Single criteria searches
        if (hasName) {
            Page<PersonDto> results = personService.search(name, pageable);
            return ResponseEntity.ok(results);
        }

        if (hasBirthYear) {
            // Default values if only one bound is provided
            Integer fromYear = birthYearFrom != null ? birthYearFrom : 0;
            Integer toYear = birthYearTo != null ? birthYearTo : 9999;
            Page<PersonDto> results = personService.searchByBirthYearRange(fromYear, toYear, pageable);
            return ResponseEntity.ok(results);
        }

        if (hasLocation) {
            Page<PersonDto> results = personService.searchByLocation(location, pageable);
            return ResponseEntity.ok(results);
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/descendants/{ancestorId}")
    public ResponseEntity<Page<PersonDto>> findDescendants(
            @PathVariable UUID ancestorId,
            Pageable pageable) {
        Page<PersonDto> descendants = personService.findDescendants(ancestorId, pageable);
        return ResponseEntity.ok(descendants);
    }

    @GetMapping("/ancestors/{descendantId}")
    public ResponseEntity<Page<PersonDto>> findAncestors(
            @PathVariable UUID descendantId,
            Pageable pageable) {
        Page<PersonDto> ancestors = personService.findAncestors(descendantId, pageable);
        return ResponseEntity.ok(ancestors);
    }
}
