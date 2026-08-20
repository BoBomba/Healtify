package com.healtify.healtify.dto;

import com.healtify.healtify.models.UserProfile;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Szczegolowe dane pacjenta w postaci, w jakiej czyta je front.
 *
 * "completed" - czy uzytkownik przeszedl juz przez formularz. 
 */
public record PatientProfileResponse(
        String fullName,
        LocalDate dateOfBirth,
        Integer age,
        String gender,
        String phone,
        Integer heightCm,
        BigDecimal weightKg,
        BigDecimal bmi,
        String bloodType,
        String allergies,
        String chronicDiseases,
        String medications,
        boolean completed
) {
    public static PatientProfileResponse from(UserProfile profile) {
        return new PatientProfileResponse(
                profile.getFullName(),
                profile.getDateOfBirth(),
                profile.getAge(),
                profile.getGender(),
                profile.getPhone(),
                profile.getHeightCm(),
                profile.getWeightKg(),
                profile.getBmi(),
                profile.getBloodType(),
                profile.getAllergies(),
                profile.getChronicDiseases(),
                profile.getMedications(),
                true
        );
    }

    /** Odpowiedz dla kogos, kto nie wypelnil jeszcze formularza. */
    public static PatientProfileResponse empty() {
        return new PatientProfileResponse(
                null, null, null, null, null, null, null, null, null, null, null, null, false
        );
    }
}
