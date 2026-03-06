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
        log.info("Registering new user with username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User with this username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.UserRole.USER)
                .status(User.UserStatus.PENDING_APPROVAL)
                .failedLoginAttempts(0)
                .build();

        User savedUser = userRepository.save(user);

        auditService.log(
                AuditLog.AuditAction.REGISTER,
                "User",
                savedUser.getId(),
                null,
                Map.of("username", savedUser.getUsername()),
                savedUser.getId(),
                null
        );

        log.info("User registered successfully: {}", savedUser.getUsername());

        return RegisterResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .message("Registration successful. Your account is pending approval by an administrator.")
                .build();
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (user.getStatus() == User.UserStatus.PENDING_APPROVAL) {
            throw new AccountLockedException("Account is pending approval by an administrator");
        }

        if (user.getStatus() == User.UserStatus.SUSPENDED) {
            throw new AccountLockedException("Account has been suspended. Please contact an administrator.");
        }

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(Instant.now())) {
            throw new AccountLockedException("Account is temporarily locked. Please try again later.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            handleFailedLogin(user);
            throw new BadCredentialsException("Invalid username or password");
        }

        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setLastLogin(Instant.now());
        userRepository.save(user);

        UserDetails userDetails = createUserDetails(user);
        String accessToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        auditService.log(
                AuditLog.AuditAction.LOGIN,
                "User",
                user.getId(),
                null,
                Map.of("username", user.getUsername()),
                user.getId(),
                null
        );

        log.info("User logged in successfully: {}", user.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getJwtExpiration())
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        log.info("Token refresh request");

        String username = jwtService.extractUsername(request.getRefreshToken());

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        UserDetails userDetails = createUserDetails(user);

        if (!jwtService.isTokenValid(request.getRefreshToken(), userDetails)) {
            throw new BadCredentialsException("Invalid or expired refresh token");
        }

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
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
        log.info("User logged out: {}", username);

        userRepository.findByUsername(username).ifPresent(user ->
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

        if (attempts >= 5) {
            user.setLockedUntil(Instant.now().plusSeconds(15 * 60));
            log.warn("Account locked due to {} failed login attempts: {}", attempts, user.getUsername());
        }

        userRepository.save(user);
    }

    private UserDetails createUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                user.getStatus() == User.UserStatus.ACTIVE,
                true,
                true,
                user.getLockedUntil() == null || user.getLockedUntil().isBefore(Instant.now()),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
