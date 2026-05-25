package com.proyecto.app.security.service;

import com.proyecto.app.security.domain.Credential;
import com.proyecto.app.security.domain.Token;
import com.proyecto.app.security.dto.request.ChangeEmailRequest;
import com.proyecto.app.security.dto.request.ChangePasswordRequest;
import com.proyecto.app.security.dto.request.LoginRequest;
import com.proyecto.app.security.dto.request.RegisterRequest;
import com.proyecto.app.security.dto.response.LoginResponse;
import com.proyecto.app.security.repository.CredentialRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CredentialService {

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;

    public CredentialService(CredentialRepository credentialRepository,
                             PasswordEncoder passwordEncoder,
                             TokenService tokenService,
                             AuthenticationManager authenticationManager) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        Credential credential = credentialRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Token token = tokenService.generateToken(credential);

        return new LoginResponse(
            token.getToken(),
            credential.getEmail(),
            credential.getRole(),
            credential.getUserId()
        );
    }

    public void logout(String token) {
        tokenService.revokeToken(token);
    }

    public Credential createCredential(RegisterRequest request) {
        if (credentialRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        String roleName = (request.getRole() != null && request.getRole().getName() != null
            && !request.getRole().getName().isBlank())
                ? request.getRole().getName().trim()
                : "USER";

        Credential credential = new Credential(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
            roleName,
            request.getUserId()
        );

        return credentialRepository.save(credential);
    }

    public void changePassword(ChangePasswordRequest request) {
        Credential credential = credentialRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), credential.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        credential.setPassword(passwordEncoder.encode(request.getNewPassword()));
        credentialRepository.save(credential);
    }

    public String recoverPassword(String email) {
        Credential credential = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not registered"));

        String temporaryPassword = generateTemporaryPassword();
        credential.setPassword(passwordEncoder.encode(temporaryPassword));
        credentialRepository.save(credential);
        return temporaryPassword;
    }

    public void changeEmail(ChangeEmailRequest request) {
        Credential credential = credentialRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("Credencial no encontrada"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), credential.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        if (credentialRepository.existsByEmail(request.getNewEmail())) {
            throw new IllegalArgumentException("Email ya registrado");
        }

        credential.setEmail(request.getNewEmail());
        credentialRepository.save(credential);
    }

    private String generateTemporaryPassword() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }

    public Credential getCredentialByUserId(Long userId) {
        return credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Credencial no encontrada para userId: " + userId));
    }

    public Credential getCredentialById(Long id) {
        return credentialRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Credencial no encontrada con id: " + id));
    }
}