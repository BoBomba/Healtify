package com.healtify.healtify.controller;

import com.healtify.healtify.dto.AppointmentResponse;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.AppointmentRepository;
import com.healtify.healtify.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

/**
 * Wizyty zalogowanego pacjenta - tylko do odczytu. Wizyty zaklada lekarz
 * (POST jest w DoctorController), pacjent moze je wylacznie ogladac w swoim kalendarzu.
 */
@RestController
@RequestMapping("/api/data/appointments")
public class AppointmentController {

    private final AppointmentRepository appointmentRepository;
    private final UserAccountRepository userAccountRepository;

    public AppointmentController(
            AppointmentRepository appointmentRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(Principal principal) {
        UserAccount user = currentUser(principal);

        List<AppointmentResponse> appointments = appointmentRepository
                .findByPatientOrderByAppointmentAtAsc(user)
                .stream()
                .map(AppointmentResponse::from)
                .toList();

        return ResponseEntity.ok(appointments);
    }

    private UserAccount currentUser(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji");
        }
        return userAccountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji"));
    }
}
