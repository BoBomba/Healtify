package com.healtify.healtify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Dane profilu lekarza zakladanego przez admina razem z nadaniem roli ROLE_DOCTOR. */
public class GrantDoctorRequest {

    @NotBlank(message = "Imie i nazwisko lekarza jest wymagane")
    @Size(max = 120, message = "Imie i nazwisko moze miec maksymalnie 120 znakow")
    private String doctorName;

    @Size(max = 120, message = "Specjalizacja moze miec maksymalnie 120 znakow")
    private String specialization;

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
