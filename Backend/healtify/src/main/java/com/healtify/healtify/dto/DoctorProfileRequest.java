package com.healtify.healtify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Dane, ktore lekarz uzupelnia u siebie. Imie i nazwisko jest wymagane, 
 * reszta jest opcjonalna - formularz mozna zapisac czesciowo i wrocic do niego pozniej.
 */
public record DoctorProfileRequest(
        @NotBlank(message = "Imię i nazwisko jest wymagane")
        @Size(max = 120, message = "Imię i nazwisko może mieć najwyżej 120 znaków")
        String doctorName,

        @Size(max = 60, message = "Tytuł zawodowy może mieć najwyżej 60 znaków")
        String title,

        @Size(max = 120, message = "Specjalizacja może mieć najwyżej 120 znaków")
        String specialization,

        // PWZ w Polsce to 7 cyfr. Dopuszczamy puste, ale nie byle co.
        @Pattern(regexp = "^$|^[0-9]{7}$", message = "Numer PWZ składa się z 7 cyfr")
        String licenseNumber,

        @Size(max = 160, message = "Nazwa placówki może mieć najwyżej 160 znaków")
        String workplace,

        @Size(max = 200, message = "Adres może mieć najwyżej 200 znaków")
        String workAddress,

        @Pattern(
                regexp = "^$|^[0-9+ ()-]{6,20}$",
                message = "Telefon może zawierać tylko cyfry, spacje i znaki + ( ) -"
        )
        String phone
) {
}
