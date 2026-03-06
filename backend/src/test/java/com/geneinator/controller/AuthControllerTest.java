package com.geneinator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geneinator.dto.auth.LoginRequest;
import com.geneinator.dto.auth.LoginResponse;
import com.geneinator.dto.auth.RegisterRequest;
import com.geneinator.dto.auth.RegisterResponse;
import com.geneinator.exception.GlobalExceptionHandler;
import com.geneinator.security.JwtAuthenticationFilter;
import com.geneinator.security.JwtService;
import com.geneinator.security.UserDetailsServiceImpl;
import com.geneinator.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("should return 200 with user id for valid request")
        void shouldReturnSuccessForValidRequest() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                    .email("test@example.com")
                    .password("password123")
                    .displayName("Test User")
                    .build();

            RegisterResponse response = RegisterResponse.builder()
                    .userId(UUID.randomUUID())
                    .email(request.getEmail())
                    .message("Registration successful. Awaiting admin approval.")
                    .build();

            when(authService.register(any(RegisterRequest.class))).thenReturn(response);

            // When/Then
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(request.getEmail()));
        }

        @Test
        @DisplayName("should return 400 for invalid email")
        void shouldReturnBadRequestForInvalidEmail() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                    .email("invalid-email")
                    .password("password123")
                    .displayName("Test User")
                    .build();

            // When/Then
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 for password too short")
        void shouldReturnBadRequestForShortPassword() throws Exception {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                    .email("test@example.com")
                    .password("short")
                    .displayName("Test User")
                    .build();

            // When/Then
            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("should return tokens for valid credentials")
        void shouldReturnTokensForValidCredentials() throws Exception {
            // Given
            LoginRequest request = LoginRequest.builder()
                    .email("user@example.com")
                    .password("password123")
                    .build();

            LoginResponse response = LoginResponse.builder()
                    .accessToken("access_token")
                    .refreshToken("refresh_token")
                    .tokenType("Bearer")
                    .expiresIn(86400000)
                    .userId(UUID.randomUUID())
                    .email(request.getEmail())
                    .displayName("Test User")
                    .role("USER")
                    .build();

            when(authService.login(any(LoginRequest.class))).thenReturn(response);

            // When/Then
            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
        }
    }
}
