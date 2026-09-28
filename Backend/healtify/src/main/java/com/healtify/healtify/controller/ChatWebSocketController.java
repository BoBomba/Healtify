package com.healtify.healtify.controller;

import com.healtify.healtify.dto.SendMessageRequest;
import com.healtify.healtify.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.Map;

/**
 * Wejscie WebSocketowe czatu.
 *
 * Klient wysyla wiadomosc na /app/chat.send, a serwis rozsyla ja na prywatne kolejki
 * obu stron (/user/queue/chat) - patrz ChatService#send. Metoda nic nie zwraca, bo push
 * i tak trafi do nadawcy razem z odbiorca; dzieki temu wiadomosc wyglada tak samo
 * niezaleznie od tego, czy przyszla z wlasnej karty, czy od drugiej strony.
 *
 * Principal jest tu podpiety przy CONNECT przez WebSocketAuthChannelInterceptor - to on,
 * a nie tresc ramki, decyduje, kim jest nadawca.
 */
@Controller
public class ChatWebSocketController {

    private final ChatService chatService;

    public ChatWebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat.send")
    public void send(SendMessageRequest request, Principal principal) {
        if (request == null || request.getSharingId() == null) {
            throw new IllegalArgumentException("Brak id rozmowy");
        }
        chatService.send(
                principal == null ? null : principal.getName(),
                request.getSharingId(),
                request.getContent()
        );
    }

    /**
     * Blad przy wysylce po WebSockecie nie ma gdzie wrocic jako kod HTTP - odsylamy go
     * na prywatna kolejke bledow nadawcy, zeby front mogl pokazac komunikat.
     */
    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public Map<String, String> onError(Exception exception) {
        // ChatService rzuca ResponseStatusException (bo ten sam kod obsluguje REST-a);
        // getMessage() dokleilby do tresci kod HTTP, wiec bierzemy sam powod.
        String reason = exception instanceof ResponseStatusException failure
                ? failure.getReason()
                : exception.getMessage();
        return Map.of("message", reason == null ? "Nie udalo sie wyslac wiadomosci" : reason);
    }
}
