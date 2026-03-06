package com.geneinator.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CookieService")
class CookieServiceTest {

    @Nested
    @DisplayName("Access Token Cookie")
    class AccessTokenCookie {

        @Test
        @DisplayName("should create HttpOnly cookie with correct path and maxAge")
        void shouldCreateAccessTokenCookie() {
            CookieService cookieService = new CookieService(86400000L, 604800000L, false);

            ResponseCookie cookie = cookieService.createAccessTokenCookie("test-token");

            assertThat(cookie.getName()).isEqualTo("ACCESS_TOKEN");
            assertThat(cookie.getValue()).isEqualTo("test-token");
            assertThat(cookie.getPath()).isEqualTo("/api");
            assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(86400);
            assertThat(cookie.isHttpOnly()).isTrue();
            assertThat(cookie.isSecure()).isFalse();
            assertThat(cookie.getSameSite()).isEqualTo("Lax");
        }

        @Test
        @DisplayName("should set Secure flag when configured for production")
        void shouldSetSecureFlagInProd() {
            CookieService cookieService = new CookieService(86400000L, 604800000L, true);

            ResponseCookie cookie = cookieService.createAccessTokenCookie("test-token");

            assertThat(cookie.isSecure()).isTrue();
        }

        @Test
        @DisplayName("should create clear cookie with maxAge 0")
        void shouldCreateClearAccessTokenCookie() {
            CookieService cookieService = new CookieService(86400000L, 604800000L, false);

            ResponseCookie cookie = cookieService.clearAccessTokenCookie();

            assertThat(cookie.getName()).isEqualTo("ACCESS_TOKEN");
            assertThat(cookie.getValue()).isEmpty();
            assertThat(cookie.getMaxAge().getSeconds()).isZero();
            assertThat(cookie.getPath()).isEqualTo("/api");
        }
    }

    @Nested
    @DisplayName("Refresh Token Cookie")
    class RefreshTokenCookie {

        @Test
        @DisplayName("should create HttpOnly cookie with refresh path and maxAge")
        void shouldCreateRefreshTokenCookie() {
            CookieService cookieService = new CookieService(86400000L, 604800000L, false);

            ResponseCookie cookie = cookieService.createRefreshTokenCookie("refresh-token");

            assertThat(cookie.getName()).isEqualTo("REFRESH_TOKEN");
            assertThat(cookie.getValue()).isEqualTo("refresh-token");
            assertThat(cookie.getPath()).isEqualTo("/api/auth/refresh");
            assertThat(cookie.getMaxAge().getSeconds()).isEqualTo(604800);
            assertThat(cookie.isHttpOnly()).isTrue();
            assertThat(cookie.getSameSite()).isEqualTo("Lax");
        }

        @Test
        @DisplayName("should create clear cookie with maxAge 0")
        void shouldCreateClearRefreshTokenCookie() {
            CookieService cookieService = new CookieService(86400000L, 604800000L, false);

            ResponseCookie cookie = cookieService.clearRefreshTokenCookie();

            assertThat(cookie.getName()).isEqualTo("REFRESH_TOKEN");
            assertThat(cookie.getValue()).isEmpty();
            assertThat(cookie.getMaxAge().getSeconds()).isZero();
            assertThat(cookie.getPath()).isEqualTo("/api/auth/refresh");
        }
    }
}
