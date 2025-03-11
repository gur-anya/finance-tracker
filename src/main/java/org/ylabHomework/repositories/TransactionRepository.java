package org.ylabHomework.repositories;


import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Репозиторий для работы с сущностью Transaction.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 07.03.2025
 * </p>
 */
public class TransactionRepository {
    private User user;

    private List<Transaction> transactionList;

    /**
     * Конструктор для инициализации репозитория для работы с транзакциями, используя заданного пользователя.
     *
     * @param user пользователь, с транзакциями которого ведется работа
     */
    public TransactionRepository(User user) {
        this.user = user;
        this.transactionList = user.getTransactions();
    }

    /**
     * Добавляет новую транзакцию в список транзакций пользователя.
     *
     * @param transaction транзакция для добавления
     */
    public void createTransaction(Transaction transaction) {
        transactionList.add(transaction);
    }

    /**
     * Находит все транзакции пользователя.
     *
     * @return список всех транзакций пользователя
     */
    public List<Transaction> getAllTransactions() {
        return transactionList;
    }

    /**
     * Находит транзакции заданного пользователя по типу (доход/расход).
     *
     * @param type тип транзакции для фильтрации
     * @return список транзакций указанного типа
     */
    public List<Transaction> getTransactionsByType(Transaction.TransactionTYPE type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (transaction.getType() == type) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * Находит транзакции заданного пользователя по категории.
     *
     * @param category категория для фильтрации; нормализуется в методе
     * @return список транзакций указанной категории
     */
    public List<Transaction> getTransactionsByCategory(String category) {
        List<Transaction> result = new ArrayList<>();
        String normalizedCategory = category.trim().toLowerCase();

        for (Transaction transaction : transactionList) {
            if (transaction.getCategory() != null &&
                    transaction.getCategory().trim().toLowerCase().equals(normalizedCategory)) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * Находит транзакции, совершенные до указанной даты и времени.
     *
     * @param timestamp временная метка для фильтрации
     * @return список транзакций, совершенных до указанного времени
     */
    public List<Transaction> getTransactionsBeforeTimestamp(LocalDateTime timestamp) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (transaction.getTimestamp().isBefore(timestamp) || transaction.getTimestamp().isEqual(timestamp)) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * Находит транзакции, совершенные после указанной даты и времени.
     *
     * @param timestamp временная метка для фильтрации
     * @return список транзакций, совершенных после указанного времени
     */
    public List<Transaction> getTransactionsAfterTimestamp(LocalDateTime timestamp) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if (transaction.getTimestamp().isAfter(timestamp) || transaction.getTimestamp().isEqual(timestamp)) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * Находит транзакции, совершенные между указанными временными рамками.
     *
     * @param timestamp1 нижняя граница времени
     * @param timestamp2 верхняя граница времени
     * @return список транзакций, совершенных в указанных рамках
     */
    public List<Transaction> getTransactionsBetweenTimestamps(LocalDateTime timestamp1, LocalDateTime timestamp2) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactionList) {
            if ((transaction.getTimestamp().isAfter(timestamp1) || transaction.getTimestamp().isEqual(timestamp1)) &&
                    (transaction.getTimestamp().isBefore(timestamp2) || transaction.getTimestamp().isEqual(timestamp2))) {
                result.add(transaction);
            }
        }
        return result;
    }

    /**
     * Изменяет тип заданной транзакции.
     *
     * @param newType     новый тип транзакции
     * @param transaction транзакция, для которой меняется тип
     */
    public void updateTransactionType(Transaction.TransactionTYPE newType, Transaction transaction) {
        transaction.setType(newType);
    }

    /**
     * Изменяет сумму заданной транзакции.
     *
     * @param newSum      новая сумма транзакции
     * @param transaction транзакция, для которой меняется сумма
     */
    public void updateTransactionSum(double newSum, Transaction transaction) {
        transaction.setSum(newSum);
    }

    /**
     * Изменяет категорию заданной транзакции.
     *
     * @param newCategory новая категория транзакции
     * @param transaction транзакция, для которой меняется категория
     */
    public void updateTransactionCategory(String newCategory, Transaction transaction) {
        transaction.setCategory(newCategory);
    }

    /**
     * Изменяет описание заданной транзакции.
     *
     * @param description новое описание транзакции
     * @param transaction транзакция, для которой меняется описание
     */
    public void updateTransactionDescription(String description, Transaction transaction) {
        transaction.setDescription(description);
    }

    /**
     * Удаляет заданную транзакцию из списка пользователя.
     *
     * @param transaction транзакция для удаления
     * @return true, если удаление успешно; false, если транзакция не найдена
     */
    public boolean deleteTransaction(Transaction transaction) {
        return transactionList.remove(transaction);
    }

    /**
     * Получает месячный бюджет пользователя.
     *
     * @return значение месячного бюджета
     */
    public double getMonthlyBudget() {
        return user.getMonthlyBudget();
    }

    /**
     * Устанавливает месячный бюджет пользователя.
     *
     * @param budget новое значение бюджета
     */
    public void setMonthlyBudget(double budget) {
        this.user.setMonthlyBudget(budget);
    }

    /**
     * Получает финансовую цель пользователя.
     *
     * @return значение финансовой цели
     */
    public double getGoal() {
        return user.getGoal();
    }

    /**
     * Устанавливает финансовую цель пользователя.
     *
     * @param goal новое значение цели
     */
    public void setGoal(double goal) {
        this.user.setGoal(goal);
    }

    /**
     * Фильтрует список транзакций, возвращая те, которые произошли после указанного времени или в момент времени.
     *
     * @param timestamp временная метка, начиная с которой нужно отфильтровать транзакции
     * @param sorted    исходный список транзакций для фильтрации
     * @return список транзакций, произошедших после или в момент указанной временной метки
     */
    public List<Transaction> getSortedTransactionsAfterTimestamp(LocalDateTime timestamp, List<Transaction> sorted) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : sorted) {
            if (transaction.getTimestamp().isAfter(timestamp) || transaction.getTimestamp().isEqual(timestamp)) {
                result.add(transaction);
            }
        }
        return result;
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
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : sorted) {
            if (transaction.getCategory().toLowerCase().trim().equals(category)) {
                result.add(transaction);
            }
        }
        return result;
    }
}