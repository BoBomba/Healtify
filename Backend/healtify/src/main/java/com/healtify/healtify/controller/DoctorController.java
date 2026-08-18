package com.healtify.healtify.controller;

import com.healtify.healtify.dto.AppointmentRequest;
import com.healtify.healtify.dto.AppointmentResponse;
import com.healtify.healtify.dto.DoctorResponse;
import com.healtify.healtify.dto.PatientResponse;
import com.healtify.healtify.dto.PatientSearchResponse;
import com.healtify.healtify.dto.SharingResponse;
import com.healtify.healtify.models.Appointment;
import com.healtify.healtify.models.DataSharing;
import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.SharingInitiator;
import com.healtify.healtify.models.SharingStatus;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.AppointmentRepository;
import com.healtify.healtify.repository.DoctorRepository;
import com.healtify.healtify.repository.SharingRepository;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.security.service.RoleEnum;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel lekarza.
 *
 * Zasady bezpieczenstwa:
 * - caly kontroler jest za rola ROLE_DOCTOR (@PreAuthorize na klasie),
 * - lekarz jest zawsze brany z tokenu JWT, nigdy z parametru zadania,
 * - kazdy dostep do pacjenta przechodzi przez requireLinkedPatient(), czyli wymaga
 *   powiazania ze statusem ACCEPTED - bez zgody pacjenta lekarz nie zobaczy nawet jego maila,
 * - lekarz nie ma tu zadnego wgladu w dziennik pacjenta; widzi wylacznie wizyty i dane kontaktowe.
 */
@RestController
@RequestMapping("/api/doctor")
@PreAuthorize("hasRole('DOCTOR')")
public class DoctorController {

    /** Zabezpieczenie przed zasypaniem bazy wizytami z jednego konta. */
    private static final long MAX_APPOINTMENTS_PER_DOCTOR = 10000;

    /** Ile wynikow wyszukiwarki oddajemy - zeby pusta fraza nie zwrocila calej bazy. */
    private static final int MAX_SEARCH_RESULTS = 20;

    private final DoctorRepository doctorRepository;
    private final SharingRepository sharingRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserAccountRepository userAccountRepository;

