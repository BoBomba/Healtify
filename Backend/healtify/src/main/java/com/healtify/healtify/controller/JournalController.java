package com.healtify.healtify.controller;

import com.healtify.healtify.dto.JournalEntryRequest;
import com.healtify.healtify.dto.JournalEntryResponse;
import com.healtify.healtify.models.DataSharing;
import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.JournalEntry;
import com.healtify.healtify.models.JournalEntryShare;
import com.healtify.healtify.models.SharingStatus;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.JournalEntryRepository;
import com.healtify.healtify.repository.JournalEntryShareRepository;
import com.healtify.healtify.repository.SharingRepository;
import com.healtify.healtify.repository.UserAccountRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wpisy do dziennika zalogowanego pacjenta.
 *
 * Zasady bezpieczenstwa:
 * - kazdy endpoint dziala tylko w kontekscie usera z tokenu JWT (Principal),
 * - GET zwraca tylko wpisy tego uzytkownika (filtr po user_id w zapytaniu),
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
    private final JournalEntryShareRepository journalEntryShareRepository;
    private final SharingRepository sharingRepository;

    public JournalController(
            JournalEntryRepository journalEntryRepository,
            UserAccountRepository userAccountRepository,
            JournalEntryShareRepository journalEntryShareRepository,
            SharingRepository sharingRepository
    ) {
        this.journalEntryRepository = journalEntryRepository;
        this.userAccountRepository = userAccountRepository;
        this.journalEntryShareRepository = journalEntryShareRepository;
        this.sharingRepository = sharingRepository;
    }

    @GetMapping
    public ResponseEntity<List<JournalEntryResponse>> getMyEntries(Principal principal) {
        UserAccount userAccount = currentUser(principal);

        List<JournalEntry> entries = journalEntryRepository.findByUserAccountOrderByEntryAtAsc(userAccount);

        // Dociagamy jednym zapytaniem dla calej listy i grupujemy w pamieci by nie spamić bazy w petli.
        Map<Long, List<Long>> sharesByEntry = sharesByEntryId(entries);

        List<JournalEntryResponse> response = entries.stream()
                .map(entry -> JournalEntryResponse.from(
                        entry,
                        sharesByEntry.getOrDefault(entry.getEntryId(), List.of())))
                .toList();

        // Pusta lista zamiast 204 - front nie musi rozrozniac "brak danych" od bledu.
        return ResponseEntity.ok(response);
    }

    /**
     * Ustawienie, ktorym lekarzom widoczny jest ten wpis. 
     * Wysylamy komplet zaznaczonych lekarzy, a backend doprowadza stan do zgodnosci - dodaje brakujace, kasuje odznaczone.
     * Udostepnic mozna wylacznie lekarzowi z ACCEPTED
     */
    @Transactional
    @PutMapping("/{entryId}/shares")
    public ResponseEntity<JournalEntryResponse> updateShares(
            @PathVariable Long entryId,
            @RequestBody EntrySharesRequest request,
            Principal principal
    ) {
        UserAccount userAccount = currentUser(principal);
        JournalEntry entry = requireOwnEntry(entryId, principal);

        Set<Long> wanted = request.doctorIds() == null
                ? Set.of()
                : new HashSet<>(request.doctorIds());

        // Lekarze, ktorzy faktycznie opiekuja sie tym pacjentem.
        Map<Long, Doctor> allowed = sharingRepository
                .findByUserAccountAndRequestStatusOrderByRequestSentDateDesc(userAccount, SharingStatus.ACCEPTED)
                .stream()
                .map(DataSharing::getDoctor)
                .collect(Collectors.toMap(Doctor::getDoctorId, doctor -> doctor, (a, b) -> a));

        for (Long doctorId : wanted) {
            if (!allowed.containsKey(doctorId)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Ten lekarz nie ma dostepu do Twoich danych");
            }
        }

        List<JournalEntryShare> current = journalEntryShareRepository.findByJournalEntry(entry);
        Set<Long> currentIds = current.stream()
                .map(share -> share.getDoctor().getDoctorId())
                .collect(Collectors.toSet());

        // Odznaczone - kasujemy.
        List<JournalEntryShare> removed = current.stream()
                .filter(share -> !wanted.contains(share.getDoctor().getDoctorId()))
                .toList();
        journalEntryShareRepository.deleteAll(removed);

        // Nowo zaznaczone - dodajemy.
        for (Long doctorId : wanted) {
            if (!currentIds.contains(doctorId)) {
                journalEntryShareRepository.save(new JournalEntryShare(entry, allowed.get(doctorId)));
            }
        }

        return ResponseEntity.ok(JournalEntryResponse.from(entry, List.copyOf(wanted)));
    }

    /** entryId -> lista doctorId, ktorym wpis jest udostepniony. */
    private Map<Long, List<Long>> sharesByEntryId(List<JournalEntry> entries) {
        if (entries.isEmpty()) {
            // "where entry in ()" to zly SQL - pusta lista nie ma leciec do bazy.
            return Map.of();
        }
        return journalEntryShareRepository.findByJournalEntryIn(entries).stream()
                .collect(Collectors.groupingBy(
                        share -> share.getJournalEntry().getEntryId(),
                        Collectors.mapping(share -> share.getDoctor().getDoctorId(), Collectors.toList())));
    }

    /** Lista zaznaczonych lekarzy z modala udostepniania. */
    public record EntrySharesRequest(List<Long> doctorIds) {
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
     * Edycja wlasnego wpisu. Pola sa nadpisywane w calosci, 
     * modal wysyla komplet danych.
     */
    @PutMapping("/{entryId}")
    public ResponseEntity<JournalEntryResponse> updateEntry(
            @PathVariable Long entryId,
            @Valid @RequestBody JournalEntryRequest request,
            Principal principal
    ) {
        JournalEntry entry = requireOwnEntry(entryId, principal);

        entry.setTitle(request.getTitle().trim());
        entry.setDescription(normalizeDescription(request.getDescription()));
        entry.setEntryAt(request.getEntryAt());
        entry.setMoodScale(request.getMoodScale());
        entry.setSymptoms(normalizeSymptoms(request.getSymptoms()));
        entry.setReminder(request.isReminder());

        return ResponseEntity.ok(JournalEntryResponse.from(journalEntryRepository.save(entry)));
    }

    @Transactional
    @DeleteMapping("/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long entryId, Principal principal) {
        JournalEntry entry = requireOwnEntry(entryId, principal);
        journalEntryShareRepository.deleteByJournalEntry(entry);
        journalEntryRepository.delete(entry);
        return ResponseEntity.noContent().build();
    }

    /**
     * Wpis nalezacy do zalogowanego uzytkownika. 
     * Cudzy wpis dostaje 404, a nie 403 by nie zdradzac istnienia.
     */
    private JournalEntry requireOwnEntry(Long entryId, Principal principal) {
        UserAccount userAccount = currentUser(principal);
        JournalEntry entry = journalEntryRepository.findById(entryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego wpisu"));

        if (!entry.getUserAccount().getUserId().equals(userAccount.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego wpisu");
        }
        return entry;
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
