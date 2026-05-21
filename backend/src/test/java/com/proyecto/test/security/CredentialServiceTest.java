package com.proyecto.test.security;

import com.proyecto.app.security.domain.Credential;
import com.proyecto.app.security.domain.Token;
import com.proyecto.app.security.dto.request.ChangePasswordRequest;
import com.proyecto.app.security.dto.request.LoginRequest;
import com.proyecto.app.security.dto.request.RegisterRequest;
import com.proyecto.app.security.dto.response.LoginResponse;
import com.proyecto.app.security.dto.response.RoleDTO;
import com.proyecto.app.security.repository.CredentialRepository;
import com.proyecto.app.security.service.CredentialService;
import com.proyecto.app.security.service.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CredentialService - Pruebas Unitarias")
class CredentialServiceTest {

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private CredentialService credentialService;

    private Credential credential;
    private Token token;

    @BeforeEach
    void setUp() {
        credential = mock(Credential.class);
        token = mock(Token.class);
    }

    @Test
    @DisplayName("login: autentica y retorna token exitosamente")
    void login_successfullyAuthenticatesAndReturnsToken() {

        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(credentialRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(credential));

        when(tokenService.generateToken(credential))
                .thenReturn(token);

        when(token.getToken()).thenReturn("jwt-token-123");
        when(credential.getEmail()).thenReturn("test@example.com");
        when(credential.getRole()).thenReturn("USER");
        when(credential.getUserId()).thenReturn(1L);

        LoginResponse response = credentialService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token-123");
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("login: lanza excepción si el email no está registrado")
    void login_throwsIfEmailNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("password");

        when(credentialRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> credentialService.login(request))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }


    @Test
    @DisplayName("logout: revoca el token correctamente")
    void logout_revokesToken() {
        credentialService.logout("jwt-token-123");

        verify(tokenService).revokeToken("jwt-token-123");
    }


    @Test
    @DisplayName("createCredential: lanza excepción si el email ya existe")
    void createCredential_throwsIfEmailAlreadyRegistered() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("pass");
        request.setUserId(1L);

        when(credentialRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> credentialService.createCredential(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("createCredential: crea con rol por defecto USER si el rol viene null")
    void createCredential_usesDefaultRoleIfNullRole() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("nuevo@example.com");
        request.setPassword("pass");
        request.setUserId(2L);
        request.setRole(null);

        when(credentialRepository.existsByEmail("nuevo@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");
        when(credentialRepository.save(any(Credential.class))).thenReturn(credential);

        Credential result = credentialService.createCredential(request);

        assertThat(result).isNotNull();
        verify(credentialRepository).save(argThat(c -> c.getClass().equals(Credential.class)));
    }

    @Test
    @DisplayName("createCredential: crea credencial con rol explícito")
    void createCredential_createsWithExplicitRole() {
        RoleDTO roleDTO = new RoleDTO("VISITOR", Set.of());

        RegisterRequest request = new RegisterRequest();
        request.setEmail("visitor@example.com");
        request.setPassword("pass");
        request.setUserId(3L);
        request.setRole(roleDTO);

        when(credentialRepository.existsByEmail("visitor@example.com")).thenReturn(false);
        when(passwordEncoder.encode("pass")).thenReturn("encoded-pass");
        when(credentialRepository.save(any(Credential.class))).thenReturn(credential);

        Credential result = credentialService.createCredential(request);

        assertThat(result).isNotNull();
        verify(credentialRepository).save(any(Credential.class));
    }

    @Test
    @DisplayName("changePassword: lanza excepción si el email no existe")
    void changePassword_throwsIfEmailNotFound() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("noexiste@example.com");
        request.setCurrentPassword("old");
        request.setNewPassword("new");

        when(credentialRepository.findByEmail("noexiste@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> credentialService.changePassword(request))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("changePassword: lanza excepción si la contraseña actual es incorrecta")
    void changePassword_throwsIfCurrentPasswordIncorrect() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("test@example.com");
        request.setCurrentPassword("wrong");
        request.setNewPassword("newpass");

        when(credentialRepository.findByEmail("test@example.com")).thenReturn(Optional.of(credential));
        when(credential.getPassword()).thenReturn("encoded-correct");
        when(passwordEncoder.matches("wrong", "encoded-correct")).thenReturn(false);

        assertThatThrownBy(() -> credentialService.changePassword(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Current password is incorrect");
    }

    @Test
    @DisplayName("changePassword: cambia la contraseña exitosamente")
    void changePassword_updatesPasswordSuccessfully() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setEmail("test@example.com");
        request.setCurrentPassword("correct");
        request.setNewPassword("newpass");

        when(credentialRepository.findByEmail("test@example.com")).thenReturn(Optional.of(credential));
        when(credential.getPassword()).thenReturn("encoded-correct");
        when(passwordEncoder.matches("correct", "encoded-correct")).thenReturn(true);
        when(passwordEncoder.encode("newpass")).thenReturn("encoded-newpass");

        credentialService.changePassword(request);

        verify(credential).setPassword("encoded-newpass");
        verify(credentialRepository).save(credential);
    }


    @Test
    @DisplayName("recoverPassword: lanza excepción si el email no está registrado")
    void recoverPassword_throwsIfEmailNotFound() {
        when(credentialRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> credentialService.recoverPassword("ghost@example.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Email not registered");
    }

    @Test
    @DisplayName("recoverPassword: genera y guarda contraseña temporal")
    void recoverPassword_generatesAndSavesTemporaryPassword() {
        when(credentialRepository.findByEmail("test@example.com")).thenReturn(Optional.of(credential));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-temp");

        String tempPassword = credentialService.recoverPassword("test@example.com");

        assertThat(tempPassword).isNotBlank();
        assertThat(tempPassword).hasSizeLessThanOrEqualTo(8);
        verify(credential).setPassword("encoded-temp");
        verify(credentialRepository).save(credential);
    }


    @Test
    @DisplayName("getCredentialByUserId: lanza excepción si no existe la credencial")
    void getCredentialByUserId_throwsIfNotFound() {
        when(credentialRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> credentialService.getCredentialByUserId(99L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Credencial no encontrada para userId");
    }

    @Test
    @DisplayName("getCredentialByUserId: retorna credencial si existe")
    void getCredentialByUserId_returnsCredentialIfFound() {

        when(credentialRepository.findByUserId(1L))
                .thenReturn(Optional.of(credential));

        when(credential.getEmail())
                .thenReturn("test@example.com");

        Credential result = credentialService.getCredentialByUserId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("getCredentialById: lanza excepción si no existe")
    void getCredentialById_throwsIfNotFound() {
        when(credentialRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> credentialService.getCredentialById(99L))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Credencial no encontrada con id");
    }

    @Test
    @DisplayName("getCredentialById: retorna credencial si existe")
    void getCredentialById_returnsCredentialIfFound() {
        when(credentialRepository.findById(1L)).thenReturn(Optional.of(credential));

        Credential result = credentialService.getCredentialById(1L);

        assertThat(result).isNotNull();
    }
}
