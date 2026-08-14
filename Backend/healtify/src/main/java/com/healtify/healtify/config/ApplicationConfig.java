package com.healtify.healtify.config;


import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
public class ApplicationConfig {

    private final UserAccountRepository repository;

    public ApplicationConfig(UserAccountRepository repository) {
        this.repository = repository;
    }


    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                Optional<UserAccount> userAccount;
                if (username.contains("@")) {
                    userAccount = repository.findByEmail(username);
                } else {
                    userAccount = repository.findByUsername(username);
                }

                if (userAccount.isEmpty()) {
                    throw new UsernameNotFoundException("User with username/email " + username + " not found");
                }
                // Zwracamy sama encje (implementuje UserDetails), a nie kopie w springowym User
                // z pusta lista uprawnien - inaczej role gina po drodze i hasRole()/@PreAuthorize
                // odrzuca nawet admina. Nazwa principala sie nie zmienia: to nadal username.
                return userAccount.get();
            }
        };
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }




}