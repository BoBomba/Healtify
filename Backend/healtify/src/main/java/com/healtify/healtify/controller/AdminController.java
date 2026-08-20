package com.healtify.healtify.controller;

import com.healtify.healtify.dto.AdminUserResponse;
import com.healtify.healtify.dto.DoctorResponse;
import com.healtify.healtify.dto.UserDTO;
import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.DoctorRepository;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.security.service.AccountDeletionService;
import com.healtify.healtify.security.service.RoleEnum;
import com.healtify.healtify.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.security.Principal;

/**
 * Panel admina. Rola sprawdzana deklaratywnie (@PreAuthorize)
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final UserAccountRepository userAccountRepository;
    private final DoctorRepository doctorRepository;
    private final AccountDeletionService accountDeletionService;

    @Autowired
    public AdminController(
            UserService userService,
            UserAccountRepository userAccountRepository,
            DoctorRepository doctorRepository,
            AccountDeletionService accountDeletionService
    ) {
        this.userService = userService;
        this.userAccountRepository = userAccountRepository;
        this.doctorRepository = doctorRepository;
        this.accountDeletionService = accountDeletionService;
    }

    @GetMapping("/getall")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    /** Lista uzytkownikow z rolami */
    @GetMapping("/users")
    public ResponseEntity<List<AdminUserResponse>> getUsersWithRoles() {
        List<AdminUserResponse> users = userAccountRepository.findAll().stream()
                .map(AdminUserResponse::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/checkadmin")
    public ResponseEntity<Boolean> checkAdmin(Principal principal) {
        UserAccount userAccount = userService.findAccByUsername(principal.getName());
        return ResponseEntity.ok(userAccount.hasRole(RoleEnum.ROLE_ADMIN));
    }

    /* Nadanie roli userowi. */
    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<String> changeUserRole(@PathVariable Long userId, @RequestParam String role) {
        UserAccount target = requireUser(userId);

        if (RoleEnum.ROLE_DOCTOR.name().equals(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Role lekarza nadaje sie przez /grant-doctor - razem z profilem lekarza");
        }

        try {
            userService.changeUserRole(target, role);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
        return ResponseEntity.ok("Rola użytkownika zmieniona na: " + role);
    }

    /**
     * Nadanie ROLE_DOCTOR + profil w doctors.
     *
     * Danych zawodowych nie wpisuje juz admin, tylko sam lekarz
     * przy 1 zalogowaniu (DoctorController#saveProfile). 
     * - profileCompleted na false.
     */
    @PostMapping("/users/{userId}/grant-doctor")
    public ResponseEntity<DoctorResponse> grantDoctor(@PathVariable Long userId) {
        UserAccount target = requireUser(userId);

        if (doctorRepository.existsByUserAccount(target)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "To konto ma już profil lekarza");
        }

        if (!target.hasRole(RoleEnum.ROLE_DOCTOR)) {
            userService.changeUserRole(target, RoleEnum.ROLE_DOCTOR.name());
        }

        Doctor saved = doctorRepository.save(new Doctor(target, target.getUsername(), null));

        return ResponseEntity.status(HttpStatus.CREATED).body(DoctorResponse.from(saved));
    }

    /**
     * Skasowanie cudzego konta z jego danymi 
     * (dziennik, wizyty po obu stronach, powiazania pacjent-lekarz, profil lekarza, tokeny) 
     * -> AccountDeletionService.
     */
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId, Principal principal) {
        UserAccount target = requireUser(userId);
        UserAccount admin = userService.findAccByUsername(principal.getName());

        if (target.getUserId().equals(admin.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Własnego konta nie kasuje się z panelu admina - zrób to w ustawieniach");
        }

        accountDeletionService.deleteAccount(target);
        return ResponseEntity.noContent().build();
    }

    private UserAccount requireUser(Long userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Nie ma takiego użytkownika"));
    }
}
