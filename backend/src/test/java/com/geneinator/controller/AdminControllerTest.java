package com.geneinator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geneinator.dto.audit.AuditLogDto;
import com.geneinator.dto.settings.SystemSettingsDto;
import com.geneinator.dto.user.UserDto;
import com.geneinator.exception.GlobalExceptionHandler;
import com.geneinator.exception.ResourceNotFoundException;
import com.geneinator.security.JwtAuthenticationFilter;
import com.geneinator.security.JwtService;
import com.geneinator.security.UserDetailsServiceImpl;
import com.geneinator.service.AuditService;
import com.geneinator.service.PhotoService;
import com.geneinator.service.SettingsService;
import com.geneinator.service.UserService;
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
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("AdminController")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuditService auditService;

    @MockitoBean
    private SettingsService settingsService;

    @MockitoBean
    private PhotoService photoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private UserDto sampleUser;
    private UUID sampleUserId;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        sampleUserId = UUID.randomUUID();
        sampleUser = UserDto.builder()
                .id(sampleUserId)
                .username("testuser")
                .role("USER")
                .status("PENDING_APPROVAL")
                .createdAt(Instant.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/admin/users")
    class FindAllUsers {

        @Test
        @DisplayName("should return paginated list of users")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnPaginatedUsers() throws Exception {
            // Given
            Page<UserDto> page = new PageImpl<>(List.of(sampleUser));
            when(userService.findAll(any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/admin/users")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("testuser"))
                .andExpect(jsonPath("$.content[0].status").value("PENDING_APPROVAL"));
        }

        @Test
        @DisplayName("should return empty page when no users exist")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnEmptyPage() throws Exception {
            // Given
            Page<UserDto> emptyPage = new PageImpl<>(List.of());
            when(userService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("PATCH /api/admin/users/{id}/approve")
    class ApproveUser {

        @Test
        @DisplayName("should approve user and return 204")
        @WithMockUser(roles = "ADMIN")
        void shouldApproveUser() throws Exception {
            // Given
            doNothing().when(userService).approveUser(sampleUserId);

            // When/Then
            mockMvc.perform(patch("/api/admin/users/{id}/approve", sampleUserId))
                .andExpect(status().isNoContent());

            verify(userService).approveUser(sampleUserId);
        }

        @Test
        @DisplayName("should return 404 when user not found")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("User not found"))
                    .when(userService).approveUser(nonExistentId);

            // When/Then
            mockMvc.perform(patch("/api/admin/users/{id}/approve", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH /api/admin/users/{id}/suspend")
    class SuspendUser {

        @Test
        @DisplayName("should suspend user and return 204")
        @WithMockUser(roles = "ADMIN")
        void shouldSuspendUser() throws Exception {
            // Given
            doNothing().when(userService).suspendUser(sampleUserId);

            // When/Then
            mockMvc.perform(patch("/api/admin/users/{id}/suspend", sampleUserId))
                .andExpect(status().isNoContent());

            verify(userService).suspendUser(sampleUserId);
        }

        @Test
        @DisplayName("should return 404 when user not found")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("User not found"))
                    .when(userService).suspendUser(nonExistentId);

            // When/Then
            mockMvc.perform(patch("/api/admin/users/{id}/suspend", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/admin/users/{id}")
    class DeleteUser {

        @Test
        @DisplayName("should delete user and return 204")
        @WithMockUser(roles = "ADMIN")
        void shouldDeleteUser() throws Exception {
            // Given
            doNothing().when(userService).delete(sampleUserId);

            // When/Then
            mockMvc.perform(delete("/api/admin/users/{id}", sampleUserId))
                .andExpect(status().isNoContent());

            verify(userService).delete(sampleUserId);
        }

        @Test
        @DisplayName("should return 404 when user not found")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn404WhenUserNotFound() throws Exception {
            // Given
            UUID nonExistentId = UUID.randomUUID();
            doThrow(new ResourceNotFoundException("User not found"))
                    .when(userService).delete(nonExistentId);

            // When/Then
            mockMvc.perform(delete("/api/admin/users/{id}", nonExistentId))
                .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/audit-logs")
    class FindAuditLogs {

        @Test
        @DisplayName("should return paginated audit logs")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnPaginatedAuditLogs() throws Exception {
            // Given
            AuditLogDto log = AuditLogDto.builder()
                    .id(UUID.randomUUID())
                    .userId(sampleUserId)
                    .userUsername("testuser")
                    .action("USER_CREATED")
                    .entityType("USER")
                    .entityId(sampleUserId)
                    .timestamp(Instant.now())
                    .build();

            Page<AuditLogDto> page = new PageImpl<>(List.of(log));
            when(auditService.findAll(any(Pageable.class))).thenReturn(page);

            // When/Then
            mockMvc.perform(get("/api/admin/audit-logs")
                    .param("page", "0")
                    .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].action").value("USER_CREATED"))
                .andExpect(jsonPath("$.content[0].userUsername").value("testuser"));
        }

        @Test
        @DisplayName("should return empty page when no logs exist")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnEmptyPage() throws Exception {
            // Given
            Page<AuditLogDto> emptyPage = new PageImpl<>(List.of());
            when(auditService.findAll(any(Pageable.class))).thenReturn(emptyPage);

            // When/Then
            mockMvc.perform(get("/api/admin/audit-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/stats")
    class GetStats {

        @Test
        @DisplayName("should return admin statistics")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnStats() throws Exception {
            // Given
            Page<UserDto> pendingUsers = new PageImpl<>(List.of(sampleUser));
            when(userService.findByStatus(any(), any(Pageable.class))).thenReturn(pendingUsers);

            // When/Then
            mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pendingApprovals").exists());
        }
    }

    @Nested
    @DisplayName("GET /api/admin/settings")
    class GetSettings {

        @Test
        @DisplayName("should return system settings")
        @WithMockUser(roles = "ADMIN")
        void shouldReturnSettings() throws Exception {
            // Given
            SystemSettingsDto settings = SystemSettingsDto.builder()
                    .spouseFamilyVisible(true)
                    .maxRelationshipHops(3)
                    .includeMarriageConnections(true)
                    .build();
            when(settingsService.getSettings()).thenReturn(settings);

            // When/Then
            mockMvc.perform(get("/api/admin/settings"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.spouseFamilyVisible").value(true))
                    .andExpect(jsonPath("$.maxRelationshipHops").value(3))
                    .andExpect(jsonPath("$.includeMarriageConnections").value(true));
        }
    }

    @Nested
    @DisplayName("PUT /api/admin/settings")
    class UpdateSettings {

        @Test
        @DisplayName("should update system settings and return 200")
        @WithMockUser(roles = "ADMIN")
        void shouldUpdateSettings() throws Exception {
            // Given
            SystemSettingsDto request = SystemSettingsDto.builder()
                    .spouseFamilyVisible(false)
                    .maxRelationshipHops(5)
                    .includeMarriageConnections(false)
                    .build();

            when(settingsService.updateSettings(any(SystemSettingsDto.class))).thenReturn(request);

            // When/Then
            mockMvc.perform(put("/api/admin/settings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.spouseFamilyVisible").value(false))
                    .andExpect(jsonPath("$.maxRelationshipHops").value(5))
                    .andExpect(jsonPath("$.includeMarriageConnections").value(false));

            verify(settingsService).updateSettings(any(SystemSettingsDto.class));
        }

        @Test
        @DisplayName("should allow partial updates")
        @WithMockUser(roles = "ADMIN")
        void shouldAllowPartialUpdates() throws Exception {
            // Given - only updating maxRelationshipHops
            SystemSettingsDto request = SystemSettingsDto.builder()
                    .maxRelationshipHops(10)
                    .build();

            SystemSettingsDto response = SystemSettingsDto.builder()
                    .spouseFamilyVisible(true)
                    .maxRelationshipHops(10)
                    .includeMarriageConnections(true)
                    .build();

            when(settingsService.updateSettings(any(SystemSettingsDto.class))).thenReturn(response);

            // When/Then
            mockMvc.perform(put("/api/admin/settings")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.maxRelationshipHops").value(10));
        }
    }
}
