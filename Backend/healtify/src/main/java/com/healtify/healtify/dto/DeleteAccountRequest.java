package com.healtify.healtify.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Potwierdzenie kasowania wlasnego konta.
 *
 * Sam token nie wystarcza: zyje 24h i lezy w localStorage, 
 * Haslo pozwala, ze operacje zleca wlasciciel konta, a nie ktokolwiek z dostepem do otwartej sesji.
 */
public class DeleteAccountRequest {

    @NotBlank(message = "Haslo jest wymagane")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
