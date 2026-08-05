package com.healtify.healtify.repository;

import com.healtify.healtify.models.JournalEntry;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    /**
     * Wpisy jednego uzytkownika. Kazde zapytanie o wpisy MUSI filtrowac po wlascicielu,
     * zeby pacjent nie widzial cudzych danych.
     */
    List<JournalEntry> findByUserAccountOrderByEntryAtAsc(UserAccount userAccount);

    long countByUserAccount(UserAccount userAccount);
}
