package com.geneinator.service;

import com.geneinator.dto.auth.LoginRequest;
import com.geneinator.dto.auth.RegisterRequest;
import com.geneinator.entity.User;
import com.geneinator.exception.DuplicateResourceException;
import com.geneinator.exception.AccountLockedException;
import com.geneinator.repository.UserRepository;
import com.geneinator.security.JwtService;
import com.geneinator.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService")
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuditService auditService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, passwordEncoder, jwtService, auditService);
    }

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("should create new user with PENDING_APPROVAL status")
        void shouldCreateNewUserWithPendingStatus() {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                    .username("testuser")
                    .password("password123")
                    .build();

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_password");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                return User.builder()
                        .username(user.getUsername())
                        .passwordHash(user.getPasswordHash())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .build();
            });

            // When
            var response = authService.register(request);

            // Then
            assertThat(response).isNotNull();
            assertThat(response.getUsername()).isEqualTo(request.getUsername());
            assertThat(response.getMessage()).containsIgnoringCase("approval");

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            User savedUser = userCaptor.getValue();
            assertThat(savedUser.getStatus()).isEqualTo(User.UserStatus.PENDING_APPROVAL);
            assertThat(savedUser.getRole()).isEqualTo(User.UserRole.USER);
        }

        @Test
        @DisplayName("should throw exception when username already exists")
        void shouldThrowExceptionWhenUsernameExists() {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                    .username("existinguser")
                    .password("password123")
                    .build();

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

            // When/Then
            assertThatThrownBy(() -> authService.register(request))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("username");
        }

        @Test
        @DisplayName("should hash password before saving")
        void shouldHashPassword() {
            // Given
            RegisterRequest request = RegisterRequest.builder()
                    .username("testuser")
                    .password("plaintext123")
                    .build();

            when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
            when(passwordEncoder.encode("plaintext123")).thenReturn("hashed_password");
            when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

            // When
            authService.register(request);

            // Then
            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());
            assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("hashed_password");
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("should return tokens for valid credentials")
        void shouldReturnTokensForValidCredentials() {
            // Given
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .password("password123")
                    .build();

            User user = User.builder()
                    .username(request.getUsername())
                    .passwordHash("encoded_password")
                    .role(User.UserRole.USER)
                    .status(User.UserStatus.ACTIVE)
                    .build();

            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(true);
            when(jwtService.generateToken(any(UserDetails.class))).thenReturn("access_token");
            when(jwtService.generateRefreshToken(any(UserDetails.class))).thenReturn("refresh_token");
            when(jwtService.getJwtExpiration()).thenReturn(86400000L);

            // When
            var response = authService.login(request);

            // Then
            assertThat(response.getAccessToken()).isEqualTo("access_token");
            assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
            assertThat(response.getTokenType()).isEqualTo("Bearer");
            assertThat(response.getUsername()).isEqualTo(request.getUsername());
        }

        @Test
        @DisplayName("should reject login for pending approval users")
        void shouldRejectPendingApprovalUsers() {
            // Given
            LoginRequest request = LoginRequest.builder()
                    .username("pendinguser")
                    .password("password123")
                    .build();

            User user = User.builder()
                    .username(request.getUsername())
                    .passwordHash("encoded_password")
                    .status(User.UserStatus.PENDING_APPROVAL)
                    .build();

            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

            // When/Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(AccountLockedException.class)
                    .hasMessageContaining("pending");
        }

        @Test
        @DisplayName("should reject login for suspended users")
        void shouldRejectSuspendedUsers() {
            // Given
            LoginRequest request = LoginRequest.builder()
                    .username("suspendeduser")
                    .password("password123")
                    .build();

            User user = User.builder()
                    .username(request.getUsername())
                    .passwordHash("encoded_password")
                    .status(User.UserStatus.SUSPENDED)
                    .build();

            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

            // When/Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(AccountLockedException.class)
                    .hasMessageContaining("suspended");
        }

        @Test
        @DisplayName("should throw BadCredentialsException for wrong password")
        void shouldThrowBadCredentialsForWrongPassword() {
            // Given
            LoginRequest request = LoginRequest.builder()
                    .username("testuser")
                    .password("wrong_password")
                    .build();

            User user = User.builder()
                    .username(request.getUsername())
                    .passwordHash("encoded_password")
                    .status(User.UserStatus.ACTIVE)
                    .build();

            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));
            when(passwordEncoder.matches(request.getPassword(), user.getPasswordHash())).thenReturn(false);

            // When/Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        @DisplayName("should throw BadCredentialsException for non-existent user")
        void shouldThrowBadCredentialsForNonExistentUser() {
            // Given
            LoginRequest request = LoginRequest.builder()
                    .username("nonexistent")
                    .password("password123")
                    .build();

            when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);
        }
    }
}
