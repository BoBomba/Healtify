package com.healtify.healtify.controller;
import com.healtify.healtify.models.*;

import com.healtify.healtify.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.security.Principal;


/**
 * Dane konta i profilu uzytkownika. Wpisy do dziennika (jedyna tabela z danymi
 * pacjenta) obsluguje osobny JournalController.
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

    @GetMapping("/general")
    public ResponseEntity<?> getGeneralData(Principal principal) {
        String username = principal.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())) {
            Optional<UserProfile> userProfileOpt = userProfileRepository.findByUserAccount(userAccount);
            if (userProfileOpt.isPresent()) {
                return ResponseEntity.ok(userProfileOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
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

    @GetMapping("/sharing")
    public ResponseEntity<?> getSharing(Principal principal) {
        String username = principal.getName();
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            Optional<DataSharing> dataSharingOpt = sharingRepository.findByUserAccount(userAccount);
            if (dataSharingOpt.isPresent()) {
                return ResponseEntity.ok(dataSharingOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
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
