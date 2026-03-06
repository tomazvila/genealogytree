package com.geneinator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geneinator.dto.common.ApproximateDateDto;
import com.geneinator.dto.event.EventCreateRequest;
import com.geneinator.dto.event.EventDto;
import com.geneinator.dto.event.EventUpdateRequest;
import com.geneinator.exception.GlobalExceptionHandler;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.security.JwtAuthenticationFilter;
import com.geneinator.security.JwtService;
import com.geneinator.security.UserDetailsServiceImpl;
import com.geneinator.service.EventService;
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

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("EventController")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private EventService eventService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private EventDto sampleEvent;
    private UUID sampleEventId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        sampleEventId = UUID.randomUUID();
        sampleEvent = EventDto.builder()
                .id(sampleEventId)
                .eventType("WEDDING")
                .title("John and Jane's Wedding")
                .description("A beautiful ceremony")
                .eventDate(ApproximateDateDto.builder()
                        .year(1975)
                        .month(6)
                        .day(15)
                        .build())
                .location("Vilnius, Lithuania")
                .participants(List.of(
                        EventDto.EventParticipantDto.builder()
                                .personId(UUID.randomUUID())
                                .personName("John Doe")
                                .role("groom")
                                .build(),
                        EventDto.EventParticipantDto.builder()
                                .personId(UUID.randomUUID())
                                .personName("Jane Doe")
                                .role("bride")
                                .build()
                ))
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/events")
    class FindAll {

        @Test
        @DisplayName("should return paginated list of events")
        @WithMockUser
        void shouldReturnPaginatedEvents() throws Exception {
            // Given
            Page<EventDto> page = new PageImpl<>(List.of(sampleEvent));
            when(eventService.findAll(any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/events"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].title").value("John and Jane's Wedding"))
                    .andExpect(jsonPath("$.content[0].eventType").value("WEDDING"));
        }

        @Test
        @DisplayName("should return empty page when no events")
        @WithMockUser
        void shouldReturnEmptyPage() throws Exception {
            // Given
            Page<EventDto> emptyPage = new PageImpl<>(List.of());
            when(eventService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/events"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/events")
    class Create {

        @Test
        @DisplayName("should create event and return 201")
        @WithMockUser
        void shouldCreateEvent() throws Exception {
            // Given
            EventCreateRequest request = EventCreateRequest.builder()
                    .eventType("WEDDING")
                    .title("John and Jane's Wedding")
                    .description("A beautiful ceremony")
                    .eventDate(ApproximateDateDto.builder()
                            .year(1975)
                            .month(6)
                            .day(15)
                            .build())
                    .location("Vilnius, Lithuania")
                    .build();

            when(eventService.create(any(EventCreateRequest.class), any()))
                    .thenReturn(sampleEvent);

            // When/Then
            mockMvc.perform(post("/api/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(sampleEventId.toString()))
                    .andExpect(jsonPath("$.title").value("John and Jane's Wedding"));

            verify(eventService).create(any(EventCreateRequest.class), any());
        }

        @Test
        @DisplayName("should return 400 when title is missing")
        @WithMockUser
        void shouldReturn400WhenTitleMissing() throws Exception {
            // Given
            EventCreateRequest request = EventCreateRequest.builder()
                    .eventType("WEDDING")
                    .build();

            // When/Then
            mockMvc.perform(post("/api/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 when event type is missing")
        @WithMockUser
        void shouldReturn400WhenEventTypeMissing() throws Exception {
            // Given
            EventCreateRequest request = EventCreateRequest.builder()
                    .title("Some Event")
                    .build();

            // When/Then
            mockMvc.perform(post("/api/events")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/events/{id}")
    class Update {

        @Test
        @DisplayName("should update event and return 200")
        @WithMockUser
        void shouldUpdateEvent() throws Exception {
            // Given
            EventUpdateRequest request = EventUpdateRequest.builder()
                    .title("Updated Title")
                    .description("Updated description")
                    .build();

            EventDto updatedEvent = EventDto.builder()
                    .id(sampleEventId)
                    .eventType("WEDDING")
                    .title("Updated Title")
                    .description("Updated description")
                    .createdAt(Instant.now())
                    .build();

            when(eventService.update(eq(sampleEventId), any(EventUpdateRequest.class)))
                    .thenReturn(updatedEvent);

            // When/Then
            mockMvc.perform(put("/api/events/{id}", sampleEventId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Title"))
                    .andExpect(jsonPath("$.description").value("Updated description"));

            verify(eventService).update(eq(sampleEventId), any(EventUpdateRequest.class));
        }

        @Test
        @DisplayName("should return 404 when event not found")
        @WithMockUser
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            EventUpdateRequest request = EventUpdateRequest.builder()
                    .title("Updated Title")
                    .build();

            when(eventService.update(eq(nonExistentId), any(EventUpdateRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Event not found"));

            // When/Then
            mockMvc.perform(put("/api/events/{id}", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/events/{id}")
    class Delete {

        @Test
        @DisplayName("should delete event and return 204")
        @WithMockUser
        void shouldDeleteEvent() throws Exception {
            // Given
            doNothing().when(eventService).delete(sampleEventId);

            // When/Then
            mockMvc.perform(delete("/api/events/{id}", sampleEventId))
                    .andExpect(status().isNoContent());

            verify(eventService).delete(sampleEventId);
        }

        @Test
        @DisplayName("should return 404 when event not found")
        @WithMockUser
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Event not found"))
                    .when(eventService).delete(nonExistentId);

            // When/Then
            mockMvc.perform(delete("/api/events/{id}", nonExistentId))
                    .andExpect(status().isNotFound());
        }
    }
}
