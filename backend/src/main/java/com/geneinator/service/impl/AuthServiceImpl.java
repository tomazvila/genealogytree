package com.geneinator.service.impl;

import com.geneinator.dto.auth.*;
import com.geneinator.entity.AuditLog;
import com.geneinator.entity.User;
import com.geneinator.exception.AccountLockedException;
import com.geneinator.exception.DuplicateResourceException;
import com.geneinator.repository.UserRepository;
import com.geneinator.security.JwtService;
import com.geneinator.service.AuditService;
import com.geneinator.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuditService auditService;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User with this email already exists");
        }

        // Create new user with PENDING_APPROVAL status
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getDisplayName())
                .role(User.UserRole.USER)
                .status(User.UserStatus.PENDING_APPROVAL)
                .failedLoginAttempts(0)
                .build();

        User savedUser = userRepository.save(user);

        // Log the registration
        auditService.log(
                AuditLog.AuditAction.REGISTER,
                "User",
                savedUser.getId(),
                null,
                Map.of("email", savedUser.getEmail(), "displayName", savedUser.getDisplayName()),
                savedUser.getId(),
                null
        );

        log.info("User registered successfully: {}", savedUser.getEmail());

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .message("Registration successful. Your account is pending approval by an administrator.")
                .build();
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        // Check user status
        if (user.getStatus() == User.UserStatus.PENDING_APPROVAL) {
            throw new AccountLockedException("Account is pending approval by an administrator");
        }

        if (user.getStatus() == User.UserStatus.SUSPENDED) {
            throw new AccountLockedException("Account has been suspended. Please contact an administrator.");
        }

        // Check if account is locked due to failed attempts
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            throw new AccountLockedException("Account is temporarily locked. Please try again later.");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new BadCredentialsException("Invalid email or password");
        }

        // Reset failed attempts on successful login
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        // Generate tokens
        UserDetails userDetails = createUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Log successful login
        auditService.log(
                AuditLog.AuditAction.LOGIN,
                "User",
                user.getId(),
                null,
                Map.of("email", user.getEmail()),
                user.getId(),
                null
        );

        log.info("User logged in successfully: {}", user.getEmail());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpiration())
                .userId(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("Token refresh request");

        // Extract username from refresh token
        String userEmail = jwtService.extractUsername(request.getRefreshToken());

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        UserDetails userDetails = createUserDetails(user);

        // Validate refresh token
        if (!jwtService.isTokenValid(request.getRefreshToken(), userDetails)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

        // Generate new tokens
        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpiration())
                .build();
    }

    @Override
    @Transactional
    public void logout(String token) {
        // In a stateless JWT setup, we don't actually invalidate tokens
        // For proper logout, you'd need a token blacklist (Redis) or short token expiry
        // Here we just log the action
        String userEmail = jwtService.extractUsername(token.replace("Bearer ", ""));
        log.info("User logged out: {}", userEmail);

        userRepository.findByEmail(userEmail).ifPresent(user ->
                auditService.log(
                        AuditLog.AuditAction.LOGOUT,
                        "User",
                        user.getId(),
                        null,
                        null,
                        user.getId(),
                        null
                )
        );
    }

    private void handleFailedLogin(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        // Lock account after 5 failed attempts for 15 minutes
        if (attempts >= 5) {
            user.setLockedUntil(Instant.now().plusSeconds(15 * 60));
            log.warn("Account locked due to {} failed login attempts: {}", attempts, user.getEmail());
        }

        userRepository.save(user);
    }

    private UserDetails createUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                user.getStatus() == User.UserStatus.ACTIVE,
                true,
                true,
                user.getLockedUntil() == null || user.getLockedUntil().isBefore(Instant.now()),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
