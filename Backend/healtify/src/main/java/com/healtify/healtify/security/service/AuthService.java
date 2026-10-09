package com.healtify.healtify.security.service;

import com.healtify.healtify.models.Role;
import com.healtify.healtify.dto.AuthRequest;
import com.healtify.healtify.dto.AuthResponse;
import com.healtify.healtify.dto.RegisterRequest;
import com.healtify.healtify.models.UserAccount;
import com.healtify.healtify.repository.RoleRepository;
import com.healtify.healtify.repository.UserAccountRepository;
import com.healtify.healtify.security.token.Token;
import com.healtify.healtify.security.token.TokenRepository;
import com.healtify.healtify.security.token.TokenType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AuthService {

    private final UserAccountRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;


    // Rejestracja nie loguje - tokeny dostaje się dopiero przez /authenticate.
    public void register(RegisterRequest request) {
        // Pobierz rolę ROLE_USER z bazy
        Role userRole = roleRepository.findByName("ROLE_USER").orElse(null);

        // Jeśli nie istnieje, utwórz i zapisz nową rolę
        if (userRole == null) {
            userRole = new Role();
            userRole.setName("ROLE_USER");
            userRole = roleRepository.save(userRole);
        }

        // Utwórz zbiór ról i dodaj ROLE_USER
        HashSet<Role> roles = new HashSet<>();
        roles.add(userRole);

        var user = new UserAccount.Builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .roles(roles) // przypisz rolę
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        System.out.println("AuthResponse register username :"+user.getUsername());
        System.out.println("AuthResponse register Roles :"+user.getRoles());

        userRepository.save(user);
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        return issueTokens(user);
    }

    /**
     * Rotacja: ważny refresh token wymienia się na nową parę, a stary od razu
     * przestaje działać - drugi raz tego samego refresh tokena użyć się nie da.
     */
    public AuthResponse refresh(String refreshToken) {
        boolean active = tokenRepository.findByToken(refreshToken)
                .map(t -> t.getTokenType() == TokenType.REFRESH && !t.isExpired() && !t.isRevoked())
                .orElse(false);
        if (!active) {
            throw new BadCredentialsException("Refresh token odrzucony");
        }

        var user = (UserAccount) userDetailsService.loadUserByUsername(jwtService.extractUsername(refreshToken));
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadCredentialsException("Refresh token odrzucony");
        }
        return issueTokens(user);
    }

    // Jedna aktywna sesja na konto - nowa para unieważnia wszystkie poprzednie tokeny.
    private AuthResponse issueTokens(UserAccount user) {
        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken, TokenType.BEARER);
        saveUserToken(user, refreshToken, TokenType.REFRESH);
        return new AuthResponse.Builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private void saveUserToken(UserAccount user, String jwtToken, TokenType type) {
        var token = new Token.Builder()
                .userAccount(user)
                .token(jwtToken)
                .tokenType(type)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(UserAccount user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    //args constructor
    public AuthService(UserAccountRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, JwtService jwtService, RoleRepository roleRepository, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }
}
