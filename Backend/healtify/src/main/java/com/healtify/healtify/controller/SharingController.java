package com.healtify.healtify.controller;

import com.healtify.healtify.dto.DoctorResponse;
import com.healtify.healtify.dto.DoctorSearchResponse;
import com.healtify.healtify.dto.SharingResponse;
import com.healtify.healtify.models.DataSharing;
import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.SharingInitiator;
import com.healtify.healtify.models.SharingStatus;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.AppointmentRepository;
import com.healtify.healtify.repository.DoctorRepository;
import com.healtify.healtify.repository.SharingRepository;
import com.healtify.healtify.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Udostepnianie danych po stronie pacjenta - druga polowa tego, co lekarz widzi
 * w /api/doctor. Pacjent decyduje, kto ma dostep do jego danych: sam prosi lekarza
 * o opieke albo akceptuje/odrzuca zaproszenie, ktore dostal.
 *
 * Wszystko dziala w kontekscie uzytkownika z tokenu - nie ma tu zadnego "za kogos".
 */
@RestController
@RequestMapping("/api/sharing")
public class SharingController {

    private static final int MAX_SEARCH_RESULTS = 20;

    private final DoctorRepository doctorRepository;
    private final SharingRepository sharingRepository;
    private final UserAccountRepository userAccountRepository;
    private final AppointmentRepository appointmentRepository;

    public SharingController(
            DoctorRepository doctorRepository,
            SharingRepository sharingRepository,
            UserAccountRepository userAccountRepository,
            AppointmentRepository appointmentRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.sharingRepository = sharingRepository;
        this.userAccountRepository = userAccountRepository;
        this.appointmentRepository = appointmentRepository;
    }

    /** Lekarze, ktorzy maja dostep do danych pacjenta. */
    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorResponse>> getMyDoctors(Principal principal) {
        List<DoctorResponse> doctors = sharingRepository
                .findByUserAccountAndRequestStatusOrderByRequestSentDateDesc(currentUser(principal), SharingStatus.ACCEPTED)
                .stream()
                .map(sharing -> DoctorResponse.from(sharing.getDoctor()))
                .toList();
        return ResponseEntity.ok(doctors);
    }

    /** Wiszace zaproszenia w obie strony (front rozdziela je po initiatedBy). */
    @GetMapping("/requests")
    public ResponseEntity<List<SharingResponse>> getPendingRequests(Principal principal) {
        List<SharingResponse> requests = sharingRepository
                .findByUserAccountAndRequestStatusOrderByRequestSentDateDesc(currentUser(principal), SharingStatus.PENDING)
                .stream()
                .map(SharingResponse::from)
                .toList();
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/doctors/search")
    public ResponseEntity<List<DoctorSearchResponse>> searchDoctors(
            @RequestParam("q") String query,
            Principal principal
    ) {
        UserAccount user = currentUser(principal);

        String trimmed = query == null ? "" : query.trim();
        if (trimmed.length() < 2) {
            return ResponseEntity.ok(List.of());
        }

        List<DoctorSearchResponse> results = new ArrayList<>();
        for (Doctor doctor : doctorRepository.search(trimmed)) {
            DataSharing sharing = sharingRepository.findByUserAccountAndDoctor(user, doctor).orElse(null);
            results.add(DoctorSearchResponse.from(
                    doctor,
                    sharing == null ? null : sharing.getRequestStatus(),
                    sharing == null ? null : sharing.getInitiatedBy()
            ));
            if (results.size() >= MAX_SEARCH_RESULTS) {
                break;
            }
        }
        return ResponseEntity.ok(results);
    }

    /** Prosba pacjenta o opieke. Dostep powstaje dopiero po akceptacji lekarza. */
    @PostMapping("/doctors/{doctorId}/request")
    public ResponseEntity<SharingResponse> requestDoctor(@PathVariable Long doctorId, Principal principal) {
        UserAccount user = currentUser(principal);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego lekarza"));

        DataSharing existing = sharingRepository.findByUserAccountAndDoctor(user, doctor).orElse(null);
        if (existing != null) {
            if (existing.getRequestStatus() == SharingStatus.ACCEPTED) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ten lekarz ma juz dostep do Twoich danych");
            }
            if (existing.getRequestStatus() == SharingStatus.PENDING) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Prosba juz czeka na odpowiedz");
            }
            existing.setRequestStatus(SharingStatus.PENDING);
            existing.setInitiatedBy(SharingInitiator.PATIENT);
            existing.setRequestSentDate(LocalDateTime.now());
            existing.setRequestAcceptedDate(null);
            return ResponseEntity.ok(SharingResponse.from(sharingRepository.save(existing)));
        }

