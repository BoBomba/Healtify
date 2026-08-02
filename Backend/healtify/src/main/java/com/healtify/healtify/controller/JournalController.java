package com.healtify.healtify.controller;

import com.healtify.healtify.dto.JournalEntryRequest;
import com.healtify.healtify.dto.JournalEntryResponse;
import com.healtify.healtify.models.JournalEntry;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.JournalEntryRepository;
import com.healtify.healtify.repository.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Wpisy do dziennika zalogowanego pacjenta.
 *
 * Zasady bezpieczenstwa:
 * - kazdy endpoint dziala tylko w kontekscie uzytkownika z tokenu JWT (Principal),
 * - GET zwraca wylacznie wpisy tego uzytkownika (filtr po user_id w zapytaniu),
 * - POST zapisuje wpis zawsze na tego uzytkownika; klient nie ma jak wskazac wlasciciela,
 * - dane wejsciowe sa walidowane (@Valid) i przycinane, a limit wpisow chroni przed zasypaniem bazy.
 */
@RestController
@RequestMapping("/api/data/journal")
public class JournalController {

    /** Zabezpieczenie przed zapychaniem bazy przez jedno konto. */
    private static final long MAX_ENTRIES_PER_USER = 5000;

    private static final int MAX_SYMPTOMS = 30;

    private final JournalEntryRepository journalEntryRepository;
    private final UserAccountRepository userAccountRepository;

    public JournalController(
            JournalEntryRepository journalEntryRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.journalEntryRepository = journalEntryRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping
    public ResponseEntity<List<JournalEntryResponse>> getMyEntries(Principal principal) {
        UserAccount userAccount = currentUser(principal);

        List<JournalEntryResponse> entries = journalEntryRepository
                .findByUserAccountOrderByEntryAtAsc(userAccount)
                .stream()
                .map(JournalEntryResponse::from)
                .toList();

        // Pusta lista zamiast 204 - front nie musi rozrozniac "brak danych" od bledu.
        return ResponseEntity.ok(entries);
    }

    @PostMapping
    public ResponseEntity<JournalEntryResponse> addEntry(
            @Valid @RequestBody JournalEntryRequest request,
            Principal principal
    ) {
        UserAccount userAccount = currentUser(principal);

        if (journalEntryRepository.countByUserAccount(userAccount) >= MAX_ENTRIES_PER_USER) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Osiagnieto limit wpisow w dzienniku");
        }

        JournalEntry entry = new JournalEntry();
        entry.setUserAccount(userAccount);
        entry.setTitle(request.getTitle().trim());
        entry.setDescription(normalizeDescription(request.getDescription()));
        entry.setEntryAt(request.getEntryAt());
        entry.setMoodScale(request.getMoodScale());
        entry.setSymptoms(normalizeSymptoms(request.getSymptoms()));
        entry.setReminder(request.isReminder());
        entry.setCreatedAt(LocalDateTime.now());

        JournalEntry saved = journalEntryRepository.save(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(JournalEntryResponse.from(saved));
    }

    /**
     * Zalogowany uzytkownik na podstawie tokenu. Brak principala albo konta = 401,
     * nigdy nie schodzimy do bazy z nazwa przyslana przez klienta.
     */
    private UserAccount currentUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji");
        }
        return userAccountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji"));
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }
        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /** Trim + usuniecie pustych i duplikatow, z twardym limitem na dlugosc listy. */
    private List<String> normalizeSymptoms(List<String> symptoms) {
        List<String> normalized = new ArrayList<>();
        if (symptoms == null) {
            return normalized;
        }
        for (String symptom : symptoms) {
            if (symptom == null) {
                continue;
            }
            String single = symptom.replaceAll("\\R", " ").trim();
            if (!single.isEmpty() && !normalized.contains(single)) {
                normalized.add(single);
            }
            if (normalized.size() >= MAX_SYMPTOMS) {
                break;
            }
        }
        return normalized;
    }
}
