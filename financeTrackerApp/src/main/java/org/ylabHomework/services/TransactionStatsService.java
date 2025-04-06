package org.ylabHomework.services;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для работы со статистикой сущности Transaction.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 09.03.2025
 * </p>
 */
@Service
@Data
@RequiredArgsConstructor
public class TransactionStatsService {

    public final TransactionRepository repository;



    /**
     * Проверяет остаток месячного бюджета пользователя.
     * Учитывает доходы и расходы за текущий месяц относительно установленного бюджета.
     *
     * @return остаток бюджета (положительное значение — есть запас, отрицательное — превышение)
     */
    public double checkMonthlyBudgetLimit(User user) {
        List<Transaction> incomes;
        List<Transaction> expenses;
        try {
            incomes = repository.getTransactionsByType(user,1);
            expenses = repository.getTransactionsByType(user,2);
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return 0;
        }

        LocalDateTime startOfMonth = LocalDateTime.of(
                LocalDate.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), 1),
                LocalTime.of(0, 0, 0, 0)
        );

        try {
            List<Transaction> sortedIncomes = repository.getSortedTransactionsAfterTimestamp(startOfMonth, incomes);
            List<Transaction> sortedExpenses = repository.getSortedTransactionsAfterTimestamp(startOfMonth, expenses);
            double totalIncome = sortedIncomes.stream().mapToDouble(Transaction::getSum).sum();
            double totalExpense = sortedExpenses.stream().mapToDouble(Transaction::getSum).sum();

            return repository.getMonthlyBudget(user) + totalIncome - totalExpense;
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return 0;
        }
    }

    /**
     * Проверяет прогресс достижения финансовой цели пользователя.
     * Учитывает транзакции с категорией "цель".
     *
     * @return разница между целью и накоплениями (положительное — осталось накопить, отрицательное — превышение)
     */
    public double checkGoalProgress(User user) {
        String goalCategory = "цель";
        List<Transaction> incomes;
        List<Transaction> expenses;
        try {
            incomes = repository.getTransactionsByType(user,1);
            expenses = repository.getTransactionsByType(user,2);
            List<Transaction> sortedIncomes = repository.getSortedTransactionsByCategory(goalCategory, incomes);
            List<Transaction> sortedExpenses = repository.getSortedTransactionsByCategory(goalCategory, expenses);

            double totalIncome = sortedIncomes.stream().mapToDouble(Transaction::getSum).sum();
            double totalExpense = sortedExpenses.stream().mapToDouble(Transaction::getSum).sum();

            return repository.getGoal(user) - totalIncome + totalExpense;
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return 0;
        }
    }

    /**
     * Анализирует расходы пользователя по категориям.
     * Возвращает карту, где ключ — категория, значение — сумма расходов (отрицательная).
     *
     * @return карта категорий и соответствующих расходов
     */
    public Map<String, Double> analyzeExpenseByCategories(User user) {
        List<Transaction> transactionList;
        try {
            transactionList = repository.getAllTransactions(user);
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return new LinkedHashMap<>();
        }
        Map<String, Double> result = new LinkedHashMap<>();
        for (Transaction transaction : transactionList) {
            String category = transaction.getCategory().toLowerCase().trim();
            double currTrans = transaction.getSum();
            if (transaction.getType() == 2) {
                result.put(category, result.getOrDefault(category, 0.0) + currTrans);
            }
        }
        return result;
    }

    /**
     * Подсчитывает текущий баланс пользователя на основе всех транзакций.
     *
     * @return уведомление текущем о балансе (доходы минус расходы)
     */
    public String calculateBalance(User user) {
        List<Transaction> transactionList;
        try {
            transactionList = repository.getAllTransactions(user);
        } catch (SQLException e) {
            return databaseError(e);
        }

        double totalIncome = transactionList.stream()
                .filter(t -> t.getType() == 1)
                .mapToDouble(Transaction::getSum)
                .sum();
        double totalExpense = transactionList.stream()
                .filter(t -> t.getType() == 2)
                .mapToDouble(Transaction::getSum)
                .sum();
        double balance = totalIncome - totalExpense;

        return String.format("Ваш баланс: %.2f руб.", balance);
    }

    /**
     * Возвращает статистику доходов и расходов за указанный период.
     *
     * @param timestamp1 начальная дата периода
     * @param timestamp2 конечная дата периода
     * @return массив: [доходы, расходы, баланс]
     */
    public double[] getIncomeExpenseForPeriod(User user, LocalDateTime timestamp1, LocalDateTime timestamp2) {
        List<Transaction> transactionList;
        try {
            transactionList = repository.getTransactionsBetweenTimestamps(user, timestamp1, timestamp2);
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return new double[]{0, 0, 0};
        }

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
     * @param startTime начальная дата периода (может быть null для полного периода)
     * @param endTime   конечная дата периода (может быть null для полного периода)
     * @return объект FinancialReport с данными отчёта или null, если транзакций нет
     */
    public FinancialReport generateGeneralReport(User user, LocalDateTime startTime, LocalDateTime endTime) {
        List<Transaction> transactions = getTransactionsForPeriod(user, startTime, endTime);
        if (!transactions.isEmpty()) {
            double[] basicStats = getBasicStats(transactions);
            assert basicStats != null;
            double totalIncome = basicStats[0];
            double totalExpense = basicStats[1];
            double totalBalance = basicStats[2];

            Map<String, double[]> categoryReport = new HashMap<>();
            for (Transaction transaction : transactions) {
                String category = transaction.getCategory().trim().toLowerCase();

                double income = 0.0;
                if (transaction.getType() == 1) {
                    income = transaction.getSum();
                }
                double expense = 0.0;
                if (transaction.getType() == 2) {
                    expense = transaction.getSum();
                }

                double[] stats = new double[3];

                if (categoryReport.containsKey(category)) {
                    stats = categoryReport.get(category);
                }
                stats[0] += income;
                stats[1] += expense;
                stats[2] = stats[0] - stats[1];

                categoryReport.put(category, stats);
            }

            double[] goalData = calculateGoalData(user, startTime, endTime);

            return new FinancialReport(totalIncome, totalExpense, totalBalance, categoryReport, goalData);
        }
        return null;
    }

    /**
     * Вспомогательный метод для получения транзакций за указанный период.
     *
     * @param startTime начальная дата периода (может быть null)
     * @param endTime   конечная дата периода (может быть null)
     * @return список транзакций за период или все транзакции, если период не задан
     */
    private List<Transaction> getTransactionsForPeriod(User user, LocalDateTime startTime, LocalDateTime endTime) {
        try {
            if (startTime == null && endTime == null) {
                return repository.getAllTransactions(user);
            }
            if (startTime != null && endTime != null) {
                return repository.getTransactionsBetweenTimestamps(user, startTime, endTime);
            }
            if (startTime != null) {
                return repository.getTransactionsAfterTimestamp(user, startTime);
            }
            return repository.getTransactionsBeforeTimestamp(user, endTime);
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return new ArrayList<>();
        }
    }

    /**
     * Вспомогательный метод для расчёта статистики по финансовой цели.
     *
     * @return массив: [цель, доходы по цели, расходы по цели, накоплено, осталось], или null, если нет транзакций
     */
    public double[] calculateGoalData(User user, LocalDateTime start, LocalDateTime end) {
        List<Transaction> goalTransactions;
        try {
            if (start == null && end != null) {
                goalTransactions = repository.getTransactionsBeforeTimestamp(user, end)
                        .stream()
                        .filter(t -> "цель".equalsIgnoreCase(t.getCategory()))
                        .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                        .collect(Collectors.toList());
            } else if (start != null && end == null) {
                goalTransactions = repository.getTransactionsAfterTimestamp(user, start)
                        .stream()
                        .filter(t -> "цель".equalsIgnoreCase(t.getCategory()))
                        .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                        .collect(Collectors.toList());
            } else if (start == null & end == null) {
                goalTransactions = repository.getAllTransactions(user)
                        .stream()
                        .filter(t -> "цель".equalsIgnoreCase(t.getCategory()))
                        .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                        .collect(Collectors.toList());
            } else {
                goalTransactions = repository.getTransactionsBetweenTimestamps(user, start, end)
                        .stream()
                        .filter(t -> "цель".equalsIgnoreCase(t.getCategory()))
                        .sorted(Comparator.comparing(Transaction::getTimestamp).reversed())
                        .collect(Collectors.toList());
            }
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            if ("22001".equals(sqlState)) {
                System.out.println("Слишком длинная категория: " + message + " Попробуйте ещё раз!");
            } else System.out.println(databaseError(e));
            return null;
        }
        if (!goalTransactions.isEmpty()) {
            double[] basicStats = getBasicStats(goalTransactions);
            if (basicStats == null) return null;
            double goalIncome = basicStats[0];
            double goalExpense = basicStats[1];
            double saved = basicStats[2];

            double goalSum;
            try {
                goalSum = repository.getGoal(user);
            } catch (SQLException e) {
                System.out.println(databaseError(e));
                return null;
            }
            double leftToSave = goalSum - saved;

            return new double[]{goalSum, goalIncome, goalExpense, saved, leftToSave};
        }
        return null;
    }

    /**
     * Вспомогательный метод для подсчёта базовой статистики по списку транзакций.
     *
     * @param transactionsList список транзакций
     * @return массив: [доходы, расходы, баланс], или null, если список пуст
     */
    private double[] getBasicStats(List<Transaction> transactionsList) {
        if (transactionsList.isEmpty()) {
            return null;
        }

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

    public String databaseError(Exception e) {
        return "Ошибка базы данных: " + e.getMessage() + " Попробуйте ещё раз!";
    }

    public double getGoal(User user) {
        try {
            return repository.getGoal(user);
        } catch (SQLException e) {
            System.out.println("Ошибка! " + e.getMessage());
            return 0;
        }
    }

    public void setGoal(User user, double newGoal) {
        try {
            repository.setGoal(user, newGoal);
        } catch (SQLException e) {
            System.out.println("Ошибка! " + e.getMessage());
        }
    }

    public double getMonthlyBudget(User user) {
        try {
            return repository.getMonthlyBudget(user);
        } catch (SQLException e) {
            System.out.println("Ошибка! " + e.getMessage());
            return 0;
        }
    }

    public void setMonthlyBudget(User user, double newBudget) {
        try {
            repository.setMonthlyBudget(user, newBudget);
        } catch (SQLException e) {
            System.out.println("Ошибка! " + e.getMessage());
        }
    }

    /**
     * Финансовый отчёт пользователя.
     *
     */
    public static class FinancialReport {
        public double totalIncome;
        public double totalExpense;
        public double totalBalance;
        public Map<String, double[]> categoryReport;
        public double[] goalData;

        public FinancialReport(double totalIncome, double totalExpense, double totalBalance, Map<String, double[]> categoryReport, double[] goalData) {
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
            this.totalBalance = totalBalance;
            this.categoryReport = categoryReport;
            this.goalData = goalData;
        }
    }
}