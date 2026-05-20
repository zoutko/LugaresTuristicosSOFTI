package com.proyecto.app.security.service;

import com.proyecto.app.security.domain.Credential;
import com.proyecto.app.security.domain.Token;
import com.proyecto.app.security.repository.TokenRepository;
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

    public Token generateToken(Credential credential) {
        Date expiration = new Date(System.currentTimeMillis() + expirationMs);

        String jwt = Jwts.builder()
                .subject(credential.getUsername())
                .claim("userId", credential.getUserId())  
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();

        revokePreviousTokens(credential.getId());

        Token token = new Token(jwt, expiration, credential);
        return tokenRepository.save(token);
    }

    public boolean validateToken(String tokenValue, UserDetails userDetails) {
        String username = extractUsername(tokenValue);
        Token token = tokenRepository.findByToken(tokenValue).orElse(null);

        if (token == null) return false;

        return username.equals(userDetails.getUsername()) && token.isValid();
    }

    public String extractUsername(String tokenValue) {
        return extractClaims(tokenValue).getSubject();
    }

    public void revokeToken(String tokenValue) {
        tokenRepository.findByToken(tokenValue).ifPresent(token -> {
            token.setRevoked(true);
            tokenRepository.save(token);
        });
    }

    private void revokePreviousTokens(Long credentialId) {
        List<Token> activeTokens = tokenRepository.findValidTokensByCredentialId(credentialId);
        activeTokens.forEach(t -> t.setRevoked(true));
        tokenRepository.saveAll(activeTokens);
    }

    private Claims extractClaims(String tokenValue) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(tokenValue)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}