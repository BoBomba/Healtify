package com.healtify.healtify.controller;

import com.healtify.healtify.dto.ChangeEmailRequest;
import com.healtify.healtify.dto.ChangeUsernameRequest;
import com.healtify.healtify.dto.UserDTO;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.security.service.JwtService;
import com.healtify.healtify.security.service.UserService;
import com.healtify.healtify.security.token.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.healtify.healtify.security.service.RoleEnum;

import java.util.List;
import java.util.Optional;

import static com.healtify.healtify.dto.UserDTO.mapToUserDto;

@RestController
@RequestMapping(path = "/api/user")
public class UserController {
    private final UserService userService;
    private final UserAccountRepository userAccountRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    @Autowired
    public UserController(
            UserService userService,
            UserAccountRepository userAccountRepository,
            TokenRepository tokenRepository,
            JwtService jwtService
    ) {
        this.userService = userService;
        this.userAccountRepository = userAccountRepository;
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
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
    public ResponseEntity<List<UserDTO>> getAllUsers(@RequestHeader("Authorization") String token) {
        System.out.println("token: " + token);
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
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
    public ResponseEntity<UserDTO> getUser(@RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token);
        UserDTO userDto = userService.findDTOByUsername(username);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PostMapping(path = "/update")
    public ResponseEntity<UserDTO> updateUser(@RequestHeader("Authorization") String token,@RequestBody UserAccount user) {

        String username = jwtService.extractUsername(token);

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
    public ResponseEntity<Void> updateUsername(@RequestHeader("Authorization") String token, @RequestBody ChangeUsernameRequest changeUsernameRequest) {
        System.out.println("token: " + token);
        System.out.println("username: " + changeUsernameRequest.getUsername());

        String oldusername = jwtService.extractUsername(token.replace("Bearer ", ""));
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
    public ResponseEntity<Void> updateEmail(@RequestHeader("Authorization") String token, @RequestBody ChangeEmailRequest changeEmailRequest) {
        System.out.println("token: " + token);
        System.out.println("email: " + changeEmailRequest.getEmail());

        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
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

    @DeleteMapping(path = "/delete")
    public ResponseEntity<UserDTO> deleteUser(@RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token);
        Optional<Long> userId = userService.getUserIdByUsername(username);
        if (userId.isPresent()) {
            tokenRepository.deleteByUserId(userId.get());
            userService.deleteById(userId.get());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/checkadmin")
    public ResponseEntity<Boolean> checkAdmin(@RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token.replace("Bearer ", ""));
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