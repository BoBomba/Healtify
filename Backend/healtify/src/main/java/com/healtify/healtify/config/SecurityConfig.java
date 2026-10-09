package com.healtify.healtify.config;


import com.healtify.healtify.security.AuthCookies;
import com.healtify.healtify.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] WHITE_LIST_URL = {
        "/",
        "/api/auth/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/error"
    };


    private final JwtAuthFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;
    private final AuthCookies authCookies;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // CORS w łańcuchu security, żeby odpowiedzi 401/403 też miały nagłówki CORS
                // (inaczej front widzi błąd sieci zamiast statusu i nie odświeży sesji).
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository())
                        .csrfTokenRequestHandler(csrfRequestHandler())
                )
                .authorizeHttpRequests(req ->
                        req.requestMatchers("/api/auth/validate")
                                .authenticated()
                                .requestMatchers(WHITE_LIST_URL)
                                .permitAll()
                                //.requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())
                                //.requestMatchers(GET, "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                                //.requestMatchers(POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                                //.requestMatchers(PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                                //.requestMatchers(DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())
                                .anyRequest()
                                .authenticated()
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(unauthenticatedEntryPoint()))
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> {
                                    SecurityContextHolder.clearContext();
                                    response.setStatus(HttpStatus.NO_CONTENT.value());
                                })
                )
                .build();
    }

    /**
     * Double-submit: token CSRF w ciasteczku XSRF-TOKEN (czytelnym dla JS), a axios odsyła go
     * w nagłówku X-XSRF-TOKEN. Obca strona nie odczyta ciasteczka, więc nie ustawi nagłówka.
     */
    private CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(cookie -> cookie.sameSite("Strict").secure(authCookies.isSecure()));
        return repository;
    }

    // Surowy token zamiast maskowanego (XOR) i ładowany przy każdym żądaniu,
    // żeby ciasteczko było ustawione zanim SPA wyśle pierwszy POST.
    private CsrfTokenRequestAttributeHandler csrfRequestHandler() {
        CsrfTokenRequestAttributeHandler handler = new CsrfTokenRequestAttributeHandler();
        handler.setCsrfRequestAttributeName(null);
        return handler;
    }

    // 401 z tym body = brak ważnego access tokena, sygnał dla frontu do odświeżenia sesji.
    // Zwykłe 401 z kontrolerów (np. złe hasło) go nie mają.
    private AuthenticationEntryPoint unauthenticatedEntryPoint() {
        return (request, response, exception) -> {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("{\"error\":\"unauthenticated\"}");
        };
    }

    //args constructor
    public SecurityConfig(JwtAuthFilter jwtAuthFilter, AuthenticationProvider authenticationProvider, LogoutHandler logoutHandler, AuthCookies authCookies) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
        this.logoutHandler = logoutHandler;
        this.authCookies = authCookies;
    }

}
