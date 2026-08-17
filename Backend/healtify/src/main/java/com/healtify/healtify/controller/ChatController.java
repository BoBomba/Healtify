package com.healtify.healtify.controller;

import com.healtify.healtify.dto.ChatConversationResponse;
import com.healtify.healtify.dto.ChatMessageResponse;
import com.healtify.healtify.dto.ChatPageResponse;
import com.healtify.healtify.dto.SendMessageRequest;
import com.healtify.healtify.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Czat pacjent - lekarz, czesc REST-owa: historia, wysylanie awaryjne i liczniki.
 * Wiadomosci na zywo chodza WebSocketem (w ChatWebSocketController) - tutaj jest
 * to, co WebSocket robi zle albo wcale: pobranie historii ze stronicowaniem i stan poczatkowy.
 *
 * Dostepu pilnuje ChatService#access - kazdy endpoint zaczyna sie od tego samego sprawdzenia.
 */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Rozmowy zalogowanego uzytkownika - panel udostepniania bierze stad sharingId do linku
     * i liczbe nieprzeczytanych na kropke przy nazwisku.
     */
    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversationResponse>> getConversations(Principal principal) {
        return ResponseEntity.ok(chatService.conversations(name(principal)));
    }

    // Naglowek czatu: z kim pisze i ktora strona rozmowy jestem.
    @GetMapping("/{sharingId}")
    public ResponseEntity<ChatConversationResponse> getConversation(
            @PathVariable Long sharingId,
            Principal principal
    ) {
        return ResponseEntity.ok(chatService.describe(name(principal), sharingId));
    }

    /**
     * Historia chatu: Bez {@code before} - najnowsze wiadomosci,
     * z {@code before=<messageId>} - starsze od tej, ktora jest juz na gorze listy.
     */
    @GetMapping("/{sharingId}/messages")
    public ResponseEntity<ChatPageResponse> getMessages(
            @PathVariable Long sharingId,
            @RequestParam(name = "before", required = false) Long before,
            @RequestParam(name = "limit", required = false) Integer limit,
            Principal principal
    ) {
        return ResponseEntity.ok(chatService.loadHistory(name(principal), sharingId, before, limit));
    }

    /**
     * Wyslanie wiadomosci po HTTP. Normalna droga to WebSocket - endpoint jest zapasem
     * w razie zerwanego polaczenia, zeby wiadomosc nie przepadla razem z gniazdem.
     * Obie drogi wchodza w ten sam ChatService#send, wiec reguly sa identyczne.
     */
    @PostMapping("/{sharingId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long sharingId,
            @Valid @RequestBody SendMessageRequest request,
            Principal principal
    ) {
        ChatMessageResponse message = chatService.send(name(principal), sharingId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    // Otwarcie rozmowy zeruje licznik nieprzeczytanych po stronie czytajacego.
    @PostMapping("/{sharingId}/read")
    public ResponseEntity<Void> markRead(@PathVariable Long sharingId, Principal principal) {
        chatService.markRead(name(principal), sharingId);
        return ResponseEntity.noContent().build();
    }

    private String name(Principal principal) {
        return principal == null ? null : principal.getName();
    }
}
