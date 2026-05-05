package com.proyecto.test.security.controller;

import com.proyecto.test.security.domain.Credential;
import com.proyecto.test.security.dto.request.CambiarContrasenaRequest;
import com.proyecto.test.security.dto.request.LoginRequest;
import com.proyecto.test.security.dto.request.RecuperarContrasenaRequest;
import com.proyecto.test.security.dto.request.RegistroRequest;
import com.proyecto.test.security.dto.response.LoginResponse;
import com.proyecto.test.security.service.CredentialService;
import com.proyecto.test.security.service.EmailService;

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

    // ── POST /api/auth/login ──────────────────────────────────────────────
    // Público: cualquiera puede intentar iniciar sesión
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = credentialService.login(request);
        return ResponseEntity.ok(response);
    }

    // ── POST /api/auth/logout ─────────────────────────────────────────────
    // Requiere token válido: solo usuarios autenticados pueden cerrar sesión
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader) {
        String valorToken = authHeader.substring(7); // quita "Bearer "
        credentialService.cerrarSesion(valorToken);
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada correctamente"));
    }

    // ── POST /api/auth/registro ───────────────────────────────────────────
    // Público: para crear nuevas credenciales
    @PostMapping("/registro")
    public ResponseEntity<Map<String, String>> registro(@RequestBody RegistroRequest request) {
        Credential credential = credentialService.crearCredencial(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("mensaje", "Credencial creada para: " + credential.getCorreo()));
    }

    // ── POST /api/auth/recuperar-contrasena ───────────────────────────────
    // Público: el usuario no está autenticado cuando recupera su contraseña
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<Map<String, String>> recuperarContrasena(
            @RequestBody RecuperarContrasenaRequest request) {
        String temporal = credentialService.recuperarContrasena(request.getCorreo());
        emailService.enviarTokenRecuperacion(request.getCorreo(), temporal);
        return ResponseEntity.ok(Map.of("mensaje", "Se ha enviado una contraseña temporal al correo"));
    }

    // ── PUT /api/auth/cambiar-contrasena ──────────────────────────────────
    // Requiere token válido: solo usuarios autenticados pueden cambiar su
    // contraseña
    @PutMapping("/cambiar-contrasena")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> cambiarContrasena(
            @RequestBody CambiarContrasenaRequest request) {
        credentialService.cambiarContrasena(request);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
    }
}