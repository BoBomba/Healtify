package com.healtify.healtify.controller;

import com.healtify.healtify.dto.AuthRequest;
import com.healtify.healtify.dto.AuthResponse;
import com.healtify.healtify.dto.RegisterRequest;
import com.healtify.healtify.dto.ValidateResponse;
import com.healtify.healtify.security.AuthCookies;
import com.healtify.healtify.security.service.AuthService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;
    private final AuthCookies authCookies;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            service.register(registerRequest);
            System.out.println(new ResponseEntity<>(HttpStatus.CREATED));
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Tokeny idą tylko w ciasteczkach HttpOnly - w body nie ma nic, co JS mógłby przechwycić.
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
    @RequestBody AuthRequest request,
    HttpServletResponse response
    ) {
    try {
            AuthResponse tokens = service.authenticate(request);
            authCookies.write(response, tokens.getAccessToken(), tokens.getRefreshToken());
            return ResponseEntity.noContent().build();
        }
        catch (AccessDeniedException e) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        }
        catch (LockedException e) {
        return new ResponseEntity<>("User account is locked", HttpStatus.UNAUTHORIZED);
        }
        catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
        }
        catch (Exception e) {
            e.printStackTrace(); // log the exception
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Wymaga zalogowania (SecurityConfig) - bez ważnego access tokena odpowiada 401.
    @GetMapping("/validate")
    public ValidateResponse validateToken(@AuthenticationPrincipal UserDetails user) {
        return new ValidateResponse(true, user.getUsername());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            String refreshToken = AuthCookies.read(request, AuthCookies.REFRESH_TOKEN)
                    .orElseThrow(() -> new BadCredentialsException("Brak refresh tokena"));
            AuthResponse tokens = service.refresh(refreshToken);
            authCookies.write(response, tokens.getAccessToken(), tokens.getRefreshToken());
            return ResponseEntity.noContent().build();
        } catch (AuthenticationException | JwtException e) {
            authCookies.clear(response);
            return new ResponseEntity<>("Sesja wygasła", HttpStatus.UNAUTHORIZED);
        }
    }

    // Samo przejście przez CsrfFilter ustawia ciasteczko XSRF-TOKEN.
    @GetMapping("/csrf")
    public ResponseEntity<Void> csrf() {
        return ResponseEntity.noContent().build();
    }

    //args constructor
    public AuthController(AuthService service, AuthCookies authCookies) {
        this.service = service;
        this.authCookies = authCookies;
    }
}
