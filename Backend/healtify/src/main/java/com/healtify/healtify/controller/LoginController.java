package com.healtify.healtify.controller;


import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.UserAccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    private final UserAccountRepository userRepository;

    public LoginController(UserAccountRepository userRepository) {
        this.userRepository = userRepository;
    }

    // private final RoleRepository roleRepository;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@RequestBody UserAccount user){
        userRepository.save(user);

    }

}