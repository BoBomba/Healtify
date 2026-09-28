package com.healtify.healtify.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Zapis szczegolowych danych pacjenta. Wszystkie pola sa opcjonalne, 
 * walidujemy tylko zakresy, zeby do bazy nie trafil wzrost 5 m albo data urodzenia z przyszlosci.
 */
public record PatientProfileRequest(
        @Size(max = 255, message = "Imię i nazwisko może mieć najwyżej 255 znaków")
        String fullName,

        @Past(message = "Data urodzenia musi być z przeszłości")
        LocalDate dateOfBirth,

        @Size(max = 255, message = "Płeć może mieć najwyżej 255 znaków")
        String gender,

        @Pattern(
                regexp = "^$|^[0-9+ ()-]{6,20}$",
                message = "Telefon może zawierać tylko cyfry, spacje i znaki + ( ) -"
        )
        String phone,

        @Min(value = 50, message = "Wzrost musi być z zakresu 50-260 cm")
        @Max(value = 260, message = "Wzrost musi być z zakresu 50-260 cm")
        Integer heightCm,

        @DecimalMin(value = "1.0", message = "Waga musi być z zakresu 1-500 kg")
        @DecimalMax(value = "500.0", message = "Waga musi być z zakresu 1-500 kg")
        BigDecimal weightKg,

        @Size(max = 255, message = "Grupa krwi może mieć najwyżej 255 znaków")
        String bloodType,

        @Size(max = 2000, message = "Alergie mogą mieć najwyżej 2000 znaków")
        String allergies,

        @Size(max = 2000, message = "Choroby przewlekłe mogą mieć najwyżej 2000 znaków")
        String chronicDiseases,

        @Size(max = 2000, message = "Przyjmowane leki mogą mieć najwyżej 2000 znaków")
        String medications
) {
}
