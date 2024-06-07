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
    public ResponseEntity<UserProfile> getGeneralData(@RequestParam("token") String token) {
        System.out.println("token: " + token);
        String username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        if (userAccount != null && username.equals(userAccount.getUsername())){

            UserProfile userProfile = userProfileRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return new ResponseEntity<>(userProfile, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/heart")
    public ResponseEntity<VitalSigns> getHeartData(@RequestParam("token") String token) {
        System.out.println("token: " + token);
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the data

//            AddData addData = new AddData(heartDataRepository);
//            addData.addUserProfileData(userAccount);

            VitalSigns vitalSigns = heartDataRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("user: " + vitalSigns);
//            return null;
            return ResponseEntity.ok(vitalSigns);
        } else {
            // Return an error message
            return null;
        }
    }

    @GetMapping("/symptoms")
    public ResponseEntity<SymptomTracker> getSymptoms(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches

        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
//            AddData addData = new AddData(symptomTrackerRepository);
//            addData.addUserProfileData(userAccount);

            SymptomTracker symptomTracker = symptomTrackerRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("user: " + symptomTracker);
//            return null;
            return ResponseEntity.ok(symptomTracker);
        } else {
            // Return an error message
            return null;
        }
    }

    @GetMapping("/medications")
    public ResponseEntity<Medication> getMedications(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
//            AddData addData = new AddData(medicationsRepository);
//            addData.addUserProfileData(userAccount);

            Medication medications = medicationsRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("user: " + medications);
            return ResponseEntity.ok(medications);
        } else {
            // Return an error message
            return null;
        }
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarEvent> getCalendar(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data

//            AddData addData = new AddData(calendarRepository);
//            addData.addUserProfileData(userAccount);

            CalendarEvent calendarEvent = calendarRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("username: " + username);
            return ResponseEntity.ok(calendarEvent);
        } else {
            // Return an error message
            return null;
        }
    }

    @GetMapping("/mood")
    public ResponseEntity<MoodTracker> getMood(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data

//            AddData addData = new AddData(moodRepository);
//            addData.addUserProfileData(userAccount);

            MoodTracker moodTracker = moodRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("username: " + username);
            return ResponseEntity.ok(moodTracker);
        } else {
            // Return an error message
            return null;
        }
    }

    @GetMapping("/sleep")
    public ResponseEntity<SleepRecord> getSleep(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data

//            AddData addData = new AddData(sleepRepository);
//            addData.addUserProfileData(userAccount);

            SleepRecord sleepRecord = sleepRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("username: " + username);
            return ResponseEntity.ok(sleepRecord);
        } else {
            // Return an error message
            return null;
        }
    }

    @GetMapping("/history")
    public ResponseEntity<MedicalHistory> getMedHistory (@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data

//            AddData addData = new AddData(historyRepository);
//            addData.addUserProfileData(userAccount);

            MedicalHistory medicalHistory = historyRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("username: " + username);
            return ResponseEntity.ok(medicalHistory);
        } else {
            // Return an error message
            return null;
        }
    }

    @GetMapping("/food")
    public ResponseEntity<NutritionLog> getFood(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data

//            AddData addData = new AddData(foodRepository);
//            addData.addUserProfileData(userAccount);

            NutritionLog nutritionLog = foodRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("username: " + username);
            return ResponseEntity.ok(nutritionLog);
        } else {
            // Return an error message
            return null;
        }
    }

    @GetMapping("/activity")
    public ResponseEntity<Activity> getActivity(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data

//            AddData addData = new AddData(activityRepository);
//            addData.addUserProfileData(userAccount);

            Activity activity = activityRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("username: " + username);
            return ResponseEntity.ok(activity);
        } else {
            // Return an error message
            return null;
        }
    }

    @GetMapping("/sharing")
    public ResponseEntity<DataSharing> getSharing(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data

//            AddData addData = new AddData(sharingRepository);
//            addData.addUserProfileData(userAccount);

            DataSharing dataSharing = sharingRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("username: " + username);
            return ResponseEntity.ok(dataSharing);
        } else {
            // Return an error message
            return null;
        }
    }

    @GetMapping("/settings")
    public ResponseEntity<UserAccount> getSettings(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data

//            AddData addData = new AddData(settingsRepository);
//            addData.addUserProfileData(userAccount);

            UserProfile userProfile = userProfileRepository.findByUserAccount(userAccount)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            System.out.println("username: " + username);
            return ResponseEntity.ok(userAccount);
        } else {
            // Return an error message
            return null;
        }
    }

}
