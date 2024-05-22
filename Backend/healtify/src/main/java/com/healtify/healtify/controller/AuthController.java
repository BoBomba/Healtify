package com.healtify.healtify.controller;

import com.healtify.healtify.dto.AuthRequest;
import com.healtify.healtify.dto.AuthResponse;
import com.healtify.healtify.dto.RegisterRequest;
import com.healtify.healtify.security.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            service.register(registerRequest);
            System.out.println(new ResponseEntity<>(HttpStatus.CREATED));
//            return new ResponseEntity<>(HttpStatus.CREATED);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>("Email already exists", HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // @PostMapping("/authenticate")
    // public ResponseEntity<AuthResponse> authenticate(
    //         @RequestBody AuthRequest request
    // ) {
    //     System.out.println("Dziala cos");
    //     return ResponseEntity.ok(service.authenticate(request));
    // }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
    @RequestBody AuthRequest request
    ) {
    try {
            return ResponseEntity.ok(service.authenticate(request));
        } 
        catch (AccessDeniedException e) {
            return new ResponseEntity<>("Access denied", HttpStatus.FORBIDDEN);
        } 
        catch (LockedException e) {
        return new ResponseEntity<>("User account is locked", HttpStatus.UNAUTHORIZED); 
        }
        catch (Exception e) {
            e.printStackTrace(); // log the exception
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        service.refreshToken(request, response);
    }

    //args constructor
    public AuthController(AuthService service) {
        this.service = service;
    }
}