package com.healtify.healtify.dto;

import com.healtify.healtify.models.JournalEntry;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Wpis zwracany na front. Nie zawiera niczego o koncie uzytkownika -
 * pacjent i tak dostaje tylko swoje wpisy, wiec nie ma po co wysylac danych konta.
 *
 * sharedWithDoctorIds mowi, ktorym lekarzom pacjent udostepnil ten wpis. 
 * Idzie razem z lista, zeby przycisk "Udostepnij" znal swoj stan od razu po wejsciu.
 */
public record JournalEntryResponse(
        Long entryId,
        String title,
        String description,
        LocalDateTime entryAt,
        int moodScale,
        List<String> symptoms,
        boolean reminder,
        LocalDateTime createdAt,
        List<Long> sharedWithDoctorIds
) {
    /** Wersja dla lekarza i wszedzie tam, gdzie stan udostepnien nie jest potrzebny. */
    public static JournalEntryResponse from(JournalEntry entry) {
        return from(entry, List.of());
    }

    public static JournalEntryResponse from(JournalEntry entry, List<Long> sharedWithDoctorIds) {
        return new JournalEntryResponse(
                entry.getEntryId(),
                entry.getTitle(),
                entry.getDescription(),
                entry.getEntryAt(),
                entry.getMoodScale(),
                List.copyOf(entry.getSymptoms()),
                entry.isReminder(),
                entry.getCreatedAt(),
                List.copyOf(sharedWithDoctorIds)
        );
    }
}
