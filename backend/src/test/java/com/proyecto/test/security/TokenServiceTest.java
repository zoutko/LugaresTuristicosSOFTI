package com.proyecto.test.security;

import com.proyecto.app.security.domain.Credential;
import com.proyecto.app.security.domain.Token;
import com.proyecto.app.security.repository.TokenRepository;
import com.proyecto.app.security.service.TokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenService - Pruebas Unitarias")
class TokenServiceTest {

    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;
    private static final String SECRET = "VGhpc0lzQVN1cGVyU2VjdXJlS2V5Rm9ySFMyNTZUZXN0aW5nMTIzNDU2";

    private static final long EXPIRATION = 3_600_000L;

    private Credential credential;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secretKey", SECRET);
        ReflectionTestUtils.setField(tokenService, "expirationMs", EXPIRATION);

        credential = mock(Credential.class);
    }

    @Test
    @DisplayName("generateToken: genera y guarda el token")
    void generateToken_generatesAndPersistsToken() {

        when(credential.getId()).thenReturn(1L);
        when(credential.getUsername()).thenReturn("user@example.com");

        when(tokenRepository.findValidTokensByCredentialId(1L))
                .thenReturn(List.of());

        Token savedToken = mock(Token.class);

        when(savedToken.getToken()).thenReturn("jwt-value");

        when(tokenRepository.save(any(Token.class)))
                .thenReturn(savedToken);

        Token result = tokenService.generateToken(credential);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt-value");

        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    @DisplayName("generateToken: revoca tokens previos antes de generar uno nuevo")
    void generateToken_revokesPreviousTokensFirst() {

        when(credential.getId()).thenReturn(1L);
        when(credential.getUsername()).thenReturn("user@example.com");

        Token previousToken = mock(Token.class);

        when(tokenRepository.findValidTokensByCredentialId(1L))
                .thenReturn(List.of(previousToken));

        when(tokenRepository.save(any(Token.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        tokenService.generateToken(credential);

        verify(previousToken).setRevoked(true);
        verify(tokenRepository).saveAll(List.of(previousToken));
    }


    @Test
    @DisplayName("validateToken: retorna false si el token no existe en el repositorio")
    void validateToken_returnsFalseIfTokenNotInRepository() {

        when(credential.getId()).thenReturn(1L);
        when(credential.getUsername()).thenReturn("user@example.com");

        when(tokenRepository.findValidTokensByCredentialId(1L))
                .thenReturn(List.of());

        Token savedToken = new Token();

        when(tokenRepository.save(any(Token.class)))
                .thenAnswer(inv -> {
                    Token t = inv.getArgument(0);
                    savedToken.setToken(t.getToken());
                    return t;
                });

        tokenService.generateToken(credential);

        when(tokenRepository.findByToken(anyString()))
                .thenReturn(Optional.empty());

        UserDetails userDetails = mock(UserDetails.class);
        boolean valid = tokenService.validateToken(savedToken.getToken(), userDetails);

        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("validateToken: retorna true si el token es válido y el usuario coincide")
    void validateToken_returnsTrueForValidToken() {

        when(credential.getId()).thenReturn(1L);
        when(credential.getUsername()).thenReturn("user@example.com");

        when(tokenRepository.findValidTokensByCredentialId(1L))
                .thenReturn(List.of());

        final String[] jwtHolder = new String[1];

        when(tokenRepository.save(any(Token.class)))
                .thenAnswer(inv -> {
                    Token t = inv.getArgument(0);
                    jwtHolder[0] = t.getToken();
                    return t;
                });

        tokenService.generateToken(credential);

        Token storedToken = mock(Token.class);

        when(storedToken.isValid()).thenReturn(true);

        when(tokenRepository.findByToken(jwtHolder[0]))
                .thenReturn(Optional.of(storedToken));

        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername())
                .thenReturn("user@example.com");

        boolean valid = tokenService.validateToken(jwtHolder[0], userDetails);

        assertThat(valid).isTrue();
    }

    @Test
    @DisplayName("validateToken: retorna false si el token está revocado")
    void validateToken_returnsFalseIfTokenRevoked() {

        when(credential.getId()).thenReturn(1L);
        when(credential.getUsername()).thenReturn("user@example.com");

        when(tokenRepository.findValidTokensByCredentialId(1L))
                .thenReturn(List.of());

        final String[] jwtHolder = new String[1];

        when(tokenRepository.save(any(Token.class)))
                .thenAnswer(inv -> {
                    Token t = inv.getArgument(0);
                    jwtHolder[0] = t.getToken();
                    return t;
                });

        tokenService.generateToken(credential);

        Token storedToken = mock(Token.class);

        when(storedToken.isValid()).thenReturn(false);

        when(tokenRepository.findByToken(jwtHolder[0]))
                .thenReturn(Optional.of(storedToken));

        UserDetails userDetails = mock(UserDetails.class);

        when(userDetails.getUsername())
                .thenReturn("user@example.com");

        boolean valid = tokenService.validateToken(jwtHolder[0], userDetails);

        assertThat(valid).isFalse();
    }


    @Test
    @DisplayName("extractUsername: extrae el usuario del JWT")
    void extractUsername_extractsSubjectFromJwt() {

        when(credential.getId()).thenReturn(1L);
        when(credential.getUsername()).thenReturn("user@example.com");

        when(tokenRepository.findValidTokensByCredentialId(1L))
                .thenReturn(List.of());

        final String[] jwtHolder = new String[1];

        when(tokenRepository.save(any(Token.class)))
                .thenAnswer(inv -> {
                    Token t = inv.getArgument(0);
                    jwtHolder[0] = t.getToken();
                    return t;
                });

        tokenService.generateToken(credential);

        String username = tokenService.extractUsername(jwtHolder[0]);

        assertThat(username).isEqualTo("user@example.com");
    }


    @Test
    @DisplayName("revokeToken: revoca el token si existe")
    void revokeToken_revokesTokenIfPresent() {

        Token storedToken = mock(Token.class);

        when(tokenRepository.findByToken("some-jwt"))
                .thenReturn(Optional.of(storedToken));

        tokenService.revokeToken("some-jwt");

        verify(storedToken).setRevoked(true);
        verify(tokenRepository).save(storedToken);
    }

    @Test
    @DisplayName("revokeToken: no hace nada si el token no existe")
    void revokeToken_doesNothingIfTokenNotFound() {

        when(tokenRepository.findByToken("ghost-token"))
                .thenReturn(Optional.empty());

        tokenService.revokeToken("ghost-token");

        verify(tokenRepository, never()).save(any());
    }
}