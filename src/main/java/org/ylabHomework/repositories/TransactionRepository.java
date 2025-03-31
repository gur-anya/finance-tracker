package org.ylabHomework.repositories;

import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Config;
import org.ylabHomework.serviceClasses.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
public class TransactionRepository {
    private final User user;
    private final UserRepository userRepository;

    /**
     * Конструктор для инициализации репозитория для работы с транзакциями, используя заданного пользователя и его репозиторий.
     * Соединение с базой данных устанавливается для каждой операции отдельно.
     *
     * @param user пользователь, с транзакциями которого ведется работа
     */
    public TransactionRepository(User user) {
        this.user = user;
        this.userRepository = new UserRepository();
    }

    /**
     * Добавляет новую транзакцию в таблицу транзакций пользователя.
     *
     * @param transaction транзакция для добавления
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void createTransaction(Transaction transaction) throws SQLException {
        Config config = new Config();
        String sql = Constants.ADD_TRANSACTION;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, transaction.getType());
            statement.setDouble(2, transaction.getSum());
            statement.setString(3, transaction.getCategory());
            statement.setString(4, transaction.getDescription());
            statement.setTimestamp(5, Timestamp.valueOf(transaction.getTimestamp()));
            statement.setInt(6, userRepository.findUserIdByEmail(user.getEmail()));
            statement.executeUpdate();
        }
    }

    /**
     * Находит все транзакции пользователя.
     *
     * @return список всех транзакций пользователя
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public List<Transaction> getAllTransactions() throws SQLException {
        Config config = new Config();
        List<Transaction> transactions = new ArrayList<>();
        String sql = Constants.FIND_ALL_TRANSACTIONS_BY_USER;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userRepository.findUserIdByEmail(user.getEmail()));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction currTrans = new Transaction(
                            resultSet.getInt("type"),
                            resultSet.getDouble("sum"),
                            resultSet.getString("category"),
                            resultSet.getString("description"));
                    currTrans.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
                    transactions.add(currTrans);
                }
            }
        }
        return transactions;
    }

    /**
     * Находит транзакции заданного пользователя по типу (доход/расход).
     *
     * @param type тип транзакции для фильтрации
     * @return список транзакций указанного типа
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public List<Transaction> getTransactionsByType(int type) throws SQLException {
        Config config = new Config();
        List<Transaction> transactions = new ArrayList<>();
        String sql = Constants.FIND_TRANSACTIONS_BY_TYPE;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, type);
            statement.setInt(2, userRepository.findUserIdByEmail(user.getEmail()));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction currTrans = new Transaction(
                            resultSet.getInt("type"),
                            resultSet.getDouble("sum"),
                            resultSet.getString("category"),
                            resultSet.getString("description"));
                    currTrans.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
                    transactions.add(currTrans);
                }
            }
        }
        return transactions;
    }

    /**
     * Находит транзакции заданного пользователя по категории.
     *
     * @param category категория для фильтрации; нормализуется в методе
     * @return список транзакций указанной категории
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public List<Transaction> getTransactionsByCategory(String category) throws SQLException {
        Config config = new Config();
        List<Transaction> transactions = new ArrayList<>();
        String normalizedCategory = category.trim().toLowerCase();
        String sql = Constants.FIND_TRANSACTIONS_BY_CATEGORY;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, normalizedCategory);
            statement.setInt(2, userRepository.findUserIdByEmail(user.getEmail()));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction currTrans = new Transaction(
                            resultSet.getInt("type"),
                            resultSet.getDouble("sum"),
                            resultSet.getString("category"),
                            resultSet.getString("description"));
                    currTrans.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
                    transactions.add(currTrans);
                }
            }
        }
        return transactions;
    }

    /**
     * Находит транзакции, совершенные до указанной даты и времени.
     *
     * @param timestamp временная метка для фильтрации
     * @return список транзакций, совершенных до указанного времени
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public List<Transaction> getTransactionsBeforeTimestamp(LocalDateTime timestamp) throws SQLException {
        Config config = new Config();
        List<Transaction> transactions = new ArrayList<>();
        String sql = Constants.FIND_TRANSACTIONS_BEFORE_TIMESTAMP;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(timestamp));
            statement.setInt(2, userRepository.findUserIdByEmail(user.getEmail()));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction currTrans = new Transaction(
                            resultSet.getInt("type"),
                            resultSet.getDouble("sum"),
                            resultSet.getString("category"),
                            resultSet.getString("description"));
                    currTrans.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
                    transactions.add(currTrans);
                }
            }
        }
        return transactions;
    }

    /**
     * Находит транзакции, совершенные после указанной даты и времени.
     *
     * @param timestamp временная метка для фильтрации
     * @return список транзакций, совершенных после указанного времени
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public List<Transaction> getTransactionsAfterTimestamp(LocalDateTime timestamp) throws SQLException {
        Config config = new Config();
        List<Transaction> transactions = new ArrayList<>();
        String sql = Constants.FIND_TRANSACTIONS_AFTER_TIMESTAMP;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(timestamp));
            statement.setInt(2, userRepository.findUserIdByEmail(user.getEmail()));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction currTrans = new Transaction(
                            resultSet.getInt("type"),
                            resultSet.getDouble("sum"),
                            resultSet.getString("category"),
                            resultSet.getString("description"));
                    currTrans.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
                    transactions.add(currTrans);
                }
            }
        }
        return transactions;
    }

    /**
     * Находит транзакции, совершенные между указанными временными рамками.
     *
     * @param timestamp1 нижняя граница времени
     * @param timestamp2 верхняя граница времени
     * @return список транзакций, совершенных в указанных рамках
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public List<Transaction> getTransactionsBetweenTimestamps(LocalDateTime timestamp1, LocalDateTime timestamp2) throws SQLException {
        Config config = new Config();
        List<Transaction> transactions = new ArrayList<>();
        String sql = Constants.FIND_TRANSACTIONS_BETWEEN_TIMESTAMPS;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(timestamp1));
            statement.setTimestamp(2, Timestamp.valueOf(timestamp2));
            statement.setInt(3, userRepository.findUserIdByEmail(user.getEmail()));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Transaction currTrans = new Transaction(
                            resultSet.getInt("type"),
                            resultSet.getDouble("sum"),
                            resultSet.getString("category"),
                            resultSet.getString("description"));
                    currTrans.setTimestamp(resultSet.getTimestamp("timestamp").toLocalDateTime());
                    transactions.add(currTrans);
                }
            }
        }
        return transactions;
    }

    /**
     * Изменяет тип заданной транзакции.
     *
     * @param newType     новый тип транзакции
     * @param transaction транзакция, для которой меняется тип
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void updateTransactionType(int newType, Transaction transaction) throws SQLException {
        Config config = new Config();
        String sql = Constants.UPDATE_TRANSACTION_TYPE;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, newType);
            statement.setTimestamp(2, Timestamp.valueOf(transaction.getTimestamp()));
            statement.setInt(3, userRepository.findUserIdByEmail(user.getEmail()));
            statement.executeUpdate();
        }
    }

    /**
     * Изменяет сумму заданной транзакции.
     *
     * @param newSum      новая сумма транзакции
     * @param transaction транзакция, для которой меняется сумма
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void updateTransactionSum(double newSum, Transaction transaction) throws SQLException {
        Config config = new Config();
        String sql = Constants.UPDATE_TRANSACTION_SUM;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, newSum);
            statement.setTimestamp(2, Timestamp.valueOf(transaction.getTimestamp()));
            statement.setInt(3, userRepository.findUserIdByEmail(user.getEmail()));
            statement.executeUpdate();
        }
    }

    /**
     * Изменяет категорию заданной транзакции.
     *
     * @param newCategory новая категория транзакции
     * @param transaction транзакция, для которой меняется категория
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void updateTransactionCategory(String newCategory, Transaction transaction) throws SQLException {
        Config config = new Config();
        String sql = Constants.UPDATE_TRANSACTION_CATEGORY;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newCategory);
            statement.setTimestamp(2, Timestamp.valueOf(transaction.getTimestamp()));
            statement.setInt(3, userRepository.findUserIdByEmail(user.getEmail()));
            statement.executeUpdate();
        }
    }

    /**
     * Изменяет описание заданной транзакции.
     *
     * @param description новое описание транзакции
     * @param transaction транзакция, для которой меняется описание
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void updateTransactionDescription(String description, Transaction transaction) throws SQLException {
        Config config = new Config();
        String sql = Constants.UPDATE_TRANSACTION_DESCRIPTION;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, description);
            statement.setTimestamp(2, Timestamp.valueOf(transaction.getTimestamp()));
            statement.setInt(3, userRepository.findUserIdByEmail(user.getEmail()));
            statement.executeUpdate();
        }
    }

    /**
     * Удаляет заданную транзакцию из таблицы пользователя.
     *
     * @param transaction транзакция для удаления
     * @return true, если удаление успешно; false иначе
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public boolean deleteTransaction(Transaction transaction) throws SQLException {
        Config config = new Config();
        String sql = Constants.DELETE_TRANSACTION;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.valueOf(transaction.getTimestamp()));
            statement.setInt(2, userRepository.findUserIdByEmail(user.getEmail()));
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
     * Получает месячный бюджет пользователя из базы данных.
     *
     * @return значение месячного бюджета
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public double getMonthlyBudget() throws SQLException {
        Config config = new Config();
        String sql = Constants.GET_MONTHLY_BUDGET;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userRepository.findUserIdByEmail(user.getEmail()));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("monthly_budget");
                }
            }
        }
        return 0.0;
    }

    /**
     * Устанавливает месячный бюджет пользователя в базе данных.
     *
     * @param budget новое значение бюджета
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void setMonthlyBudget(double budget) throws SQLException {
        Config config = new Config();
        String sql = Constants.SET_MONTHLY_BUDGET;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, budget);
            statement.setInt(2, userRepository.findUserIdByEmail(user.getEmail()));
            statement.executeUpdate();
        }
    }

    /**
     * Получает финансовую цель пользователя из базы данных.
     *
     * @return значение финансовой цели
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public double getGoal() throws SQLException {
        Config config = new Config();
        String sql = Constants.GET_GOAL;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, userRepository.findUserIdByEmail(user.getEmail()));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("goal");
                }
            }
        }
        return 0.0;
    }

    /**
     * Устанавливает финансовую цель пользователя в базе данных.
     *
     * @param goal новое значение цели
     * @throws SQLException если произошла ошибка при работе с базой данных
     */
    public void setGoal(double goal) throws SQLException {
        Config config = new Config();
        String sql = Constants.SET_GOAL;

        try (Connection connection = config.establishConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, goal);
            statement.setInt(2, userRepository.findUserIdByEmail(user.getEmail()));
            statement.executeUpdate();
        }
    }

    /**
     * Фильтрует список транзакций, возвращая те, которые произошли после указанного времени или в момент времени.
     *
     * @param timestamp временная метка, начиная с которой нужно отфильтровать транзакции
     * @param sorted    исходный список транзакций для фильтрации
     * @return список транзакций, произошедших после или в момент указанной временной метки
     */
    public List<Transaction> getSortedTransactionsAfterTimestamp(LocalDateTime timestamp, List<Transaction> sorted) {
        List<Transaction> transactions = new ArrayList<>();
        for (Transaction t : sorted) {
            if (t.getTimestamp().isAfter(timestamp) || t.getTimestamp().equals(timestamp)){
                transactions.add(t);
            }
        }
        return transactions;
    }

    /**
     * Фильтрует список транзакций по указанной категории.
     * <p>Сравнение категорий выполняется без учёта регистра и пробелов в начале или конце.</p>
     *
     * @param category категория транзакций для фильтрации (в нижнем регистре, без лишних пробелов)
     * @param sorted   исходный список транзакций для фильтрации
     * @return список транзакций, соответствующих указанной категории
     */
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