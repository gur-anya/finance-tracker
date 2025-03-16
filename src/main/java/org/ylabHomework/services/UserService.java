package org.ylabHomework.services;

import lombok.Getter;
import lombok.Setter;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;

import org.mindrot.jbcrypt.BCrypt;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Сервис для работы с сущностью User
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 09.03.2025
 * </p>
 */
@Getter
@Setter
public class UserService {
    private final UserRepository repository;

    /**
     * Конструктор для создания сервиса с заданным репозиторием для работы с пользователями.
     *
     * @param repository репозиторий для работы с пользователями
     */
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Создает нового пользователя с заданными данными.
     *
     * @param name     имя пользователя
     * @param email    электронная почта пользователя
     * @param password пароль пользователя; шифруется в методе
     */

    public void createUser(String name, String email, String password) {
        String encryptedPass = encrypt(password);
        try {
            repository.addUser(new User(name, email, encryptedPass, 1));
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            if ("23505".equals(sqlState)) {
                throw new IllegalArgumentException("Пользователь с email " + email + " уже существует!");
            }else if ("22001".equals(sqlState)) {
                throw new IllegalArgumentException("Слишком длинное имя, email или пароль: " + message);
            } else if ("23502".equals(sqlState)) {
                throw new IllegalArgumentException("Поля не могут быть пустыми: " + message);
            } else if (sqlState != null && sqlState.startsWith("08")) {
                throw new RuntimeException("Ошибка подключения к базе данных: " + message);
            } else {
                throw new RuntimeException("Ошибка базы данных: " + message, e);
            }
        }
    }

    /**
     * Выполняет вход пользователя в систему по электронной почте и паролю.
     *
     * @param email    электронная почта пользователя
     * @param password пароль пользователя в незашифрованном виде; сравнивается с зашифрованным в методе
     * @return объект LoginResult с результатом входа и пользователем, если вход успешен
     */
    public LoginResult loginUser(String email, String password) {
        String normalizedEmail = email.toLowerCase().trim();
        User foundUser;
        try {
            foundUser = repository.readUserByEmail(normalizedEmail);
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            if (sqlState != null && sqlState.startsWith("08")) {
                throw new RuntimeException("Ошибка подключения к базе данных: " + message);
            } else {
                throw new RuntimeException("Пользователь с таким email не найден!");
            }
        }
        if (foundUser != null && comparePass(password, normalizedEmail)) {
            return new LoginResult(true, foundUser);
        }
        return new LoginResult(false, null);
    }

    /**
     * Проверяет, приемлемо ли имя пользователя (не пусто ли оно).
     *
     * @param name имя для проверки
     * @return имя или сообщение об ошибке
     */
    public String nameCheck(String name) {
        if (name.isEmpty()) {
            return "Имя не может быть пустым! Пожалуйста, введите имя!";
        }
        return "OK";
    }

    /**
     * Проверяет, приемлема ли электронная почта пользователя (соответствует ли шаблону для электронной почты
     * и не зарегистрирована ли она уже другим пользователем).
     *
     * @param email электронная почта пользователя
     * @return "OK" если новый email корректен, "FOUND" если email существует, "INVALID" если некорректен
     */
    public String emailCheck(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        if (!isEmailValid(normalizedEmail)) {
            return "INVALID";
        }
        try {
            if (repository.getEmails().contains(normalizedEmail)) {
                return "FOUND";
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            if (sqlState != null && sqlState.startsWith("08")) {
                throw new RuntimeException("Ошибка подключения к базе данных: " + message);
            } else {
                throw new RuntimeException("Ошибка базы данных при проверке email: " + message, e);
            }
        }
        return "OK";
    }

    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern emailPattern = Pattern.compile(emailRegex);
        return emailPattern.matcher(email).matches();
    }

    /**
     * Обновляет имя пользователя.
     *
     * @param newName новое имя пользователя
     * @param email   электронная почта пользователя
     * @return новое имя или сообщение об ошибке
     */
    public String updateName(String newName, String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = repository.readUserByEmail(normalizedEmail);
        if (user == null) {
            return "Пользователь не найден!";
        }
        String res = nameCheck(newName);
        if (res.equals("OK")) {
            repository.updateName(newName, user);
            return newName + ", имя изменено успешно!";
        }
        return res;
    }

    /**
     * Обновляет электронную почту пользователя.
     *
     * @param newEmail новая электронная почта пользователя
     * @param oldEmail старая электронная почта пользователя
     * @return новая электронная почта или сообщение об ошибке
     */
    public String updateEmail(String newEmail, String oldEmail) {
        String normalizedOldEmail = oldEmail.toLowerCase().trim();
        String normalizedNewEmail = newEmail.toLowerCase().trim();
        User user = repository.readUserByEmail(normalizedOldEmail);
        if (user == null) {
            return "Пользователь не найден!";
        }
        String res = emailCheck(newEmail);
        if (res.equals("OK")) {
            repository.updateEmail(normalizedNewEmail, user);
            return "Адрес электронной почты обновлен успешно! Новый адрес: " + newEmail;
        }
        if (res.equals("INVALID")) {
            return "Пожалуйста, введите корректный email!";
        }
        return "Email уже занят!";
    }

