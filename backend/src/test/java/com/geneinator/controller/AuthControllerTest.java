package com.geneinator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geneinator.dto.auth.*;
import com.geneinator.entity.User;
import com.geneinator.exception.GlobalExceptionHandler;
import com.geneinator.repository.UserRepository;
import com.geneinator.security.CookieService;
import com.geneinator.security.JwtAuthenticationFilter;
import com.geneinator.security.JwtService;
import com.geneinator.security.UserDetailsServiceImpl;
import com.geneinator.service.AuthService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockitoBean
    private CookieService cookieService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("should return 200 with user id for valid request")
        void shouldReturnSuccessForValidRequest() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("testuser")
                    .password("password123")
                    .build();

            RegisterResponse response = RegisterResponse.builder()
                    .userId(UUID.randomUUID())
                    .username(request.getUsername())
                    .message("Registration successful. Awaiting admin approval.")
                    .build();

            when(authService.register(any(RegisterRequest.class))).thenReturn(response);

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(request.getUsername()));
        }

        @Test
        @DisplayName("should return 400 for missing username")
        void shouldReturnBadRequestForMissingUsername() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("")
                    .password("password123")
                    .build();

            mockMvc.perform(post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("should return 400 for password too short")
        void shouldReturnBadRequestForShortPassword() throws Exception {
            RegisterRequest request = RegisterRequest.builder()
                    .username("testuser")
                    .password("short")
                    .build();

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
        @DisplayName("should return user info with Set-Cookie headers and no tokens in body")
        void shouldReturnUserInfoWithCookies() throws Exception {
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .password("password123")
                    .build();

            LoginResponse loginResponse = LoginResponse.builder()
                    .accessToken("access_token")
                    .refreshToken("refresh_token")
                    .tokenType("Bearer")
                    .expiresIn(86400000)
                    .userId(UUID.randomUUID())
                    .username(request.getUsername())
                    .role("USER")
                    .build();

            when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);
            when(cookieService.createAccessTokenCookie("access_token"))
                    .thenReturn(ResponseCookie.from("ACCESS_TOKEN", "access_token").path("/api").build());
            when(cookieService.createRefreshTokenCookie("refresh_token"))
                    .thenReturn(ResponseCookie.from("REFRESH_TOKEN", "refresh_token").path("/api/auth/refresh").build());

            mockMvc.perform(post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.userId").exists())
                .andExpect(jsonPath("$.username").value(request.getUsername()))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.accessToken").doesNotExist())
                .andExpect(jsonPath("$.refreshToken").doesNotExist());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/refresh")
    class Refresh {

        @Test
        @DisplayName("should refresh tokens from cookie and return user info")
        void shouldRefreshFromCookie() throws Exception {
            TokenResponse tokenResponse = TokenResponse.builder()
                    .accessToken("new_access_token")
                    .refreshToken("new_refresh_token")
                    .tokenType("Bearer")
                    .expiresIn(86400000)
                    .build();

            User user = User.builder()
                    .username("testuser")
                    .passwordHash("hash")
                    .role(User.UserRole.USER)
                    .status(User.UserStatus.ACTIVE)
                    .build();

            when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(tokenResponse);
            when(jwtService.extractUsername("old_refresh_token")).thenReturn("testuser");
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
            when(cookieService.createAccessTokenCookie("new_access_token"))
                    .thenReturn(ResponseCookie.from("ACCESS_TOKEN", "new_access_token").path("/api").build());
            when(cookieService.createRefreshTokenCookie("new_refresh_token"))
                    .thenReturn(ResponseCookie.from("REFRESH_TOKEN", "new_refresh_token").path("/api/auth/refresh").build());

            mockMvc.perform(post("/api/auth/refresh")
                    .cookie(new Cookie("REFRESH_TOKEN", "old_refresh_token")))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"))
                .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("should return 401 when no refresh cookie present")
        void shouldReturn401WhenNoCookie() throws Exception {
            mockMvc.perform(post("/api/auth/refresh"))
                .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout")
    class Logout {

        @Test
        @DisplayName("should clear cookies on logout")
        void shouldClearCookiesOnLogout() throws Exception {
            when(cookieService.clearAccessTokenCookie())
                    .thenReturn(ResponseCookie.from("ACCESS_TOKEN", "").maxAge(0).path("/api").build());
            when(cookieService.clearRefreshTokenCookie())
                    .thenReturn(ResponseCookie.from("REFRESH_TOKEN", "").maxAge(0).path("/api/auth/refresh").build());

            mockMvc.perform(post("/api/auth/logout")
                    .cookie(new Cookie("ACCESS_TOKEN", "some_token")))
                .andExpect(status().isNoContent())
                .andExpect(header().exists("Set-Cookie"));
        }
    }

    @Nested
    @DisplayName("GET /api/auth/me")
    class Me {

        @Test
        @DisplayName("should return 401 when no authenticated user")
        void shouldReturn401WhenNotAuthenticated() throws Exception {
            mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
        }
    }
}
