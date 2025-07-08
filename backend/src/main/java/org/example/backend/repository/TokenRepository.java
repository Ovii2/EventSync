package org.example.backend.repository;

import org.example.backend.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    List<Token> findAllByUserIdAndExpiredIsFalseAndRevokedIsFalse(UUID userId);
    Optional<Token> findByToken(String token);
    List<Token> findAllByUserId(UUID userId);
}
