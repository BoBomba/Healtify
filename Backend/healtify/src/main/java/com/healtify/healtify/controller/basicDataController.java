package com.healtify.healtify.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/data")
public class basicDataController {

    @GetMapping("/user")
    public String getLogin() {
        // Zwróć dane o loginie
        return "Login data";
    }

    @GetMapping("/general_data")
    public String getGeneralData() {
        // Zwróć dane ogólne
        return "General data";
    }

    @GetMapping("/user_profile")
    public String getUserProfile() {
        // Zwróć dane profilu użytkownika
        return "User profile data";
    }

}
