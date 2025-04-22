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
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email").toLowerCase().trim(),
                            rs.getString("password"),
                            rs.getInt("role_id"),
                            rs.getBoolean("is_active"),
                            rs.getDouble("monthly_budget"),
                            rs.getDouble("goal"));
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
    public User readUserByEmail(String email) {
        String normalizedEmail = email.toLowerCase().trim();
        return jdbcTemplate.query(
                Constants.FIND_USER_BY_EMAIL,
                new Object[]{normalizedEmail},
                (rs) -> {
                    if (rs.next()) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email").toLowerCase().trim(),
                                rs.getString("password"),
                                rs.getInt("role_id"),
                                rs.getBoolean("is_active"),
                                rs.getDouble("monthly_budget"),
                                rs.getDouble("goal"));
                    }
                    return null;
                });
    }

    /**
     * Находит пользователя по id.
     *
     * @param id id пользователя
     * @return объект User, если пользователь найден; null иначе
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Transactional(readOnly = true)
    public User readUserById(int id) {
        return jdbcTemplate.query(
                Constants.FIND_USER_BY_ID,
                new Object[]{id},
                (rs) -> {
                    if (rs.next()) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("email").toLowerCase().trim(),
                                rs.getString("password"),
                                rs.getInt("role_id"),
                                rs.getBoolean("is_active"),
                                rs.getDouble("monthly_budget"),
                                rs.getDouble("goal"));
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
        int rowsAffected = jdbcTemplate.update(Constants.DELETE_USER_BY_EMAIL,
                normalizedEmail);
        return rowsAffected > 0;
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
     * @param is_active новый статус активности аккаунта пользователя
     * @param user     пользователь, для которого обновляется статус
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateActive(boolean is_active, User user) throws SQLException {
        jdbcTemplate.update(Constants.UPDATE_USER_ACTIVITY,
                is_active,
                user.getEmail().toLowerCase().trim());
    }


    @Transactional(rollbackFor = Exception.class)
    public void setMonthlyBudget(User user, double budget) throws SQLException {
        jdbcTemplate.update(Constants.SET_MONTHLY_BUDGET,
                budget,
                user);
    }


    @Transactional(rollbackFor = Exception.class)
    public void setGoal(User user, double goal) throws SQLException {
        jdbcTemplate.update(Constants.SET_GOAL,
                goal,
                user);
    }


}
