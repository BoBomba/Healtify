package com.healtify.healtify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Nowa wizyta zakladana przez lekarza. Lekarz jest brany z tokenu, a patientId jest
 * sprawdzane wzgledem jego zaakceptowanych pacjentow - nie da sie umowic obcej osoby.
 */
public class AppointmentRequest {

    @NotNull(message = "Pacjent jest wymagany")
    private Long patientId;

    @NotNull(message = "Data i godzina wizyty sa wymagane")
    private LocalDateTime appointmentAt;

    @NotBlank(message = "Tytul wizyty jest wymagany")
    @Size(max = 120, message = "Tytul moze miec maksymalnie 120 znakow")
    private String title;

    @Size(max = 5000, message = "Notatka moze miec maksymalnie 5000 znakow")
    private String notes;

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public LocalDateTime getAppointmentAt() {
        return appointmentAt;
    }

    public void setAppointmentAt(LocalDateTime appointmentAt) {
        this.appointmentAt = appointmentAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
