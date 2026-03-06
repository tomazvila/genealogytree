package com.geneinator.controller;

import com.geneinator.dto.auth.*;
import com.geneinator.entity.User;
import com.geneinator.repository.UserRepository;
import com.geneinator.security.CookieService;
import com.geneinator.security.JwtService;
import com.geneinator.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieService cookieService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserInfoResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse loginResponse = authService.login(request);

        UserInfoResponse userInfo = UserInfoResponse.builder()
                .userId(loginResponse.getUserId())
                .username(loginResponse.getUsername())
                .role(loginResponse.getRole())
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(loginResponse.getAccessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(loginResponse.getRefreshToken()).toString())
                .body(userInfo);
    }

    @PostMapping("/refresh")
    public ResponseEntity<UserInfoResponse> refreshToken(HttpServletRequest request) {
        String refreshToken = extractCookieValue(request, "REFRESH_TOKEN");
        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        RefreshTokenRequest refreshRequest = RefreshTokenRequest.builder()
                .refreshToken(refreshToken)
                .build();

        TokenResponse tokenResponse = authService.refreshToken(refreshRequest);

        String username = jwtService.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username).orElse(null);

        UserInfoResponse userInfo = user != null
                ? UserInfoResponse.builder()
                    .userId(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .build()
                : UserInfoResponse.builder().build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieService.createAccessTokenCookie(tokenResponse.getAccessToken()).toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.createRefreshTokenCookie(tokenResponse.getRefreshToken()).toString())
                .body(userInfo);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String accessToken = extractCookieValue(request, "ACCESS_TOKEN");
        if (accessToken != null) {
            authService.logout("Bearer " + accessToken);
        }

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookieService.clearAccessTokenCookie().toString())
                .header(HttpHeaders.SET_COOKIE, cookieService.clearRefreshTokenCookie().toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).build();
        }

        String username = auth.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        UserInfoResponse userInfo = UserInfoResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();

        return ResponseEntity.ok(userInfo);
    }

    private String extractCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
