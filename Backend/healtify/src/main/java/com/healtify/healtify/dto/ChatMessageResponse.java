package com.healtify.healtify.dto;

import com.healtify.healtify.models.ChatMessage;
import com.healtify.healtify.models.MessageSender;

import java.time.LocalDateTime;

/**
 * Jeden dymek czatu. sharingId jedzie w kazdej wiadomosci, bo push po WebSockecie
 * leci na jedna kolejke uzytkownika (/user/queue/chat) i odbiorca musi wiedziec,
 * do ktorej rozmowy nalezy - czat filtruje po nim, a lista rozmow podbija na nim licznik.
 */
public record ChatMessageResponse(
        Long messageId,
        Long sharingId,
        MessageSender sender,
        String content,
        LocalDateTime sentAt,
        LocalDateTime readAt
) {
    /** Tresc jest juz odszyfrowana - deszyfrowanie robi ChatService przez MessageCryptoService. */
    public static ChatMessageResponse of(ChatMessage message, String decryptedContent) {
        return new ChatMessageResponse(
                message.getMessageId(),
                message.getSharing().getSharingId(),
                message.getSender(),
                decryptedContent,
                message.getSentAt(),
                message.getReadAt()
        );
    }
}
