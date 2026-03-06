package com.geneinator.service;

import com.geneinator.entity.BranchPermission;
import com.geneinator.entity.BranchPermission.PermissionType;
import com.geneinator.entity.Person;
import com.geneinator.entity.Relationship;
import com.geneinator.entity.Relationship.RelationshipType;
import com.geneinator.entity.User;
import com.geneinator.repository.BranchPermissionRepository;
import com.geneinator.repository.PersonRepository;
import com.geneinator.repository.RelationshipRepository;
import com.geneinator.service.impl.VisibilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VisibilityService")
class VisibilityServiceTest {

    @Mock
    private BranchPermissionRepository branchPermissionRepository;

    @Mock
    private RelationshipRepository relationshipRepository;

    @Mock
    private PersonRepository personRepository;

    private VisibilityService visibilityService;

    private static final int DEFAULT_MAX_HOPS = 3;

    @BeforeEach
    void setUp() {
        visibilityService = new VisibilityServiceImpl(
                branchPermissionRepository,
                relationshipRepository,
                personRepository,
                DEFAULT_MAX_HOPS
        );
    }

    @Nested
    @DisplayName("canView")
    class CanView {

        @Test
        @DisplayName("should allow view when user has VIEWER permission on branch")
        void shouldAllowViewWhenUserHasViewerPermission() {
            // Given
            UUID userId = UUID.randomUUID();
            UUID personId = UUID.randomUUID();
            UUID rootPersonId = UUID.randomUUID();

            Person rootPerson = Person.builder().fullName("Root").createdBy(userId).build();
            setId(rootPerson, rootPersonId);

            BranchPermission permission = BranchPermission.builder()
                    .user(User.builder().build())
                    .rootPerson(rootPerson)
                    .permissionType(PermissionType.VIEWER)
                    .grantedBy(userId)
                    .grantedAt(Instant.now())
                    .build();

            when(branchPermissionRepository.findByUserId(userId)).thenReturn(List.of(permission));

            // Person is within max hops from root
            Person targetPerson = Person.builder().fullName("Target").createdBy(userId).build();
            setId(targetPerson, personId);

            Relationship rel = Relationship.builder()
                    .personFrom(rootPerson)
                    .personTo(targetPerson)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            when(relationshipRepository.findAllByPersonId(rootPersonId)).thenReturn(List.of(rel));

            // When
            boolean result = visibilityService.canView(userId, personId);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should deny view when user has no permissions")
        void shouldDenyViewWhenUserHasNoPermissions() {
            // Given
            UUID userId = UUID.randomUUID();
            UUID personId = UUID.randomUUID();

            when(branchPermissionRepository.findByUserId(userId)).thenReturn(List.of());

            // When
            boolean result = visibilityService.canView(userId, personId);

            // Then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("should allow view when user is viewing their root person")
        void shouldAllowViewWhenViewingRootPerson() {
            // Given
            UUID userId = UUID.randomUUID();
            UUID rootPersonId = UUID.randomUUID();

            Person rootPerson = Person.builder().fullName("Root").createdBy(userId).build();
            setId(rootPerson, rootPersonId);

            BranchPermission permission = BranchPermission.builder()
                    .user(User.builder().build())
                    .rootPerson(rootPerson)
                    .permissionType(PermissionType.VIEWER)
                    .grantedBy(userId)
                    .grantedAt(Instant.now())
                    .build();

            when(branchPermissionRepository.findByUserId(userId)).thenReturn(List.of(permission));

            // When
            boolean result = visibilityService.canView(userId, rootPersonId);

            // Then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("canEdit")
    class CanEdit {

        @Test
        @DisplayName("should allow edit when user has EDITOR permission")
        void shouldAllowEditWhenUserHasEditorPermission() {
            // Given
            UUID userId = UUID.randomUUID();
            UUID personId = UUID.randomUUID();
            UUID rootPersonId = UUID.randomUUID();

            Person rootPerson = Person.builder().fullName("Root").createdBy(userId).build();
            setId(rootPerson, rootPersonId);

            Person targetPerson = Person.builder().fullName("Target").createdBy(userId).build();
            setId(targetPerson, personId);

            BranchPermission permission = BranchPermission.builder()
                    .user(User.builder().build())
                    .rootPerson(rootPerson)
                    .permissionType(PermissionType.EDITOR)
                    .grantedBy(userId)
                    .grantedAt(Instant.now())
                    .build();

            when(branchPermissionRepository.findByUserId(userId)).thenReturn(List.of(permission));

            Relationship rel = Relationship.builder()
                    .personFrom(rootPerson)
                    .personTo(targetPerson)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            when(relationshipRepository.findAllByPersonId(rootPersonId)).thenReturn(List.of(rel));

            // When
            boolean result = visibilityService.canEdit(userId, personId);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should allow edit when user has OWNER permission")
        void shouldAllowEditWhenUserHasOwnerPermission() {
            // Given
            UUID userId = UUID.randomUUID();
            UUID rootPersonId = UUID.randomUUID();

            Person rootPerson = Person.builder().fullName("Root").createdBy(userId).build();
            setId(rootPerson, rootPersonId);

            BranchPermission permission = BranchPermission.builder()
                    .user(User.builder().build())
                    .rootPerson(rootPerson)
                    .permissionType(PermissionType.OWNER)
                    .grantedBy(userId)
                    .grantedAt(Instant.now())
                    .build();

            when(branchPermissionRepository.findByUserId(userId)).thenReturn(List.of(permission));

            // When
            boolean result = visibilityService.canEdit(userId, rootPersonId);

            // Then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("should deny edit when user only has VIEWER permission")
        void shouldDenyEditWhenUserOnlyHasViewerPermission() {
            // Given
            UUID userId = UUID.randomUUID();
            UUID personId = UUID.randomUUID();
            UUID rootPersonId = UUID.randomUUID();

            Person rootPerson = Person.builder().fullName("Root").createdBy(userId).build();
            setId(rootPerson, rootPersonId);

            BranchPermission permission = BranchPermission.builder()
                    .user(User.builder().build())
                    .rootPerson(rootPerson)
                    .permissionType(PermissionType.VIEWER)
                    .grantedBy(userId)
                    .grantedAt(Instant.now())
                    .build();

            when(branchPermissionRepository.findByUserId(userId)).thenReturn(List.of(permission));

            // When - VIEWER permission skips relationship check, so no relationship mock needed
            boolean result = visibilityService.canEdit(userId, personId);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("getRelationshipDistance")
    class GetRelationshipDistance {

        @Test
        @DisplayName("should return 0 for same person")
        void shouldReturnZeroForSamePerson() {
            // Given
            UUID personId = UUID.randomUUID();

            // When
            int distance = visibilityService.getRelationshipDistance(personId, personId);

            // Then
            assertThat(distance).isEqualTo(0);
        }

        @Test
        @DisplayName("should return 1 for direct relationship")
        void shouldReturnOneForDirectRelationship() {
            // Given
            UUID person1Id = UUID.randomUUID();
            UUID person2Id = UUID.randomUUID();

            Person person1 = Person.builder().fullName("Person 1").createdBy(UUID.randomUUID()).build();
            Person person2 = Person.builder().fullName("Person 2").createdBy(UUID.randomUUID()).build();
            setId(person1, person1Id);
            setId(person2, person2Id);

            Relationship rel = Relationship.builder()
                    .personFrom(person1)
                    .personTo(person2)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            when(relationshipRepository.findAllByPersonId(person1Id)).thenReturn(List.of(rel));

            // When
            int distance = visibilityService.getRelationshipDistance(person1Id, person2Id);

            // Then
            assertThat(distance).isEqualTo(1);
        }

        @Test
        @DisplayName("should return 2 for grandparent relationship")
        void shouldReturnTwoForGrandparentRelationship() {
            // Given
            UUID grandparentId = UUID.randomUUID();
            UUID parentId = UUID.randomUUID();
            UUID childId = UUID.randomUUID();

            Person grandparent = Person.builder().fullName("Grandparent").createdBy(UUID.randomUUID()).build();
            Person parent = Person.builder().fullName("Parent").createdBy(UUID.randomUUID()).build();
            Person child = Person.builder().fullName("Child").createdBy(UUID.randomUUID()).build();
            setId(grandparent, grandparentId);
            setId(parent, parentId);
            setId(child, childId);

            Relationship rel1 = Relationship.builder()
                    .personFrom(grandparent)
                    .personTo(parent)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            Relationship rel2 = Relationship.builder()
                    .personFrom(parent)
                    .personTo(child)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            when(relationshipRepository.findAllByPersonId(grandparentId)).thenReturn(List.of(rel1));
            when(relationshipRepository.findAllByPersonId(parentId)).thenReturn(List.of(rel1, rel2));

            // When
            int distance = visibilityService.getRelationshipDistance(grandparentId, childId);

            // Then
            assertThat(distance).isEqualTo(2);
        }

        @Test
        @DisplayName("should return -1 when persons are not related")
        void shouldReturnMinusOneWhenNotRelated() {
            // Given
            UUID person1Id = UUID.randomUUID();
            UUID person2Id = UUID.randomUUID();

            when(relationshipRepository.findAllByPersonId(person1Id)).thenReturn(List.of());

            // When
            int distance = visibilityService.getRelationshipDistance(person1Id, person2Id);

            // Then
            assertThat(distance).isEqualTo(-1);
        }
    }

    @Nested
    @DisplayName("getVisiblePersonIds")
    class GetVisiblePersonIds {

        @Test
        @DisplayName("should return root person and connected persons within max hops")
        void shouldReturnRootAndConnectedPersons() {
            // Given
            UUID userId = UUID.randomUUID();
            UUID rootPersonId = UUID.randomUUID();
            UUID childId = UUID.randomUUID();

            Person rootPerson = Person.builder().fullName("Root").createdBy(userId).build();
            Person child = Person.builder().fullName("Child").createdBy(userId).build();
            setId(rootPerson, rootPersonId);
            setId(child, childId);

            BranchPermission permission = BranchPermission.builder()
                    .user(User.builder().build())
                    .rootPerson(rootPerson)
                    .permissionType(PermissionType.VIEWER)
                    .grantedBy(userId)
                    .grantedAt(Instant.now())
                    .build();

            when(branchPermissionRepository.findByUserId(userId)).thenReturn(List.of(permission));

            Relationship rel = Relationship.builder()
                    .personFrom(rootPerson)
                    .personTo(child)
                    .relationshipType(RelationshipType.PARENT)
                    .build();

            when(relationshipRepository.findAllByPersonId(rootPersonId)).thenReturn(List.of(rel));
            when(relationshipRepository.findAllByPersonId(childId)).thenReturn(List.of(rel));

            // When
            Set<UUID> visibleIds = visibilityService.getVisiblePersonIds(userId);

            // Then
            assertThat(visibleIds).contains(rootPersonId, childId);
        }

        @Test
        @DisplayName("should return empty set when user has no permissions")
        void shouldReturnEmptySetWhenNoPermissions() {
            // Given
            UUID userId = UUID.randomUUID();

            when(branchPermissionRepository.findByUserId(userId)).thenReturn(List.of());

            // When
            Set<UUID> visibleIds = visibilityService.getVisiblePersonIds(userId);

            // Then
            assertThat(visibleIds).isEmpty();
        }

        @Test
        @DisplayName("should combine visible persons from multiple branch permissions")
        void shouldCombineVisiblePersonsFromMultipleBranches() {
            // Given
            UUID userId = UUID.randomUUID();
            UUID root1Id = UUID.randomUUID();
            UUID root2Id = UUID.randomUUID();

            Person root1 = Person.builder().fullName("Root 1").createdBy(userId).build();
            Person root2 = Person.builder().fullName("Root 2").createdBy(userId).build();
            setId(root1, root1Id);
            setId(root2, root2Id);

            BranchPermission perm1 = BranchPermission.builder()
                    .user(User.builder().build())
                    .rootPerson(root1)
                    .permissionType(PermissionType.VIEWER)
                    .grantedBy(userId)
                    .grantedAt(Instant.now())
                    .build();

            BranchPermission perm2 = BranchPermission.builder()
                    .user(User.builder().build())
                    .rootPerson(root2)
                    .permissionType(PermissionType.VIEWER)
                    .grantedBy(userId)
                    .grantedAt(Instant.now())
                    .build();

            when(branchPermissionRepository.findByUserId(userId)).thenReturn(List.of(perm1, perm2));
            when(relationshipRepository.findAllByPersonId(root1Id)).thenReturn(List.of());
            when(relationshipRepository.findAllByPersonId(root2Id)).thenReturn(List.of());

            // When
            Set<UUID> visibleIds = visibilityService.getVisiblePersonIds(userId);

            // Then
            assertThat(visibleIds).contains(root1Id, root2Id);
        }
    }

    // Helper to set ID on entity via reflection (since ID is normally set by JPA)
    private void setId(Person person, UUID id) {
        try {
            var field = person.getClass().getSuperclass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(person, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