        DataSharing sharing = new DataSharing(user, doctor, SharingInitiator.PATIENT);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SharingResponse.from(sharingRepository.save(sharing)));
    }

    @PostMapping("/requests/{sharingId}/accept")
    public ResponseEntity<SharingResponse> acceptRequest(@PathVariable Long sharingId, Principal principal) {
        DataSharing sharing = requireOwnPendingRequest(sharingId, principal);
        sharing.setRequestStatus(SharingStatus.ACCEPTED);
        sharing.setRequestAcceptedDate(LocalDateTime.now());
        return ResponseEntity.ok(SharingResponse.from(sharingRepository.save(sharing)));
    }

    @PostMapping("/requests/{sharingId}/reject")
    public ResponseEntity<SharingResponse> rejectRequest(@PathVariable Long sharingId, Principal principal) {
        DataSharing sharing = requireOwnPendingRequest(sharingId, principal);
        sharing.setRequestStatus(SharingStatus.REJECTED);
        sharing.setRequestAcceptedDate(null);
        return ResponseEntity.ok(SharingResponse.from(sharingRepository.save(sharing)));
    }

    /**
     * Cofniecie zgody - pacjent w kazdej chwili moze odciac lekarza od swoich danych.
     *
     * Razem z dostepem znikaja wizyty umowione u tego lekarza: pacjent, ktory rezygnuje
     * z opieki, nie ma po co blokowac jego terminow, a lekarz nie moze trzymac wizyty
     * pacjenta, ktorego danych juz nie widzi. Zwolnione godziny wracaja do jego kalendarza.
     */
    @Transactional
    @DeleteMapping("/doctors/{doctorId}")
    public ResponseEntity<Void> revokeAccess(@PathVariable Long doctorId, Principal principal) {
        UserAccount user = currentUser(principal);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego lekarza"));

        DataSharing sharing = sharingRepository.findByUserAccountAndDoctor(user, doctor)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ten lekarz nie ma dostepu"));

        appointmentRepository.deleteAll(appointmentRepository.findByPatientAndDoctor(user, doctor));

        sharing.setRequestStatus(SharingStatus.REJECTED);
        sharing.setRequestAcceptedDate(null);
        sharingRepository.save(sharing);
        return ResponseEntity.noContent().build();
    }

    // --- helpery ---

    private UserAccount currentUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji");
        }
        return userAccountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji"));
    }

    /** Wiszace zaproszenie skierowane DO pacjenta (czyli wyslane przez lekarza). */
    private DataSharing requireOwnPendingRequest(Long sharingId, Principal principal) {
        UserAccount user = currentUser(principal);
        DataSharing sharing = sharingRepository.findById(sharingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego zaproszenia"));

        if (!sharing.getUserAccount().getUserId().equals(user.getUserId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego zaproszenia");
        }
        if (sharing.getRequestStatus() != SharingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "To zaproszenie zostalo juz rozpatrzone");
        }
        // Wlasnej prosby pacjent nie zatwierdza za lekarza.
        if (sharing.getInitiatedBy() != SharingInitiator.DOCTOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ta prosba czeka na decyzje lekarza");
        }
        return sharing;
    }
}
