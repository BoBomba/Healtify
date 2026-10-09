package com.healtify.healtify.security;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Uwierzytelnienie polaczenia WebSocketowego.
 *
 * Handshake to zwykle zadanie HTTP, wiec przegladarka sama dolacza ciasteczko access_token,
 * a JwtAuthFilter ustala na nim tozsamosc (bez niej SecurityConfig odrzuca handshake 401).
 * Spring przypina tego Principala do sesji STOMP - on decyduje pozniej, czyja jest kolejka
 * /user/queue/chat i kim jest nadawca w ChatService.
 *
 * Tutaj tylko druga linia obrony: CONNECT bez Principala konczy sie bledem.
 */
@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        if (!(accessor.getUser() instanceof Authentication auth) || !auth.isAuthenticated()) {
            throw new IllegalArgumentException("Brak uwierzytelnienia");
        }
        return message;
    }
}
