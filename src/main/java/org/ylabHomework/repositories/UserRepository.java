package org.ylabHomework.repositories;

import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Config;
import org.ylabHomework.serviceClasses.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Репозиторий для работы с сущностью User через базу данных.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 09.03.2025
 * </p>
 */
public class UserRepository {

    /**
     * Конструктор для инициализации репозитория для работы с пользователями.
     * Соединение с базой данных устанавливается для каждой операции отдельно.
     */
    public UserRepository() {
    }

    /**
     * Находит всех созданных пользователей. В случае выброса SQLException выводится содержимое исключения.
     *
     * @return список из всех пользователей
     */
    public Set<User> getUsers() throws SQLException {
        Config config = new Config();
        Set<User> users = new HashSet<>();
        String sql = Constants.FIND_ALL_USERS;

        Connection connection = config.establishConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            User user = new User(
                    resultSet.getString("name"),
                    resultSet.getString("email").toLowerCase().trim(),
                    resultSet.getString("password"),
                    resultSet.getInt("role_id"));
            user.setActive(resultSet.getBoolean("is_active"));
            users.add(user);
        }
        return users;
    }

    /**
     * Добавляет новую запись в таблицу пользователей. Аккаунт пользователя изначально активен и имеет роль 1 (обычный пользователь).
     *
     * @param user новый пользователь для добавления
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void addUser(User user) throws SQLException {
        Config config = new Config();
        String normalizedEmail = user.getEmail().toLowerCase().trim();
        String sql = Constants.ADD_USER;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, normalizedEmail);
            statement.setString(3, user.getPassword());
            statement.setInt(4, 1);
            statement.setBoolean(5, true);

            statement.executeUpdate();
        }
    }

    /**
     * Находит пользователя по заданному адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя; нормализуется в методе
     * @return объект User, если пользователь найден; null иначе
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public User readUserByEmail(String email) throws SQLException {
        Config config = new Config();
        String normalizedEmail = email.toLowerCase().trim();
        String sql = Constants.FIND_USER_BY_EMAIL;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizedEmail);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User(
                            resultSet.getString("name"),
                            resultSet.getString("email").toLowerCase().trim(),
                            resultSet.getString("password"),
                            resultSet.getInt("role_id"));
                    user.setActive(resultSet.getBoolean("is_active"));
                    return user;
                }
            }
        }
        return null;
    }

    /**
     * Удаляет пользователя по заданному адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя; нормализуется в методе
     * @return true, если удаление успешно; false иначе
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public boolean deleteUserByEmail(String email) throws SQLException {
        Config config = new Config();
        String normalizedEmail = email.toLowerCase().trim();
        String sql = Constants.DELETE_USER_BY_EMAIL;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizedEmail);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Обновляет имя заданного пользователя.
     *
     * @param newName новое имя для пользователя
     * @param user    пользователь, для которого обновляется имя
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void updateName(String newName, User user) throws SQLException {
        Config config = new Config();
        String sql = Constants.UPDATE_USER_NAME;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newName);
            statement.setString(2, user.getEmail().toLowerCase().trim());
            statement.executeUpdate();
        }
    }

    /**
     * Обновляет адрес электронной почты пользователя.
     *
     * @param newEmail новая электронная почта пользователя; нормализуется в методе
     * @param user     пользователь, для которого обновляется электронная почта
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void updateEmail(String newEmail, User user) throws SQLException {
        Config config = new Config();
        String normalizedNewEmail = newEmail.toLowerCase().trim();
        String sql = Constants.UPDATE_USER_EMAIL;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizedNewEmail);
            statement.setString(2, user.getEmail().toLowerCase().trim());
            statement.executeUpdate();
        }
    }

    /**
     * Присваивает пользователю новый зашифрованный пароль.
     *
     * @param newPass новый зашифрованный пароль
     * @param user    пользователь, для которого обновляется пароль
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void updatePassword(String newPass, User user) throws SQLException {
        Config config = new Config();
        String sql = Constants.UPDATE_USER_PASSWORD;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newPass);
            statement.setString(2, user.getEmail().toLowerCase().trim());
            statement.executeUpdate();
        }
    }

    /**
     * Изменяет статус активности аккаунта пользователя.
     *
     * @param isActive новый статус активности аккаунта пользователя
     * @param user     пользователь, для которого обновляется статус
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void updateActive(boolean isActive, User user) throws SQLException {
        Config config = new Config();
        String sql = Constants.UPDATE_USER_ACTIVITY;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, isActive);
            statement.setString(2, user.getEmail().toLowerCase().trim());
            statement.executeUpdate();
        }
    }

    /**
     * Возвращает все зарегистрированные адреса электронной почты.
     *
     * @return список зарегистрированных email
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public Set<String> getEmails() throws SQLException {
        Config config = new Config();
        Set<String> emails = new HashSet<>();
        String sql = Constants.FIND_ALL_EMAILS;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                emails.add(resultSet.getString("email").toLowerCase().trim());
            }
        }
        return emails;
    }

    /**
     * Находит id пользователя по заданному адресу электронной почты. В случае выброса SQLException выводится содержимое исключения.
     *
     * @param email адрес электронной почты пользователя; нормализуется в методе
     * @return int id - id пользователя с заданным адресом электронной почты, если пользователь существует;
     * -1 иначе
     */
    public int findUserIdByEmail(String email) throws SQLException {
        Config config = new Config();
        Connection connection = config.establishConnection();
        email = email.toLowerCase().replaceAll("\\s+", " ").trim();
        String sql = Constants.FIND_USER_ID_BY_EMAIL;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            int userId = -1;
            if (resultSet.next()) {
                userId = resultSet.getInt("id");
            }
            return userId;
        }
    }
}