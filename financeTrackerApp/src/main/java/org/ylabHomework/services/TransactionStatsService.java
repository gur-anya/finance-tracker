package org.ylabHomework.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ylabHomework.DTOs.TransactionsDTOs.serviceDTOs.PeriodDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.serviceDTOs.ServiceBalanceDTO;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.serviceClasses.customExceptions.CustomDatabaseException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы со статистикой сущности Transaction.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 09.03.2025
 */
@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class TransactionStatsService {
    private final TransactionRepository repository;
    private final UserService userService;

    /**
     * Вычисляет прогресс достижения финансовой цели пользователя.
     * Учитывает транзакции с категорией "цель".
     *
     * @param userId идентификатор пользователя
     * @return результат с разницей между целью и накоплениями или сообщение об ошибке
     */
    public double calculateGoalProgress(int userId) {
        String goalCategory = "цель";
        try {
            List<Transaction> incomes = repository.getTransactionsByType(userId, 1);
            List<Transaction> expenses = repository.getTransactionsByType(userId, 2);

            return incomes.stream()
                    .filter(t -> goalCategory.equalsIgnoreCase(t.getCategory()))
                    .mapToDouble(Transaction::getSum)
                    .sum()
                    - expenses.stream()
                    .filter(t -> goalCategory.equalsIgnoreCase(t.getCategory()))
                    .mapToDouble(Transaction::getSum)
                    .sum();
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }


    /**
     * Проверяет остаток месячного бюджета пользователя.
     * Учитывает доходы и расходы за текущий месяц относительно установленного бюджета.
     *
     * @return остаток бюджета (положительное значение — есть запас, отрицательное — превышение)
     */
    public double calculateMonthlyBudgetLimit(User user) {
        try {
            int userId = user.getId();
            LocalDateTime startOfMonth = LocalDateTime.of(
                    LocalDate.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), 1),
                    LocalTime.of(0, 0, 0, 0)
            );

            List<Transaction> transactions = repository.getTransactionsAfterTimestamp(userId, startOfMonth);


            double monthBalance = transactions.stream()
                    .filter(t -> t.getType() == 1)
                    .mapToDouble(Transaction::getSum)
                    .sum()
                    - transactions.stream()
                    .filter(t -> t.getType() == 2)
                    .mapToDouble(Transaction::getSum)
                    .sum();

            return user.getMonthlyBudget() + monthBalance;
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }


    /**
     * Возвращает расходы пользователя по категориям.
     *
     * @param userId идентификатор пользователя
     * @return карта категорий и соответствующих расходов
     */
    public Map<String, Double> getExpensesByCategory(int userId) {
        try {
            List<Transaction> transactionList = repository.getAllTransactions(userId);

            Map<String, Double> result = new LinkedHashMap<>();
            for (Transaction transaction : transactionList) {
                String category = transaction.getCategory().toLowerCase().trim();
                double currTrans = transaction.getSum();
                if (transaction.getType() == 2) {
                    result.put(category, result.getOrDefault(category, 0.0) + currTrans);
                }
            }

            if (result.isEmpty()) {
                return null;
            }
            return result;
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    /**
     * Возвращает текущий баланс пользователя на основе всех транзакций.
     *
     * @param userId идентификатор пользователя
     * @return строка с уведомлением о балансе
     */
    public ServiceBalanceDTO getCurrentBalance(int userId) {
        try {
            List<Transaction> transactionList = repository.getAllTransactions(userId);
            if (transactionList.isEmpty()) {
                return new ServiceBalanceDTO(0.0, true);
            }
            double totalIncome = transactionList.stream()
                    .filter(t -> t.getType() == 1)
                    .mapToDouble(Transaction::getSum)
                    .sum();
            double totalExpense = transactionList.stream()
                    .filter(t -> t.getType() == 2)
                    .mapToDouble(Transaction::getSum)
                    .sum();
            return new ServiceBalanceDTO(totalIncome - totalExpense, false);
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    /**
     * Возвращает доходы и расходы за указанный период.
     *
     * @param userId идентификатор пользователя
     * @return массив: [доходы, расходы, баланс]
     */
    public double[] getPeriodIncomeExpenseSummary(int userId, PeriodDTO periodDTO) {
        periodDTO.validate();
        LocalDateTime startTimestamp = periodDTO.getStartTimestamp();
        LocalDateTime endTimestamp = periodDTO.getEndTimestamp();

        List<Transaction> transactionList = fetchTransactionsForPeriod(userId, startTimestamp, endTimestamp);
        double totalIncome = transactionList.stream()
                .filter(t -> t.getType() == 1)
                .mapToDouble(Transaction::getSum)
                .sum();
        double totalExpense = transactionList.stream()
                .filter(t -> t.getType() == 2)
                .mapToDouble(Transaction::getSum)
                .sum();

        return new double[]{totalIncome, totalExpense, totalIncome - totalExpense};
    }

    /**
     * Формирует финансовый отчёт пользователя за указанный период или за всё время.
     *
     * @param userId идентификатор пользователя
     * @return объект FinancialReport с нулевыми значениями, если транзакций нет
     */
    public FinancialReport buildFinancialReport(int userId, PeriodDTO dto) {
        dto.validate();
        LocalDateTime startTime = dto.getStartTimestamp();
        LocalDateTime endTime = dto.getEndTimestamp();
        List<Transaction> transactions = fetchTransactionsForPeriod(userId, startTime, endTime);

        double[] summary = calculateTransactionSummary(transactions);
        double totalIncome = summary[0];
        double totalExpense = summary[1];
        double totalBalance = summary[2];

        Map<String, double[]> categoryReport = new HashMap<>();
        for (Transaction transaction : transactions) {
            String category = transaction.getCategory().trim().toLowerCase();
            double income = transaction.getType() == 1 ? transaction.getSum() : 0.0;
            double expense = transaction.getType() == 2 ? transaction.getSum() : 0.0;

            double[] stats = categoryReport.computeIfAbsent(category, key -> new double[3]);
            stats[0] += income;
            stats[1] += expense;
            stats[2] = stats[0] - stats[1];
        }

        double[] goalData = computeGoalStatistics(userId, startTime, endTime);
        return new FinancialReport(totalIncome, totalExpense, totalBalance, categoryReport, goalData);
    }

    private List<Transaction> fetchTransactionsForPeriod(int userId, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            if (startTime == null && endTime == null) {
                return repository.getAllTransactions(userId);
            }
            if (startTime != null && endTime != null) {
                return repository.getTransactionsBetweenTimestamps(userId, startTime, endTime);
            }
            if (startTime != null) {
                return repository.getTransactionsAfterTimestamp(userId, startTime);
            }
            return repository.getTransactionsBeforeTimestamp(userId, endTime);
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    private double[] calculateTransactionSummary(List<Transaction> transactionsList) {
        double income = transactionsList.stream()
                .filter(t -> t.getType() == 1)
                .mapToDouble(Transaction::getSum)
                .sum();
        double expense = transactionsList.stream()
                .filter(t -> t.getType() == 2)
                .mapToDouble(Transaction::getSum)
                .sum();
        double balance = income - expense;
        return new double[]{income, expense, balance};
    }

    private double[] computeGoalStatistics(int userId, LocalDateTime start, LocalDateTime end) {
        List<Transaction> goalTransactions;
        String goalCategory = "цель";

        try {
            if (start == null && end != null) {
                goalTransactions = repository.getTransactionsBeforeTimestamp(userId, end)
                        .stream()
                        .filter(t -> goalCategory.equalsIgnoreCase(t.getCategory()))
                        .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                        .collect(Collectors.toList());
            } else if (start != null && end == null) {
                goalTransactions = repository.getTransactionsAfterTimestamp(userId, start)
                        .stream()
                        .filter(t -> goalCategory.equalsIgnoreCase(t.getCategory()))
                        .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                        .collect(Collectors.toList());
            } else if (start == null) {
                goalTransactions = repository.getAllTransactions(userId)
                        .stream()
                        .filter(t -> goalCategory.equalsIgnoreCase(t.getCategory()))
                        .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                        .collect(Collectors.toList());
            } else {
                goalTransactions = repository.getTransactionsBetweenTimestamps(userId, start, end)
                        .stream()
                        .filter(t -> goalCategory.equalsIgnoreCase(t.getCategory()))
                        .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                        .collect(Collectors.toList());
            }

            double goal = userService.getGoal(userId);
            if (goalTransactions.isEmpty() || goal <= 0) {
                return new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
            }

            double[] summary = calculateTransactionSummary(goalTransactions);
            double goalIncome = summary[0];
            double goalExpense = summary[1];
            double saved = summary[2];
            double leftToSave = goal - saved;

            return new double[]{goal, goalIncome, goalExpense, saved, leftToSave};
        } catch (SQLException e) {
            throw new CustomDatabaseException(e);
        }
    }

    public static class FinancialReport {
        public double totalIncome;
        public double totalExpense;
        public double totalBalance;
        public Map<String, double[]> categoryReport;
        public double[] goalData;

        public FinancialReport(double totalIncome, double totalExpense, double totalBalance,
                               Map<String, double[]> categoryReport, double[] goalData) {
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
            this.totalBalance = totalBalance;
            this.categoryReport = categoryReport;
            this.goalData = goalData != null ? goalData : new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
        }
    }
}