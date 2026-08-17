package com.healtify.healtify.security;

import com.healtify.healtify.security.service.JwtService;
import com.healtify.healtify.security.token.TokenRepository;
import io.jsonwebtoken.JwtException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Uwierzytelnienie polaczenia WebSocketowego - odpowiednik JwtAuthFilter dla STOMP.
 *
 * Handshake WebSocketa idzie z przegladarki bez naglowka Authorization (WebSocket API nie
 * pozwala go ustawic), dlatego token jedzie w ramce CONNECT, a nie w zadaniu HTTP. Tutaj jest
 * jedyne miejsce, w ktorym gniazdo dostaje tozsamosc: ustawiony Principal decyduje pozniej,
 * czyja jest kolejka /user/queue/chat i kim jest nadawca w ChatService.
 *
 * Bez waznego tokenu CONNECT konczy sie bledem, wiec anonimowe gniazdo w ogole nie powstaje.
 */
@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;

    public WebSocketAuthChannelInterceptor(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            TokenRepository tokenRepository
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // Tozsamosc ustalamy raz, przy CONNECT. Kolejne ramki tej samej sesji maja
        // Principala juz przypietego przez Springa.
        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        accessor.setUser(authenticate(accessor.getFirstNativeHeader("Authorization")));
        return message;
    }

    private UsernamePasswordAuthenticationToken authenticate(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Brak tokenu");
        }
        String jwt = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(jwt);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Ten sam warunek co w JwtAuthFilter: token musi byc podpisany, niewygasly
            // i nadal aktywny w tabeli token (wylogowanie uniewaznia go od razu).
            boolean stillActive = tokenRepository.findByToken(jwt)
                    .map(token -> !token.isExpired() && !token.isRevoked())
                    .orElse(false);

            if (!stillActive || !jwtService.isTokenValid(jwt, userDetails)) {
                throw new IllegalArgumentException("Token odrzucony");
            }
            return new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
            // Wyjatek na CONNECT = gniazdo nie powstaje. Klient dostaje ramke ERROR i sprobuje
            // ponownie po odswiezeniu tokenu.
            throw new IllegalArgumentException("Nieprawidlowy token");
        }
    }
}
