package org.ylabHomework.repositories;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.serviceClasses.Constants;



/**
 * Репозиторий для записи результатов логгирования в базу данных.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 30.03.2025
 * </p>
 */
@Data
@RequiredArgsConstructor
@Repository
public class AspectsRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional(rollbackFor = Exception.class)
    public void putLoginAudit(String useremail, long loginTime, boolean success){
        jdbcTemplate.update(Constants.ADD_LOGIN_AUDIT,
             useremail, loginTime, success);
    }

    @Transactional(rollbackFor = Exception.class)
    public void putActionAudit(String useremail, String action) {
        jdbcTemplate.update(Constants.ADD_ACTION_AUDIT,
             useremail, action);
    }
}
