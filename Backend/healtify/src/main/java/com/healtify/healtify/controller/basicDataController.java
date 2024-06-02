package com.healtify.healtify.controller;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.security.service.JwtService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.healtify.healtify.models.UserAccount;


@RestController
@RequestMapping("/api/data")
public class basicDataController {
    private final UserAccountRepository userAccountRepository;
    private final JwtService jwtService;

    public basicDataController(UserAccountRepository userAccountRepository, JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.jwtService = jwtService;
    }

    @GetMapping("/general")
    public String getGeneralData(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "Login data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }

    @GetMapping("/Symptoms")
    public String getSymptoms(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "Symptoms data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }


    @GetMapping("/calendar")
    public String getCalendar(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "calendar data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }


    @GetMapping("/mood")
    public String getMood(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "mood data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }


    @GetMapping("/sleep")
    public String getSleep(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "sleep data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }


    @GetMapping("/medications")
    public String getMedications(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "drugs data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }


    @GetMapping("/history")
    public String getHistory(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "history data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }


    @GetMapping("/food")
    public String getFood(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "food data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }


    @GetMapping("/activity")
    public String getActivity(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "activity data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }


    @GetMapping("/sharing")
    public String getSharing(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "sharing data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }


    @GetMapping("/settings")
    public String getSettings(@RequestParam("token") String token) {
        // Extract the username from the token
        Object username = jwtService.extractUsername(token);

        UserAccount userAccount = userAccountRepository.findByUsername((String) username).orElse(null);
        // Check if the username matches
        if (userAccount != null && username.equals(userAccount.getUsername())){
            // Return the login data
            System.out.println("username: " + username);
            return "settings data";
        } else {
            // Return an error message
            return "Invalid username";
        }
    }


    // constructor
    



}
