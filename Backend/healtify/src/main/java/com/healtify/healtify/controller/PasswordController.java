package com.healtify.healtify.controller;

import com.healtify.healtify.dto.ChangePasswordRequest;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.security.AuthCookies;
import com.healtify.healtify.security.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
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
    private final AuthCookies authCookies;


    public PasswordController(
            AuthService service,
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder,
            AuthCookies authCookies
    ) {
        this.service = service;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.authCookies = authCookies;
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest,
            Principal principal,
            HttpServletResponse response
    ) {
        UserAccount userAccount = userAccountRepository.findByUsername(principal.getName()).orElse(null);
        if (userAccount == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Samo porównanie hashy - bez wydawania tokenów jak przy logowaniu.
        if (!passwordEncoder.matches(changePasswordRequest.getPassword(), userAccount.getPassword())) {
            return new ResponseEntity<>("Wrong password", HttpStatus.UNAUTHORIZED);
        }

        userAccount.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userAccountRepository.save(userAccount);

        // Nowe hasło wylogowuje wszystkie sesje, także ewentualnie przejęte.
        service.revokeAllUserTokens(userAccount);
        authCookies.clear(response);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
