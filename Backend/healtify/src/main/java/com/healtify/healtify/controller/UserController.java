package com.healtify.healtify.controller;

import com.healtify.healtify.dto.ChangeEmailRequest;
import com.healtify.healtify.dto.ChangeUsernameRequest;
import com.healtify.healtify.dto.CurrentUserResponse;
import com.healtify.healtify.dto.DeleteAccountRequest;
import com.healtify.healtify.dto.UserDTO;
import com.healtify.healtify.models.Doctor;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.DoctorRepository;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.repository.UserProfileRepository;
import com.healtify.healtify.security.service.AccountDeletionService;
import com.healtify.healtify.security.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.healtify.healtify.security.service.RoleEnum;

import java.util.List;
import java.util.Optional;
import java.security.Principal;

import static com.healtify.healtify.dto.UserDTO.mapToUserDto;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserService userService;
    private final UserAccountRepository userAccountRepository;
    private final UserProfileRepository userProfileRepository;
    private final DoctorRepository doctorRepository;
    private final AccountDeletionService accountDeletionService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(
            UserService userService,
            UserAccountRepository userAccountRepository,
            UserProfileRepository userProfileRepository,
            DoctorRepository doctorRepository,
            AccountDeletionService accountDeletionService,
            PasswordEncoder passwordEncoder
    ) {
        this.userService = userService;
        this.userAccountRepository = userAccountRepository;
        this.userProfileRepository = userProfileRepository;
        this.doctorRepository = doctorRepository;
        this.accountDeletionService = accountDeletionService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(path = "/add")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserAccount user) {
        user.setEmail(user.getEmail());
        user.setUsername(user.getUsername());
        user.setPassword(user.getPassword());
        UserAccount savedUser = userService.createUser(user);
        return new ResponseEntity<>(mapToUserDto(savedUser), HttpStatus.CREATED);
    }

    @GetMapping(path = "/getall")
    public ResponseEntity<List<UserDTO>> getAllUsers(Principal principal) {
        String username = principal.getName();
        UserAccount user = userService.findAccByUsername(username);

        // Sprawdź, czy użytkownik ma rolę Admin
//        user.getRoles().stream().anyMatch(type -> type.getName().equals(RoleEnum.ROLE_ADMIN.name()))

        if (user.getRoles().stream().anyMatch(type -> type.getName().equals(RoleEnum.ROLE_ADMIN.name()))) {
            List<UserDTO> users = userService.findAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(path = "/get")
    public ResponseEntity<UserDTO> getUser(Principal principal) {
        String username = principal.getName();
        UserDTO userDto = userService.findDTOByUsername(username);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping(path = "/update")
    public ResponseEntity<UserDTO> updateUser(Principal principal, @RequestBody UserAccount user) {
        String username = principal.getName();

        UserAccount userAccount = userService.findAccByUsername(username);
        // Check if the username matches

        if (userAccount != null && username.equals(userAccount.getUsername())) {
            user.setUserId(userAccount.getUserId());

            UserAccount updatedUser = userService.updateUser(username, user);
            return new ResponseEntity<>(mapToUserDto(updatedUser), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }


    @PatchMapping(path = "/update-username")
    public ResponseEntity<Void> updateUsername(Principal principal, @RequestBody ChangeUsernameRequest changeUsernameRequest) {
        System.out.println("username: " + changeUsernameRequest.getUsername());

        String oldusername = principal.getName();
        UserAccount user = userService.findAccByUsername(oldusername);
        if(userAccountRepository.existsByUsername(changeUsernameRequest.getUsername())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        else {
            userService.updateUsername(user, changeUsernameRequest.getUsername());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PatchMapping(path = "/update-email")
    public ResponseEntity<Void> updateEmail(Principal principal, @RequestBody ChangeEmailRequest changeEmailRequest) {
        System.out.println("email: " + changeEmailRequest.getEmail());

        String username = principal.getName();
        UserAccount user = userService.findAccByUsername(username);
        user.setEmail(changeEmailRequest.getEmail());
        if(userAccountRepository.existsByEmail(changeEmailRequest.getEmail())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
        else {
            userService.updateEmail(user, changeEmailRequest.getEmail());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    /**
     * Skasowanie wlasnego konta razem z calym kompletem danych 
     * Szczegoly kasowania w AccountDeletionService.
     *
     * Wymaga podania hasla (patrz DeleteAccountRequest). 
     * Sprawdzamy je przez passwordEncoder.matches(), a nie AuthService.authenticate()
     * bo authenticate() przy okazji uniewaznia wszystkie tokeny i wystawia nowe, 
     * a tu chodzi wylacznie o potwierdzenie tozsamosci.
     */
    @DeleteMapping(path = "/delete")
    public ResponseEntity<Void> deleteUser(
            Principal principal,
            @Valid @RequestBody DeleteAccountRequest request
    ) {
        Optional<UserAccount> user = userAccountRepository.findByUsername(principal.getName());
        if (user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.get().getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nieprawidłowe hasło");
        }

        accountDeletionService.deleteAccount(user.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Tozsamosc zalogowanego uzytkownika z rolami. 
     * Front woła to zaraz po zalogowaniu
     */
    @GetMapping("/me")
    public ResponseEntity<CurrentUserResponse> getCurrentUser(Principal principal) {
        UserAccount userAccount = userService.findAccByUsername(principal.getName());
        return ResponseEntity.ok(CurrentUserResponse.from(userAccount, profileCompleted(userAccount)));
    }

    /**
     * Czy konto ma uzupelnione "swoje dane" - a wiec co innego dla kazdej z rol.
     *
     * Lekarz ma wlasna flage w tabeli doctors.
     * U pacjenta wystarcza samo istnienie wiersza w user_profile.
     */
    private boolean profileCompleted(UserAccount userAccount) {
        if (userAccount.hasRole(RoleEnum.ROLE_DOCTOR)) {
            return doctorRepository.findByUserAccount(userAccount)
                    .map(Doctor::isProfileCompleted)
                    .orElse(false);
        }
        return userProfileRepository.findByUserAccount(userAccount).isPresent();
    }

    @GetMapping("/checkadmin")
    public ResponseEntity<Boolean> checkAdmin(Principal principal) {
        String username = principal.getName();
        // Optional<UserAccount> userAccount = userAccountRepository.findByUsername(username);

        UserAccount userAccount = userService.findAccByUsername(username);

        boolean isAdmin = userAccount.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
}
}
