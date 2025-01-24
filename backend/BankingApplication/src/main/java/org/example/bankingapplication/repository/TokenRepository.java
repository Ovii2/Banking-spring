package org.example.bankingapplication.repository;

import org.example.bankingapplication.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Query("""
            SELECT t
            FROM Token t
            INNER JOIN User u ON t.user.id = u.id
            WHERE u.id = :userID AND (t.isExpired = FALSE OR t.isRevoked = FALSE)
            """)
    List<Token> findAllValidTokensByUser(UUID userID);

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND (t.isExpired = false AND t.isRevoked = false)")
    List<Token> findAllValidTokensByUserId(@Param("userId") UUID userId);

    Optional<Token> findByToken(String token);

    List<Token> findAllByUserId(UUID userId);
}
