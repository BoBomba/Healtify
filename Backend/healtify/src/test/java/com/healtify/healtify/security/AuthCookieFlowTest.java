package com.healtify.healtify.security;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * Uwierzytelnianie na ciasteczkach: logowanie, CSRF, refresh z rotacją,
 * wylogowanie, zmiana hasła i handshake WebSocketa.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthCookieFlowTest {

    private static final String UNAUTHENTICATED = "{\"error\":\"unauthenticated\"}";

    @Autowired
    private MockMvc mvc;

    @LocalServerPort
    private int port;

    private Browser browser;
    private String email;
    private String password;

    /** Zachowuje się jak przeglądarka + axios: trzyma ciasteczka i odsyła token CSRF w nagłówku. */
    private class Browser {
        final Map<String, String> cookies = new LinkedHashMap<>();

        MvcResult send(MockHttpServletRequestBuilder request) throws Exception {
            cookies.forEach((name, value) -> request.cookie(new Cookie(name, value)));
            if (cookies.containsKey("XSRF-TOKEN")) {
                request.header("X-XSRF-TOKEN", cookies.get("XSRF-TOKEN"));
            }
            MvcResult result = mvc.perform(request).andReturn();
            for (Cookie cookie : result.getResponse().getCookies()) {
                if (cookie.getMaxAge() == 0) {
                    cookies.remove(cookie.getName());
                } else {
                    cookies.put(cookie.getName(), cookie.getValue());
                }
            }
            return result;
        }

        MvcResult sendJson(MockHttpServletRequestBuilder request, String body) throws Exception {
            return send(request.contentType(MediaType.APPLICATION_JSON).content(body));
        }

        int login(String email, String password) throws Exception {
            send(get("/api/auth/csrf"));
            return sendJson(post("/api/auth/authenticate"),
                    "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                    .getResponse().getStatus();
        }
    }

    @BeforeEach
    void registerAndLogin() throws Exception {
        // Każdy test na własnym koncie - logowanie unieważnia wcześniejsze tokeny tego konta.
        String username = "user" + UUID.randomUUID().toString().substring(0, 8);
        email = username + "@test.com";
        password = "haslo123";

        browser = new Browser();
        browser.send(get("/api/auth/csrf"));
        int registered = browser.sendJson(post("/api/auth/register"),
                "{\"username\":\"" + username + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
                .getResponse().getStatus();
        assertThat(registered).isEqualTo(200);
        assertThat(browser.login(email, password)).isEqualTo(204);
    }

    // --- logowanie ---

    @Test
    void loginSetsHttpOnlyCookiesAndReturnsNoTokenInBody() throws Exception {
        MvcResult result = new Browser() {{ send(get("/api/auth/csrf")); }}
                .sendJson(post("/api/auth/authenticate"),
                        "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}");

        assertThat(result.getResponse().getStatus()).isEqualTo(204);
        assertThat(result.getResponse().getContentAsString()).isEmpty();

        Cookie access = result.getResponse().getCookie(AuthCookies.ACCESS_TOKEN);
        Cookie refresh = result.getResponse().getCookie(AuthCookies.REFRESH_TOKEN);
        assertThat(access.isHttpOnly()).isTrue();
        assertThat(access.getSecure()).isTrue();
        assertThat(access.getAttribute("SameSite")).isEqualTo("Strict");
        assertThat(access.getPath()).isEqualTo("/");
        assertThat(refresh.isHttpOnly()).isTrue();
        assertThat(refresh.getPath()).isEqualTo("/api/auth");
    }

    @Test
    void wrongPasswordLoginIsRejected() throws Exception {
        assertThat(new Browser().login(email, "zlehaslo")).isEqualTo(401);
    }

    @Test
    void validateReturnsUsernameForLoggedInUser() throws Exception {
        MvcResult result = browser.send(get("/api/auth/validate"));

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains(email.substring(0, email.indexOf('@')));
    }

    // --- dostęp do API ---

    @Test
    void requestWithoutCookieGets401WithRefreshMarker() throws Exception {
        MvcResult result = mvc.perform(get("/api/user/me")).andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(401);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(UNAUTHENTICATED);
    }

    @Test
    void bearerHeaderIsNoLongerAccepted() throws Exception {
        String accessToken = browser.cookies.get(AuthCookies.ACCESS_TOKEN);

        int status = mvc.perform(get("/api/user/me").header("Authorization", "Bearer " + accessToken))
                .andReturn().getResponse().getStatus();

        assertThat(status).isEqualTo(401);
    }

    @Test
    void refreshTokenCannotBeUsedAsAccessToken() throws Exception {
        String refreshToken = browser.cookies.get(AuthCookies.REFRESH_TOKEN);

        int status = mvc.perform(get("/api/user/me").cookie(new Cookie(AuthCookies.ACCESS_TOKEN, refreshToken)))
                .andReturn().getResponse().getStatus();

        assertThat(status).isEqualTo(401);
    }

    // --- CSRF ---

    @Test
    void postWithoutCsrfHeaderIsRejected() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/auth/refresh-token");
        browser.cookies.forEach((name, value) -> request.cookie(new Cookie(name, value)));

        assertThat(mvc.perform(request).andReturn().getResponse().getStatus()).isEqualTo(403);
    }

    @Test
    void postWithForgedCsrfHeaderIsRejected() throws Exception {
        MockHttpServletRequestBuilder request = post("/api/auth/refresh-token").header("X-XSRF-TOKEN", "podrobiony");
        browser.cookies.forEach((name, value) -> request.cookie(new Cookie(name, value)));

        assertThat(mvc.perform(request).andReturn().getResponse().getStatus()).isEqualTo(403);
    }

    // --- refresh ---

    @Test
    void refreshRotatesBothTokens() throws Exception {
        String oldAccess = browser.cookies.get(AuthCookies.ACCESS_TOKEN);
        String oldRefresh = browser.cookies.get(AuthCookies.REFRESH_TOKEN);

        assertThat(browser.send(post("/api/auth/refresh-token")).getResponse().getStatus()).isEqualTo(204);
        assertThat(browser.cookies.get(AuthCookies.ACCESS_TOKEN)).isNotEqualTo(oldAccess);
        assertThat(browser.cookies.get(AuthCookies.REFRESH_TOKEN)).isNotEqualTo(oldRefresh);

        // Nowa para działa, stara już nie.
        assertThat(browser.send(get("/api/user/me")).getResponse().getStatus()).isEqualTo(200);
        assertThat(withAccessToken(oldAccess)).isEqualTo(401);
        assertThat(refreshWith(oldRefresh)).isEqualTo(401);
    }

    @Test
    void refreshWithoutCookieIsRejected() throws Exception {
        browser.cookies.remove(AuthCookies.REFRESH_TOKEN);

        assertThat(browser.send(post("/api/auth/refresh-token")).getResponse().getStatus()).isEqualTo(401);
    }

    // --- wylogowanie ---

    @Test
    void logoutClearsCookiesAndRevokesCopiedTokens() throws Exception {
        String access = browser.cookies.get(AuthCookies.ACCESS_TOKEN);
        String refresh = browser.cookies.get(AuthCookies.REFRESH_TOKEN);

        assertThat(browser.send(post("/api/auth/logout")).getResponse().getStatus()).isEqualTo(204);
        assertThat(browser.cookies).doesNotContainKeys(AuthCookies.ACCESS_TOKEN, AuthCookies.REFRESH_TOKEN);

        // Skopiowane wcześniej tokeny też są martwe - unieważnione w bazie, nie tylko w przeglądarce.
        assertThat(withAccessToken(access)).isEqualTo(401);
        assertThat(refreshWith(refresh)).isEqualTo(401);
    }

    @Test
    void newLoginRevokesPreviousSession() throws Exception {
        String firstAccess = browser.cookies.get(AuthCookies.ACCESS_TOKEN);

        assertThat(new Browser().login(email, password)).isEqualTo(204);

        assertThat(withAccessToken(firstAccess)).isEqualTo(401);
    }

    // --- zmiana hasła ---

    @Test
    void wrongCurrentPasswordKeepsSessionAndHasNoRefreshMarker() throws Exception {
        String access = browser.cookies.get(AuthCookies.ACCESS_TOKEN);

        MvcResult result = browser.sendJson(patch("/api/user/update-password"),
                "{\"password\":\"zlehaslo\",\"newPassword\":\"nowe12345\"}");

        assertThat(result.getResponse().getStatus()).isEqualTo(401);
        assertThat(result.getResponse().getContentAsString()).isEqualTo("Wrong password");
        assertThat(browser.cookies.get(AuthCookies.ACCESS_TOKEN)).isEqualTo(access);
        assertThat(browser.send(get("/api/user/me")).getResponse().getStatus()).isEqualTo(200);
    }

    @Test
    void passwordChangeLogsOutEverywhereAndNewPasswordWorks() throws Exception {
        String access = browser.cookies.get(AuthCookies.ACCESS_TOKEN);

        MvcResult result = browser.sendJson(patch("/api/user/update-password"),
                "{\"password\":\"" + password + "\",\"newPassword\":\"nowe12345\"}");

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(browser.cookies).doesNotContainKeys(AuthCookies.ACCESS_TOKEN, AuthCookies.REFRESH_TOKEN);
        assertThat(withAccessToken(access)).isEqualTo(401);
        assertThat(new Browser().login(email, password)).isEqualTo(401);
        assertThat(new Browser().login(email, "nowe12345")).isEqualTo(204);
    }

    // --- WebSocket ---

    @Test
    void websocketHandshakeWithoutCookieIsRejected() {
        assertThatThrownBy(() -> openWebSocket(null))
                .isInstanceOf(ExecutionException.class);
    }

    @Test
    void websocketHandshakeWithAccessCookieSucceeds() throws Exception {
        WebSocketSession session = openWebSocket(browser.cookies.get(AuthCookies.ACCESS_TOKEN));
        try {
            assertThat(session.isOpen()).isTrue();
        } finally {
            session.close();
        }
    }

    // --- pomocnicze ---

    private int withAccessToken(String accessToken) throws Exception {
        return mvc.perform(get("/api/user/me").cookie(new Cookie(AuthCookies.ACCESS_TOKEN, accessToken)))
                .andReturn().getResponse().getStatus();
    }

    private int refreshWith(String refreshToken) throws Exception {
        Browser other = new Browser();
        other.send(get("/api/auth/csrf"));
        other.cookies.put(AuthCookies.REFRESH_TOKEN, refreshToken);
        return other.send(post("/api/auth/refresh-token")).getResponse().getStatus();
    }

    private WebSocketSession openWebSocket(String accessToken) throws Exception {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.setOrigin("http://localhost:3000");
        if (accessToken != null) {
            headers.add("Cookie", AuthCookies.ACCESS_TOKEN + "=" + accessToken);
        }
        return new StandardWebSocketClient()
                .execute(new TextWebSocketHandler(), headers, URI.create("ws://localhost:" + port + "/ws"))
                .get(5, TimeUnit.SECONDS);
    }
}
