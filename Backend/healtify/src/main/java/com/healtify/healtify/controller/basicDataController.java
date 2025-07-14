package com.healtify.healtify.controller;
import com.healtify.healtify.models.*;

import com.healtify.healtify.repository.*;
import com.healtify.healtify.security.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/data")
public class basicDataController {
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final HeartDataRepository heartDataRepository;
    private final SymptomTrackerRepository symptomTrackerRepository;
    private final MedicationsRepository medicationsRepository;
    private final CalendarRepository calendarRepository;
    private final MoodRepository moodRepository;
    private final SleepRepository sleepRepository;
    private final MedHistoryRepository historyRepository;
    private final FoodRepository foodRepository;
    private final ActivityRepository activityRepository;
    private final SharingRepository sharingRepository;
//    private final SettingsRepository settingsRepository;

    private final JwtService jwtService;

    public basicDataController(
            UserAccountRepository userAccountRepository,
            UserProfileRepository userProfileRepository,
            HeartDataRepository heartDataRepository,
            SymptomTrackerRepository symptomTrackerRepository,
            MedicationsRepository medicationsRepository,
            CalendarRepository calendarRepository,
            MoodRepository moodRepository,
            SleepRepository sleepRepository,
            MedHistoryRepository historyRepository,
            FoodRepository foodRepository,
            ActivityRepository activityRepository,
            SharingRepository sharingRepository,
//            SettingsRepository settingsRepository,
            JwtService jwtService)
    {
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.heartDataRepository = heartDataRepository;
        this.symptomTrackerRepository = symptomTrackerRepository;
        this.medicationsRepository = medicationsRepository;
        this.calendarRepository = calendarRepository;
        this.moodRepository = moodRepository;
        this.sleepRepository = sleepRepository;
        this.historyRepository = historyRepository;
        this.foodRepository = foodRepository;
        this.activityRepository = activityRepository;
        this.sharingRepository = sharingRepository;
//        this.settingsRepository = settingsRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/general")
    public ResponseEntity<?> getGeneralData(@RequestParam("token") String token) {
        String username = jwtService.extractUsername(token);
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
    public ResponseEntity<?> getUserData(@RequestParam("token") String token) {
        String username = jwtService.extractUsername(token);
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())) {
            // Zwróć dane z UserAccount
            return ResponseEntity.ok(userAccount);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/heart")
    public ResponseEntity<?> getHeartData(@RequestParam("token") String token) {
        String username = jwtService.extractUsername(token);
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())) {
            Optional<VitalSigns> vitalSignsOpt = heartDataRepository.findByUserAccount(userAccount);
            if (vitalSignsOpt.isPresent()) {
                return ResponseEntity.ok(vitalSignsOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych : " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/symptoms")
    public ResponseEntity<?> getSymptoms(@RequestParam("token") String token) {
        String username = (String) jwtService.extractUsername(token);
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            Optional<SymptomTracker> symptomTrackerOpt = symptomTrackerRepository.findByUserAccount(userAccount);
            if (symptomTrackerOpt.isPresent()) {
                return ResponseEntity.ok(symptomTrackerOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/medications")
    public ResponseEntity<?> getMedications(@RequestParam("token") String token) {
        String username = (String) jwtService.extractUsername(token);
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            Optional<Medication> medicationsOpt = medicationsRepository.findByUserAccount(userAccount);
            if (medicationsOpt.isPresent()) {
                return ResponseEntity.ok(medicationsOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/calendar")
    public ResponseEntity<?> getCalendar(@RequestParam("token") String token) {
        String username = (String) jwtService.extractUsername(token);
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            Optional<CalendarEvent> calendarEventOpt = calendarRepository.findByUserAccount(userAccount);
            if (calendarEventOpt.isPresent()) {
                return ResponseEntity.ok(calendarEventOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/mood")
    public ResponseEntity<?> getMood(@RequestParam("token") String token) {
        String username = (String) jwtService.extractUsername(token);
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            Optional<MoodTracker> moodTrackerOpt = moodRepository.findByUserAccount(userAccount);
            if (moodTrackerOpt.isPresent()) {
                return ResponseEntity.ok(moodTrackerOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/sleep")
    public ResponseEntity<?> getSleep(@RequestParam("token") String token) {
        String username = (String) jwtService.extractUsername(token);
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            Optional<SleepRecord> sleepRecordOpt = sleepRepository.findByUserAccount(userAccount);
            if (sleepRecordOpt.isPresent()) {
                return ResponseEntity.ok(sleepRecordOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getMedHistory (@RequestParam("token") String token) {
        String username = (String) jwtService.extractUsername(token);
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            Optional<MedicalHistory> medicalHistoryOpt = historyRepository.findByUserAccount(userAccount);
            if (medicalHistoryOpt.isPresent()) {
                return ResponseEntity.ok(medicalHistoryOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/food")
    public ResponseEntity<?> getFood(@RequestParam("token") String token) {
        String username = (String) jwtService.extractUsername(token);
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            Optional<NutritionLog> nutritionLogOpt = foodRepository.findByUserAccount(userAccount);
            if (nutritionLogOpt.isPresent()) {
                return ResponseEntity.ok(nutritionLogOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/activity")
    public ResponseEntity<?> getActivity(@RequestParam("token") String token) {
        String username = (String) jwtService.extractUsername(token);
        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){
            Optional<Activity> activityOpt = activityRepository.findByUserAccount(userAccount);
            if (activityOpt.isPresent()) {
                return ResponseEntity.ok(activityOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body("Brak danych: " + username);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Brak autoryzacji");
        }
    }

    @GetMapping("/sharing")
    public ResponseEntity<?> getSharing(@RequestParam("token") String token) {
        String username = (String) jwtService.extractUsername(token);
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
    public ResponseEntity<?> getSettings(@RequestParam("token") String token) {
        String username = (String) jwtService.extractUsername(token);
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
