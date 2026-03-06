package com.geneinator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geneinator.dto.person.PersonDto;
import com.geneinator.dto.relationship.RelationshipDto;
import com.geneinator.dto.tree.TreeCreateRequest;
import com.geneinator.dto.tree.TreeDto;
import com.geneinator.dto.tree.TreeStructureDto;
import com.geneinator.exception.GlobalExceptionHandler;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.security.JwtAuthenticationFilter;
import com.geneinator.security.JwtService;
import com.geneinator.security.UserDetailsServiceImpl;
import com.geneinator.service.TreeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TreeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("TreeController")
class TreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private TreeService treeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private TreeDto sampleTree;
    private UUID sampleTreeId;
    private UUID sampleCreatedBy;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        sampleTreeId = UUID.randomUUID();
        sampleCreatedBy = UUID.randomUUID();
        sampleTree = TreeDto.builder()
                .id(sampleTreeId)
                .name("Doe Family Tree")
                .description("The Doe family genealogy")
                .rootPersonId(UUID.randomUUID())
                .rootPersonName("John Doe")
                .personCount(15)
                .isMergeable(true)
                .createdBy(sampleCreatedBy)
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/trees")
    class FindAll {

        @Test
        @DisplayName("should return paginated list of trees")
        @WithMockUser
        void shouldReturnPaginatedTrees() throws Exception {
            // Given
            Page<TreeDto> page = new PageImpl<>(List.of(sampleTree));
            when(treeService.findAll(any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/trees")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Doe Family Tree"))
                .andExpect(jsonPath("$.content[0].id").value(sampleTreeId.toString()));
        }

        @Test
        @DisplayName("should include createdBy field in tree response")
        @WithMockUser
        void shouldIncludeCreatedByInResponse() throws Exception {
            // Given
            Page<TreeDto> page = new PageImpl<>(List.of(sampleTree));
            when(treeService.findAll(any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/trees")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].createdBy").value(sampleCreatedBy.toString()));
        }

        @Test
        @DisplayName("should return empty page when no trees exist")
        @WithMockUser
        void shouldReturnEmptyPage() throws Exception {
            // Given
            Page<TreeDto> emptyPage = new PageImpl<>(List.of());
            when(treeService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/trees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/trees/{id}")
    class FindById {

        @Test
        @DisplayName("should return tree structure when found")
        @WithMockUser
        void shouldReturnTreeStructureWhenFound() throws Exception {
            // Given
            PersonDto person = PersonDto.builder()
                    .id(UUID.randomUUID())
                    .fullName("John Doe")
                    .build();

            RelationshipDto relationship = RelationshipDto.builder()
                    .id(UUID.randomUUID())
                    .relationshipType("PARENT_CHILD")
                    .build();

            TreeStructureDto structure = TreeStructureDto.builder()
                    .treeId(sampleTreeId)
                    .treeName("Doe Family Tree")
                    .createdBy(sampleCreatedBy)
                    .persons(List.of(person))
                    .relationships(List.of(relationship))
                    .build();

            when(treeService.getTreeStructure(eq(sampleTreeId), any())).thenReturn(structure);

            // When/Then
            mockMvc.perform(get("/api/trees/{id}", sampleTreeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.treeId").value(sampleTreeId.toString()))
                .andExpect(jsonPath("$.treeName").value("Doe Family Tree"))
                .andExpect(jsonPath("$.createdBy").value(sampleCreatedBy.toString()))
                .andExpect(jsonPath("$.persons[0].fullName").value("John Doe"));
        }

        @Test
        @DisplayName("should return 404 when tree not found")
        @WithMockUser
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(treeService.getTreeStructure(eq(nonExistentId), any()))
                    .thenThrow(new ResourceNotFoundException("Tree not found with id: " + nonExistentId));

            // When/Then
            mockMvc.perform(get("/api/trees/{id}", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/trees")
    class Create {

        @Test
        @DisplayName("should create tree and return 201")
        @WithMockUser
        void shouldCreateTree() throws Exception {
            // Given
            TreeCreateRequest request = TreeCreateRequest.builder()
                    .name("Smith Family Tree")
                    .description("A new family tree")
                    .isMergeable(true)
                    .build();

            TreeDto createdTree = TreeDto.builder()
                    .id(UUID.randomUUID())
                    .name("Smith Family Tree")
                    .description("A new family tree")
                    .personCount(0)
                    .isMergeable(true)
                    .createdAt(Instant.now())
                    .build();

            when(treeService.create(any(TreeCreateRequest.class), any()))
                    .thenReturn(createdTree);

            // When/Then
            mockMvc.perform(post("/api/trees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Smith Family Tree"))
                .andExpect(jsonPath("$.description").value("A new family tree"));
        }

        @Test
        @DisplayName("should return 400 when name is missing")
        @WithMockUser
        void shouldReturn400WhenNameMissing() throws Exception {
            // Given
            TreeCreateRequest request = TreeCreateRequest.builder()
                    .description("A tree without name")
                    .build();

            // When/Then
            mockMvc.perform(post("/api/trees")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/trees/merge")
    class MergeTrees {

        @Test
        @DisplayName("should merge trees and return 204")
        @WithMockUser
        void shouldMergeTrees() throws Exception {
            // Given
            UUID sourceTreeId = UUID.randomUUID();
            UUID targetTreeId = UUID.randomUUID();
            doNothing().when(treeService).mergeTrees(sourceTreeId, targetTreeId);

            // When/Then
            mockMvc.perform(post("/api/trees/merge")
                    .param("sourceTreeId", sourceTreeId.toString())
                    .param("targetTreeId", targetTreeId.toString()))
                .andExpect(status().isNoContent());

            verify(treeService).mergeTrees(sourceTreeId, targetTreeId);
        }

        @Test
        @DisplayName("should return 404 when source tree not found")
        @WithMockUser
        void shouldReturn404WhenSourceNotFound() throws Exception {
            // Given
            UUID sourceTreeId = UUID.randomUUID();
            UUID targetTreeId = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Tree not found with id: " + sourceTreeId))
                    .when(treeService).mergeTrees(sourceTreeId, targetTreeId);

            // When/Then
            mockMvc.perform(post("/api/trees/merge")
                    .param("sourceTreeId", sourceTreeId.toString())
                    .param("targetTreeId", targetTreeId.toString()))
                .andExpect(status().isNotFound());
        }
    }
}
