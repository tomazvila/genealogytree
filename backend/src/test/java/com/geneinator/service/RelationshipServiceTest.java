package com.geneinator.service;

import com.geneinator.dto.relationship.RelationshipCreateRequest;
import com.geneinator.dto.relationship.RelationshipDto;
import com.geneinator.entity.ApproximateDate;
import com.geneinator.entity.Person;
import com.geneinator.entity.Relationship;
import com.geneinator.entity.Relationship.RelationshipType;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.repository.PersonRepository;
import com.geneinator.repository.RelationshipRepository;
import com.geneinator.service.impl.RelationshipServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RelationshipService")
class RelationshipServiceTest {

    @Mock
    private RelationshipRepository relationshipRepository;

    @Mock
    private PersonRepository personRepository;

    private RelationshipService relationshipService;

    @BeforeEach
    void setUp() {
        relationshipService = new RelationshipServiceImpl(relationshipRepository, personRepository);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create parent-child relationship")
        void shouldCreateParentChildRelationship() {
            // Given
            UUID parentId = UUID.randomUUID();
            UUID childId = UUID.randomUUID();

            Person parent = Person.builder()
                    .fullName("Jonas Mažvila")
                    .birthDate(ApproximateDate.fromYear(1900))
                    .createdBy(UUID.randomUUID())
                    .build();
            Person child = Person.builder()
                    .fullName("Petras Mažvila")
                    .birthDate(ApproximateDate.fromYear(1925))
                    .createdBy(UUID.randomUUID())
                    .build();

            RelationshipCreateRequest request = RelationshipCreateRequest.builder()
                    .personFromId(parentId)
                    .personToId(childId)
                    .relationshipType("PARENT")
                    .build();

            when(personRepository.findById(parentId)).thenReturn(Optional.of(parent));
            when(personRepository.findById(childId)).thenReturn(Optional.of(child));
            when(relationshipRepository.save(any(Relationship.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            RelationshipDto result = relationshipService.create(request);

            // Then
            assertThat(result.getRelationshipType()).isEqualTo("PARENT");
            assertThat(result.getPersonFromName()).isEqualTo("Jonas Mažvila");
            assertThat(result.getPersonToName()).isEqualTo("Petras Mažvila");
            verify(relationshipRepository).save(any(Relationship.class));
        }

        @Test
        @DisplayName("should create spouse relationship")
        void shouldCreateSpouseRelationship() {
            // Given
            UUID person1Id = UUID.randomUUID();
            UUID person2Id = UUID.randomUUID();

            Person person1 = Person.builder()
                    .fullName("Jonas Mažvila")
                    .createdBy(UUID.randomUUID())
                    .build();
            Person person2 = Person.builder()
                    .fullName("Ona Mažvilienė")
                    .createdBy(UUID.randomUUID())
                    .build();

            RelationshipCreateRequest request = RelationshipCreateRequest.builder()
                    .personFromId(person1Id)
                    .personToId(person2Id)
                    .relationshipType("SPOUSE")
                    .build();

            when(personRepository.findById(person1Id)).thenReturn(Optional.of(person1));
            when(personRepository.findById(person2Id)).thenReturn(Optional.of(person2));
            when(relationshipRepository.save(any(Relationship.class))).thenAnswer(inv -> inv.getArgument(0));

            // When
            RelationshipDto result = relationshipService.create(request);

            // Then
            assertThat(result.getRelationshipType()).isEqualTo("SPOUSE");
        }

        @Test
        @DisplayName("should throw exception when person from not found")
        void shouldThrowExceptionWhenPersonFromNotFound() {
            // Given
            UUID personFromId = UUID.randomUUID();
            UUID personToId = UUID.randomUUID();

            RelationshipCreateRequest request = RelationshipCreateRequest.builder()
                    .personFromId(personFromId)
                    .personToId(personToId)
                    .relationshipType("PARENT")
                    .build();

            when(personRepository.findById(personFromId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> relationshipService.create(request))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByPersonId")
    class FindByPersonId {

        @Test
        @DisplayName("should return all relationships for a person")
        void shouldReturnAllRelationshipsForPerson() {
            // Given
            UUID personId = UUID.randomUUID();
            Person person1 = Person.builder().fullName("Person 1").createdBy(UUID.randomUUID()).build();
            Person person2 = Person.builder().fullName("Person 2").createdBy(UUID.randomUUID()).build();

            Relationship rel = Relationship.builder()
                    .personFrom(person1)
                    .personTo(person2)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            when(relationshipRepository.findAllByPersonId(personId)).thenReturn(List.of(rel));

            // When
            List<RelationshipDto> result = relationshipService.findByPersonId(personId);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRelationshipType()).isEqualTo("PARENT");
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete relationship")
        void shouldDeleteRelationship() {
            // Given
            UUID relationshipId = UUID.randomUUID();
            Relationship relationship = Relationship.builder()
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            when(relationshipRepository.findById(relationshipId)).thenReturn(Optional.of(relationship));

            // When
            relationshipService.delete(relationshipId);

            // Then
            verify(relationshipRepository).delete(relationship);
        }

        @Test
        @DisplayName("should throw exception when relationship not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            UUID relationshipId = UUID.randomUUID();
            when(relationshipRepository.findById(relationshipId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> relationshipService.delete(relationshipId))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
