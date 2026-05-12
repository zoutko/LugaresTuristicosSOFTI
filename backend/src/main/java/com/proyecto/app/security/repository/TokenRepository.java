package com.proyecto.app.security.repository;

import com.proyecto.app.security.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    void deleteByCredential_Id(Long credentialId);

    @Query("""
            SELECT t FROM Token t
            WHERE t.credential.id = :credentialId
            AND t.revoked = false
            AND t.expirationDate > CURRENT_TIMESTAMP
           """)
    List<Token> findValidTokensByCredentialId(@Param("credentialId") Long credentialId);
}