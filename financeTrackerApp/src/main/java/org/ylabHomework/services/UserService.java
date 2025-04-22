package org.ylabHomework.services;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.customExceptions.CustomDatabaseException;
import org.ylabHomework.serviceClasses.customExceptions.EmailAlreadyExistsException;
import org.ylabHomework.serviceClasses.customExceptions.EmptyValueException;
import org.ylabHomework.serviceClasses.customExceptions.NoUpdatesException;
import org.ylabHomework.serviceClasses.springConfigs.security.UserDetailsImpl;

import java.sql.SQLException;

/**
 * Сервис для работы с сущностью User.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 09.03.2025
 */
@Service
@Slf4j
@Data
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedEmail = normalizeEmail(username);
        User user = readUserByEmail(normalizedEmail);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь с email " + normalizedEmail + " не найден");
        }
        return UserDetailsImpl.build(user);
    }

    /**
     * Создаёт нового пользователя с заданными данными.
     */
    public void createUser(User newUser) {
        String normalizedEmail = normalizeEmail(newUser.getEmail());
        if (repository.readUserByEmail(normalizedEmail) != null) {
            throw new EmailAlreadyExistsException();
        }
        String encryptedPass = passwordEncoder.encode(newUser.getPassword());
        newUser.setEmail(normalizedEmail);
        newUser.setPassword(encryptedPass);
        newUser.setRole(1);
        newUser.setActive(true);
        newUser.setMonthlyBudget(0.0);
        newUser.setGoal(0.0);
        try {
            repository.addUser(newUser);
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    public void updateUserEmail(String newEmail, User user) {
        if (newEmail == null || newEmail.trim().isEmpty()) {
            throw new EmptyValueException("email");
        }
        String normalizedEmail = normalizeEmail(newEmail);
        if (repository.readUserByEmail(normalizedEmail) != null) {
            throw new EmailAlreadyExistsException();
        }
        try {
            repository.updateEmail(normalizedEmail, user);
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    public void updateUserName(String newName, User user) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new EmptyValueException("имя");
        }
        try {
            repository.updateName(newName, user);
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    public void updateUserPassword(String newPassword, String oldPassword, User user) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new EmptyValueException("пароль");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Неверный текущий пароль!");
        }
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new NoUpdatesException("пароль");
        }
        try {
            repository.updatePassword(passwordEncoder.encode(newPassword), user);
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    /**
     * Удаляет пользователя.
     *
     * @return сообщение об успешном удалении или об ошибке
     */
    public boolean deleteUser(User user) {
        try {
            return repository.deleteUserByEmail(normalizeEmail(user.getEmail()));
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    public double getGoal(int userId) {
        User user = repository.readUserById(userId);
        if (user == null) {
            log.error("Пользователь с id {} = null", userId);
            return -1;
        }
        return user.getGoal();
    }

    public User readUserByEmail(String email) {
        return repository.readUserByEmail(email);
    }

    private String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.toLowerCase().trim();
    }
}

