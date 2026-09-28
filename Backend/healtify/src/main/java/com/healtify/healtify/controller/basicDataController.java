package com.healtify.healtify.controller;
import com.healtify.healtify.models.*;

import com.healtify.healtify.dto.PatientProfileRequest;
import com.healtify.healtify.dto.PatientProfileResponse;
import com.healtify.healtify.dto.SharingResponse;
import com.healtify.healtify.repository.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.security.Principal;


/**
 * Dane konta i profilu uzytkownika. 
 * Wpisy do dziennika to JournalController.
 */
@RestController
@RequestMapping("/api/data")
public class basicDataController {
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final SharingRepository sharingRepository;
//    private final SettingsRepository settingsRepository;

    public basicDataController(
            UserAccountRepository userAccountRepository,
            UserProfileRepository userProfileRepository,
            SharingRepository sharingRepository)
    {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.sharingRepository = sharingRepository;
//        this.settingsRepository = settingsRepository;
    }

    /**
     * Starsza sciezka do tych samych danych co /profile. 
     * Oddaje DTO, a nie encje -
     * UserProfile ma @OneToOne na UserAccount i Jackson wywalal sie na proxy
     * (lub przy otwartej sesji, doladowywal cale konto uzytkownika do odpowiedzi).
     */
    @GetMapping("/general")
    public ResponseEntity<PatientProfileResponse> getGeneralData(Principal principal) {
        UserAccount userAccount = requireUserAccount(principal);
        return ResponseEntity.ok(userProfileRepository.findByUserAccount(userAccount)
                .map(PatientProfileResponse::from)
                .orElseGet(PatientProfileResponse::empty));
    }

    /**
     * Szczegolowe dane pacjenta. 
     * Zwracamy 200 nawet gdy nic jeszcze nie wypelnione,
     * front dostaje wtedy komplet nulli i "completed": false, 
     */
    @GetMapping("/profile")
    public ResponseEntity<PatientProfileResponse> getProfile(Principal principal) {
        UserAccount userAccount = requireUserAccount(principal);
        return ResponseEntity.ok(userProfileRepository.findByUserAccount(userAccount)
                .map(PatientProfileResponse::from)
                .orElseGet(PatientProfileResponse::empty));
    }

    /**
     * Zapis szczegolowych danych - upsert, 
     * Samo istnienie wiersza jest znacznikiem "uzytkownik przeszedl
     * juz przez uzupelnianie danych", wiec pusty formularz tez zapisujemy.
     */
    @PutMapping("/profile")
    public ResponseEntity<PatientProfileResponse> saveProfile(
            Principal principal,
            @Valid @RequestBody PatientProfileRequest request
    ) {
        UserAccount userAccount = requireUserAccount(principal);

        UserProfile profile = userProfileRepository.findByUserAccount(userAccount)
                .orElseGet(() -> {
                    UserProfile fresh = new UserProfile();
                    fresh.setUserAccount(userAccount);
                    return fresh;
                });

        profile.setFullName(trimToNull(request.fullName()));
        profile.setDateOfBirth(request.dateOfBirth());
        profile.setGender(trimToNull(request.gender()));
        profile.setPhone(trimToNull(request.phone()));
        profile.setHeightCm(request.heightCm());
        profile.setWeightKg(request.weightKg());
        profile.setBloodType(trimToNull(request.bloodType()));
        profile.setAllergies(trimToNull(request.allergies()));
        profile.setChronicDiseases(trimToNull(request.chronicDiseases()));
        profile.setMedications(trimToNull(request.medications()));

        return ResponseEntity.ok(PatientProfileResponse.from(userProfileRepository.save(profile)));
    }

    /**
     * Puste pole formularza przychodzi jako "" - w bazie ma byc null.
     */
    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private UserAccount requireUserAccount(Principal principal) {
        return userAccountRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak autoryzacji"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserData(Principal principal) {
        String username = principal.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())) {
            // Zwróć dane z UserAccount
            return ResponseEntity.ok(userAccount);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    /**
     * Podglad powiazan pacjenta z lekarzami. 
     * Pacjent moze miec ich wielu, wiec jest lista
     * (operacje na zaproszeniach - /api/sharing).
     */
    @GetMapping("/sharing")
    public ResponseEntity<?> getSharing(Principal principal) {
        String username = principal.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            List<SharingResponse> sharings = sharingRepository
                    .findByUserAccountOrderByRequestSentDateDesc(userAccount)
                    .stream()
                    .map(SharingResponse::from)
                    .toList();
            return ResponseEntity.ok(sharings);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<?> getSettings(Principal principal) {
        String username = principal.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            Optional<UserProfile> userProfileOpt = userProfileRepository.findByUserAccount(userAccount);
            if (userProfileOpt.isPresent()) {
                return ResponseEntity.ok(userAccount);
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

}
