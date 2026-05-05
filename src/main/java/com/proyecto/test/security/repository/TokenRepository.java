package com.proyecto.test.security.repository;

import com.proyecto.test.security.domain.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByValor(String valor);
    
    void deleteByCredentialId(Long credentialId);

    @Query("""
                SELECT t FROM Token t
                WHERE t.credential.id = :credentialId
                AND t.revocado = false
            """)
    List<Token> findTokensValidosByCredentialId(@Param("credentialId") Long credentialId);
}