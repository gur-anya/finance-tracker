package org.ylabHomework.repositories;

import org.ylabHomework.models.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Репозиторий для работы с сущностью User
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 19.10.2024
 * </p>
 */
public class UserRepository {
    private final Map<String, User> users;

    /**
     * Конструктор для инициализации репозитория для работы с пользователем, используя коллекции вместо сессии соединения с базой данных.
     */
    public UserRepository() {
        this.users = new HashMap<>();
    }

    /**
     * Находит всех созданных пользователей.
     *
     * @return список из всех пользователей
     */
    public Set<User> getUsers() {
        return new HashSet<>(users.values());
    }

    /**
     * Добавляет новую запись в коллекцию пользователей. Аккаунт пользователя всегда имеет роль 1 (обычный пользователь),
     * изначально активен.
     *
     * @param user новый пользователь
     */
    public void addUser(User user) {
        String normalizedEmail = user.getEmail().toLowerCase().trim();
        user.setEmail(normalizedEmail);
        user.setActive(true);
        users.put(normalizedEmail, user);
    }

    /**
     * Находит пользователя по заданному адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя; нормализуется в методе
     * @return объект User, если пользователь найден; null иначе
     */
    public User readUserByEmail(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        return users.get(normalizedEmail);
    }

    /**
     * Удаляет пользователя по заданному адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя; нормализуется в методе
     */
    public void deleteUserByEmail(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        users.remove(normalizedEmail);
    }

    /**
     * Обновляет имя заданного пользователя.
     *
     * @param newName новое имя для пользователя
     * @param user    пользователь, для которого обновляется имя
     */
    public void updateName(String newName, User user) {
            user.setName(newName);
    }

    /**
     * Обновляет адрес электронной почты пользователя.
     *
     * @param newEmail новая электронная почта пользователя
     * @param user     пользователь, для которого обновляется электронная почта
     */
    public void updateEmail(String newEmail, User user) {
            String oldEmail = user.getEmail().toLowerCase().trim();
            String normalizedNewEmail = newEmail.toLowerCase().trim();
            users.remove(oldEmail);
            users.put(normalizedNewEmail, user);
    }

    /**
     * Присваивает пользователю новый зашифрованный пароль.
     *
     * @param newPass новый зашифрованный пароль
     * @param user    пользователь, для которого обновляется пароль
     */
    public void updatePassword(String newPass, User user) {
            user.setPassword(newPass);
    }

    /**
     * Изменяет статус активности аккаунта пользователя.
     *
     * @param isActive новый статус активности аккаунта пользователя
     * @param user     пользователь, для которого обновляется статус
     */
    public void updateActive(boolean isActive, User user) {
            user.setActive(isActive);
    }

    /**
     * Возвращает все зарегистрированные email.
     *
     * @return зарегистрированные email
     */
    public Set<String> getEmails(){
        return users.keySet();
    }

}