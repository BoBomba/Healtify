package com.healtify.healtify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Zgoda pacjenta na pokazanie JEDNEGO wpisu z dziennika JEDNEMU lekarzowi.
 *
 * Dziennik jest domyslnie prywatny - data_sharing daje lekarzowi dostep tylko do
 * danych profilowych pacjenta. 
 * Kazdy wpis pacjent udostepnia osobno i osobno moze cofnac
 */
@Entity
@Table(
        name = "journal_entry_shares",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_entry_share_entry_doctor",
                columnNames = {"entry_id", "doctor_id"}
        ),
        indexes = @Index(name = "idx_entry_share_doctor", columnList = "doctor_id")
)
public class JournalEntryShare {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "share_id")
    private Long shareId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "entry_id", nullable = false)
    private JournalEntry journalEntry;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "shared_at", nullable = false)
    private LocalDateTime sharedAt;

    public JournalEntryShare() {
    }

    public JournalEntryShare(JournalEntry journalEntry, Doctor doctor) {
        this.journalEntry = journalEntry;
        this.doctor = doctor;
        this.sharedAt = LocalDateTime.now();
    }

    // Getters and setters

    public Long getShareId() {
        return shareId;
    }

    public void setShareId(Long shareId) {
        this.shareId = shareId;
    }

    public JournalEntry getJournalEntry() {
        return journalEntry;
    }

    public void setJournalEntry(JournalEntry journalEntry) {
        this.journalEntry = journalEntry;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public LocalDateTime getSharedAt() {
        return sharedAt;
    }

    public void setSharedAt(LocalDateTime sharedAt) {
        this.sharedAt = sharedAt;
    }
}
