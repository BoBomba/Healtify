package com.healtify.healtify.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.time.Duration;
import java.util.Optional;

/**
 * Ciasteczka z tokenami. HttpOnly - JS na froncie ich nie odczyta,
 * SameSite=Strict - przeglądarka nie dołączy ich do żądań z obcych stron.
 */
@Component
public class AuthCookies {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    // Refresh leci tylko na /api/auth/** (odświeżenie i wylogowanie), nie przy każdym żądaniu.
    private static final String REFRESH_PATH = "/api/auth";

    @Value("${application.security.jwt.expiration}")
    private long accessExpiration;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    // application.yml jest w .gitignore - domyślnie true, żeby brak wpisu nie wywalał startu.
    @Value("${application.security.cookie.secure:true}")
    private boolean secure;

    public boolean isSecure() {
        return secure;
    }

    public void write(HttpServletResponse response, String accessToken, String refreshToken) {
        add(response, ACCESS_TOKEN, accessToken, "/", Duration.ofMillis(accessExpiration));
        add(response, REFRESH_TOKEN, refreshToken, REFRESH_PATH, Duration.ofMillis(refreshExpiration));
    }

    public void clear(HttpServletResponse response) {
        add(response, ACCESS_TOKEN, "", "/", Duration.ZERO);
        add(response, REFRESH_TOKEN, "", REFRESH_PATH, Duration.ZERO);
    }

    public static Optional<String> read(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        return Optional.ofNullable(cookie)
                .map(Cookie::getValue)
                .filter(value -> !value.isBlank());
    }

    private void add(HttpServletResponse response, String name, String value, String path, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path(path)
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