    public DoctorController(
            DoctorRepository doctorRepository,
            SharingRepository sharingRepository,
            AppointmentRepository appointmentRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.doctorRepository = doctorRepository;
        this.sharingRepository = sharingRepository;
        this.appointmentRepository = appointmentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    // --- profil ---

    @GetMapping("/me")
    public ResponseEntity<DoctorResponse> getMyProfile(Principal principal) {
        return ResponseEntity.ok(DoctorResponse.from(currentDoctor(principal)));
    }

    // --- pacjenci i zaproszenia ---

    /** Zaakceptowani pacjenci - "przypisani pacjenci" w panelu udostepniania. */
    @GetMapping("/patients")
    public ResponseEntity<List<PatientResponse>> getMyPatients(Principal principal) {
        List<PatientResponse> patients = sharingRepository
                .findByDoctorAndRequestStatusOrderByRequestSentDateDesc(currentDoctor(principal), SharingStatus.ACCEPTED)
                .stream()
                .map(sharing -> PatientResponse.from(sharing.getUserAccount()))
                .toList();
        return ResponseEntity.ok(patients);
    }

    /**
     * Wiszace zaproszenia w obie strony. Front rozdziela je po polu initiatedBy:
     * PATIENT = prosby do akceptacji, DOCTOR = zaproszenia czekajace na pacjenta.
     */
    @GetMapping("/requests")
    public ResponseEntity<List<SharingResponse>> getPendingRequests(Principal principal) {
        List<SharingResponse> requests = sharingRepository
                .findByDoctorAndRequestStatusOrderByRequestSentDateDesc(currentDoctor(principal), SharingStatus.PENDING)
                .stream()
                .map(SharingResponse::from)
                .toList();
        return ResponseEntity.ok(requests);
    }

    /**
     * Wyszukiwarka nowych pacjentow. Zwraca takze stan powiazania z tym lekarzem,
     * zeby front wiedzial, kogo mozna jeszcze zaprosic.
     */
    @GetMapping("/patients/search")
    public ResponseEntity<List<PatientSearchResponse>> searchPatients(
            @RequestParam("q") String query,
            Principal principal
    ) {
        Doctor doctor = currentDoctor(principal);

        String trimmed = query == null ? "" : query.trim();
        // Pusta fraza nie moze wylistowac wszystkich kont w bazie.
        if (trimmed.length() < 2) {
            return ResponseEntity.ok(List.of());
        }

        List<PatientSearchResponse> results = new ArrayList<>();
        for (UserAccount candidate : userAccountRepository.searchPatients(trimmed)) {
            DataSharing sharing = sharingRepository.findByUserAccountAndDoctor(candidate, doctor).orElse(null);
            results.add(PatientSearchResponse.from(
                    candidate,
                    sharing == null ? null : sharing.getRequestStatus(),
                    sharing == null ? null : sharing.getInitiatedBy()
            ));
            if (results.size() >= MAX_SEARCH_RESULTS) {
                break;
            }
        }
        return ResponseEntity.ok(results);
    }

    /** Zaproszenie pacjenta. Dostep dostaje dopiero, gdy pacjent je zaakceptuje. */
    @PostMapping("/patients/{patientId}/invite")
    public ResponseEntity<SharingResponse> invitePatient(@PathVariable Long patientId, Principal principal) {
        Doctor doctor = currentDoctor(principal);
        UserAccount patient = userAccountRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego pacjenta"));

        if (patient.hasRole(RoleEnum.ROLE_DOCTOR)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lekarz nie moze byc pacjentem innego lekarza");
        }

        DataSharing existing = sharingRepository.findByUserAccountAndDoctor(patient, doctor).orElse(null);
        if (existing != null) {
            if (existing.getRequestStatus() == SharingStatus.ACCEPTED) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ten pacjent jest juz przypisany");
            }
            if (existing.getRequestStatus() == SharingStatus.PENDING) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Zaproszenie juz czeka na odpowiedz");
            }
            // Po odrzuceniu mozna sprobowac jeszcze raz - odswiezamy istniejacy wiersz,
            // zeby nie mnozyc powiazan dla tej samej pary.
            existing.setRequestStatus(SharingStatus.PENDING);
            existing.setInitiatedBy(SharingInitiator.DOCTOR);
            existing.setRequestSentDate(LocalDateTime.now());
            existing.setRequestAcceptedDate(null);
            return ResponseEntity.ok(SharingResponse.from(sharingRepository.save(existing)));
        }

