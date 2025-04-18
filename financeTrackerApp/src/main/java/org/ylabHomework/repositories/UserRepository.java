package org.ylabHomework.repositories;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;


import java.sql.SQLException;
import java.util.List;

/**
 * Репозиторий для работы с сущностью User через базу данных.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 09.03.2025
 * </p>
 */
@Data
@RequiredArgsConstructor
@Repository
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Находит всех созданных пользователей. В случае выброса SQLException выводится содержимое исключения.
     *
     * @return список из всех пользователей
     */
    @Transactional(readOnly = true)
    public List<User> getUsers() throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_ALL_USERS,
                (rs, rowNum) -> {
                        User user = new User(
                                rs.getString("name"),
                                rs.getString("email").toLowerCase().trim(),
                                rs.getString("password"),
                                rs.getInt("role_id"));
                        user.setActive(rs.getBoolean("is_active"));
                    return user;
                });
    }

    /**
     * Добавляет новую запись в таблицу пользователей. Аккаунт пользователя изначально активен и имеет роль 1 (обычный пользователь).
     *
     * @param user новый пользователь для добавления
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public void addUser(User user) throws SQLException {
        String normalizedEmail = user.getEmail().toLowerCase().trim();
        jdbcTemplate.update(Constants.ADD_USER,
                user.getName(),
                normalizedEmail,
                user.getPassword(),
                1,
                true);
    }

    /**
     * Находит пользователя по заданному адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя; нормализуется в методе
     * @return объект User, если пользователь найден; null иначе
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Transactional(readOnly = true)
    public User readUserByEmail(String email) throws SQLException {
        String normalizedEmail = email.toLowerCase().trim();
        return jdbcTemplate.query(
                Constants.FIND_USER_BY_EMAIL,
                new Object[]{normalizedEmail},
                (rs) -> {
                    if (rs.next()) {
                        User user = new User(
                                rs.getString("name"),
                                rs.getString("email").toLowerCase().trim(),
                                rs.getString("password"),
                                rs.getInt("role_id"));
                        user.setActive(rs.getBoolean("is_active"));
                        return user;
                    }
                    return null;
                });
    }

    /**
     * Удаляет пользователя по заданному адресу электронной почты.
     *
     * @param email адрес электронной почты пользователя; нормализуется в методе
     * @return true, если удаление успешно; false иначе
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserByEmail(String email) throws SQLException {
        String normalizedEmail = email.toLowerCase().trim();
        jdbcTemplate.update(Constants.DELETE_USER_BY_EMAIL,
                normalizedEmail);
        return true;
    }

    /**
     * Обновляет имя заданного пользователя.
     *
     * @param newName новое имя для пользователя
     * @param user    пользователь, для которого обновляется имя
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateName(String newName, User user) throws SQLException {
        jdbcTemplate.update(Constants.UPDATE_USER_NAME,
                newName,
                user.getEmail().toLowerCase().trim());
    }

    /**
     * Обновляет адрес электронной почты пользователя.
     *
     * @param newEmail новая электронная почта пользователя; нормализуется в методе
     * @param user     пользователь, для которого обновляется электронная почта
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateEmail(String newEmail, User user) throws SQLException {
        jdbcTemplate.update(Constants.UPDATE_USER_EMAIL,
                newEmail.toLowerCase().trim(),
                user.getEmail().toLowerCase().trim());
    }

    /**
     * Присваивает пользователю новый зашифрованный пароль.
     *
     * @param newPass новый зашифрованный пароль
     * @param user    пользователь, для которого обновляется пароль
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String newPass, User user) throws SQLException {
        jdbcTemplate.update(Constants.UPDATE_USER_PASSWORD,
                newPass,
                user.getEmail().toLowerCase().trim());
    }

    /**
     * Изменяет статус активности аккаунта пользователя.
     *
     * @param isActive новый статус активности аккаунта пользователя
     * @param user     пользователь, для которого обновляется статус
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateActive(boolean isActive, User user) throws SQLException {
        jdbcTemplate.update(Constants.UPDATE_USER_ACTIVITY,
                isActive,
                user.getEmail().toLowerCase().trim());
    }

    /**
     * Находит id пользователя по заданному адресу электронной почты. В случае выброса SQLException выводится содержимое исключения.
     *
     * @param email адрес электронной почты пользователя; нормализуется в методе
     * @return int id - id пользователя с заданным адресом электронной почты, если пользователь существует;
     * -1 иначе
     */
    @Transactional(readOnly = true)
    public int findUserIdByEmail(String email) throws SQLException {
        String normalizedEmail = email.toLowerCase().trim();
        return jdbcTemplate.query(
                Constants.FIND_USER_BY_EMAIL,
                new Object[]{normalizedEmail},
                (rs) -> {
                    int userId = -1;
                    if (rs.next()) {
                        userId = rs.getInt("id");
                    }
                    return userId;
                });
    }
}
