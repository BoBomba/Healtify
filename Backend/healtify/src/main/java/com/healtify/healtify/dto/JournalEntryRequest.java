package com.healtify.healtify.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Dane wpisu przyslane przez pacjenta. Swiadomie NIE ma tu pola z id uzytkownika ani id wpisu -
 * wlasciciel jest brany z tokenu JWT, wiec klient nie moze pisac "na kogos innego".
 */
public class JournalEntryRequest {

    @NotBlank(message = "Tytul wpisu jest wymagany")
    @Size(max = 120, message = "Tytul moze miec maksymalnie 120 znakow")
    private String title;

    @Size(max = 5000, message = "Opis moze miec maksymalnie 5000 znakow")
    private String description;

    @NotNull(message = "Data i godzina wpisu sa wymagane")
    private LocalDateTime entryAt;

    @NotNull(message = "Samopoczucie jest wymagane")
    @Min(value = 1, message = "Samopoczucie musi byc w skali 1-5")
    @Max(value = 5, message = "Samopoczucie musi byc w skali 1-5")
    private Integer moodScale;

    // Puste/duplikaty odsiewa normalizacja w kontrolerze - tu pilnujemy tylko rozmiarow.
    @Size(max = 30, message = "Mozna dodac maksymalnie 30 objawow")
    private List<@Size(max = 60, message = "Objaw moze miec maksymalnie 60 znakow") String> symptoms;

    private boolean reminder;

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

    public Integer getMoodScale() {
        return moodScale;
    }

    public void setMoodScale(Integer moodScale) {
        this.moodScale = moodScale;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public boolean isReminder() {
        return reminder;
    }

    public void setReminder(boolean reminder) {
        this.reminder = reminder;
    }
}
