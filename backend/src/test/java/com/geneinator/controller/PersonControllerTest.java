package com.geneinator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geneinator.dto.common.ApproximateDateDto;
import com.geneinator.dto.event.EventDto;
import com.geneinator.dto.person.PersonCreateRequest;
import com.geneinator.dto.person.PersonDto;
import com.geneinator.dto.person.PersonMergeRequest;
import com.geneinator.dto.person.PersonUpdateRequest;
import com.geneinator.dto.person.RelativeDto;
import com.geneinator.dto.photo.PhotoDto;
import com.geneinator.exception.GlobalExceptionHandler;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.security.JwtAuthenticationFilter;
import com.geneinator.security.JwtService;
import com.geneinator.security.UserDetailsServiceImpl;
import com.geneinator.service.EventService;
import com.geneinator.service.PersonService;
import com.geneinator.service.PhotoService;
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

@WebMvcTest(PersonController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("PersonController")
class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private PersonService personService;

    @MockitoBean
    private PhotoService photoService;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private PersonDto samplePerson;
    private UUID samplePersonId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        samplePersonId = UUID.randomUUID();
        samplePerson = PersonDto.builder()
                .id(samplePersonId)
                .fullName("John Doe")
                .birthDate(ApproximateDateDto.builder()
                        .year(1950)
                        .month(5)
                        .day(15)
                        .isApproximate(false)
                        .build())
                .gender("MALE")
                .biography("A great person")
                .locationBirth("New York")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/persons")
    class FindAll {

        @Test
        @DisplayName("should return paginated list of persons")
        @WithMockUser
        void shouldReturnPaginatedPersons() throws Exception {
            // Given
            Page<PersonDto> page = new PageImpl<>(List.of(samplePerson));
            when(personService.findAll(any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/persons")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("John Doe"))
                .andExpect(jsonPath("$.content[0].id").value(samplePersonId.toString()));
        }

        @Test
        @DisplayName("should return empty page when no persons exist")
        @WithMockUser
        void shouldReturnEmptyPage() throws Exception {
            // Given
            Page<PersonDto> emptyPage = new PageImpl<>(List.of());
            when(personService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/persons"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/persons/{id}")
    class FindById {

        @Test
        @DisplayName("should return person when found")
        @WithMockUser
        void shouldReturnPersonWhenFound() throws Exception {
            // Given
            when(personService.findById(samplePersonId)).thenReturn(samplePerson);

            // When/Then
            mockMvc.perform(get("/api/persons/{id}", samplePersonId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(samplePersonId.toString()))
                .andExpect(jsonPath("$.fullName").value("John Doe"))
                .andExpect(jsonPath("$.birthDate.year").value(1950));
        }

        @Test
        @DisplayName("should return 404 when person not found")
        @WithMockUser
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(personService.findById(nonExistentId))
                    .thenThrow(new ResourceNotFoundException("Person not found with id: " + nonExistentId));

            // When/Then
            mockMvc.perform(get("/api/persons/{id}", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/persons")
    class Create {

        @Test
        @DisplayName("should create person and return 201")
        @WithMockUser(username = "user@example.com")
        void shouldCreatePerson() throws Exception {
            // Given
            PersonCreateRequest request = PersonCreateRequest.builder()
                    .fullName("Jane Doe")
                    .birthDate(ApproximateDateDto.builder()
                            .year(1980)
                            .month(3)
                            .day(20)
                            .isApproximate(false)
                            .build())
                    .gender("FEMALE")
                    .locationBirth("Los Angeles")
                    .build();

            PersonDto createdPerson = PersonDto.builder()
                    .id(UUID.randomUUID())
                    .fullName("Jane Doe")
                    .birthDate(request.getBirthDate())
                    .gender("FEMALE")
                    .locationBirth("Los Angeles")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            when(personService.create(any(PersonCreateRequest.class), any()))
                    .thenReturn(createdPerson);

            // When/Then
            mockMvc.perform(post("/api/persons")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.gender").value("FEMALE"));
        }

        @Test
        @DisplayName("should return 400 when fullName is missing")
        @WithMockUser
        void shouldReturn400WhenFullNameMissing() throws Exception {
            // Given
            PersonCreateRequest request = PersonCreateRequest.builder()
                    .birthDate(ApproximateDateDto.builder()
                            .year(1980)
                            .build())
                    .build();

            // When/Then
            mockMvc.perform(post("/api/persons")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when birthDate is missing")
        @WithMockUser
        void shouldReturn400WhenBirthDateMissing() throws Exception {
            // Given
            PersonCreateRequest request = PersonCreateRequest.builder()
                    .fullName("John Doe")
                    .build();

            // When/Then
            mockMvc.perform(post("/api/persons")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/persons/{id}")
    class Update {

        @Test
        @DisplayName("should update person and return 200")
        @WithMockUser
        void shouldUpdatePerson() throws Exception {
            // Given
            PersonUpdateRequest request = PersonUpdateRequest.builder()
                    .fullName("John Doe Updated")
                    .biography("Updated biography")
                    .build();

            PersonDto updatedPerson = PersonDto.builder()
                    .id(samplePersonId)
                    .fullName("John Doe Updated")
                    .birthDate(samplePerson.getBirthDate())
                    .biography("Updated biography")
                    .createdAt(samplePerson.getCreatedAt())
                    .updatedAt(Instant.now())
                    .build();

            when(personService.update(eq(samplePersonId), any(PersonUpdateRequest.class)))
                    .thenReturn(updatedPerson);

            // When/Then
            mockMvc.perform(put("/api/persons/{id}", samplePersonId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("John Doe Updated"))
                .andExpect(jsonPath("$.biography").value("Updated biography"));
        }

        @Test
        @DisplayName("should return 404 when updating non-existent person")
        @WithMockUser
        void shouldReturn404WhenUpdatingNonExistent() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            PersonUpdateRequest request = PersonUpdateRequest.builder()
                    .fullName("Updated Name")
                    .build();

            when(personService.update(eq(nonExistentId), any(PersonUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Person not found with id: " + nonExistentId));

            // When/Then
            mockMvc.perform(put("/api/persons/{id}", nonExistentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/persons/{id}")
    class Delete {

        @Test
        @DisplayName("should delete person and return 204")
        @WithMockUser
        void shouldDeletePerson() throws Exception {
            // Given
            doNothing().when(personService).delete(samplePersonId);

            // When/Then
            mockMvc.perform(delete("/api/persons/{id}", samplePersonId))
                .andExpect(status().isNoContent());

            verify(personService).delete(samplePersonId);
        }

        @Test
        @DisplayName("should return 404 when deleting non-existent person")
        @WithMockUser
        void shouldReturn404WhenDeletingNonExistent() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Person not found with id: " + nonExistentId))
                    .when(personService).delete(nonExistentId);

            // When/Then
            mockMvc.perform(delete("/api/persons/{id}", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/persons/{id}/relatives")
    class FindRelatives {

        @Test
        @DisplayName("should return list of relatives with relationship type")
        @WithMockUser
        void shouldReturnRelatives() throws Exception {
            // Given
            RelativeDto relative = RelativeDto.builder()
                    .id(UUID.randomUUID())
                    .fullName("Jane Doe")
                    .birthDate(ApproximateDateDto.builder().year(1975).build())
                    .relationshipType("PARENT")
                    .build();

            when(personService.findRelatives(samplePersonId)).thenReturn(List.of(relative));

            // When/Then
            mockMvc.perform(get("/api/persons/{id}/relatives", samplePersonId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Jane Doe"))
                .andExpect(jsonPath("$[0].relationshipType").value("PARENT"));
        }

        @Test
        @DisplayName("should return empty list when no relatives")
        @WithMockUser
        void shouldReturnEmptyListWhenNoRelatives() throws Exception {
            // Given
            when(personService.findRelatives(samplePersonId)).thenReturn(List.of());

            // When/Then
            mockMvc.perform(get("/api/persons/{id}/relatives", samplePersonId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/persons/search")
    class Search {

        @Test
        @DisplayName("should return matching persons")
        @WithMockUser
        void shouldReturnMatchingPersons() throws Exception {
            // Given
            Page<PersonDto> results = new PageImpl<>(List.of(samplePerson));
            when(personService.search(eq("John"), any(Pageable.class))).thenReturn(results);

            // When/Then
            mockMvc.perform(get("/api/persons/search")
                    .param("q", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("John Doe"));
        }

        @Test
        @DisplayName("should return empty results for no matches")
        @WithMockUser
        void shouldReturnEmptyForNoMatches() throws Exception {
            // Given
            Page<PersonDto> emptyResults = new PageImpl<>(List.of());
            when(personService.search(eq("xyz"), any(Pageable.class))).thenReturn(emptyResults);

            // When/Then
            mockMvc.perform(get("/api/persons/search")
                    .param("q", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/persons/{id}/photos")
    class FindPhotos {

        @Test
        @DisplayName("should return paginated photos for person")
        @WithMockUser
        void shouldReturnPhotosForPerson() throws Exception {
            // Given
            PhotoDto photo = PhotoDto.builder()
                    .id(UUID.randomUUID())
                    .originalUrl("/photos/original.jpg")
                    .thumbnailSmallUrl("/photos/thumbnail.jpg")
                    .build();

            Page<PhotoDto> photos = new PageImpl<>(List.of(photo));
            when(photoService.findByPersonId(eq(samplePersonId), any(Pageable.class))).thenReturn(photos);

            // When/Then
            mockMvc.perform(get("/api/persons/{id}/photos", samplePersonId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].originalUrl").value("/photos/original.jpg"));
        }
    }

    @Nested
    @DisplayName("GET /api/persons/{id}/events")
    class FindEvents {

        @Test
        @DisplayName("should return paginated events for person")
        @WithMockUser
        void shouldReturnEventsForPerson() throws Exception {
            // Given
            EventDto event = EventDto.builder()
                    .id(UUID.randomUUID())
                    .title("Birthday Party")
                    .eventType("BIRTHDAY")
                    .build();

            Page<EventDto> events = new PageImpl<>(List.of(event));
            when(eventService.findByPersonId(eq(samplePersonId), any(Pageable.class))).thenReturn(events);

            // When/Then
            mockMvc.perform(get("/api/persons/{id}/events", samplePersonId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Birthday Party"));
        }
    }

    @Nested
    @DisplayName("POST /api/persons/merge")
    class Merge {

        @Test
        @DisplayName("should merge persons and return 200")
        @WithMockUser
        void shouldMergePersons() throws Exception {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .mergeBiography(true)
                    .build();

            PersonDto mergedPerson = PersonDto.builder()
                    .id(primaryId)
                    .fullName("Merged Person")
                    .birthDate(ApproximateDateDto.builder().year(1960).build())
                    .build();

            when(personService.merge(any(PersonMergeRequest.class), any())).thenReturn(mergedPerson);

            // When/Then
            mockMvc.perform(post("/api/persons/merge")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(primaryId.toString()))
                .andExpect(jsonPath("$.fullName").value("Merged Person"));

            verify(personService).merge(any(PersonMergeRequest.class), any());
        }

        @Test
        @DisplayName("should return 400 when merging person with itself")
        @WithMockUser
        void shouldReturn400WhenMergingSamePerson() throws Exception {
            // Given
            UUID sameId = UUID.randomUUID();

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(sameId)
                    .secondaryPersonId(sameId)
                    .build();

            when(personService.merge(any(PersonMergeRequest.class), any()))
                    .thenThrow(new IllegalArgumentException("Cannot merge a person with itself"));

            // When/Then
            mockMvc.perform(post("/api/persons/merge")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 404 when primary person not found")
        @WithMockUser
        void shouldReturn404WhenPrimaryNotFound() throws Exception {
            // Given
            UUID primaryId = UUID.randomUUID();
            UUID secondaryId = UUID.randomUUID();

            PersonMergeRequest request = PersonMergeRequest.builder()
                    .primaryPersonId(primaryId)
                    .secondaryPersonId(secondaryId)
                    .build();

            when(personService.merge(any(PersonMergeRequest.class), any()))
                    .thenThrow(new ResourceNotFoundException("Primary person not found with id: " + primaryId));

            // When/Then
            mockMvc.perform(post("/api/persons/merge")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should return 400 when primaryPersonId is missing")
        @WithMockUser
        void shouldReturn400WhenPrimaryIdMissing() throws Exception {
            // Given - only secondary ID provided
            String jsonRequest = "{\"secondaryPersonId\":\"" + UUID.randomUUID() + "\"}";

            // When/Then
            mockMvc.perform(post("/api/persons/merge")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonRequest))
                .andExpect(status().isBadRequest());
        }
    }
}
