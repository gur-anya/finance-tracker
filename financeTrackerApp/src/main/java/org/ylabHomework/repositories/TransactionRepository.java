package org.ylabHomework.repositories;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.serviceClasses.Constants;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Репозиторий для работы с сущностью Transaction через базу данных.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 15.03.2025
 * </p>
 */
@Data
@RequiredArgsConstructor
@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Записывает транзакцию в базу данных.
     *
     * @param transaction записываемая транзакция
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public void createTransaction(Transaction transaction) throws SQLException {
        jdbcTemplate.update(Constants.ADD_TRANSACTION,
                transaction.getType(),
                transaction.getSum(),
                transaction.getCategory(),
                transaction.getDescription(),
                Timestamp.valueOf(transaction.getTimestamp()),
                transaction.getUserId());
    }

    /**
     * Получает все транзакции пользователя по его id.
     *
     * @param userId id пользователя, для которого получаются транзакции
     * @return List всех транзакций пользователя; пуст, если транзакций нет
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions(int userId) throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_ALL_TRANSACTIONS_BY_USER,
                new Object[]{userId},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getInt("user_id"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }

    /**
     * Получает все транзакции с заданным типом (доход/расход) для пользователя по его id.
     *
     * @param userId id пользователя, для которого получаются транзакции
     * @param type тип транзакции (1 - доход, 2 - расход)
     * @return List транзакций пользователя с заданным типом; пуст, если транзакций нет
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByType(int userId, int type) throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_TRANSACTIONS_BY_TYPE,
                new Object[]{type, userId},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getInt("user_id"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }

    /**
     * Получает все транзакции с заданной категорией для пользователя по его id.
     *
     * @param userId id пользователя, для которого получаются транзакции
     * @param category категория транзакций
     * @return List транзакций пользователя с заданной категорией; пуст, если транзакций нет
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCategory(int userId, String category) throws SQLException {
        String normalizedCategory = category.trim().toLowerCase();
        return jdbcTemplate.query(
                Constants.FIND_TRANSACTIONS_BY_CATEGORY,
                new Object[]{normalizedCategory, userId},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getInt("user_id"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }


    /**
     * Получает все транзакции до заданной даты для пользователя по его id.
     *
     * @param userId id пользователя, для которого получаются транзакции
     * @param timestamp дата до
     * @return List транзакций пользователя до заданной даты; пуст, если транзакций нет
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsBeforeTimestamp(int userId, LocalDateTime timestamp) throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_TRANSACTIONS_BEFORE_TIMESTAMP,
                new Object[]{Timestamp.valueOf(timestamp), userId},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getInt("user_id"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }

    /**
     * Получает все транзакции после заданной даты для пользователя по его id.
     *
     * @param userId id пользователя, для которого получаются транзакции
     * @param timestamp дата после
     * @return List транзакций пользователя после заданной даты; пуст, если транзакций нет
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsAfterTimestamp(int userId, LocalDateTime timestamp) throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_TRANSACTIONS_AFTER_TIMESTAMP,
                new Object[]{Timestamp.valueOf(timestamp), userId},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getInt("user_id"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }

    /**
     * Получает все транзакции за период для пользователя по его id.
     *
     * @param userId id пользователя, для которого получаются транзакции
     * @param timestamp1 начало периода
     * @param timestamp2 конец период
     * @return List транзакций пользователя за заданный период; пуст, если транзакций нет
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsBetweenTimestamps(int userId, LocalDateTime timestamp1, LocalDateTime timestamp2) throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_TRANSACTIONS_BETWEEN_TIMESTAMPS,
                new Object[]{Timestamp.valueOf(timestamp1), Timestamp.valueOf(timestamp2), userId},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getInt("user_id"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }


    /**
     * Обновляет тип транзакции пользователя по его id. Транзакция идентифицируется по дате и времени ее создания.
     *
     * @param userId id пользователя, для которого обновляется транзакция
     * @param timestamp дата и время создания транзакции
     * @param newType новый тип транзакции
     * @return true если транзакция обновилась, false иначе
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTransactionType(int newType, int userId, LocalDateTime timestamp) throws SQLException {
        int rowsAffected = jdbcTemplate.update(Constants.UPDATE_TRANSACTION_TYPE,
                newType,
                Timestamp.valueOf(timestamp),
                userId);

        return rowsAffected > 0;
    }

    /**
     * Обновляет сумму транзакции пользователя по его id. Транзакция идентифицируется по дате и времени ее создания.
     *
     * @param userId id пользователя, для которого обновляется транзакция
     * @param timestamp дата и время создания транзакции
     * @param newSum новая сумма транзакции
     * @return true если транзакция обновилась, false иначе
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTransactionSum(double newSum, int userId, LocalDateTime timestamp) throws SQLException {
        int rowsAffected = jdbcTemplate.update(Constants.UPDATE_TRANSACTION_SUM,
                newSum,
                Timestamp.valueOf(timestamp),
                userId);
        return rowsAffected > 0;
    }
    /**
     * Обновляет категорию транзакции пользователя по его id. Транзакция идентифицируется по дате и времени ее создания.
     *
     * @param userId id пользователя, для которого обновляется транзакция
     * @param timestamp дата и время создания транзакции
     * @param newCategory новая категория транзакции
     * @return true если транзакция обновилась, false иначе
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTransactionCategory(String newCategory, int userId, LocalDateTime timestamp) throws SQLException {
        int rowsAffected = jdbcTemplate.update(Constants.UPDATE_TRANSACTION_CATEGORY,
                newCategory,
                Timestamp.valueOf(timestamp),
                userId);

        return rowsAffected > 0;
    }

    /**
     * Обновляет описание транзакции пользователя по его id. Транзакция идентифицируется по дате и времени ее создания.
     *
     * @param userId id пользователя, для которого обновляется транзакция
     * @param timestamp дата и время создания транзакции
     * @param newDescription новая сумма транзакции
     * @return true если транзакция обновилась, false иначе
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTransactionDescription(String newDescription, int userId, LocalDateTime timestamp) throws SQLException {
        int rowsAffected = jdbcTemplate.update(Constants.UPDATE_TRANSACTION_DESCRIPTION,
                newDescription,
                Timestamp.valueOf(timestamp),
                userId);
        return rowsAffected > 0;
    }
    /**
     * Удаляет транзакцию пользователя по его id. Транзакция идентифицируется по дате и времени ее создания.
     *
     * @param userId id пользователя, для которого удаляется транзакция
     * @param timestamp дата и время создания транзакции
     * @return true если транзакция удалена, false иначе
     * @throws SQLException при ошибке при работе с базой данных
     */

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTransaction(int userId, LocalDateTime timestamp) throws SQLException {
        int rowsAffected = jdbcTemplate.update(Constants.DELETE_TRANSACTION,
                userId,
                Timestamp.valueOf(timestamp));
        return rowsAffected > 0;
    }

    /**
     * Получает тип транзакции пользователя по его id. Транзакция идентифицируется по дате и времени ее создания.
     *
     * @param userId id пользователя, для которого получается тип транзакции
     * @param timestamp дата и время создания транзакции
     * @param newSum новая сумма транзакции
     * @return тип
     * @throws SQLException при ошибке при работе с базой данных
     */
    @Transactional(readOnly = true)
    public int getType(int userId, LocalDateTime timestamp) {
        return jdbcTemplate.query(
                Constants.GET_TRANSACTION,
                new Object[]{userId, timestamp},
                (rs) -> {
                    if (rs.next()) {
                        return
                                rs.getInt("type");
                    }
                    return 0;
                });
    }

}
