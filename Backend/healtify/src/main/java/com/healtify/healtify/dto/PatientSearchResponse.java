package com.healtify.healtify.dto;

import com.healtify.healtify.models.SharingInitiator;
import com.healtify.healtify.models.SharingStatus;
import com.healtify.healtify.models.UserAccount;

/**
 * Wynik wyszukiwarki pacjentow. Poza samym pacjentem niesie stan powiazania z pytajacym
 * lekarzem, zeby front mogl wyszarzyc "Zapros" u kogos, kto juz jest pacjentem
 * albo ma wiszace zaproszenie.
 */
public record PatientSearchResponse(
        Long userId,
        String username,
        String email,
        SharingStatus status,
        SharingInitiator initiatedBy
) {
    public static PatientSearchResponse from(UserAccount user, SharingStatus status, SharingInitiator initiatedBy) {
        return new PatientSearchResponse(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                status,
                initiatedBy
        );
    }
}