        DataSharing sharing = new DataSharing(patient, doctor, SharingInitiator.DOCTOR);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SharingResponse.from(sharingRepository.save(sharing)));
    }

    /** Akceptacja prosby wyslanej przez pacjenta. Zaproszen wlasnych nie da sie zaakceptowac samemu. */
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
     * Zakonczenie opieki nad pacjentem - odpowiednik rezygnacji po stronie pacjenta
     * (DELETE /api/sharing/doctors/{doctorId}). Lekarz traci dostep do danych pacjenta,
     * a umowione wizyty tej pary znikaja, wiec terminy wracaja do jego kalendarza.
     *
     * Powiazanie zostaje jako REJECTED (a nie kasujemy wiersza), aby obie strony
     * mogly je pozniej odnowic zaproszeniem.
     */
    @Transactional
    @DeleteMapping("/patients/{patientId}")
    public ResponseEntity<Void> removePatient(@PathVariable Long patientId, Principal principal) {
        Doctor doctor = currentDoctor(principal);
        UserAccount patient = userAccountRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego pacjenta"));

        DataSharing sharing = sharingRepository.findByUserAccountAndDoctor(patient, doctor)
                .filter(s -> s.getRequestStatus() == SharingStatus.ACCEPTED)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Ten pacjent nie jest przypisany do lekarza"));

        appointmentRepository.deleteAll(appointmentRepository.findByPatientAndDoctor(patient, doctor));

        sharing.setRequestStatus(SharingStatus.REJECTED);
        sharing.setRequestAcceptedDate(null);
        sharingRepository.save(sharing);
        return ResponseEntity.noContent().build();
    }

    // --- wizyty ---

    /**
     * Wizyty lekarza. Domyslnie tylko nadchodzace (dashboard i zakladka "Dane"),
     * bo panel lekarza z zalozenia patrzy do przodu; kalendarz prosi o wszystkie.
     */
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getAppointments(
            @RequestParam(name = "scope", defaultValue = "upcoming") String scope,
            Principal principal
    ) {
        Doctor doctor = currentDoctor(principal);

        List<Appointment> appointments = "all".equalsIgnoreCase(scope)
                ? appointmentRepository.findByDoctorOrderByAppointmentAtAsc(doctor)
                : appointmentRepository.findByDoctorAndAppointmentAtGreaterThanEqualOrderByAppointmentAtAsc(
                        doctor, LocalDateTime.now());

        return ResponseEntity.ok(appointments.stream().map(AppointmentResponse::from).toList());
    }

    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> addAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Principal principal
    ) {
        Doctor doctor = currentDoctor(principal);
        UserAccount patient = requireLinkedPatient(request.getPatientId(), doctor);

        if (appointmentRepository.countByDoctor(doctor) >= MAX_APPOINTMENTS_PER_DOCTOR) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Osiagnieto limit wizyt");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentAt(request.getAppointmentAt());
        appointment.setTitle(request.getTitle().trim());
        appointment.setNotes(normalizeNotes(request.getNotes()));
        appointment.setCreatedAt(LocalDateTime.now());

        Appointment saved = appointmentRepository.save(appointment);
        return ResponseEntity.status(HttpStatus.CREATED).body(AppointmentResponse.from(saved));
    }

    /**
     * Edycja wizyty przez lekarza, ktory ja zalozyl.
     */
    @PutMapping("/appointments/{appointmentId}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable Long appointmentId,
            @Valid @RequestBody AppointmentRequest request,
            Principal principal
    ) {
        Doctor doctor = currentDoctor(principal);
        Appointment appointment = requireOwnAppointment(appointmentId, doctor);
        UserAccount patient = requireLinkedPatient(request.getPatientId(), doctor);

        appointment.setPatient(patient);
        appointment.setAppointmentAt(request.getAppointmentAt());
        appointment.setTitle(request.getTitle().trim());
        appointment.setNotes(normalizeNotes(request.getNotes()));

        return ResponseEntity.ok(AppointmentResponse.from(appointmentRepository.save(appointment)));
    }

    /** Odwolanie wizyty przez lekarza, ktory ja zalozyl. */
    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long appointmentId, Principal principal) {
        appointmentRepository.delete(requireOwnAppointment(appointmentId, currentDoctor(principal)));
        return ResponseEntity.noContent().build();
    }

    // --- helpery ---

    /**
     * Profil lekarza zalogowanego uzytkownika. Konto z rola ROLE_DOCTOR, ale bez wiersza
     * w tabeli doctors, to blad nadania roli - lepiej powiedziec to wprost niz sypnac 500.
     */
    private Doctor currentDoctor(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji");
        }
        UserAccount userAccount = userAccountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji"));

        return doctorRepository.findByUserAccount(userAccount)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "Konto ma role lekarza, ale nie ma profilu lekarza"));
    }

    /** Wizyta zalozona przez tego lekarza. */
    private Appointment requireOwnAppointment(Long appointmentId, Doctor doctor) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiej wizyty"));

        if (!appointment.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiej wizyty");
        }
        return appointment;
    }

    /** Pacjent, z ktorym lekarz ma zaakceptowane powiazanie. W kazdym innym wypadku 403. */
    private UserAccount requireLinkedPatient(Long patientId, Doctor doctor) {
        UserAccount patient = userAccountRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego pacjenta"));

        boolean linked = sharingRepository.existsByUserAccountAndDoctorAndRequestStatus(
                patient, doctor, SharingStatus.ACCEPTED);
        if (!linked) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ten pacjent nie jest przypisany do lekarza");
        }
        return patient;
    }

    /** Wiszaca prosba skierowana DO tego lekarza. */
    private DataSharing requireOwnPendingRequest(Long sharingId, Principal principal) {
        Doctor doctor = currentDoctor(principal);
        DataSharing sharing = sharingRepository.findById(sharingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego zaproszenia"));

        if (!sharing.getDoctor().getDoctorId().equals(doctor.getDoctorId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego zaproszenia");
        }
        if (sharing.getRequestStatus() != SharingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "To zaproszenie zostalo juz rozpatrzone");
        }
        // Wlasnego zaproszenia lekarz nie moze zatwierdzic za pacjenta.
        if (sharing.getInitiatedBy() != SharingInitiator.PATIENT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "To zaproszenie czeka na decyzje pacjenta");
        }
        return sharing;
    }

    private String normalizeNotes(String notes) {
        if (notes == null) {
            return null;
        }
        String trimmed = notes.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
