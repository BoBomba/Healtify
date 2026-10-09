package com.healtify.healtify.security.service;

import com.healtify.healtify.security.AuthCookies;
import com.healtify.healtify.security.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;
    private final AuthCookies authCookies;

    public LogoutService(TokenRepository tokenRepository, AuthCookies authCookies) {
        this.tokenRepository = tokenRepository;
        this.authCookies = authCookies;
    }

    // Unieważnia oba tokeny w bazie - samo skasowanie ciasteczek nie wystarczy,
    // bo skopiowany wcześniej token dalej by działał.
    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        Stream.of(AuthCookies.ACCESS_TOKEN, AuthCookies.REFRESH_TOKEN)
                .map(name -> AuthCookies.read(request, name))
                .flatMap(Optional::stream)
                .flatMap(jwt -> tokenRepository.findByToken(jwt).stream())
                .forEach(storedToken -> {
                    storedToken.setExpired(true);
                    storedToken.setRevoked(true);
                    tokenRepository.save(storedToken);
                });
        authCookies.clear(response);
        SecurityContextHolder.clearContext();
    }
}
