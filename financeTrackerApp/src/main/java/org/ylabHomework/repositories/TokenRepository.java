package org.ylabHomework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ylabHomework.models.Token;

import java.util.Optional;

/**
 * Репозиторий для записи токена в базу данных и чтения токена из базы данных.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 01.08.2025
 * </p>
 */
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenValue(String tokenValue);
}
