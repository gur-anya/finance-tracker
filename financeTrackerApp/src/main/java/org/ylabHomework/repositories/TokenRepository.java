

package org.ylabHomework.repositories;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.serviceClasses.Constants;
/**
 * Репозиторий для записи токена в базу данных и чтения токена из базы данных.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 15.03.2025
 * </p>
 */
@Data
@RequiredArgsConstructor
@Repository
public class TokenRepository {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Добавляет токен в таблицу базы данных с blacklisted (уже нерабочими) токенами.
     *
     * @param token JWT токен
     */
    @Transactional(rollbackFor = Exception.class)
    public void addToken(String token) {
        jdbcTemplate.update(Constants.ADD_TOKEN,
                token);
    }

    /**
     * Получает токен из таблицы blacklisted токенов.
     * @param token JWT токен
     * @return токен, если он найден в blacklisted таблице; null иначе
     */
    @Transactional(readOnly = true)
    public String getToken(String token) {
        return jdbcTemplate.query(
                Constants.GET_TOKEN,
                new Object[]{token},
                (rs) -> {
                    if (rs.next()) {
                        return rs.getString("token");
                    }
                    return null;
                });
    }
}
