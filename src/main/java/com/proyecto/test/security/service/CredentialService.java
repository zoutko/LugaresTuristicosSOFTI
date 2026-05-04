package com.proyecto.test.security.service;

import com.proyecto.test.security.domain.Credential;
import com.proyecto.test.security.domain.Token;
import com.proyecto.test.security.dto.request.CambiarContrasenaRequest;
import com.proyecto.test.security.dto.request.LoginRequest;
import com.proyecto.test.security.dto.request.RegistroRequest;
import com.proyecto.test.security.dto.response.LoginResponse;
import com.proyecto.test.security.repository.CredentialRepository;
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

    // ── Login ─────────────────────────────────────────────────────────────

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(),
                        request.getContrasena()
                )
        );

        Credential credential = credentialRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Token token = tokenService.generarToken(credential);

        return new LoginResponse(
                token.getValor(),
                credential.getCorreo(),
                credential.getRole()
        );
    }

    // ── Cerrar sesión ─────────────────────────────────────────────────────

    public void cerrarSesion(String valorToken) {
        tokenService.revocarToken(valorToken);
    }

    // ── Crear credencial ──────────────────────────────────────────────────

    public Credential crearCredencial(RegistroRequest request) {
        if (credentialRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Credential credential = new Credential(
                request.getCorreo(),
                passwordEncoder.encode(request.getContrasena()),
                request.getRole().getName(),
                request.getUserId()
        );

        return credentialRepository.save(credential);
    }

    // ── Cambiar contraseña ────────────────────────────────────────────────

    public void cambiarContrasena(CambiarContrasenaRequest request) {
        Credential credential = credentialRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getContrasenaActual(), credential.getPassword())) {
            throw new BadCredentialsException("La contraseña actual es incorrecta");
        }

        credential.setContrasena(passwordEncoder.encode(request.getNuevaContrasena()));
        credentialRepository.save(credential);
    }

    // ── Recuperar contraseña ──────────────────────────────────────────────

    public void recuperarContrasena(String correo) {
        Credential credential = credentialRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Correo no registrado"));

        String contrasenaTemporal = generarContrasenaTemporal();
        credential.setContrasena(passwordEncoder.encode(contrasenaTemporal));
        credentialRepository.save(credential);

        // TODO: enviar por email cuando se integre el servicio de correo
        System.out.println("Contraseña temporal para " + correo + ": " + contrasenaTemporal);
    }

    // ── Utilidades ────────────────────────────────────────────────────────

    private String generarContrasenaTemporal() {
        return java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}