package com.proyecto.app.security.controller;

import com.proyecto.app.security.domain.Credential;
import com.proyecto.app.security.dto.request.ChangeEmailRequest;
import com.proyecto.app.security.dto.request.ChangePasswordRequest;
import com.proyecto.app.security.dto.request.LoginRequest;
import com.proyecto.app.security.dto.request.RecoverPasswordRequest;
import com.proyecto.app.security.dto.request.RegisterRequest;
import com.proyecto.app.security.dto.response.LoginResponse;
import com.proyecto.app.security.service.CredentialService;
import com.proyecto.app.security.service.EmailService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final CredentialService credentialService;
    private final EmailService emailService;

    public AuthController(CredentialService credentialService, EmailService emailService) {
        this.credentialService = credentialService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = credentialService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7); 
        credentialService.logout(token);
        return ResponseEntity.ok(Map.of("message", "Successfully logged out"));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        Credential credential = credentialService.createCredential(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Credential created for: " + credential.getEmail()));
    }

    @PostMapping("/recover-password")
    public ResponseEntity<Map<String, String>> recoverPassword(
            @RequestBody RecoverPasswordRequest request) {
        String temporaryPassword = credentialService.recoverPassword(request.getEmail());
        emailService.sendRecoveryToken(request.getEmail(), temporaryPassword);
        return ResponseEntity.ok(Map.of("message", "A temporary password has been sent to your email"));
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody ChangePasswordRequest request) {
        credentialService.changePassword(request);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
    }

    @PatchMapping("/change-email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> changeEmail(
            @RequestBody ChangeEmailRequest request) {
        credentialService.changeEmail(request);
        return ResponseEntity.ok(Map.of("message", "Email actualizado correctamente"));
    }
}