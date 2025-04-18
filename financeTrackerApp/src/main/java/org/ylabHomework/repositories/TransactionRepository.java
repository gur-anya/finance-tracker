package org.ylabHomework.repositories;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    @Transactional(rollbackFor = Exception.class)
    public void createTransaction(User user, Transaction transaction) throws SQLException {
        jdbcTemplate.update(Constants.ADD_TRANSACTION,
                transaction.getType(),
                transaction.getSum(),
                transaction.getCategory(),
                transaction.getDescription(),
                Timestamp.valueOf(transaction.getTimestamp()),
                userRepository.findUserIdByEmail(user.getEmail()));
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions(User user) throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_ALL_TRANSACTIONS_BY_USER,
                new Object[]{userRepository.findUserIdByEmail(user.getEmail())},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByType(User user, int type) throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_TRANSACTIONS_BY_TYPE,
                new Object[]{type, userRepository.findUserIdByEmail(user.getEmail())},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCategory(User user, String category) throws SQLException {
        String normalizedCategory = category.trim().toLowerCase();
        return jdbcTemplate.query(
                Constants.FIND_TRANSACTIONS_BY_CATEGORY,
                new Object[]{normalizedCategory, userRepository.findUserIdByEmail(user.getEmail())},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsBeforeTimestamp(User user, LocalDateTime timestamp) throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_TRANSACTIONS_BEFORE_TIMESTAMP,
                new Object[]{Timestamp.valueOf(timestamp), userRepository.findUserIdByEmail(user.getEmail())},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsAfterTimestamp(User user, LocalDateTime timestamp) throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_TRANSACTIONS_AFTER_TIMESTAMP,
                new Object[]{Timestamp.valueOf(timestamp), userRepository.findUserIdByEmail(user.getEmail())},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsBetweenTimestamps(User user, LocalDateTime timestamp1, LocalDateTime timestamp2) throws SQLException {
        return jdbcTemplate.query(
                Constants.FIND_TRANSACTIONS_BETWEEN_TIMESTAMPS,
                new Object[]{Timestamp.valueOf(timestamp1), Timestamp.valueOf(timestamp2), userRepository.findUserIdByEmail(user.getEmail())},
                (rs, rowNum) -> {
                    Transaction currTrans = new Transaction(
                            rs.getInt("type"),
                            rs.getDouble("sum"),
                            rs.getString("category"),
                            rs.getString("description"));
                    currTrans.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
                    return currTrans;
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTransactionType(User user, int newType, Transaction transaction) throws SQLException {
        jdbcTemplate.update(Constants.UPDATE_TRANSACTION_TYPE,
                newType,
                Timestamp.valueOf(transaction.getTimestamp()),
                userRepository.findUserIdByEmail(user.getEmail()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTransactionSum(User user, double newSum, Transaction transaction) throws SQLException {
        jdbcTemplate.update(Constants.UPDATE_TRANSACTION_SUM,
                newSum,
                Timestamp.valueOf(transaction.getTimestamp()),
                userRepository.findUserIdByEmail(user.getEmail()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTransactionCategory(User user, String newCategory, Transaction transaction) throws SQLException {
        jdbcTemplate.update(Constants.UPDATE_TRANSACTION_CATEGORY,
                newCategory,
                Timestamp.valueOf(transaction.getTimestamp()),
                userRepository.findUserIdByEmail(user.getEmail()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateTransactionDescription(User user, String description, Transaction transaction) throws SQLException {
        jdbcTemplate.update(Constants.UPDATE_TRANSACTION_DESCRIPTION,
                description,
                Timestamp.valueOf(transaction.getTimestamp()),
                userRepository.findUserIdByEmail(user.getEmail()));
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTransaction(User user, Transaction transaction) throws SQLException {
        int rowsAffected = jdbcTemplate.update(Constants.DELETE_TRANSACTION,
                userRepository.findUserIdByEmail(user.getEmail()),
                transaction.getType(),
                transaction.getSum(),
                transaction.getCategory(),
                transaction.getDescription(),
                Timestamp.valueOf(transaction.getTimestamp()));
        return rowsAffected > 0;
    }

    @Transactional(readOnly = true)
    public double getMonthlyBudget(User user) throws SQLException {
        return jdbcTemplate.query(Constants.GET_MONTHLY_BUDGET,
                new Object[]{userRepository.findUserIdByEmail(user.getEmail())},
                (rs) -> {
                    if (rs.next()) {
                        return rs.getDouble("monthly_budget");
                    }
                    return 0.0;
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public void setMonthlyBudget(User user, double budget) throws SQLException {
        jdbcTemplate.update(Constants.SET_MONTHLY_BUDGET,
                budget,
                userRepository.findUserIdByEmail(user.getEmail()));
    }

    @Transactional(readOnly = true)
    public double getGoal(User user) throws SQLException {
        return jdbcTemplate.query(Constants.GET_GOAL,
                new Object[]{userRepository.findUserIdByEmail(user.getEmail())},
                (rs) -> {
                    if (rs.next()) {
                        return rs.getDouble("goal");
                    }
                    return 0.0;
                });
    }

    @Transactional(rollbackFor = Exception.class)
    public void setGoal(User user, double goal) throws SQLException {
        jdbcTemplate.update(Constants.SET_GOAL,
                goal,
                userRepository.findUserIdByEmail(user.getEmail()));
    }

    @Transactional(readOnly = true)
    public List<Transaction> getSortedTransactionsAfterTimestamp(LocalDateTime timestamp, List<Transaction> sorted) {
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction t : sorted) {
            if (t.getTimestamp().isAfter(timestamp) || t.getTimestamp().equals(timestamp)) {
                transactions.add(t);
            }
        }
        return transactions;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getSortedTransactionsByCategory(String category, List<Transaction> sorted) {
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction t : sorted) {
            if (t.getCategory().equalsIgnoreCase(category)) {
                transactions.add(t);
            }
        }
        return transactions;
    }
}
