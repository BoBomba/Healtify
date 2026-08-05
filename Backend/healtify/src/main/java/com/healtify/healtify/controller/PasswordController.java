package com.healtify.healtify.controller;

import com.healtify.healtify.dto.AuthRequest;
import com.healtify.healtify.dto.AuthResponse;
import com.healtify.healtify.dto.ChangePasswordRequest;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.security.service.AuthService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;


@RestController
@RequestMapping("/api/user")
public class PasswordController {
    private final AuthService service;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;


    public PasswordController(
            AuthService service,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.service = service;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, Principal principal){
        System.out.println("Current password: " + changePasswordRequest.getPassword());
        System.out.println("New password: " + changePasswordRequest.getNewPassword());
        String username = principal.getName();

        UserAccount userAccount = userAccountRepository.findByUsername(username).orElse(null);

        String email = userAccount.getEmail();
        String password =  changePasswordRequest.getPassword();

        AuthRequest request = new AuthRequest(email, password);
        if (service.authenticate(request) == null) {
            return new ResponseEntity<>("Wrong password", HttpStatus.UNAUTHORIZED);
        }
        else {
            userAccount.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userAccountRepository.save(userAccount);
            return new ResponseEntity<>(HttpStatus.OK);
        }

    }

}
