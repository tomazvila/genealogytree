package com.geneinator.service.impl;

import com.geneinator.dto.common.ApproximateDateDto;
import com.geneinator.dto.person.PersonCreateRequest;
import com.geneinator.dto.person.PersonDto;
import com.geneinator.dto.person.PersonMergeRequest;
import com.geneinator.dto.person.PersonUpdateRequest;
import com.geneinator.dto.person.RelativeDto;
import com.geneinator.entity.ApproximateDate;
import com.geneinator.entity.Person;
import com.geneinator.entity.PersonPhoto;
import com.geneinator.entity.Photo;
import com.geneinator.entity.Relationship;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.repository.PersonRepository;
import com.geneinator.repository.PhotoRepository;
import com.geneinator.repository.RelationshipRepository;
import com.geneinator.service.PersonService;
import com.geneinator.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;
    private final RelationshipRepository relationshipRepository;
    private final PhotoRepository photoRepository;
    private final StorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public PersonDto findById(UUID id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));
        return toDto(person);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonDto> findAll(Pageable pageable) {
        return personRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonDto> search(String query, Pageable pageable) {
        return personRepository.searchByName(query, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonDto> searchByBirthYearRange(Integer fromYear, Integer toYear, Pageable pageable) {
        return personRepository.findByBirthYearBetween(fromYear, toYear, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonDto> searchByLocation(String location, Pageable pageable) {
        return personRepository.searchByLocation(location, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonDto> searchAdvanced(String name, Integer birthYearFrom, Integer birthYearTo,
                                           String location, Pageable pageable) {
        return personRepository.searchAdvanced(name, birthYearFrom, birthYearTo, location, pageable)
                .map(this::toDto);
    }

    @Override
    @Transactional
    public PersonDto create(PersonCreateRequest request, UUID createdBy) {
        log.info("Creating person: {} by user: {}", request.getFullName(), createdBy);

        Person person = Person.builder()
                .fullName(request.getFullName())
                .birthDate(toEntity(request.getBirthDate()))
                .deathDate(request.getDeathDate() != null ? toEntity(request.getDeathDate()) : null)
                .gender(request.getGender() != null ? Person.Gender.valueOf(request.getGender()) : null)
                .biography(request.getBiography())
                .contactInfo(request.getContactInfo())
                .locationBirth(request.getLocationBirth())
                .locationDeath(request.getLocationDeath())
                .locationBurial(request.getLocationBurial())
                .createdBy(createdBy)
                .privacySettings(request.getPrivacySettings())
                .build();

        Person saved = personRepository.save(person);
        log.info("Person created with id: {}", saved.getId());

        return toDto(saved);
    }

    @Override
    @Transactional
    public PersonDto update(UUID id, PersonUpdateRequest request) {
        log.info("Updating person: {}", id);

        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));

        if (request.getFullName() != null) {
            person.setFullName(request.getFullName());
        }
        if (request.getBirthDate() != null) {
            person.setBirthDate(toEntity(request.getBirthDate()));
        }
        if (request.getDeathDate() != null) {
            person.setDeathDate(toEntity(request.getDeathDate()));
        }
        if (request.getGender() != null) {
            person.setGender(Person.Gender.valueOf(request.getGender()));
        }
        if (request.getBiography() != null) {
            person.setBiography(request.getBiography());
        }
        if (request.getContactInfo() != null) {
            person.setContactInfo(request.getContactInfo());
        }
        if (request.getLocationBirth() != null) {
            person.setLocationBirth(request.getLocationBirth());
        }
        if (request.getLocationDeath() != null) {
            person.setLocationDeath(request.getLocationDeath());
        }
        if (request.getLocationBurial() != null) {
            person.setLocationBurial(request.getLocationBurial());
        }
        if (request.getPrivacySettings() != null) {
            person.setPrivacySettings(request.getPrivacySettings());
        }

        Person saved = personRepository.save(person);
        log.info("Person updated: {}", id);

        return toDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.info("Deleting person: {}", id);

        Person person = personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));

        personRepository.delete(person);
        log.info("Person deleted: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RelativeDto> findRelatives(UUID personId) {
        Person person = personRepository.findByIdWithRelationships(personId);
        if (person == null) {
            throw new ResourceNotFoundException("Person not found with id: " + personId);
        }

        List<RelativeDto> relatives = new java.util.ArrayList<>();
        Set<UUID> addedRelativeIds = new HashSet<>();

        // Collect relatives from relationships where this person is "from"
        // The relationship type describes this person's role (e.g., PARENT means "I am the parent")
        // So we need to invert it to get the relative's role from this person's perspective
        // - If I have PARENT relationship, I am the parent, so the relative (personTo) is my CHILD
        // - If I have CHILD relationship, I am the child, so the relative (personTo) is my PARENT
        for (Relationship rel : person.getRelationshipsFrom()) {
            UUID relativeId = rel.getPersonTo().getId();
            if (!addedRelativeIds.contains(relativeId)) {
                String invertedType = invertRelationshipType(rel.getRelationshipType());
                relatives.add(toRelativeDto(rel.getPersonTo(), invertedType));
                addedRelativeIds.add(relativeId);
            }
        }

        // Collect relatives from relationships where this person is "to"
        // The relationship type describes the other person's (personFrom's) role
        // - If personFrom has PARENT relationship to me, they are MY PARENT
        // - If personFrom has CHILD relationship to me, they are MY CHILD
        // So the type directly describes the relative from this person's perspective
        for (Relationship rel : person.getRelationshipsTo()) {
            UUID relativeId = rel.getPersonFrom().getId();
            if (!addedRelativeIds.contains(relativeId)) {
                relatives.add(toRelativeDto(rel.getPersonFrom(), rel.getRelationshipType().name()));
                addedRelativeIds.add(relativeId);
            }
        }

        // Add inferred siblings (people who share at least one parent)
        List<UUID> siblingIds = relationshipRepository.findSiblingIds(personId);
        if (!siblingIds.isEmpty()) {
            // Filter out already-added relatives
            List<UUID> newSiblingIds = siblingIds.stream()
                    .filter(id -> !addedRelativeIds.contains(id))
                    .collect(Collectors.toList());
            if (!newSiblingIds.isEmpty()) {
                List<Person> siblings = personRepository.findAllById(newSiblingIds);
                for (Person sibling : siblings) {
                    relatives.add(toRelativeDto(sibling, "SIBLING"));
                    addedRelativeIds.add(sibling.getId());
                }
            }
        }

        // Add inferred cousins (parent's sibling's children)
        List<UUID> cousinIds = relationshipRepository.findCousinIds(personId);
        if (!cousinIds.isEmpty()) {
            // Filter out already-added relatives
            List<UUID> newCousinIds = cousinIds.stream()
                    .filter(id -> !addedRelativeIds.contains(id))
                    .collect(Collectors.toList());
            if (!newCousinIds.isEmpty()) {
                List<Person> cousins = personRepository.findAllById(newCousinIds);
                for (Person cousin : cousins) {
                    relatives.add(toRelativeDto(cousin, "COUSIN"));
                    addedRelativeIds.add(cousin.getId());
                }
            }
        }

        return relatives;
    }

    private String invertRelationshipType(Relationship.RelationshipType type) {
        return switch (type) {
            case PARENT -> "CHILD";
            case CHILD -> "PARENT";
            case SPOUSE -> "SPOUSE";
            case SIBLING -> "SIBLING";
        };
    }

    @Override
    @Transactional(readOnly = true)
    public PersonDto findWithRelationships(UUID id) {
        Person person = personRepository.findByIdWithRelationships(id);
        if (person == null) {
            throw new ResourceNotFoundException("Person not found with id: " + id);
        }
        return toDto(person);
    }

    @Override
    @Transactional
    public PersonDto merge(PersonMergeRequest request) {
        // Delegate to overloaded method with null requestedBy (for backward compatibility)
        return mergeInternal(request, null);
    }

    @Override
    @Transactional
    public PersonDto merge(PersonMergeRequest request, UUID requestedBy) {
        return mergeInternal(request, requestedBy);
    }

    private PersonDto mergeInternal(PersonMergeRequest request, UUID requestedBy) {
        UUID primaryId = request.getPrimaryPersonId();
        UUID secondaryId = request.getSecondaryPersonId();

        log.info("Merging person {} into person {}", secondaryId, primaryId);

        // Validate not merging with self
        if (primaryId.equals(secondaryId)) {
            throw new IllegalArgumentException("Cannot merge a person with itself");
        }

        // Find both persons
        Person primary = personRepository.findById(primaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Primary person not found with id: " + primaryId));

        Person secondary = personRepository.findById(secondaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Secondary person not found with id: " + secondaryId));

        // Authorization check: user must own at least one of the persons being merged
        if (requestedBy != null) {
            boolean ownsPrimary = requestedBy.equals(primary.getCreatedBy());
            boolean ownsSecondary = requestedBy.equals(secondary.getCreatedBy());
            if (!ownsPrimary && !ownsSecondary) {
                throw new org.springframework.security.access.AccessDeniedException(
                        "Not authorized to merge these persons");
            }
        }

        // Transfer relationships from secondary to primary
        transferRelationships(primary, secondary);

        // Transfer photos from secondary to primary
        transferPhotos(primary, secondary);

        // Merge biographies if requested
        if (Boolean.TRUE.equals(request.getMergeBiography())) {
            mergeBiographies(primary, secondary);
        }

        // Save primary with all changes
        Person saved = personRepository.save(primary);

        // Delete secondary person
        personRepository.delete(secondary);

        log.info("Person {} merged into person {} successfully", secondaryId, primaryId);

        return toDto(saved);
    }

    private void transferRelationships(Person primary, Person secondary) {
        List<Relationship> secondaryRelationships = relationshipRepository.findAllByPersonId(secondary.getId());

        for (Relationship rel : secondaryRelationships) {
            UUID targetPersonId;
            boolean isFromSecondary = rel.getPersonFrom().getId().equals(secondary.getId());

            if (isFromSecondary) {
                targetPersonId = rel.getPersonTo().getId();
            } else {
                targetPersonId = rel.getPersonFrom().getId();
            }

            // Skip if this would create a self-relationship
            if (targetPersonId.equals(primary.getId())) {
                relationshipRepository.delete(rel);
                continue;
            }

            // Check for duplicate relationship
            boolean isDuplicate;
            if (isFromSecondary) {
                isDuplicate = relationshipRepository.existsByPersonFromIdAndPersonToIdAndRelationshipType(
                        primary.getId(), targetPersonId, rel.getRelationshipType());
            } else {
                isDuplicate = relationshipRepository.existsByPersonFromIdAndPersonToIdAndRelationshipType(
                        targetPersonId, primary.getId(), rel.getRelationshipType());
            }

            if (isDuplicate) {
                relationshipRepository.delete(rel);
            } else {
                // Transfer the relationship
                if (isFromSecondary) {
                    rel.setPersonFrom(primary);
                } else {
                    rel.setPersonTo(primary);
                }
                relationshipRepository.save(rel);
            }
        }
    }

    private void transferPhotos(Person primary, Person secondary) {
        if (secondary.getPhotos() == null || secondary.getPhotos().isEmpty()) {
            return;
        }

        Set<PersonPhoto> photosToTransfer = new HashSet<>(secondary.getPhotos());

        for (PersonPhoto personPhoto : photosToTransfer) {
            // Create new PersonPhoto for primary
            PersonPhoto newPersonPhoto = PersonPhoto.builder()
                    .id(new PersonPhoto.PersonPhotoId(primary.getId(), personPhoto.getPhoto().getId()))
                    .person(primary)
                    .photo(personPhoto.getPhoto())
                    .isPrimary(false) // Don't override primary photo
                    .build();

            primary.getPhotos().add(newPersonPhoto);
        }

        // Clear secondary's photos to avoid orphan removal issues
        secondary.getPhotos().clear();
    }

    private void mergeBiographies(Person primary, Person secondary) {
        if (secondary.getBiography() == null || secondary.getBiography().isBlank()) {
            return;
        }

        if (primary.getBiography() == null || primary.getBiography().isBlank()) {
            primary.setBiography(secondary.getBiography());
        } else {
            primary.setBiography(primary.getBiography() + "\n\n" + secondary.getBiography());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonDto> findDescendants(UUID ancestorId, Pageable pageable) {
        log.info("Finding descendants of person: {}", ancestorId);

        // Verify ancestor exists
        if (!personRepository.existsById(ancestorId)) {
            throw new ResourceNotFoundException("Person not found with id: " + ancestorId);
        }

        // Find all descendants using efficient recursive CTE query (single DB call)
        List<UUID> descendantIds = relationshipRepository.findAllDescendantIds(ancestorId);

        if (descendantIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Fetch all descendants and convert to page
        List<Person> descendants = personRepository.findAllById(descendantIds);
        List<PersonDto> dtos = descendants.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // Manual pagination (will be fixed in separate issue)
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());

        if (start >= dtos.size()) {
            return Page.empty(pageable);
        }

        List<PersonDto> pageContent = dtos.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonDto> findAncestors(UUID descendantId, Pageable pageable) {
        log.info("Finding ancestors of person: {}", descendantId);

        // Verify descendant exists
        if (!personRepository.existsById(descendantId)) {
            throw new ResourceNotFoundException("Person not found with id: " + descendantId);
        }

        // Find all ancestors using efficient recursive CTE query (single DB call)
        List<UUID> ancestorIds = relationshipRepository.findAllAncestorIds(descendantId);

        if (ancestorIds.isEmpty()) {
            return Page.empty(pageable);
        }

        // Fetch all ancestors and convert to page
        List<Person> ancestors = personRepository.findAllById(ancestorIds);
        List<PersonDto> dtos = ancestors.stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        // Manual pagination (will be fixed in separate issue)
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtos.size());

        if (start >= dtos.size()) {
            return Page.empty(pageable);
        }

        List<PersonDto> pageContent = dtos.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtos.size());
    }

    private PersonDto toDto(Person person) {
        String primaryPhotoUrl = null;
        Photo primaryPhoto = photoRepository.findPrimaryPhotoByPersonId(person.getId());
        if (primaryPhoto != null) {
            // Prefer thumbnail for profile display, fall back to original
            String photoPath = primaryPhoto.getThumbnailMedium() != null
                    ? primaryPhoto.getThumbnailMedium()
                    : primaryPhoto.getOriginalPath();
            primaryPhotoUrl = storageService.getUrl(photoPath);
        }

        return PersonDto.builder()
                .id(person.getId())
                .fullName(person.getFullName())
                .birthDate(toDto(person.getBirthDate()))
                .deathDate(person.getDeathDate() != null ? toDto(person.getDeathDate()) : null)
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

    private RelativeDto toRelativeDto(Person person, String relationshipType) {
        String primaryPhotoUrl = null;
        Photo primaryPhoto = photoRepository.findPrimaryPhotoByPersonId(person.getId());
        if (primaryPhoto != null) {
            String photoPath = primaryPhoto.getThumbnailMedium() != null
                    ? primaryPhoto.getThumbnailMedium()
                    : primaryPhoto.getOriginalPath();
            primaryPhotoUrl = storageService.getUrl(photoPath);
        }

        return RelativeDto.builder()
                .id(person.getId())
                .fullName(person.getFullName())
                .birthDate(toDto(person.getBirthDate()))
                .deathDate(person.getDeathDate() != null ? toDto(person.getDeathDate()) : null)
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
                .relationshipType(relationshipType)
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
