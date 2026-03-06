package com.geneinator.service.impl;

import com.geneinator.dto.common.ApproximateDateDto;
import com.geneinator.dto.relationship.RelationshipCreateRequest;
import com.geneinator.dto.relationship.RelationshipDto;
import com.geneinator.entity.ApproximateDate;
import com.geneinator.entity.Person;
import com.geneinator.entity.Relationship;
import com.geneinator.entity.Relationship.RelationshipType;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.repository.PersonRepository;
import com.geneinator.repository.RelationshipRepository;
import com.geneinator.service.RelationshipService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelationshipServiceImpl implements RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final PersonRepository personRepository;

    @Override
    @Transactional
    public RelationshipDto create(RelationshipCreateRequest request) {
        log.info("Creating relationship: {} -> {} ({})",
                request.getPersonFromId(), request.getPersonToId(), request.getRelationshipType());

        Person personFrom = personRepository.findById(request.getPersonFromId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found with id: " + request.getPersonFromId()));

        Person personTo = personRepository.findById(request.getPersonToId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Person not found with id: " + request.getPersonToId()));

        Relationship relationship = Relationship.builder()
                .personFrom(personFrom)
                .personTo(personTo)
                .relationshipType(RelationshipType.valueOf(request.getRelationshipType()))
                .startDate(toEntity(request.getStartDate()))
                .endDate(toEntity(request.getEndDate()))
                .isDivorced(request.getIsDivorced())
                .build();

        Relationship saved = relationshipRepository.save(relationship);
        log.info("Relationship created with id: {}", saved.getId());

        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting relationship: {}", id);

        Relationship relationship = relationshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Relationship not found with id: " + id));

        relationshipRepository.delete(relationship);
        log.info("Relationship deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelationshipDto> findByPersonId(UUID personId) {
        return relationshipRepository.findAllByPersonId(personId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean areRelated(UUID personId1, UUID personId2, int maxHops) {
        if (personId1.equals(personId2)) {
            return true;
        }

        // BFS to find if persons are related within maxHops
        Set<UUID> visited = new HashSet<>();
        Queue<UUID> queue = new LinkedList<>();
        Map<UUID, Integer> distances = new HashMap<>();

        queue.add(personId1);
        visited.add(personId1);
        distances.put(personId1, 0);

        while (!queue.isEmpty()) {
            UUID current = queue.poll();
            int currentDistance = distances.get(current);

            if (currentDistance >= maxHops) {
                continue;
            }

            List<Relationship> relationships = relationshipRepository.findAllByPersonId(current);

            for (Relationship rel : relationships) {
                UUID nextPerson = rel.getPersonFrom().getId().equals(current)
                        ? rel.getPersonTo().getId()
                        : rel.getPersonFrom().getId();

                if (nextPerson.equals(personId2)) {
                    return true;
                }

                if (!visited.contains(nextPerson)) {
                    visited.add(nextPerson);
                    queue.add(nextPerson);
                    distances.put(nextPerson, currentDistance + 1);
                }
            }
        }

        return false;
    }

    private RelationshipDto toDto(Relationship relationship) {
        return RelationshipDto.builder()
                .id(relationship.getId())
                .personFromId(relationship.getPersonFrom().getId())
                .personFromName(relationship.getPersonFrom().getFullName())
                .personToId(relationship.getPersonTo().getId())
                .personToName(relationship.getPersonTo().getFullName())
                .relationshipType(relationship.getRelationshipType().name())
                .startDate(toDto(relationship.getStartDate()))
                .endDate(toDto(relationship.getEndDate()))
                .isDivorced(relationship.getIsDivorced())
                .build();
    }

    private ApproximateDateDto toDto(ApproximateDate date) {
        if (date == null) return null;
        return ApproximateDateDto.builder()
                .year(date.getYear())
                .month(date.getMonth())
                .day(date.getDay())
                .isApproximate(date.getIsApproximate())
                .dateText(date.getDateText())
                .build();
    }

    private ApproximateDate toEntity(ApproximateDateDto dto) {
        if (dto == null) return null;
        return ApproximateDate.builder()
                .year(dto.getYear())
                .month(dto.getMonth())
                .day(dto.getDay())
                .isApproximate(dto.getIsApproximate())
                .dateText(dto.getDateText())
                .build();
    }
}
