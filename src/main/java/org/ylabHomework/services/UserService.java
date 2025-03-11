package org.ylabHomework.services;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.UserRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Сервис для работы с сущностью User
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 19.10.2024
 * </p>
 */
@Getter
@Setter
public class UserService {
    private final UserRepository repository;
    private final byte[] salt = generateSalt();



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
        User user = new User(name, email.toLowerCase().trim(), encryptedPass, 1);
        repository.addUser(user);
    }

    /**
     * Выполняет вход пользователя в систему по электронной почте и паролю.
     *
     * @param email    электронная почта пользователя
     * @param password пароль пользователя в незашифрованном виде; сравнивается с зашифрованным в методе
     * @return массив, где первый элемент - результат входа true/false,
     * а второй элемент - сообщение о причине неудачи при входе или имя пользователя
     */
    public LoginResult loginUser(String email, String password) {
        User foundUser = repository.readUserByEmail(email);
        if (comparePass(password, foundUser.getEmail())) {
            return new LoginResult(true, foundUser);
        } else {
            return new LoginResult(false, null);
        }
    }

    private byte[] generateSalt() {
        byte[] salt = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        return salt;
    }

    /**
     * Шифрует пароль с использованием соли.
     *
     * @param data пароль в незашифрованном виде
     * @return зашифрованный пароль
     */
    public String encrypt(String data) {
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            char c = data.charAt(i);
            char encryptedChar = (char) (c + salt[0]);
            encrypted.append(encryptedChar);
        }
        return encrypted.toString();
    }

    private String decrypt(String encryptedData) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < encryptedData.length(); i++) {
            char c = encryptedData.charAt(i);
            char decryptedChar = (char) (c - salt[0]);
            decrypted.append(decryptedChar);
        }
        return decrypted.toString();
    }

    /**
     * Сравнивает незашифрованный пароль с зашифрованным паролем пользователя.
     *
     * @param passToCheck введенный пароль
     * @param userEmail   электронная почта пользователя
     * @return true, если пароли совпадают; иначе false
     */
    public boolean comparePass(String passToCheck, String userEmail) {
        User foundUser = repository.readUserByEmail(userEmail);
        if (foundUser == null) {
            return false;
        }
        String userPass = decrypt(foundUser.getPassword());
        return userPass.equals(passToCheck);
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
        } else {
            return name;
        }
    }

    /**
     * Проверяет, приемлема ли электронная почта пользователя (соответствует ли шаблону для электронной почты
     * и не зарегистрирована ли она уже другим пользователем).
     *
     * @param email электронная почта пользователя
     * @return электронная почта или сообщение об ошибке
     */
    public String emailCheck(String email) {
        if (!isEmailValid(email)) {
            return "Пожалуйста, введите корректный email!";
        } else {
            String normalizedEmail = email.toLowerCase().trim();
            if (repository.getEmails().contains(normalizedEmail)) {
                return "Пользователь с таким email уже зарегистрирован!";
            }
        }
        return email;
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
        User user = repository.readUserByEmail(email);
        if (user == null) {
            return "Пользователь не найден!";
        }
        String res = nameCheck(newName);
        if (res.equals(newName)) {
            repository.updateName(newName, user);
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
        User user = repository.readUserByEmail(oldEmail);
        if (user == null) {
            return "Пользователь не найден!";
        }
        String res = emailCheck(newEmail);
        if (res.equals(newEmail)) {
            repository.updateEmail(newEmail, user);
        }
        return res;
    }

    /**
     * Обновляет пароль пользователя.
     *
     * @param newPass новый пароль в незашифрованном виде; шифруется в методе
     * @param email   электронная почта пользователя
     */
    public void updatePassword(String newPass, String email) {
        User user = repository.readUserByEmail(email);
        String encryptedPass = encrypt(newPass);
        repository.updatePassword(encryptedPass, user);
    }

    public void updateActive(boolean isActive, String email){
        User user = repository.readUserByEmail(email);
        repository.updateActive(isActive, user);
    }
    /**
     * Удаляет пользователя.
     *
     * @param email электронная почта пользователя, которого нужно удалить
     */
    public void deleteUserByEmail(String email) {
        repository.deleteUserByEmail(email);
    }

    /**
     * Находит пользователя по его электронной почте.
     *
     * @param email электронная почта пользователя
     * @return объект User, если пользователь найден; null иначе
     */
    public User readUserByEmail(String email) {
        return repository.readUserByEmail(email);
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
     * Результат логина.
     *
     * @param success успешность логина
     * @param user пользователь, который залогинился
     */
    public record LoginResult (boolean success, User user) {}
}