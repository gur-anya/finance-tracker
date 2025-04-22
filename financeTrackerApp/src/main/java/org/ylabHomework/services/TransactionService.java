package org.ylabHomework.services;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ylabHomework.DTOs.TransactionsDTOs.serviceDTOs.FilterDTO;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.serviceClasses.customExceptions.CustomDatabaseException;
import org.ylabHomework.serviceClasses.customExceptions.EmptyValueException;
import org.ylabHomework.serviceClasses.customExceptions.NoGoalException;
import org.ylabHomework.serviceClasses.customExceptions.TransactionNotFoundException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Сервис для работы с сущностью Transaction.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 07.03.2025
 */
@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    private final TransactionRepository repository;
    private final UserService userService;

    /**
     * Создаёт новую транзакцию с заданными данными.
     *
     * @return сообщение об успешном создании или об ошибке
     */
    public String createTransaction(Transaction transaction, User user) {
        if (transaction.getCategory().trim().equalsIgnoreCase("цель") && user.getGoal() == 0) {
            throw new NoGoalException();
        }
        try {
            repository.createTransaction(transaction);
            StringBuilder message = new StringBuilder("Транзакция успешно создана! ");

            if (transaction.getType() == 2 && user.getMonthlyBudget() > 0) {
                String budgetCheckResult = getBudgetLimitCheck(user);
                message.append(budgetCheckResult);
            }
            return message.toString();
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    public String checkMonthlyBudgetLimit(User user, double monthlyExpenses) {
        if (user.getMonthlyBudget() == 0) {
            return "";
        }
        double remainingBudget = user.getMonthlyBudget() + monthlyExpenses;
        if (remainingBudget <= 0) {
            return notifyAboutMonthlyLimit(user, Math.abs(remainingBudget));
        } else if (remainingBudget <= user.getMonthlyBudget() * 0.1) {
            return "Осторожно! Остаток бюджета составляет " + String.format("%.2f", remainingBudget) +
                    " руб. (менее 10% от лимита).";
        }
        return "";
    }

    private String notifyAboutMonthlyLimit(User user, double overgo) {
        return "Внимание! Вы превысили установленный месячный бюджет на " + String.format("%.2f", overgo) +
                " руб. " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                ". Подробности отправлены по адресу " + user.getEmail();
    }

    private String getBudgetLimitCheck(User user) {
        int userId = user.getId();
        List<Transaction> incomes = getTransactionsByType(userId, 1);
        List<Transaction> expenses = getTransactionsByType(userId, 2);
        double monthlyExpenses = getMonthlyExpenses(incomes, expenses);
        return checkMonthlyBudgetLimit(user, monthlyExpenses);
    }

    public String updateTransactionType(int newType, User user, LocalDateTime timestamp) {
        int userId = user.getId();
        try {
            if (!repository.updateTransactionType(newType, userId, timestamp)) {
                throw new TransactionNotFoundException();
            } else {
                StringBuilder message = new StringBuilder();
                if (newType == 2) {
                    String budgetCheckResult = getBudgetLimitCheck(user);
                    message.append(budgetCheckResult);
                }
                return message.toString();
            }
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    public String updateTransactionSum(double newSum, User user, LocalDateTime timestamp) {
        int userId = user.getId();
        try {
            if (!repository.updateTransactionSum(newSum, userId, timestamp)) {
                throw new TransactionNotFoundException();
            } else {
                StringBuilder message = new StringBuilder();
                int type = repository.getType(userId, timestamp);
                if (type == 0) {
                    throw new TransactionNotFoundException();
                }
                if (type == 2) {
                    String budgetCheckResult = getBudgetLimitCheck(user);
                    message.append(budgetCheckResult);
                }
                return message.toString();
            }
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    public void updateTransactionCategory(String newCategory, User user, LocalDateTime timestamp) {
        if (newCategory == null || newCategory.trim().isEmpty()) {
            throw new EmptyValueException("категория");
        }
        if (newCategory.trim().equalsIgnoreCase("цель") && user.getGoal() == 0) {
            throw new NoGoalException();
        }
        try {
            int userId = user.getId();
            if (!repository.updateTransactionCategory(newCategory, userId, timestamp)) {
                throw new TransactionNotFoundException();
            }
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    public void updateTransactionDescription(String newDescription, User user, LocalDateTime timestamp) {
        try {
            int userId = user.getId();
            if (!repository.updateTransactionDescription(newDescription, userId, timestamp)) {
                throw new TransactionNotFoundException();
            }
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    /**
     * Удаляет указанную транзакцию.
     */
    public void deleteTransaction(int userId, LocalDateTime timestamp) {
        try {
            if (!repository.deleteTransaction(userId, timestamp)) {
                throw new TransactionNotFoundException();
            }
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    public List<Transaction> getTransactions(FilterDTO filterDTO, int userId) {
        filterDTO.validate();
        String filter = filterDTO.getFilter();
        try {
            return switch (filter) {
                case "1" ->
                        transactionsFilterUpperCase(repository.getTransactionsBeforeTimestamp(userId, filterDTO.getBeforeTimestamp()));
                case "2" ->
                        transactionsFilterUpperCase(repository.getTransactionsAfterTimestamp(userId, filterDTO.getAfterTimestamp()));
                case "3" ->
                        transactionsFilterUpperCase(repository.getTransactionsByCategory(userId, filterDTO.getCategory().trim().toLowerCase()));
                case "41" -> transactionsFilterUpperCase(repository.getTransactionsByType(userId, 1));
                case "42" -> transactionsFilterUpperCase(repository.getTransactionsByType(userId, 2));
                case "5" -> transactionsFilterUpperCase(repository.getAllTransactions(userId));
                default -> throw new IllegalArgumentException("Неверный фильтр!");
            };
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }


    /**
     * Получает список транзакций по заданному типу.
     *
     * @param type тип транзакции для фильтрации
     * @return результат с транзакциями или сообщение об ошибке
     */
    public List<Transaction> getTransactionsByType(int userId, int type) {
        try {
            return transactionsFilterUpperCase(repository.getTransactionsByType(userId, type));
        } catch (SQLException e) {
            log.error("Ошибка при получении транзакций по типу: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Получает список всех транзакций пользователя.
     *
     * @return результат с транзакциями или сообщение об ошибке
     */
    public List<Transaction> getAllTransactions(int userId) {
        try {
            return transactionsFilterUpperCase(repository.getAllTransactions(userId));
        } catch (SQLException e) {
            log.error("Ошибка при получении всех транзакций: {}", e.getMessage(), e);
            return null;
        }
    }

    private List<Transaction> transactionsFilterUpperCase(List<Transaction> transactions) {
        transactions.forEach(transaction -> {
            String category = transaction.getCategory();
            if (category != null && !category.isEmpty()) {
                String capitalizedCategory = category.substring(0, 1).toUpperCase() + category.substring(1);
                transaction.setCategory(capitalizedCategory);
            }
        });
        return transactions;
    }

    private double getMonthlyExpenses(List<Transaction> incomes, List<Transaction> expenses) {
        return incomes.stream()
                .mapToDouble(Transaction::getSum)
                .sum() - expenses.stream()
                .mapToDouble(Transaction::getSum)
                .sum();
    }
}