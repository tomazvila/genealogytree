package com.geneinator.service;

import com.geneinator.dto.tree.TreeCreateRequest;
import com.geneinator.dto.tree.TreeDto;
import com.geneinator.dto.tree.TreeStructureDto;
import com.geneinator.entity.ApproximateDate;
import com.geneinator.entity.Person;
import com.geneinator.entity.Photo;
import com.geneinator.entity.Tree;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.repository.PersonRepository;
import com.geneinator.repository.PhotoRepository;
import com.geneinator.repository.RelationshipRepository;
import com.geneinator.repository.TreeRepository;
import com.geneinator.service.impl.TreeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TreeService")
class TreeServiceTest {

    @Mock
    private TreeRepository treeRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private RelationshipRepository relationshipRepository;

    @Mock
    private PhotoRepository photoRepository;

    @Mock
    private StorageService storageService;

    private TreeService treeService;

    @BeforeEach
    void setUp() {
        treeService = new TreeServiceImpl(treeRepository, personRepository, relationshipRepository, photoRepository, storageService);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create tree with name and description")
        void shouldCreateTreeWithNameAndDescription() {
            // Given
            UUID createdBy = UUID.randomUUID();
            TreeCreateRequest request = TreeCreateRequest.builder()
                    .name("Mažvila Family Tree")
                    .description("Genealogy of the Mažvila family")
                    .isMergeable(true)
                    .build();

            when(treeRepository.save(any(Tree.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            TreeDto result = treeService.create(request, createdBy);

            // Then
            assertThat(result.getName()).isEqualTo("Mažvila Family Tree");
            assertThat(result.getDescription()).isEqualTo("Genealogy of the Mažvila family");
            assertThat(result.getIsMergeable()).isTrue();
            verify(treeRepository).save(any(Tree.class));
        }

        @Test
        @DisplayName("should create tree with root person")
        void shouldCreateTreeWithRootPerson() {
            // Given
            UUID createdBy = UUID.randomUUID();
            UUID rootPersonId = UUID.randomUUID();
            Person rootPerson = Person.builder()
                    .fullName("Jonas Mažvila")
                    .createdBy(createdBy)
                    .build();

            TreeCreateRequest request = TreeCreateRequest.builder()
                    .name("Mažvila Family Tree")
                    .rootPersonId(rootPersonId)
                    .build();

            when(personRepository.findById(rootPersonId)).thenReturn(Optional.of(rootPerson));
            when(treeRepository.save(any(Tree.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            TreeDto result = treeService.create(request, createdBy);

            // Then
            assertThat(result.getName()).isEqualTo("Mažvila Family Tree");
            assertThat(result.getRootPersonName()).isEqualTo("Jonas Mažvila");
            verify(personRepository).findById(rootPersonId);
        }

        @Test
        @DisplayName("should throw exception when root person not found")
        void shouldThrowExceptionWhenRootPersonNotFound() {
            // Given
            UUID createdBy = UUID.randomUUID();
            UUID rootPersonId = UUID.randomUUID();

            TreeCreateRequest request = TreeCreateRequest.builder()
                    .name("Mažvila Family Tree")
                    .rootPersonId(rootPersonId)
                    .build();

            when(personRepository.findById(rootPersonId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> treeService.create(request, createdBy))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Person not found");
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("should return tree when found")
        void shouldReturnTreeWhenFound() {
            // Given
            UUID treeId = UUID.randomUUID();
            Tree tree = Tree.builder()
                    .name("Test Tree")
                    .description("Test Description")
                    .createdBy(UUID.randomUUID())
                    .build();

            when(treeRepository.findById(treeId)).thenReturn(Optional.of(tree));

            // When
            TreeDto result = treeService.findById(treeId);

            // Then
            assertThat(result.getName()).isEqualTo("Test Tree");
            assertThat(result.getDescription()).isEqualTo("Test Description");
        }

        @Test
        @DisplayName("should throw exception when tree not found")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            UUID treeId = UUID.randomUUID();
            when(treeRepository.findById(treeId)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> treeService.findById(treeId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Tree not found");
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("should return paginated trees")
        void shouldReturnPaginatedTrees() {
            // Given
            Tree tree1 = Tree.builder().name("Tree 1").createdBy(UUID.randomUUID()).build();
            Tree tree2 = Tree.builder().name("Tree 2").createdBy(UUID.randomUUID()).build();
            Page<Tree> treePage = new PageImpl<>(List.of(tree1, tree2));

            when(treeRepository.findAll(any(PageRequest.class))).thenReturn(treePage);

            // When
            Page<TreeDto> result = treeService.findAll(PageRequest.of(0, 10));

            // Then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).getName()).isEqualTo("Tree 1");
            assertThat(result.getContent().get(1).getName()).isEqualTo("Tree 2");
        }
    }

    @Nested
    @DisplayName("getTreeStructure")
    class GetTreeStructure {

        @Test
        @DisplayName("should include primaryPhotoUrl for persons with photos")
        void shouldIncludePrimaryPhotoUrlForPersonsWithPhotos() {
            // Given
            UUID treeId = UUID.randomUUID();
            UUID viewerId = UUID.randomUUID();
            UUID personId = UUID.randomUUID();

            Tree tree = Tree.builder()
                    .name("Test Tree")
                    .createdBy(viewerId)
                    .build();

            Person person = Person.builder()
                    .fullName("John Doe")
                    .birthDate(ApproximateDate.builder().year(1990).build())
                    .createdBy(viewerId)
                    .build();
            // Set the ID via reflection since builder doesn't set it
            setPersonId(person, personId);

            Photo primaryPhoto = Photo.builder()
                    .originalPath("originals/photo.jpg")
                    .thumbnailMedium("thumbnails/medium/photo.jpg")
                    .uploadedBy(viewerId)
                    .build();

            when(treeRepository.findById(treeId)).thenReturn(Optional.of(tree));
            when(personRepository.findByTreeId(treeId)).thenReturn(List.of(person));
            when(relationshipRepository.findAllByPersonIdsIn(List.of(personId))).thenReturn(Collections.emptyList());
            when(photoRepository.findPrimaryPhotoByPersonId(personId)).thenReturn(primaryPhoto);
            when(storageService.getUrl("thumbnails/medium/photo.jpg")).thenReturn("http://localhost/thumbnails/medium/photo.jpg");

            // When
            TreeStructureDto result = treeService.getTreeStructure(treeId, viewerId);

            // Then
            assertThat(result.getPersons()).hasSize(1);
            assertThat(result.getPersons().get(0).getPrimaryPhotoUrl()).isEqualTo("http://localhost/thumbnails/medium/photo.jpg");
        }

        @Test
        @DisplayName("should return null primaryPhotoUrl for persons without photos")
        void shouldReturnNullPrimaryPhotoUrlForPersonsWithoutPhotos() {
            // Given
            UUID treeId = UUID.randomUUID();
            UUID viewerId = UUID.randomUUID();
            UUID personId = UUID.randomUUID();

            Tree tree = Tree.builder()
                    .name("Test Tree")
                    .createdBy(viewerId)
                    .build();

            Person person = Person.builder()
                    .fullName("Jane Doe")
                    .birthDate(ApproximateDate.builder().year(1985).build())
                    .createdBy(viewerId)
                    .build();
            setPersonId(person, personId);

            when(treeRepository.findById(treeId)).thenReturn(Optional.of(tree));
            when(personRepository.findByTreeId(treeId)).thenReturn(List.of(person));
            when(relationshipRepository.findAllByPersonIdsIn(List.of(personId))).thenReturn(Collections.emptyList());
            when(photoRepository.findPrimaryPhotoByPersonId(personId)).thenReturn(null);

            // When
            TreeStructureDto result = treeService.getTreeStructure(treeId, viewerId);

            // Then
            assertThat(result.getPersons()).hasSize(1);
            assertThat(result.getPersons().get(0).getPrimaryPhotoUrl()).isNull();
        }

        private void setPersonId(Person person, UUID id) {
            try {
                var field = Person.class.getSuperclass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(person, id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
