package com.healtify.healtify.dto;

import com.healtify.healtify.models.UserAccount;

/**
 * Pacjent widziany przez lekarza. Swiadomie tylko dane kontaktowe -
 * dane psychiczne pacjenta nie wychodza przy zadnej z list w panelu lekarza.
 */
public record PatientResponse(
        Long userId,
        String username,
        String email
) {
    public static PatientResponse from(UserAccount user) {
        return new PatientResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail()
        );
    }
}
