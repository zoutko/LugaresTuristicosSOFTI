package com.proyecto.test.security.service;

import com.proyecto.test.security.domain.Credential;
import com.proyecto.test.security.domain.Token;
import com.proyecto.test.security.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class TokenService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-ms}")
    private long expirationMs;

    private final TokenRepository tokenRepository;

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    // ── Generación ────────────────────────────────────────────────────────

    public Token generarToken(Credential credential) {
        Date expiracion = new Date(System.currentTimeMillis() + expirationMs);

        // API actualizada de jjwt 0.12.x
        String valorJwt = Jwts.builder()
                .subject(credential.getUsername())
                .issuedAt(new Date())
                .expiration(expiracion)
                .signWith(getSigningKey())
                .compact();

        // Revocar tokens anteriores antes de guardar el nuevo
        revocarTokensAnteriores(credential.getId());

        Token token = new Token(valorJwt, expiracion, credential);
        return tokenRepository.save(token);
    }

    // ── Validación ────────────────────────────────────────────────────────

    public boolean validarToken(String valorToken, UserDetails userDetails) {
        String username = extraerUsername(valorToken);
        Token token = tokenRepository.findByValor(valorToken).orElse(null);

        if (token == null) return false;

        return username.equals(userDetails.getUsername()) && token.isValido();
    }

    public String extraerUsername(String valorToken) {
        return extraerClaims(valorToken).getSubject();
    }

    // ── Revocación ────────────────────────────────────────────────────────

    public void revocarToken(String valorToken) {
        tokenRepository.findByValor(valorToken).ifPresent(token -> {
            token.setRevocado(true);
            tokenRepository.save(token);
        });
    }

    private void revocarTokensAnteriores(Long credentialId) {
        List<Token> tokensActivos = tokenRepository.findTokensValidosByCredentialId(credentialId);
        tokensActivos.forEach(t -> t.setRevocado(true));
        tokenRepository.saveAll(tokensActivos);
    }

    // ── Utilidades ────────────────────────────────────────────────────────

    private Claims extraerClaims(String valorToken) {
        // API actualizada de jjwt 0.12.x
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(valorToken)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}