    /**
     * Обновляет пароль пользователя.
     *
     * @param newPass новый пароль в незашифрованном виде; шифруется в методе
     * @param email   электронная почта пользователя
     */
    public void updatePassword(String newPass, String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = repository.readUserByEmail(normalizedEmail);
        String encryptedPass = encrypt(newPass);
        repository.updatePassword(encryptedPass, user);
    }

    public void updateActive(boolean isActive, String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = repository.readUserByEmail(normalizedEmail);
        repository.updateActive(isActive, user);
    }

    /**
     * Удаляет пользователя.
     *
     * @param email электронная почта пользователя, которого нужно удалить
     * @return статус удаления пользователя
     */
    public String deleteUserByEmail(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        if (repository.deleteUserByEmail(normalizedEmail)) {
            return "Пользователь " + normalizedEmail + " удален успешно!";
        } else {
            return "Не удалось найти пользователя с " + normalizedEmail + "!";
        }
    }

    /**
     * Находит пользователя по его электронной почте.
     *
     * @param email электронная почта пользователя
     * @return объект User, если пользователь найден; null иначе
     */
    public User readUserByEmail(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        return repository.readUserByEmail(normalizedEmail);
    }

    /**
     * Получает список всех зарегистрированных пользователей вне зависимости от статуса их аккаунта.
     *
     * @return список пользователей (пустой, если пользователей нет)
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(repository.getUsers());
    }

    /**
     * Проверяет совпадение паролей при регистрации.
     *
     * @param password первый введённый пароль
     * @param repeatedPass повторно введённый пароль
     * @return true, если пароли совпадают; иначе false
     */
    public boolean checkPasswordMatch(String password, String repeatedPass) {
        return password.equals(repeatedPass);
    }

    /**
     * Проверяет, активен ли пользователь.
     *
     * @param email электронная почта пользователя
     * @return true, если пользователь активен; false, если заблокирован или не найден
     */
    public boolean isUserActive(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = repository.readUserByEmail(normalizedEmail);
        return user != null && user.isActive();
    }

    /**
     * Обновляет пароль пользователя с проверкой старого пароля.
     *
     * @param oldPass старый пароль для проверки
     * @param newPass новый пароль в незашифрованном виде
     * @param email   электронная почта пользователя
     * @return сообщение об успешном обновлении или об ошибке
     */
    public String updatePassword(String oldPass, String newPass, String email) {
        String normalizedEmail = email.toLowerCase().trim();
        if (comparePass(oldPass, normalizedEmail)) {
            updatePassword(newPass, normalizedEmail);
            return "Пароль успешно обновлен!";
        }
        return "Неправильный пароль! Повторите попытку!";
    }

    /**
     * Блокирует пользователя по его email.
     *
     * @param email электронная почта пользователя
     * @return сообщение об успешной блокировке или об ошибке
     */
    public String blockUser(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = repository.readUserByEmail(normalizedEmail);
        if (user == null) {
            return "Пользователь с таким email не найден!";
        }
        if (!user.isActive()) {
            return "Пользователь уже заблокирован.";
        }
        user.setActive(false);
        repository.updateActive(false, user);
        return "Успешно";
    }

    /**
     * Разблокирует пользователя по его email.
     *
     * @param email электронная почта пользователя
     * @return сообщение об успешной разблокировке или об ошибке
     */
    public String unblockUser(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = repository.readUserByEmail(normalizedEmail);
        if (user == null) {
            return "Пользователь с таким email не найден!";
        }
        if (user.isActive()) {
            return "Пользователь не заблокирован.";
        }
        user.setActive(true);
        repository.updateActive(true, user);
        return "Успешно";
    }

    /**
     * Шифрует пароль с использованием алгоритма BCrypt.
     *
     * @param password пароль в незашифрованном виде
     * @return зашифрованный пароль
     */
    private String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }


    /**
     * Сравнивает незашифрованный пароль с зашифрованным паролем пользователя.
     *
     * @param password  введенный пароль
     * @param userEmail электронная почта пользователя
     * @return true, если пароли совпадают; иначе false
     */
    private boolean comparePass(String password, String userEmail) {
        String hashed = repository.readUserByEmail(userEmail).getPassword();
        return BCrypt.checkpw(password, hashed);
    }
    /**
     * Результат логина.
     *
     * @param success успешность логина
     * @param user пользователь, который залогинился
     */
    public record LoginResult(boolean success, User user) {}
}