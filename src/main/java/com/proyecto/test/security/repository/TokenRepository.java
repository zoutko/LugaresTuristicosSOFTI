package com.proyecto.test.security.repository;

import com.proyecto.test.security.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByValor(String valor);

    // Todos los tokens válidos (no revocados, no expirados) de una credencial
    @Query("""
        SELECT t FROM Token t
        WHERE t.credential.id = :credentialId
        AND t.revocado = false
    """)
    List<Token> findTokensValidosByCredentialId(Long credentialId);
}