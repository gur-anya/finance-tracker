package org.ylabHomework.services;


import org.mindrot.jbcrypt.BCrypt;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;

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
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Создаёт нового пользователя с заданными данными.
     *
     * @param name     имя пользователя
     * @param email    электронная почта пользователя
     * @param password пароль пользователя (шифруется в методе)
     * @return сообщение об успешном создании или об ошибке
     */
    public String createUser(String name, String email, String password) {
        String normalizedEmail = email.toLowerCase().trim();
        String nameCheckResult = nameCheck(name);
        if (!nameCheckResult.equals("OK")) {
            return nameCheckResult + " Попробуйте ещё раз!";
        }
        String emailCheckResult = emailCheck(normalizedEmail);
        if (!emailCheckResult.equals("OK")) {
            return emailCheckResult.equals("FOUND")
                    ? "Пользователь с таким email уже существует! Попробуйте ещё раз!"
                    : "Пожалуйста, введите корректный email! Попробуйте ещё раз!";
        }
        if (password == null || password.trim().isEmpty()) {
            return "Пароль не может быть пустым! Попробуйте ещё раз!";
        }

        String encryptedPass = encrypt(password);
        try {
            repository.addUser(new User(name, normalizedEmail, encryptedPass, 1));
            return "Регистрация прошла успешно!";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            if ("22001".equals(sqlState)) {
                return "Слишком длинное имя, email или пароль: " + message + " Попробуйте ещё раз!";
            } else {
                return databaseError(e);
            }

        }
    }

    /**
     * Выполняет вход пользователя в систему по email и паролю.
     *
     * @param email    электронная почта пользователя
     * @param password пароль пользователя (незашифрованный)
     * @return объект LoginResult с результатом входа
     */
    public LoginResult loginUser(String email, String password) {
        String normalizedEmail = email.toLowerCase().trim();
        try {
            User foundUser = repository.readUserByEmail(normalizedEmail);
            if (comparePass(password, foundUser)) {
                return new LoginResult(true, foundUser);
            }
            return new LoginResult(false, null);
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return new LoginResult(false, null);
        }
    }

    /**
     * Проверяет, приемлемо ли имя пользователя.
     *
     * @param name имя для проверки
     * @return "OK" если корректно, иначе сообщение об ошибке
     */
    public String nameCheck(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Имя не может быть пустым! Пожалуйста, введите имя!";
        }
        return "OK";
    }

    /**
     * Проверяет, приемлема ли электронная почта пользователя.
     *
     * @param email электронная почта пользователя
     * @return "OK" если новый email корректен, "FOUND" если существует, "INVALID" если некорректен
     */
    public String emailCheck(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        if (!isEmailValid(normalizedEmail)) {
            return "INVALID";
        }
        try {
            if (repository.readUserByEmail(normalizedEmail) != null) {
                return "FOUND";
            }
            return "OK";
        } catch (SQLException e) {
            return databaseError(e);
        }
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
     * @param email   текущая электронная почта пользователя
     * @return сообщение об успешном обновлении или об ошибке
     */
    public String updateName(String newName, String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = readUserByEmail(normalizedEmail);
        if (user == null) {
            return "Пользователь не найден! Попробуйте ещё раз!";
        }
        String res = nameCheck(newName);
        if (!res.equals("OK")) {
            return res + " Попробуйте ещё раз!";
        }
        try {
            repository.updateName(newName, user);
            return "Имя успешно изменено на " + newName + "!";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            if ("22001".equals(sqlState)) {
                return "Слишком длинное имя: " + message + " Попробуйте ещё раз!";
            } else {
                return databaseError(e);
            }

        }
    }

    /**
     * Обновляет электронную почту пользователя.
     *
     * @param newEmail новая электронная почта пользователя
     * @param oldEmail старая электронная почта пользователя
     * @return сообщение об успешном обновлении или об ошибке
     */
    public String updateEmail(String newEmail, String oldEmail) {
        String normalizedOldEmail = oldEmail.toLowerCase().trim();
        String normalizedNewEmail = newEmail.toLowerCase().trim();
        User user = readUserByEmail(normalizedOldEmail);
        if (user == null) {
            return "Пользователь не найден! Попробуйте ещё раз!";
        }
        String res = emailCheck(normalizedNewEmail);
        if (!res.equals("OK")) {
            if (res.equals("INVALID")) {
                return "Пожалуйста, введите корректный email! Попробуйте ещё раз!";
            } else if (res.equals("FOUND")) {
                return "Email уже занят! Попробуйте ещё раз!";
            }
            return res;
        }
        try {
            repository.updateEmail(normalizedNewEmail, user);
            return "Адрес электронной почты обновлён на " + normalizedNewEmail + "!";
        } catch (SQLException e) {
                return databaseError(e);
            }
    }

    /**
     * Обновляет пароль пользователя без проверки старого пароля.
     *
     * @param newPass новый пароль (незашифрованный)
     * @param email   электронная почта пользователя
     * @return сообщение об успешном обновлении или об ошибке
     */
    public String updatePassword(String newPass, String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = readUserByEmail(normalizedEmail);
        if (user == null) {
            return "Пользователь не найден! Попробуйте ещё раз!";
        }
        if (newPass == null || newPass.trim().isEmpty()) {
            return "Пароль не может быть пустым! Попробуйте ещё раз!";
        }
        String encryptedPass = encrypt(newPass);
        try {
            repository.updatePassword(encryptedPass, user);
            return "Пароль успешно обновлён!";
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            if ("22001".equals(sqlState)) {
                return "Слишком длинный пароль: " + message + " Попробуйте ещё раз!";
            } else {
               return databaseError(e);
            }

        }
    }

    /**
     * Обновляет пароль пользователя с проверкой старого пароля.
     *
     * @param oldPass старый пароль для проверки
     * @param newPass новый пароль (незашифрованный)
     * @param email   электронная почта пользователя
     * @return сообщение об успешном обновлении или об ошибке
     */
    public String updatePassword(String oldPass, String newPass, String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = readUserByEmail(normalizedEmail);
        if (user == null) {
            return "Пользователь не найден! Попробуйте ещё раз!";
        }
        if (!comparePass(oldPass, user)) {
            return "Неправильный старый пароль! Попробуйте ещё раз!";
        }
        return updatePassword(newPass, normalizedEmail);
    }

    /**
     * Удаляет пользователя по email.
     *
     * @param email электронная почта пользователя
     * @return сообщение об успешном удалении или об ошибке
     */
    public String deleteUserByEmail(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = readUserByEmail(normalizedEmail);
        if (user == null) {
            return "Пользователь с email " + normalizedEmail + " не найден! Попробуйте ещё раз!";
        }
        try {
            if (repository.deleteUserByEmail(normalizedEmail)) {
                return "Пользователь " + normalizedEmail + " успешно удалён!";
            }
            return "Не удалось удалить пользователя! Попробуйте ещё раз!";
        } catch (SQLException e) {
           return databaseError(e);
        }
    }

    /**
     * Находит пользователя по email.
     *
     * @param email электронная почта пользователя
     * @return объект User или null, если не найден
     */
    public User readUserByEmail(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        try {
            return repository.readUserByEmail(normalizedEmail);
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return null;
        }
    }

    /**
     * Получает список всех пользователей.
     *
     * @return список пользователей или пустой список при ошибке
     */
    public List<User> getAllUsers() {
        try {
            return new ArrayList<>(repository.getUsers());
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return new ArrayList<>();
        }
    }

    /**
     * Проверяет совпадение паролей при регистрации.
     *
     * @param password     первый пароль
     * @param repeatedPass повторный пароль
     * @return true, если совпадают; false иначе
     */
    public boolean checkPasswordMatch(String password, String repeatedPass) {
        return password != null && password.equals(repeatedPass);
    }

    /**
     * Проверяет, активен ли пользователь.
     *
     * @param email электронная почта пользователя
     * @return true, если активен; false, если заблокирован или не найден
     */
    public boolean isUserActive(String email) {
        User user = readUserByEmail(email);
        return user != null && user.isActive();
    }

    /**
     * Блокирует пользователя по email.
     *
     * @param email электронная почта пользователя
     * @return сообщение об успешной блокировке или об ошибке
     */
    public String blockUser(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = readUserByEmail(normalizedEmail);
        if (user == null) {
            return "Пользователь с email " + normalizedEmail + " не найден! Попробуйте ещё раз!";
        }
        if (!user.isActive()) {
            return "Пользователь уже заблокирован! Попробуйте ещё раз!";
        }
        try {
            repository.updateActive(false, user);
            user.setActive(false);
            return "Пользователь " + normalizedEmail + " успешно заблокирован!";
        } catch (SQLException e) {
            return databaseError(e);
        }
    }

    /**
     * Разблокирует пользователя по email.
     *
     * @param email электронная почта пользователя
     * @return сообщение об успешной разблокировке или об ошибке
     */
    public String unblockUser(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        User user = readUserByEmail(normalizedEmail);
        if (user == null) {
            return "Пользователь с email " + normalizedEmail + " не найден! Попробуйте ещё раз!";
        }
        if (user.isActive()) {
            return "Пользователь не заблокирован!";
        }
        try {
            repository.updateActive(true, user);
            user.setActive(true);
            return "Пользователь " + normalizedEmail + " успешно разблокирован!";
        } catch (SQLException e) {
            return databaseError(e);
        }
    }

    /**
     * Шифрует пароль с использованием BCrypt.
     *
     * @param password пароль (незашифрованный)
     * @return зашифрованный пароль
     */
    private String encrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    /**
     * Сравнивает незашифрованный пароль с зашифрованным.
     *
     * @param password пароль (незашифрованный)
     * @param user     пользователь с зашифрованным паролем
     * @return true, если пароли совпадают; false иначе
     */
    public boolean comparePass(String password, User user) {
        if (user == null || password == null || user.getPassword() == null) {
            return false;
        }
        return BCrypt.checkpw(password, user.getPassword());
    }

    public String databaseError(Exception e) {
        return "Ошибка базы данных: " + e.getMessage() + " Попробуйте ещё раз!";
    }

    public UserRepository getRepository() {
        return repository;
    }

    /**
     * Результат логина.
     *
     * @param success успешность логина
     * @param user    пользователь, если логин успешен
     */
    public record LoginResult(boolean success, User user) {
    }
}