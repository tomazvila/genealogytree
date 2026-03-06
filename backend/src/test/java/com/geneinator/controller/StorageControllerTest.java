package com.geneinator.controller;

import com.geneinator.exception.GlobalExceptionHandler;
import com.geneinator.security.JwtAuthenticationFilter;
import com.geneinator.security.JwtService;
import com.geneinator.security.UserDetailsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StorageController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("StorageController")
class StorageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StorageController.StorageFileService storageFileService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Nested
    @DisplayName("GET /api/storage/{**path}")
    class GetFile {

        @Test
        @DisplayName("should return file when it exists")
        void shouldReturnFileWhenExists() throws Exception {
            // Given
            byte[] imageContent = "fake image content".getBytes();
            Resource resource = new ByteArrayResource(imageContent);
            when(storageFileService.loadAsResource("originals/2026/01/test.jpg")).thenReturn(resource);
            when(storageFileService.getContentType("originals/2026/01/test.jpg")).thenReturn("image/jpeg");

            // When/Then
            mockMvc.perform(get("/api/storage/originals/2026/01/test.jpg"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                    .andExpect(content().bytes(imageContent));
        }

        @Test
        @DisplayName("should return 404 when file not found")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(storageFileService.loadAsResource(anyString())).thenReturn(null);

            // When/Then
            mockMvc.perform(get("/api/storage/originals/nonexistent.jpg"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("should handle nested paths")
        void shouldHandleNestedPaths() throws Exception {
            // Given
            byte[] imageContent = "thumbnail content".getBytes();
            Resource resource = new ByteArrayResource(imageContent);
            when(storageFileService.loadAsResource("thumbnails/small/2026/01/abc.jpg")).thenReturn(resource);
            when(storageFileService.getContentType("thumbnails/small/2026/01/abc.jpg")).thenReturn("image/jpeg");

            // When/Then
            mockMvc.perform(get("/api/storage/thumbnails/small/2026/01/abc.jpg"))
                    .andExpect(status().isOk());
        }
    }
}
