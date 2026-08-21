package com.healtify.healtify.repository;

import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.JournalEntry;
import com.healtify.healtify.models.JournalEntryShare;
import com.healtify.healtify.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface JournalEntryShareRepository extends JpaRepository<JournalEntryShare, Long> {

    /** Udostepnienia wpisow pacjenta - jednym zapytaniem dla calej listy, bez N+1. */
    List<JournalEntryShare> findByJournalEntryIn(Collection<JournalEntry> entries);

    List<JournalEntryShare> findByJournalEntry(JournalEntry entry);

    /** Wpisy, ktore dany pacjent udostepnil danemu lekarzowi - od najnowszego. */
    List<JournalEntryShare> findByDoctorAndJournalEntry_UserAccountOrderByJournalEntry_EntryAtDesc(
            Doctor doctor, UserAccount patient);

    /** Kasowanie wpisu. */
    void deleteByJournalEntry(JournalEntry entry);

    /** Kasowanie konta pacjenta - wszystkie udostepnienia jego wpisow. */
    void deleteByJournalEntry_UserAccount(UserAccount patient);

    /** Kasowanie konta lekarza - wszystko, co jemu udostepniono. */
    void deleteByDoctor(Doctor doctor);

    /** Zerwanie powiazania pacjent-lekarz (z obu stron). */
    void deleteByDoctorAndJournalEntry_UserAccount(Doctor doctor, UserAccount patient);
}
