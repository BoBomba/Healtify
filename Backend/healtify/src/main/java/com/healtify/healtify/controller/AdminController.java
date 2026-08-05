package com.healtify.healtify.controller;

import com.healtify.healtify.dto.UserDTO;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.security.Principal;

import static com.healtify.healtify.dto.UserDTO.mapToUserDto;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getall")
    public ResponseEntity<List<UserDTO>> getAllUsers(Principal principal) {
        String username = principal.getName();
        UserAccount user = userService.findAccByUsername(username);

        // Sprawdź, czy użytkownik ma rolę Admin
        if (user.getRoles().stream().anyMatch(type -> type.getName().equals("ROLE_ADMIN"))) {
            List<UserDTO> users = userService.findAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/checkadmin")
    public ResponseEntity<Boolean> checkAdmin(Principal principal) {
        String username = principal.getName();
        UserAccount userAccount = userService.findAccByUsername(username);

        boolean isAdmin = userAccount.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(isAdmin);
    }

    @GetMapping("/changeRole/{userId}")
    public ResponseEntity<String> changeUserRole(@PathVariable Long userId, @RequestParam String role, Principal principal) {
        String username = principal.getName();
        UserAccount user = userService.findAccByUsername(username);

        // Sprawdź, czy użytkownik ma rolę Admin
        if (user.getRoles().stream().anyMatch(type -> type.getName().equals("ROLE_ADMIN"))) {
            userService.changeUserRole(user, role);
            return ResponseEntity.ok("Rola użytkownika zmieniona na: " + role);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Brak uprawnień");
        }
    }


    // Dodaj tutaj kolejne endpointy tylko dla admina, np. usuwanie użytkowników, zmiana ról itd.
}
