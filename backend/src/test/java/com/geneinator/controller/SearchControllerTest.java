package com.geneinator.controller;

import com.geneinator.dto.common.ApproximateDateDto;
import com.geneinator.dto.person.PersonDto;
import com.geneinator.exception.GlobalExceptionHandler;
import com.geneinator.security.JwtAuthenticationFilter;
import com.geneinator.security.JwtService;
import com.geneinator.security.UserDetailsServiceImpl;
import com.geneinator.service.PersonService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("SearchController")
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PersonService personService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private PersonDto samplePerson;

    @BeforeEach
    void setUp() {
        samplePerson = PersonDto.builder()
                .id(UUID.randomUUID())
                .fullName("John Doe")
                .birthDate(ApproximateDateDto.builder()
                        .year(1950)
                        .month(3)
                        .day(15)
                        .build())
                .gender("MALE")
                .locationBirth("Vilnius, Lithuania")
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/search")
    class Search {

        @Test
        @DisplayName("should search by name")
        @WithMockUser
        void shouldSearchByName() throws Exception {
            // Given
            Page<PersonDto> page = new PageImpl<>(List.of(samplePerson));
            when(personService.search(eq("John"), any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/search")
                            .param("name", "John"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].fullName").value("John Doe"))
                    .andExpect(jsonPath("$.content.length()").value(1));

            verify(personService).search(eq("John"), any(Pageable.class));
        }

        @Test
        @DisplayName("should search by birth year range")
        @WithMockUser
        void shouldSearchByBirthYearRange() throws Exception {
            // Given
            Page<PersonDto> page = new PageImpl<>(List.of(samplePerson));
            when(personService.searchByBirthYearRange(eq(1940), eq(1960), any(Pageable.class)))
                    .thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/search")
                            .param("birthYearFrom", "1940")
                            .param("birthYearTo", "1960"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].birthDate.year").value(1950));

            verify(personService).searchByBirthYearRange(eq(1940), eq(1960), any(Pageable.class));
        }

        @Test
        @DisplayName("should search by location")
        @WithMockUser
        void shouldSearchByLocation() throws Exception {
            // Given
            Page<PersonDto> page = new PageImpl<>(List.of(samplePerson));
            when(personService.searchByLocation(eq("Vilnius"), any(Pageable.class)))
                    .thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/search")
                            .param("location", "Vilnius"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].locationBirth").value("Vilnius, Lithuania"));

            verify(personService).searchByLocation(eq("Vilnius"), any(Pageable.class));
        }

        @Test
        @DisplayName("should return empty page when no results")
        @WithMockUser
        void shouldReturnEmptyPageWhenNoResults() throws Exception {
            // Given
            Page<PersonDto> emptyPage = new PageImpl<>(List.of());
            when(personService.search(anyString(), any(Pageable.class))).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/search")
                            .param("name", "NonExistent"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("should search with combined criteria")
        @WithMockUser
        void shouldSearchWithCombinedCriteria() throws Exception {
            // Given
            Page<PersonDto> page = new PageImpl<>(List.of(samplePerson));
            when(personService.searchAdvanced(
                    eq("John"), eq(1940), eq(1960), eq("Vilnius"), any(Pageable.class)))
                    .thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/search")
                            .param("name", "John")
                            .param("birthYearFrom", "1940")
                            .param("birthYearTo", "1960")
                            .param("location", "Vilnius"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].fullName").value("John Doe"));

            verify(personService).searchAdvanced(
                    eq("John"), eq(1940), eq(1960), eq("Vilnius"), any(Pageable.class));
        }

        @Test
        @DisplayName("should support pagination")
        @WithMockUser
        void shouldSupportPagination() throws Exception {
            // Given
            Page<PersonDto> page = new PageImpl<>(List.of(samplePerson));
            when(personService.search(eq("John"), any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/search")
                            .param("name", "John")
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }

        @Test
        @DisplayName("should return 400 when no search criteria provided")
        @WithMockUser
        void shouldReturn400WhenNoSearchCriteria() throws Exception {
            // When/Then
            mockMvc.perform(get("/api/search"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/search/descendants/{ancestorId}")
    class SearchDescendants {

        @Test
        @DisplayName("should return descendants of a person")
        @WithMockUser
        void shouldReturnDescendants() throws Exception {
            // Given
            UUID ancestorId = UUID.randomUUID();
            PersonDto child = PersonDto.builder()
                    .id(UUID.randomUUID())
                    .fullName("Child Person")
                    .birthDate(ApproximateDateDto.builder().year(1980).build())
                    .build();
            PersonDto grandchild = PersonDto.builder()
                    .id(UUID.randomUUID())
                    .fullName("Grandchild Person")
                    .birthDate(ApproximateDateDto.builder().year(2000).build())
                    .build();

            Page<PersonDto> page = new PageImpl<>(List.of(child, grandchild));
            when(personService.findDescendants(eq(ancestorId), any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/search/descendants/{ancestorId}", ancestorId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].fullName").value("Child Person"))
                    .andExpect(jsonPath("$.content[1].fullName").value("Grandchild Person"));

            verify(personService).findDescendants(eq(ancestorId), any(Pageable.class));
        }

        @Test
        @DisplayName("should return empty page when person has no descendants")
        @WithMockUser
        void shouldReturnEmptyWhenNoDescendants() throws Exception {
            // Given
            UUID ancestorId = UUID.randomUUID();
            Page<PersonDto> emptyPage = new PageImpl<>(List.of());
            when(personService.findDescendants(eq(ancestorId), any(Pageable.class))).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/search/descendants/{ancestorId}", ancestorId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("should support pagination for descendants")
        @WithMockUser
        void shouldSupportPagination() throws Exception {
            // Given
            UUID ancestorId = UUID.randomUUID();
            Page<PersonDto> page = new PageImpl<>(List.of(samplePerson));
            when(personService.findDescendants(eq(ancestorId), any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/search/descendants/{ancestorId}", ancestorId)
                            .param("page", "0")
                            .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray());
        }
    }

    @Nested
    @DisplayName("GET /api/search/ancestors/{descendantId}")
    class SearchAncestors {

        @Test
        @DisplayName("should return ancestors of a person")
        @WithMockUser
        void shouldReturnAncestors() throws Exception {
            // Given
            UUID descendantId = UUID.randomUUID();
            PersonDto parent = PersonDto.builder()
                    .id(UUID.randomUUID())
                    .fullName("Parent Person")
                    .birthDate(ApproximateDateDto.builder().year(1950).build())
                    .build();
            PersonDto grandparent = PersonDto.builder()
                    .id(UUID.randomUUID())
                    .fullName("Grandparent Person")
                    .birthDate(ApproximateDateDto.builder().year(1920).build())
                    .build();

            Page<PersonDto> page = new PageImpl<>(List.of(parent, grandparent));
            when(personService.findAncestors(eq(descendantId), any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/search/ancestors/{descendantId}", descendantId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content.length()").value(2))
                    .andExpect(jsonPath("$.content[0].fullName").value("Parent Person"))
                    .andExpect(jsonPath("$.content[1].fullName").value("Grandparent Person"));

            verify(personService).findAncestors(eq(descendantId), any(Pageable.class));
        }

        @Test
        @DisplayName("should return empty page when person has no ancestors")
        @WithMockUser
        void shouldReturnEmptyWhenNoAncestors() throws Exception {
            // Given
            UUID descendantId = UUID.randomUUID();
            Page<PersonDto> emptyPage = new PageImpl<>(List.of());
            when(personService.findAncestors(eq(descendantId), any(Pageable.class))).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/search/ancestors/{descendantId}", descendantId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }
    }
}
