package com.healtify.healtify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.healtify.healtify.models.converter.StringListConverter;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Jedyna tabela z danymi pacjenta: wpisy do dziennika.
 * Wizyty u psychologa beda osobnym bytem (nie zaklada ich pacjent), wiec nie ma tu typu wpisu.
 */
@Entity
@Table(name = "journal_entries", indexes = {
        @Index(name = "idx_journal_user_entry_at", columnList = "user_id, entry_at")
})
public class JournalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long entryId;

    /**
     * Wlasciciel wpisu. Ustawiany WYLACZNIE na podstawie zalogowanego uzytkownika
     * (nigdy z danych przyslanych przez klienta) - patrz JournalController.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Data i godzina, ktorej dotyczy wpis (to po niej kalendarz grupuje wpisy w dni). */
    @Column(name = "entry_at", nullable = false)
    private LocalDateTime entryAt;

    /** Samopoczucie w skali 1-5. */
    @Column(name = "mood_scale", nullable = false)
    private int moodScale;

    /** Lista objawow trzymana w jednej kolumnie TEXT, zeby nie rozbijac danych na kolejna tabele. */
    @Convert(converter = StringListConverter.class)
    @Column(name = "symptoms", columnDefinition = "TEXT")
    private List<String> symptoms = new ArrayList<>();

    @Column(name = "reminder", nullable = false)
    private boolean reminder;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and setters

    public Long getEntryId() {
        return entryId;
    }

    public void setEntryId(Long entryId) {
        this.entryId = entryId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getEntryAt() {
        return entryAt;
    }

    public void setEntryAt(LocalDateTime entryAt) {
        this.entryAt = entryAt;
    }

    public int getMoodScale() {
        return moodScale;
    }

    public void setMoodScale(int moodScale) {
        this.moodScale = moodScale;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms == null ? new ArrayList<>() : symptoms;
    }

    public boolean isReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // constructors

    public JournalEntry() {
    }
}
