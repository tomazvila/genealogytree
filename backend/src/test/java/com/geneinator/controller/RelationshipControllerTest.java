package com.geneinator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geneinator.dto.relationship.RelationshipCreateRequest;
import com.geneinator.dto.relationship.RelationshipDto;
import com.geneinator.exception.GlobalExceptionHandler;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.security.JwtAuthenticationFilter;
import com.geneinator.security.JwtService;
import com.geneinator.security.UserDetailsServiceImpl;
import com.geneinator.service.RelationshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RelationshipController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("RelationshipController")
class RelationshipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private RelationshipService relationshipService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Nested
    @DisplayName("POST /api/relationships")
    class Create {

        @Test
        @DisplayName("should create relationship and return 201")
        @WithMockUser
        void shouldCreateRelationship() throws Exception {
            // Given
            UUID personFromId = UUID.randomUUID();
            UUID personToId = UUID.randomUUID();

            RelationshipCreateRequest request = RelationshipCreateRequest.builder()
                    .personFromId(personFromId)
                    .personToId(personToId)
                    .relationshipType("PARENT_CHILD")
                    .build();

            RelationshipDto createdRelationship = RelationshipDto.builder()
                    .id(UUID.randomUUID())
                    .personFromId(personFromId)
                    .personFromName("John Doe")
                    .personToId(personToId)
                    .personToName("Jane Doe")
                    .relationshipType("PARENT_CHILD")
                    .build();

            when(relationshipService.create(any(RelationshipCreateRequest.class)))
                    .thenReturn(createdRelationship);

            // When/Then
            mockMvc.perform(post("/api/relationships")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.relationshipType").value("PARENT_CHILD"))
                .andExpect(jsonPath("$.personFromName").value("John Doe"))
                .andExpect(jsonPath("$.personToName").value("Jane Doe"));
        }

        @Test
        @DisplayName("should return 400 when personFromId is missing")
        @WithMockUser
        void shouldReturn400WhenPersonFromIdMissing() throws Exception {
            // Given
            RelationshipCreateRequest request = RelationshipCreateRequest.builder()
                    .personToId(UUID.randomUUID())
                    .relationshipType("PARENT_CHILD")
                    .build();

            // When/Then
            mockMvc.perform(post("/api/relationships")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when personToId is missing")
        @WithMockUser
        void shouldReturn400WhenPersonToIdMissing() throws Exception {
            // Given
            RelationshipCreateRequest request = RelationshipCreateRequest.builder()
                    .personFromId(UUID.randomUUID())
                    .relationshipType("PARENT_CHILD")
                    .build();

            // When/Then
            mockMvc.perform(post("/api/relationships")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when relationshipType is missing")
        @WithMockUser
        void shouldReturn400WhenRelationshipTypeMissing() throws Exception {
            // Given
            RelationshipCreateRequest request = RelationshipCreateRequest.builder()
                    .personFromId(UUID.randomUUID())
                    .personToId(UUID.randomUUID())
                    .build();

            // When/Then
            mockMvc.perform(post("/api/relationships")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE /api/relationships/{id}")
    class Delete {

        @Test
        @DisplayName("should delete relationship and return 204")
        @WithMockUser
        void shouldDeleteRelationship() throws Exception {
            // Given
            UUID relationshipId = UUID.randomUUID();
            doNothing().when(relationshipService).delete(relationshipId);

            // When/Then
            mockMvc.perform(delete("/api/relationships/{id}", relationshipId))
                .andExpect(status().isNoContent());

            verify(relationshipService).delete(relationshipId);
        }

        @Test
        @DisplayName("should return 404 when relationship not found")
        @WithMockUser
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Relationship not found with id: " + nonExistentId))
                    .when(relationshipService).delete(nonExistentId);

            // When/Then
            mockMvc.perform(delete("/api/relationships/{id}", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/relationships/person/{personId}")
    class FindByPerson {

        @Test
        @DisplayName("should return relationships for person")
        @WithMockUser
        void shouldReturnRelationshipsForPerson() throws Exception {
            // Given
            UUID personId = UUID.randomUUID();

            RelationshipDto relationship = RelationshipDto.builder()
                    .id(UUID.randomUUID())
                    .personFromId(personId)
                    .personFromName("John Doe")
                    .personToId(UUID.randomUUID())
                    .personToName("Jane Doe")
                    .relationshipType("SPOUSE")
                    .build();

            when(relationshipService.findByPersonId(personId)).thenReturn(List.of(relationship));

            // When/Then
            mockMvc.perform(get("/api/relationships/person/{personId}", personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].relationshipType").value("SPOUSE"))
                .andExpect(jsonPath("$[0].personFromName").value("John Doe"));
        }

        @Test
        @DisplayName("should return empty list when person has no relationships")
        @WithMockUser
        void shouldReturnEmptyListWhenNoRelationships() throws Exception {
            // Given
            UUID personId = UUID.randomUUID();
            when(relationshipService.findByPersonId(personId)).thenReturn(List.of());

            // When/Then
            mockMvc.perform(get("/api/relationships/person/{personId}", personId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/relationships/check")
    class AreRelated {

        @Test
        @DisplayName("should return true when persons are related")
        @WithMockUser
        void shouldReturnTrueWhenRelated() throws Exception {
            // Given
            UUID personId1 = UUID.randomUUID();
            UUID personId2 = UUID.randomUUID();
            when(relationshipService.areRelated(personId1, personId2, 3)).thenReturn(true);

            // When/Then
            mockMvc.perform(get("/api/relationships/check")
                    .param("personId1", personId1.toString())
                    .param("personId2", personId2.toString())
                    .param("maxHops", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.related").value(true));
        }

        @Test
        @DisplayName("should return false when persons are not related")
        @WithMockUser
        void shouldReturnFalseWhenNotRelated() throws Exception {
            // Given
            UUID personId1 = UUID.randomUUID();
            UUID personId2 = UUID.randomUUID();
            when(relationshipService.areRelated(personId1, personId2, 3)).thenReturn(false);

            // When/Then
            mockMvc.perform(get("/api/relationships/check")
                    .param("personId1", personId1.toString())
                    .param("personId2", personId2.toString())
                    .param("maxHops", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.related").value(false));
        }
    }
}
