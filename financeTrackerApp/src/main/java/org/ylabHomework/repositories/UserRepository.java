package org.ylabHomework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.ylabHomework.models.User;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностью User через базу данных.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 01.08.2025
 * </p>
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

}
