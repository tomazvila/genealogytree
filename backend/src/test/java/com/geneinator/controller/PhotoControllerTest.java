package com.geneinator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geneinator.dto.photo.PhotoDto;
import com.geneinator.dto.photo.PhotoUploadResponse;
import com.geneinator.exception.GlobalExceptionHandler;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.security.JwtAuthenticationFilter;
import com.geneinator.security.JwtService;
import com.geneinator.security.UserDetailsServiceImpl;
import com.geneinator.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PhotoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("PhotoController")
class PhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private PhotoService photoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private PhotoDto samplePhoto;
    private UUID samplePhotoId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        samplePhotoId = UUID.randomUUID();
        samplePhoto = PhotoDto.builder()
                .id(samplePhotoId)
                .originalUrl("/photos/originals/2024/01/test.jpg")
                .thumbnailSmallUrl("/photos/thumbnails/small/test.jpg")
                .thumbnailMediumUrl("/photos/thumbnails/medium/test.jpg")
                .thumbnailLargeUrl("/photos/thumbnails/large/test.jpg")
                .caption("Test photo")
                .processingStatus("COMPLETED")
                .personIds(List.of(UUID.randomUUID()))
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("POST /api/photos/upload")
    class Upload {

        @Test
        @DisplayName("should upload photo and return 201 with upload response")
        @WithMockUser
        void shouldUploadPhoto() throws Exception {
            // Given
            MockMultipartFile file = new MockMultipartFile(
                    "file",
                    "test-image.jpg",
                    MediaType.IMAGE_JPEG_VALUE,
                    "test image content".getBytes()
            );

            PhotoUploadResponse response = PhotoUploadResponse.builder()
                    .photoId(samplePhotoId)
                    .originalUrl("/photos/originals/2024/01/test.jpg")
                    .processingStatus("PENDING")
                    .message("Photo uploaded successfully. Processing will begin shortly.")
                    .build();

            when(photoService.upload(any(), any())).thenReturn(response);

            // When/Then
            mockMvc.perform(multipart("/api/photos/upload")
                            .file(file))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.photoId").value(samplePhotoId.toString()))
                    .andExpect(jsonPath("$.processingStatus").value("PENDING"))
                    .andExpect(jsonPath("$.message").exists());

            verify(photoService).upload(any(), any());
        }

        @Test
        @DisplayName("should return 400 when no file provided")
        @WithMockUser
        void shouldReturn400WhenNoFile() throws Exception {
            // When/Then
            mockMvc.perform(multipart("/api/photos/upload"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /api/photos")
    class GetMyPhotos {

        @Test
        @DisplayName("should return paginated photos for current user")
        @WithMockUser
        void shouldReturnPaginatedPhotos() throws Exception {
            // Given
            Page<PhotoDto> photoPage = new PageImpl<>(
                    List.of(samplePhoto),
                    PageRequest.of(0, 20),
                    1
            );
            when(photoService.findByUploadedBy(any(), any())).thenReturn(photoPage);

            // When/Then
            mockMvc.perform(get("/api/photos")
                            .param("page", "0")
                            .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content[0].id").value(samplePhotoId.toString()))
                    .andExpect(jsonPath("$.totalElements").value(1));
        }

        @Test
        @DisplayName("should return empty page when user has no photos")
        @WithMockUser
        void shouldReturnEmptyPage() throws Exception {
            // Given
            Page<PhotoDto> emptyPage = Page.empty();
            when(photoService.findByUploadedBy(any(), any())).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/photos"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content").isArray())
                    .andExpect(jsonPath("$.content").isEmpty())
                    .andExpect(jsonPath("$.totalElements").value(0));
        }

        @Test
        @DisplayName("should use default pagination when no params provided")
        @WithMockUser
        void shouldUseDefaultPagination() throws Exception {
            // Given
            Page<PhotoDto> photoPage = Page.empty();
            when(photoService.findByUploadedBy(any(), any())).thenReturn(photoPage);

            // When/Then
            mockMvc.perform(get("/api/photos"))
                    .andExpect(status().isOk());

            verify(photoService).findByUploadedBy(any(), any());
        }
    }

    @Nested
    @DisplayName("GET /api/photos/{id}")
    class FindById {

        @Test
        @DisplayName("should return photo by ID")
        @WithMockUser
        void shouldReturnPhotoById() throws Exception {
            // Given
            when(photoService.findById(samplePhotoId)).thenReturn(samplePhoto);

            // When/Then
            mockMvc.perform(get("/api/photos/{id}", samplePhotoId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(samplePhotoId.toString()))
                    .andExpect(jsonPath("$.originalUrl").value(samplePhoto.getOriginalUrl()))
                    .andExpect(jsonPath("$.processingStatus").value("COMPLETED"));
        }

        @Test
        @DisplayName("should return 404 when photo not found")
        @WithMockUser
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            when(photoService.findById(nonExistentId))
                    .thenThrow(new ResourceNotFoundException("Photo not found"));

            // When/Then
            mockMvc.perform(get("/api/photos/{id}", nonExistentId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/photos/{id}")
    class Delete {

        @Test
        @DisplayName("should delete photo and return 204")
        @WithMockUser
        void shouldDeletePhoto() throws Exception {
            // Given
            doNothing().when(photoService).delete(samplePhotoId);

            // When/Then
            mockMvc.perform(delete("/api/photos/{id}", samplePhotoId))
                    .andExpect(status().isNoContent());

            verify(photoService).delete(samplePhotoId);
        }

        @Test
        @DisplayName("should return 404 when photo not found")
        @WithMockUser
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("Photo not found"))
                    .when(photoService).delete(nonExistentId);

            // When/Then
            mockMvc.perform(delete("/api/photos/{id}", nonExistentId))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/photos/{id}/persons")
    class LinkToPersons {

        @Test
        @DisplayName("should link photo to persons and return 204")
        @WithMockUser
        void shouldLinkPhotoToPersons() throws Exception {
            // Given
            UUID personId1 = UUID.randomUUID();
            UUID personId2 = UUID.randomUUID();
            List<UUID> personIds = List.of(personId1, personId2);

            doNothing().when(photoService).linkToPersons(eq(samplePhotoId), eq(personIds), eq(personId1));

            // When/Then
            mockMvc.perform(post("/api/photos/{id}/persons", samplePhotoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .param("primaryPersonId", personId1.toString())
                            .content(objectMapper.writeValueAsString(personIds)))
                    .andExpect(status().isNoContent());

            verify(photoService).linkToPersons(samplePhotoId, personIds, personId1);
        }

        @Test
        @DisplayName("should link photo without primary person")
        @WithMockUser
        void shouldLinkPhotoWithoutPrimaryPerson() throws Exception {
            // Given
            UUID personId1 = UUID.randomUUID();
            List<UUID> personIds = List.of(personId1);

            doNothing().when(photoService).linkToPersons(eq(samplePhotoId), eq(personIds), isNull());

            // When/Then
            mockMvc.perform(post("/api/photos/{id}/persons", samplePhotoId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(personIds)))
                    .andExpect(status().isNoContent());

            verify(photoService).linkToPersons(samplePhotoId, personIds, null);
        }

        @Test
        @DisplayName("should return 404 when photo not found")
        @WithMockUser
        void shouldReturn404WhenPhotoNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            List<UUID> personIds = List.of(UUID.randomUUID());

            doThrow(new ResourceNotFoundException("Photo not found"))
                    .when(photoService).linkToPersons(eq(nonExistentId), any(), any());

            // When/Then
            mockMvc.perform(post("/api/photos/{id}/persons", nonExistentId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(personIds)))
                    .andExpect(status().isNotFound());
        }
    }
}
