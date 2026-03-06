package com.geneinator.service.impl;

import com.geneinator.dto.common.ApproximateDateDto;
import com.geneinator.dto.person.PersonDto;
import com.geneinator.dto.relationship.RelationshipDto;
import com.geneinator.dto.tree.TreeCreateRequest;
import com.geneinator.dto.tree.TreeDto;
import com.geneinator.dto.tree.TreeStructureDto;
import com.geneinator.entity.ApproximateDate;
import com.geneinator.entity.Person;
import com.geneinator.entity.Photo;
import com.geneinator.entity.Relationship;
import com.geneinator.entity.Tree;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.repository.PersonRepository;
import com.geneinator.repository.PhotoRepository;
import com.geneinator.repository.RelationshipRepository;
import com.geneinator.repository.TreeRepository;
import com.geneinator.service.StorageService;
import com.geneinator.service.TreeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TreeServiceImpl implements TreeService {

    private final TreeRepository treeRepository;
    private final PersonRepository personRepository;
    private final RelationshipRepository relationshipRepository;
    private final PhotoRepository photoRepository;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public TreeDto findById(UUID id) {
        Tree tree = treeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tree not found with id: " + id));
        return toDto(tree);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TreeDto> findAll(Pageable pageable) {
        return treeRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public TreeDto create(TreeCreateRequest request, UUID createdBy) {
        log.info("Creating tree: {} by user: {}", request.getName(), createdBy);

        Person rootPerson = null;
        if (request.getRootPersonId() != null) {
            rootPerson = personRepository.findById(request.getRootPersonId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Person not found with id: " + request.getRootPersonId()));
        }

        Tree tree = Tree.builder()
                .name(request.getName())
                .description(request.getDescription())
                .rootPerson(rootPerson)
                .createdBy(createdBy)
                .isMergeable(request.getIsMergeable() != null ? request.getIsMergeable() : true)
                .build();

        Tree saved = treeRepository.save(tree);
        log.info("Tree created with id: {}", saved.getId());

        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TreeStructureDto getTreeStructure(UUID treeId, UUID viewerId) {
        Tree tree = treeRepository.findById(treeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tree not found with id: " + treeId));

        // Get all persons in this tree
        List<Person> persons = personRepository.findByTreeId(treeId);
        List<PersonDto> personDtos = persons.stream()
                .map(this::toPersonDto)
                .collect(Collectors.toList());

        // Get all relationships between persons in this tree
        List<RelationshipDto> relationshipDtos;
        if (persons.isEmpty()) {
            relationshipDtos = Collections.emptyList();
        } else {
            List<UUID> personIds = persons.stream()
                    .map(Person::getId)
                    .collect(Collectors.toList());
            List<Relationship> relationships = relationshipRepository.findAllByPersonIdsIn(personIds);
            relationshipDtos = relationships.stream()
                    .map(this::toRelationshipDto)
                    .collect(Collectors.toList());
        }

        return TreeStructureDto.builder()
                .treeId(tree.getId())
                .treeName(tree.getName())
                .createdBy(tree.getCreatedBy())
                .persons(personDtos)
                .relationships(relationshipDtos)
                .build();
    }

    @Override
    @Transactional
    public void mergeTrees(UUID sourceTreeId, UUID targetTreeId) {
        log.info("Merging tree {} into tree {}", sourceTreeId, targetTreeId);

        Tree sourceTree = treeRepository.findById(sourceTreeId)
                .orElseThrow(() -> new ResourceNotFoundException("Source tree not found: " + sourceTreeId));

        Tree targetTree = treeRepository.findById(targetTreeId)
                .orElseThrow(() -> new ResourceNotFoundException("Target tree not found: " + targetTreeId));

        if (!Boolean.TRUE.equals(sourceTree.getIsMergeable())) {
            throw new IllegalStateException("Source tree is not mergeable");
        }

        // Move all persons from source tree to target tree (batch update)
        sourceTree.getPersons().forEach(person -> person.setTree(targetTree));
        personRepository.saveAll(sourceTree.getPersons());

        // Delete the source tree
        treeRepository.delete(sourceTree);
        log.info("Trees merged successfully");
    }

    private TreeDto toDto(Tree tree) {
        return TreeDto.builder()
                .id(tree.getId())
                .name(tree.getName())
                .description(tree.getDescription())
                .rootPersonId(tree.getRootPerson() != null ? tree.getRootPerson().getId() : null)
                .rootPersonName(tree.getRootPerson() != null ? tree.getRootPerson().getFullName() : null)
                .personCount(tree.getPersons() != null ? tree.getPersons().size() : 0)
                .isMergeable(tree.getIsMergeable())
                .createdBy(tree.getCreatedBy())
                .createdAt(tree.getCreatedAt())
                .build();
    }

    private PersonDto toPersonDto(Person person) {
        String primaryPhotoUrl = null;
        Photo primaryPhoto = photoRepository.findPrimaryPhotoByPersonId(person.getId());
        if (primaryPhoto != null) {
            // Prefer thumbnail for display, fall back to original
            String photoPath = primaryPhoto.getThumbnailMedium() != null
                    ? primaryPhoto.getThumbnailMedium()
                    : primaryPhoto.getOriginalPath();
            primaryPhotoUrl = storageService.getUrl(photoPath);
        }

        return PersonDto.builder()
                .id(person.getId())
                .fullName(person.getFullName())
                .birthDate(toApproximateDateDto(person.getBirthDate()))
                .deathDate(person.getDeathDate() != null ? toApproximateDateDto(person.getDeathDate()) : null)
                .gender(person.getGender() != null ? person.getGender().name() : null)
                .biography(person.getBiography())
                .contactInfo(person.getContactInfo())
                .locationBirth(person.getLocationBirth())
                .locationDeath(person.getLocationDeath())
                .locationBurial(person.getLocationBurial())
                .treeId(person.getTree() != null ? person.getTree().getId() : null)
                .primaryPhotoUrl(primaryPhotoUrl)
                .createdAt(person.getCreatedAt())
                .updatedAt(person.getUpdatedAt())
                .build();
    }

    private RelationshipDto toRelationshipDto(Relationship relationship) {
        return RelationshipDto.builder()
                .id(relationship.getId())
                .personFromId(relationship.getPersonFrom().getId())
                .personFromName(relationship.getPersonFrom().getFullName())
                .personToId(relationship.getPersonTo().getId())
                .personToName(relationship.getPersonTo().getFullName())
                .relationshipType(relationship.getRelationshipType().name())
                .startDate(toApproximateDateDto(relationship.getStartDate()))
                .endDate(toApproximateDateDto(relationship.getEndDate()))
                .isDivorced(relationship.getIsDivorced())
                .build();
    }

    private ApproximateDateDto toApproximateDateDto(ApproximateDate date) {
        if (date == null) return null;
        return ApproximateDateDto.builder()
                .year(date.getYear())
                .month(date.getMonth())
                .day(date.getDay())
                .isApproximate(date.getIsApproximate())
                .dateText(date.getDateText())
                .build();
    }
}
