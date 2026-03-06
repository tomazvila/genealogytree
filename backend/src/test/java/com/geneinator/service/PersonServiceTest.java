package com.geneinator.service;

import com.geneinator.dto.common.ApproximateDateDto;
import com.geneinator.dto.person.PersonCreateRequest;
import com.geneinator.dto.person.PersonDto;
import com.geneinator.dto.person.PersonMergeRequest;
import com.geneinator.dto.person.RelativeDto;
import com.geneinator.entity.ApproximateDate;
import com.geneinator.entity.Person;
import com.geneinator.entity.PersonPhoto;
import com.geneinator.entity.Photo;
import com.geneinator.entity.Relationship;
import com.geneinator.entity.Relationship.RelationshipType;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.repository.PersonRepository;
import com.geneinator.repository.PhotoRepository;
import com.geneinator.repository.RelationshipRepository;
import com.geneinator.service.impl.PersonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PersonService")
class PersonServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private RelationshipRepository relationshipRepository;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private StorageService storageService;

    private PersonService personService;

    @BeforeEach
    void setUp() {
        personService = new PersonServiceImpl(personRepository, relationshipRepository, photoRepository, storageService);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create person with required fields")
        void shouldCreatePersonWithRequiredFields() {
            // Given
            UUID createdBy = UUID.randomUUID();
            PersonCreateRequest request = PersonCreateRequest.builder()
                    .fullName("Jonas Mažvila")
                    .birthDate(ApproximateDateDto.builder()
                            .year(1925)
                            .month(5)
                            .day(15)
                            .build())
                    .build();

            when(personRepository.save(any(Person.class))).thenAnswer(invocation -> {
                Person person = invocation.getArgument(0);
                return person;
            });

            // When
            PersonDto result = personService.create(request, createdBy);

            // Then
            assertThat(result.getFullName()).isEqualTo("Jonas Mažvila");
            assertThat(result.getBirthDate().getYear()).isEqualTo(1925);
            assertThat(result.getBirthDate().getMonth()).isEqualTo(5);
            assertThat(result.getBirthDate().getDay()).isEqualTo(15);
            verify(personRepository).save(any(Person.class));
        }

        @Test
        @DisplayName("should handle approximate dates")
        void shouldHandleApproximateDates() {
            // Given
            UUID createdBy = UUID.randomUUID();
            PersonCreateRequest request = PersonCreateRequest.builder()
                    .fullName("Unknown Ancestor")
                    .birthDate(ApproximateDateDto.builder()
                            .year(1850)
                            .isApproximate(true)
                            .dateText("circa 1850")
                            .build())
                    .build();

            when(personRepository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            PersonDto result = personService.create(request, createdBy);

            // Then
            assertThat(result.getBirthDate().getIsApproximate()).isTrue();
            assertThat(result.getBirthDate().getDateText()).isEqualTo("circa 1850");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return person when found")
        void shouldReturnPersonWhenFound() {
            // Given
            UUID personId = UUID.randomUUID();
            Person person = Person.builder()
                    .fullName("Test Person")
                    .birthDate(ApproximateDate.fromYear(1990))
                    .createdBy(UUID.randomUUID())
                    .build();

            when(personRepository.findById(personId)).thenReturn(Optional.of(person));

            // When
            PersonDto result = personService.findById(personId);

            // Then
            assertThat(result.getFullName()).isEqualTo("Test Person");
        }

        @Test
        @DisplayName("should throw exception when not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            UUID personId = UUID.randomUUID();
            when(personRepository.findById(personId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> personService.findById(personId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Person not found");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should hard delete person")
        void shouldHardDeletePerson() {
            // Given
            UUID personId = UUID.randomUUID();
            Person person = Person.builder()
                    .fullName("To Delete")
                    .birthDate(ApproximateDate.fromYear(1990))
                    .createdBy(UUID.randomUUID())
                    .build();

            when(personRepository.findById(personId)).thenReturn(Optional.of(person));

            // When
            personService.delete(personId);

            // Then
            verify(personRepository).delete(person);
        }

        @Test
        @DisplayName("should throw exception when deleting non-existent person")
        void shouldThrowExceptionWhenDeletingNonExistent() {
            // Given
            UUID personId = UUID.randomUUID();
            when(personRepository.findById(personId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> personService.delete(personId))
                .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findRelatives")
    class FindRelatives {

        @Test
        @DisplayName("should return relatives with relationship type")
        void shouldReturnRelativesWithRelationshipType() {
            // Given
            UUID personId = UUID.randomUUID();
            UUID parentId = UUID.randomUUID();
            UUID childId = UUID.randomUUID();
            UUID spouseId = UUID.randomUUID();

            Person person = Person.builder()
                    .fullName("Test Person")
                    .birthDate(ApproximateDate.fromYear(1980))
                    .createdBy(UUID.randomUUID())
                    .relationshipsFrom(new HashSet<>())
                    .relationshipsTo(new HashSet<>())
                    .build();
            setPersonId(person, personId);

            Person parent = Person.builder()
                    .fullName("Parent Person")
                    .birthDate(ApproximateDate.fromYear(1950))
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(parent, parentId);

            Person child = Person.builder()
                    .fullName("Child Person")
                    .birthDate(ApproximateDate.fromYear(2010))
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(child, childId);

            Person spouse = Person.builder()
                    .fullName("Spouse Person")
                    .birthDate(ApproximateDate.fromYear(1982))
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(spouse, spouseId);

            // Create relationships using correct convention:
            // PARENT type: personFrom IS THE PARENT OF personTo
            // So for parent->person(PARENT): parent is person's parent
            // And for person->child(PARENT): person is child's parent
            Relationship parentRel = Relationship.builder()
                    .personFrom(parent)
                    .personTo(person)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            Relationship childRel = Relationship.builder()
                    .personFrom(person)
                    .personTo(child)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            Relationship spouseRel = Relationship.builder()
                    .personFrom(person)
                    .personTo(spouse)
                    .relationshipType(RelationshipType.SPOUSE)
                    .build();

            person.getRelationshipsTo().add(parentRel);
            person.getRelationshipsFrom().add(childRel);
            person.getRelationshipsFrom().add(spouseRel);

            when(personRepository.findByIdWithRelationships(personId)).thenReturn(person);

            // When
            List<RelativeDto> relatives = personService.findRelatives(personId);

            // Then
            assertThat(relatives).hasSize(3);

            // Find each relative and verify relationship type
            RelativeDto parentRelative = relatives.stream()
                    .filter(r -> r.getId().equals(parentId))
                    .findFirst()
                    .orElseThrow();
            assertThat(parentRelative.getRelationshipType()).isEqualTo("PARENT");
            assertThat(parentRelative.getFullName()).isEqualTo("Parent Person");

            RelativeDto childRelative = relatives.stream()
                    .filter(r -> r.getId().equals(childId))
                    .findFirst()
                    .orElseThrow();
            assertThat(childRelative.getRelationshipType()).isEqualTo("CHILD");
            assertThat(childRelative.getFullName()).isEqualTo("Child Person");

            RelativeDto spouseRelative = relatives.stream()
                    .filter(r -> r.getId().equals(spouseId))
                    .findFirst()
                    .orElseThrow();
            assertThat(spouseRelative.getRelationshipType()).isEqualTo("SPOUSE");
            assertThat(spouseRelative.getFullName()).isEqualTo("Spouse Person");
        }

        @Test
        @DisplayName("should include reverse relationships with correct type")
        void shouldIncludeReverseRelationshipsWithCorrectType() {
            // Given
            UUID personId = UUID.randomUUID();
            UUID parentFromReverseId = UUID.randomUUID();

            Person person = Person.builder()
                    .fullName("Test Person")
                    .birthDate(ApproximateDate.fromYear(1980))
                    .createdBy(UUID.randomUUID())
                    .relationshipsFrom(new HashSet<>())
                    .relationshipsTo(new HashSet<>())
                    .build();
            setPersonId(person, personId);

            Person parentFromReverse = Person.builder()
                    .fullName("Parent From Reverse")
                    .birthDate(ApproximateDate.fromYear(1950))
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(parentFromReverse, parentFromReverseId);

            // parentFromReverse -> person with type PARENT
            // This means: parentFromReverse IS THE PARENT OF person
            // So from person's perspective, parentFromReverse should show as PARENT
            Relationship reverseRel = Relationship.builder()
                    .personFrom(parentFromReverse)
                    .personTo(person)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            person.getRelationshipsTo().add(reverseRel);

            when(personRepository.findByIdWithRelationships(personId)).thenReturn(person);

            // When
            List<RelativeDto> relatives = personService.findRelatives(personId);

            // Then
            assertThat(relatives).hasSize(1);

            RelativeDto parentRelative = relatives.get(0);
            assertThat(parentRelative.getId()).isEqualTo(parentFromReverseId);
            // parentFromReverse has a PARENT relationship to person,
            // meaning parentFromReverse IS person's PARENT
            assertThat(parentRelative.getRelationshipType()).isEqualTo("PARENT");
        }

        @Test
        @DisplayName("should throw exception when person not found")
        void shouldThrowExceptionWhenPersonNotFound() {
            // Given
            UUID personId = UUID.randomUUID();
            when(personRepository.findByIdWithRelationships(personId)).thenReturn(null);

            // When/Then
            assertThatThrownBy(() -> personService.findRelatives(personId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Person not found");
        }

        private void setPersonId(Person person, UUID id) {
            try {
                var field = person.getClass().getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(person, id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("merge")
    class Merge {

        @Test
        @DisplayName("should transfer relationships from secondary to primary")
        void shouldTransferRelationships() {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();
            UUID thirdPersonId = UUID.randomUUID();

            Person primary = Person.builder()
                    .fullName("Primary Person")
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(primary, primaryId);

            Person secondary = Person.builder()
                    .fullName("Secondary Person")
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(secondary, secondaryId);

            Person thirdPerson = Person.builder()
                    .fullName("Third Person")
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(thirdPerson, thirdPersonId);

            // Secondary has a child relationship
            Relationship rel = Relationship.builder()
                    .personFrom(secondary)
                    .personTo(thirdPerson)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            when(personRepository.findById(primaryId)).thenReturn(Optional.of(primary));
            when(personRepository.findById(secondaryId)).thenReturn(Optional.of(secondary));
            when(relationshipRepository.findAllByPersonId(secondaryId)).thenReturn(List.of(rel));
            when(relationshipRepository.existsByPersonFromIdAndPersonToIdAndRelationshipType(
                    primaryId, thirdPersonId, RelationshipType.PARENT)).thenReturn(false);
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .build();

            // When
            PersonDto result = personService.merge(request);

            // Then
            verify(relationshipRepository).save(argThat(r ->
                    r.getPersonFrom().getId().equals(primaryId) &&
                    r.getPersonTo().getId().equals(thirdPersonId)
            ));
            verify(personRepository).delete(secondary);
            assertThat(result.getId()).isEqualTo(primaryId);
        }

        @Test
        @DisplayName("should transfer photos from secondary to primary")
        void shouldTransferPhotos() {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();
            UUID photoId = UUID.randomUUID();

            Person primary = Person.builder()
                    .fullName("Primary Person")
                    .createdBy(UUID.randomUUID())
                    .photos(new HashSet<>())
                    .build();
            setPersonId(primary, primaryId);

            Photo photo = Photo.builder()
                    .originalPath("originals/test.jpg")
                    .processingStatus(Photo.ProcessingStatus.COMPLETED)
                    .uploadedBy(UUID.randomUUID())
                    .build();
            setPhotoId(photo, photoId);

            PersonPhoto personPhoto = PersonPhoto.builder()
                    .id(new PersonPhoto.PersonPhotoId(secondaryId, photoId))
                    .photo(photo)
                    .isPrimary(true)
                    .build();

            Set<PersonPhoto> secondaryPhotos = new HashSet<>();
            secondaryPhotos.add(personPhoto);

            Person secondary = Person.builder()
                    .fullName("Secondary Person")
                    .createdBy(UUID.randomUUID())
                    .photos(secondaryPhotos)
                    .build();
            setPersonId(secondary, secondaryId);
            personPhoto.setPerson(secondary);

            when(personRepository.findById(primaryId)).thenReturn(Optional.of(primary));
            when(personRepository.findById(secondaryId)).thenReturn(Optional.of(secondary));
            when(relationshipRepository.findAllByPersonId(secondaryId)).thenReturn(List.of());
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .build();

            // When
            PersonDto result = personService.merge(request);

            // Then
            ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
            verify(personRepository, atLeastOnce()).save(personCaptor.capture());

            Person savedPrimary = personCaptor.getAllValues().stream()
                    .filter(p -> p.getId().equals(primaryId))
                    .findFirst()
                    .orElseThrow();
            assertThat(savedPrimary.getPhotos()).hasSize(1);
        }

        @Test
        @DisplayName("should merge biographies when both exist")
        void shouldMergeBiographies() {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();

            Person primary = Person.builder()
                    .fullName("Primary Person")
                    .biography("Primary bio.")
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(primary, primaryId);

            Person secondary = Person.builder()
                    .fullName("Secondary Person")
                    .biography("Secondary bio.")
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(secondary, secondaryId);

            when(personRepository.findById(primaryId)).thenReturn(Optional.of(primary));
            when(personRepository.findById(secondaryId)).thenReturn(Optional.of(secondary));
            when(relationshipRepository.findAllByPersonId(secondaryId)).thenReturn(List.of());
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .mergeBiography(true)
                    .build();

            // When
            PersonDto result = personService.merge(request);

            // Then
            ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);
            verify(personRepository, atLeastOnce()).save(personCaptor.capture());

            Person savedPrimary = personCaptor.getAllValues().stream()
                    .filter(p -> p.getId().equals(primaryId))
                    .findFirst()
                    .orElseThrow();
            assertThat(savedPrimary.getBiography()).contains("Primary bio.");
            assertThat(savedPrimary.getBiography()).contains("Secondary bio.");
        }

        @Test
        @DisplayName("should throw exception when primary person not found")
        void shouldThrowExceptionWhenPrimaryNotFound() {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();

            when(personRepository.findById(primaryId)).thenReturn(Optional.empty());

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .build();

            // When/Then
            assertThatThrownBy(() -> personService.merge(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Primary person not found");
        }

        @Test
        @DisplayName("should throw exception when secondary person not found")
        void shouldThrowExceptionWhenSecondaryNotFound() {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();

            Person primary = Person.builder()
                    .fullName("Primary Person")
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(primary, primaryId);

            when(personRepository.findById(primaryId)).thenReturn(Optional.of(primary));
            when(personRepository.findById(secondaryId)).thenReturn(Optional.empty());

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .build();

            // When/Then
            assertThatThrownBy(() -> personService.merge(request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Secondary person not found");
        }

        @Test
        @DisplayName("should throw exception when trying to merge person with itself")
        void shouldThrowExceptionWhenMergingSameId() {
            // Given
            UUID personId = UUID.randomUUID();

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(personId)
                    .secondaryPersonId(personId)
                    .build();

            // When/Then
            assertThatThrownBy(() -> personService.merge(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Cannot merge a person with itself");
        }

        @Test
        @DisplayName("should throw AccessDeniedException when user doesn't own either person")
        void shouldThrowAccessDeniedWhenUserDoesntOwnEitherPerson() {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();
            UUID ownerId = UUID.randomUUID();
            UUID requestingUserId = UUID.randomUUID(); // Different user

            Person primary = Person.builder()
                    .fullName("Primary Person")
                    .createdBy(ownerId)
                    .build();
            setPersonId(primary, primaryId);

            Person secondary = Person.builder()
                    .fullName("Secondary Person")
                    .createdBy(ownerId)
                    .build();
            setPersonId(secondary, secondaryId);

            when(personRepository.findById(primaryId)).thenReturn(Optional.of(primary));
            when(personRepository.findById(secondaryId)).thenReturn(Optional.of(secondary));

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .build();

            // When/Then
            assertThatThrownBy(() -> personService.merge(request, requestingUserId))
                    .isInstanceOf(org.springframework.security.access.AccessDeniedException.class)
                    .hasMessageContaining("Not authorized to merge these persons");
        }

        @Test
        @DisplayName("should allow merge when user owns primary person")
        void shouldAllowMergeWhenUserOwnsPrimary() {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();
            UUID requestingUserId = UUID.randomUUID();

            Person primary = Person.builder()
                    .fullName("Primary Person")
                    .createdBy(requestingUserId) // User owns primary
                    .build();
            setPersonId(primary, primaryId);

            Person secondary = Person.builder()
                    .fullName("Secondary Person")
                    .createdBy(UUID.randomUUID()) // Different owner
                    .build();
            setPersonId(secondary, secondaryId);

            when(personRepository.findById(primaryId)).thenReturn(Optional.of(primary));
            when(personRepository.findById(secondaryId)).thenReturn(Optional.of(secondary));
            when(relationshipRepository.findAllByPersonId(secondaryId)).thenReturn(List.of());
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .build();

            // When
            PersonDto result = personService.merge(request, requestingUserId);

            // Then
            assertThat(result.getId()).isEqualTo(primaryId);
            verify(personRepository).delete(secondary);
        }

        @Test
        @DisplayName("should allow merge when user owns secondary person")
        void shouldAllowMergeWhenUserOwnsSecondary() {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();
            UUID requestingUserId = UUID.randomUUID();

            Person primary = Person.builder()
                    .fullName("Primary Person")
                    .createdBy(UUID.randomUUID()) // Different owner
                    .build();
            setPersonId(primary, primaryId);

            Person secondary = Person.builder()
                    .fullName("Secondary Person")
                    .createdBy(requestingUserId) // User owns secondary
                    .build();
            setPersonId(secondary, secondaryId);

            when(personRepository.findById(primaryId)).thenReturn(Optional.of(primary));
            when(personRepository.findById(secondaryId)).thenReturn(Optional.of(secondary));
            when(relationshipRepository.findAllByPersonId(secondaryId)).thenReturn(List.of());
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .build();

            // When
            PersonDto result = personService.merge(request, requestingUserId);

            // Then
            assertThat(result.getId()).isEqualTo(primaryId);
            verify(personRepository).delete(secondary);
        }

        @Test
        @DisplayName("should skip duplicate relationships during merge")
        void shouldSkipDuplicateRelationships() {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();
            UUID thirdPersonId = UUID.randomUUID();

            Person primary = Person.builder()
                    .fullName("Primary Person")
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(primary, primaryId);

            Person secondary = Person.builder()
                    .fullName("Secondary Person")
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(secondary, secondaryId);

            Person thirdPerson = Person.builder()
                    .fullName("Third Person")
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(thirdPerson, thirdPersonId);

            // Secondary has relationship that primary already has
            Relationship rel = Relationship.builder()
                    .personFrom(secondary)
                    .personTo(thirdPerson)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            when(personRepository.findById(primaryId)).thenReturn(Optional.of(primary));
            when(personRepository.findById(secondaryId)).thenReturn(Optional.of(secondary));
            when(relationshipRepository.findAllByPersonId(secondaryId)).thenReturn(List.of(rel));
            // Primary already has this relationship
            when(relationshipRepository.existsByPersonFromIdAndPersonToIdAndRelationshipType(
                    primaryId, thirdPersonId, RelationshipType.PARENT)).thenReturn(true);
            when(personRepository.save(any(Person.class))).thenAnswer(inv -> inv.getArgument(0));

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .build();

            // When
            personService.merge(request);

            // Then - should delete duplicate relationship, not transfer it
            verify(relationshipRepository).delete(rel);
            verify(relationshipRepository, never()).save(any(Relationship.class));
        }

        // Helper methods to set IDs via reflection (since IDs are normally auto-generated)
        private void setPersonId(Person person, UUID id) {
            try {
                var field = person.getClass().getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(person, id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private void setPhotoId(Photo photo, UUID id) {
            try {
                var field = photo.getClass().getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(photo, id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    @DisplayName("findRelatives - Siblings and Cousins")
    class FindRelativesSiblingsAndCousins {

        @Test
        @DisplayName("should infer siblings from shared parents")
        void shouldInferSiblingsFromSharedParents() {
            // Given: Person A and Person B both have Parent P as their parent
            UUID personId = UUID.randomUUID();
            UUID siblingId = UUID.randomUUID();
            UUID parentId = UUID.randomUUID();

            Person person = Person.builder()
                    .fullName("Person A")
                    .birthDate(ApproximateDate.fromYear(1990))
                    .createdBy(UUID.randomUUID())
                    .relationshipsFrom(new HashSet<>())
                    .relationshipsTo(new HashSet<>())
                    .build();
            setPersonId(person, personId);

            Person sibling = Person.builder()
                    .fullName("Person B (Sibling)")
                    .birthDate(ApproximateDate.fromYear(1992))
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(sibling, siblingId);

            Person parent = Person.builder()
                    .fullName("Parent P")
                    .birthDate(ApproximateDate.fromYear(1960))
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(parent, parentId);

            // Person has a PARENT relationship to parent
            Relationship parentRel = Relationship.builder()
                    .personFrom(person)
                    .personTo(parent)
                    .relationshipType(RelationshipType.PARENT)
                    .build();
            person.getRelationshipsFrom().add(parentRel);

            when(personRepository.findByIdWithRelationships(personId)).thenReturn(person);
            // Return sibling ID when querying for siblings
            when(relationshipRepository.findSiblingIds(personId)).thenReturn(List.of(siblingId));
            when(relationshipRepository.findCousinIds(personId)).thenReturn(List.of());
            when(personRepository.findAllById(List.of(siblingId))).thenReturn(List.of(sibling));

            // When
            List<RelativeDto> relatives = personService.findRelatives(personId);

            // Then
            assertThat(relatives).hasSize(2); // parent + sibling

            RelativeDto siblingRelative = relatives.stream()
                    .filter(r -> r.getId().equals(siblingId))
                    .findFirst()
                    .orElseThrow();
            assertThat(siblingRelative.getRelationshipType()).isEqualTo("SIBLING");
            assertThat(siblingRelative.getFullName()).isEqualTo("Person B (Sibling)");
        }

        @Test
        @DisplayName("should detect half-siblings (shared single parent)")
        void shouldDetectHalfSiblings() {
            // Given: Person A and Person B share one parent (half-siblings)
            UUID personId = UUID.randomUUID();
            UUID halfSiblingId = UUID.randomUUID();

            Person person = Person.builder()
                    .fullName("Person A")
                    .birthDate(ApproximateDate.fromYear(1990))
                    .createdBy(UUID.randomUUID())
                    .relationshipsFrom(new HashSet<>())
                    .relationshipsTo(new HashSet<>())
                    .build();
            setPersonId(person, personId);

            Person halfSibling = Person.builder()
                    .fullName("Half-Sibling")
                    .birthDate(ApproximateDate.fromYear(1995))
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(halfSibling, halfSiblingId);

            when(personRepository.findByIdWithRelationships(personId)).thenReturn(person);
            when(relationshipRepository.findSiblingIds(personId)).thenReturn(List.of(halfSiblingId));
            when(relationshipRepository.findCousinIds(personId)).thenReturn(List.of());
            when(personRepository.findAllById(List.of(halfSiblingId))).thenReturn(List.of(halfSibling));

            // When
            List<RelativeDto> relatives = personService.findRelatives(personId);

            // Then
            RelativeDto halfSiblingRelative = relatives.stream()
                    .filter(r -> r.getId().equals(halfSiblingId))
                    .findFirst()
                    .orElseThrow();
            assertThat(halfSiblingRelative.getRelationshipType()).isEqualTo("SIBLING");
        }

        @Test
        @DisplayName("should not include self as sibling")
        void shouldNotIncludeSelfAsSibling() {
            // Given: Person should not appear in their own sibling list
            UUID personId = UUID.randomUUID();

            Person person = Person.builder()
                    .fullName("Person A")
                    .birthDate(ApproximateDate.fromYear(1990))
                    .createdBy(UUID.randomUUID())
                    .relationshipsFrom(new HashSet<>())
                    .relationshipsTo(new HashSet<>())
                    .build();
            setPersonId(person, personId);

            when(personRepository.findByIdWithRelationships(personId)).thenReturn(person);
            // Repository should never return self, but verify service handles it
            when(relationshipRepository.findSiblingIds(personId)).thenReturn(List.of());
            when(relationshipRepository.findCousinIds(personId)).thenReturn(List.of());

            // When
            List<RelativeDto> relatives = personService.findRelatives(personId);

            // Then
            assertThat(relatives).noneMatch(r -> r.getId().equals(personId));
        }

        @Test
        @DisplayName("should infer cousins (parent's sibling's children)")
        void shouldInferCousins() {
            // Given: Person's parent has a sibling, and that sibling has children (cousins)
            UUID personId = UUID.randomUUID();
            UUID cousinId = UUID.randomUUID();
            UUID parentId = UUID.randomUUID();

            Person person = Person.builder()
                    .fullName("Person A")
                    .birthDate(ApproximateDate.fromYear(1990))
                    .createdBy(UUID.randomUUID())
                    .relationshipsFrom(new HashSet<>())
                    .relationshipsTo(new HashSet<>())
                    .build();
            setPersonId(person, personId);

            Person cousin = Person.builder()
                    .fullName("Cousin C")
                    .birthDate(ApproximateDate.fromYear(1991))
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(cousin, cousinId);

            Person parent = Person.builder()
                    .fullName("Parent P")
                    .birthDate(ApproximateDate.fromYear(1960))
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(parent, parentId);

            // Person has a PARENT relationship
            Relationship parentRel = Relationship.builder()
                    .personFrom(person)
                    .personTo(parent)
                    .relationshipType(RelationshipType.PARENT)
                    .build();
            person.getRelationshipsFrom().add(parentRel);

            when(personRepository.findByIdWithRelationships(personId)).thenReturn(person);
            when(relationshipRepository.findSiblingIds(personId)).thenReturn(List.of());
            when(relationshipRepository.findCousinIds(personId)).thenReturn(List.of(cousinId));
            when(personRepository.findAllById(List.of(cousinId))).thenReturn(List.of(cousin));

            // When
            List<RelativeDto> relatives = personService.findRelatives(personId);

            // Then
            assertThat(relatives).hasSize(2); // parent + cousin

            RelativeDto cousinRelative = relatives.stream()
                    .filter(r -> r.getId().equals(cousinId))
                    .findFirst()
                    .orElseThrow();
            assertThat(cousinRelative.getRelationshipType()).isEqualTo("COUSIN");
            assertThat(cousinRelative.getFullName()).isEqualTo("Cousin C");
        }

        @Test
        @DisplayName("should not duplicate relatives that appear in multiple relationship types")
        void shouldNotDuplicateRelatives() {
            // Given: A person could theoretically be returned in both explicit and inferred queries
            UUID personId = UUID.randomUUID();
            UUID childId = UUID.randomUUID();

            Person person = Person.builder()
                    .fullName("Person A")
                    .birthDate(ApproximateDate.fromYear(1970))
                    .createdBy(UUID.randomUUID())
                    .relationshipsFrom(new HashSet<>())
                    .relationshipsTo(new HashSet<>())
                    .build();
            setPersonId(person, personId);

            Person child = Person.builder()
                    .fullName("Child")
                    .birthDate(ApproximateDate.fromYear(2000))
                    .createdBy(UUID.randomUUID())
                    .build();
            setPersonId(child, childId);

            // person -> child with PARENT type (person IS THE PARENT of child)
            // From person's perspective, child should show as CHILD
            Relationship childRel = Relationship.builder()
                    .personFrom(person)
                    .personTo(child)
                    .relationshipType(RelationshipType.PARENT)
                    .build();
            person.getRelationshipsFrom().add(childRel);

            when(personRepository.findByIdWithRelationships(personId)).thenReturn(person);
            // Sibling query also returns this person (edge case)
            when(relationshipRepository.findSiblingIds(personId)).thenReturn(List.of(childId));
            when(relationshipRepository.findCousinIds(personId)).thenReturn(List.of());
            // Note: findAllById won't be called because the relative is already in addedRelativeIds

            // When
            List<RelativeDto> relatives = personService.findRelatives(personId);

            // Then: Should only appear once with the explicit relationship type (CHILD)
            long countOfRelative = relatives.stream()
                    .filter(r -> r.getId().equals(childId))
                    .count();
            assertThat(countOfRelative).isEqualTo(1);

            // Verify the relationship type is CHILD (from person's perspective, since person is the parent)
            RelativeDto childRelative = relatives.stream()
                    .filter(r -> r.getId().equals(childId))
                    .findFirst()
                    .orElseThrow();
            assertThat(childRelative.getRelationshipType()).isEqualTo("CHILD");
        }

        private void setPersonId(Person person, UUID id) {
            try {
                var field = person.getClass().getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(person, id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
