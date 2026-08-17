package com.healtify.healtify.dto;

import com.healtify.healtify.models.MessageSender;

import java.time.LocalDateTime;

/**
 * Rozmowa widziana z listy (panel udostepniania). Jeden ksztalt dla obu stron - pole myRole
 * mowi, kim w niej jestem, wiec front wie, ktorym id (doctorId czy patientId) dopasowac
 * rozmowe do wiersza na swojej liscie i jaki kolor ma miec przycisk wyslania.
 *
 * @param partnerName z kim pisze - nazwa lekarza albo nazwa konta pacjenta
 */
public record ChatConversationResponse(
        Long sharingId,
        MessageSender myRole,
        Long doctorId,
        Long patientId,
        String partnerName,
        int unreadCount,
        LocalDateTime lastMessageAt
) {
}
