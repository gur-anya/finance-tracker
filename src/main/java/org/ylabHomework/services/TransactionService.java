package org.ylabHomework.services;

import lombok.Getter;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис для работы с сущностью Transaction.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 07.03.2025
 * </p>
 */
public class TransactionService {

    /**
     * Конструктор для создания сервиса с заданным репозиторием и пользователем.
     *
     * @param repository репозиторий для работы с транзакциями
     * @param user       пользователь, с транзакциями которого ведется работа
     */
    public TransactionService(TransactionRepository repository, User user) {
        this.repository = repository;
        this.statsService = new TransactionStatsService(repository, user);
        this.user = user;

    }

    public final TransactionRepository repository;
    @Getter
    public final TransactionStatsService statsService;
    public final User user;

    /**
     * Создает новую транзакцию с заданным типом, суммой, категорией и описанием для пользователя.
     *
     * @param type        тип транзакции (доход/расход)
     * @param sum         сумма транзакции
     * @param category    категория транзакции
     * @param description описание транзакции
     * @return сообщение об успешном создании транзакции
     */
    public String createTransaction(Transaction.TransactionTYPE type, double sum, String category,
                                    String description) {
        Transaction transaction = new Transaction(type, sum, category, description);
        repository.createTransaction(transaction);
        return "Транзакция успешно сохранена!";
    }

    /**
     * Обновляет тип транзакции.
     *
     * @param newType     новый тип транзакции
     * @param transaction объект транзакции для обновления
     * @return сообщение о результате обновления типа
     */
    public String updateTransactionType(Transaction.TransactionTYPE newType, Transaction transaction) {
        if (newType == null) {
            return "Тип транзакции не может быть пустым!";
        }
        repository.updateTransactionType(newType, transaction);
        return "Успешно обновлено!";
    }

    /**
     * Обновляет сумму транзакции.
     *
     * @param newSum      новая сумма транзакции
     * @param transaction объект транзакции для обновления
     * @return сообщение о результате обновления суммы
     */
    public String updateTransactionSum(double newSum, Transaction transaction) {
        repository.updateTransactionSum(newSum, transaction);
        return "Сумма обновлена!";
    }

    /**
     * Обновляет категорию транзакции.
     *
     * @param newCategory новая категория транзакции
     * @param transaction объект транзакции для обновления
     * @return сообщение о результате обновления категории
     */
    public String updateTransactionCategory(String newCategory, Transaction transaction) {
        if (newCategory != null && !newCategory.trim().isEmpty()) {
            repository.updateTransactionCategory(newCategory, transaction);
            return "Категория обновлена!";
        }
        return "Категория не может быть пустой!";
    }

    /**
     * Обновляет описание транзакции.
     *
     * @param newDescription новое описание транзакции
     * @param transaction    объект транзакции для обновления
     * @return сообщение о результате обновления описания
     */
    public String updateTransactionDescription(String newDescription, Transaction transaction) {
        if (newDescription != null && newDescription.length() > 200) {
            return "Описание не должно превышать 200 символов!";
        }
        repository.updateTransactionDescription(newDescription, transaction);
        return "Описание обновлено!";
    }

    /**
     * Получает список транзакций, совершенных до указанной даты и времени.
     *
     * @param timestamp временная метка для фильтрации
     * @return список транзакций
     */
    public List<Transaction> getTransactionsBeforeTimestamp(LocalDateTime timestamp) {
        return repository.getTransactionsBeforeTimestamp(timestamp);
    }

    /**
     * Получает список транзакций, совершенных после указанной даты и времени.
     *
     * @param timestamp временная метка для фильтрации
     * @return список транзакций
     */
    public List<Transaction> getTransactionsAfterTimestamp(LocalDateTime timestamp) {
        return repository.getTransactionsAfterTimestamp(timestamp);
    }

    /**
     * Получает список транзакций, совершенных между указанными датой и временем.
     *
     * @param timestamp1 нижняя граница времени
     * @param timestamp2 верхняя граница времени
     * @return список транзакций, совершенных в указанных рамках
     */
    public List<Transaction> getTransactionsBetweenTimestamps(LocalDateTime timestamp1, LocalDateTime timestamp2) {
        return repository.getTransactionsBetweenTimestamps(timestamp1, timestamp2);
    }


    /**
     * Получает список транзакций по заданной категории.
     *
     * @param category категория для фильтрации
     * @return список транзакций
     */
    public List<Transaction> getTransactionsByCategory(String category) {
        return repository.getTransactionsByCategory(category);
    }


    /**
     * Получает список транзакций по заданному типу (доход/расход).
     *
     * @param type тип транзакции для фильтрации
     * @return список транзакций
     */
    public List<Transaction> getTransactionsByType(Transaction.TransactionTYPE type) {
        return repository.getTransactionsByType(type);
    }

    /**
     * Получает список всех транзакций пользователя.
     *
     * @return список всех транзакций
     */
    public List<Transaction> getAllTransactions() {
        return repository.getAllTransactions();
    }

    /**
     * Удаляет указанную транзакцию.
     *
     * @param transaction транзакция для удаления
     * @return сообщение о результате удаления
     */
    public String deleteTransaction(Transaction transaction) {
        if (repository.deleteTransaction(transaction)) {
            return "Удаление прошло успешно";
        }
        return "Транзакция не найдена!";
    }
}