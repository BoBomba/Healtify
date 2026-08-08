package com.healtify.healtify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Wizyta pacjenta u lekarza. W przeciwienstwie do wpisow w dzienniku zaklada ja
 * WYLACZNIE lekarz i tylko pacjentowi, z ktorym ma zaakceptowane powiazanie
 * (patrz DataSharing) - pacjent widzi wizyte u siebie tylko do odczytu.
 */
@Entity
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointment_doctor_at", columnList = "doctor_id, appointment_at"),
        @Index(name = "idx_appointment_user_at", columnList = "user_id, appointment_at")
})
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    /** Pacjent. Ustawiany na podstawie powiazania lekarza, nigdy z surowego id od klienta. */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount patient;

    /** Data i godzina wizyty - to po niej kalendarz grupuje wizyty w dni. */
    @Column(name = "appointment_at", nullable = false)
    private LocalDateTime appointmentAt;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and setters

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public UserAccount getPatient() {
        return patient;
    }

    public void setPatient(UserAccount patient) {
        this.patient = patient;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // constructors

    public Appointment() {
    }
}
