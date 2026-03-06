package com.geneinator.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieService {

    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;
    private final boolean secure;

    public CookieService(
            @Value("${app.jwt.expiration-ms}") long accessTokenExpirationMs,
            @Value("${app.jwt.refresh-expiration-ms}") long refreshTokenExpirationMs,
            @Value("${app.cookie.secure:false}") boolean secure) {
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
        this.secure = secure;
    }

    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("ACCESS_TOKEN", token)
                .httpOnly(true)
                .secure(secure)
                .path("/api")
                .maxAge(Duration.ofMillis(accessTokenExpirationMs))
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("REFRESH_TOKEN", token)
                .httpOnly(true)
                .secure(secure)
                .path("/api/auth/refresh")
                .maxAge(Duration.ofMillis(refreshTokenExpirationMs))
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie clearAccessTokenCookie() {
        return ResponseCookie.from("ACCESS_TOKEN", "")
                .httpOnly(true)
                .secure(secure)
                .path("/api")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }

    public ResponseCookie clearRefreshTokenCookie() {
        return ResponseCookie.from("REFRESH_TOKEN", "")
                .httpOnly(true)
                .secure(secure)
                .path("/api/auth/refresh")
                .maxAge(0)
                .sameSite("Lax")
                .build();
    }
}
