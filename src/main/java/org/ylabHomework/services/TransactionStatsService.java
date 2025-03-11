package org.ylabHomework.services;

import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис для работы с со статистикой сущности Transaction.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 09.03.2025
 * </p>
 */
public class TransactionStatsService {

    public final TransactionRepository repository;


    public final User user;

    /**
     * Конструктор сервиса статистики транзакций.
     *
     * @param repository репозиторий транзакций
     * @param user       пользователь, для которого выполняется анализ
     */
    public TransactionStatsService(TransactionRepository repository, User user) {
        this.repository = repository;
        this.user = user;
    }


    /**
     * Проверяет остаток месячного бюджета пользователя.
     * Учитывает доходы и расходы за текущий месяц относительно установленного бюджета.
     *
     * @return остаток бюджета (положительное значение — есть запас, отрицательное — превышение)
     */
    public double checkMonthlyBudgetLimit() {
        List<Transaction> incomes = repository.getTransactionsByType(Transaction.TransactionTYPE.INCOME);
        List<Transaction> expenses = repository.getTransactionsByType(Transaction.TransactionTYPE.EXPENSE);

        LocalDateTime startOfMonth = LocalDateTime.of(
                LocalDate.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), 1),
                LocalTime.of(0, 0, 0, 0)
        );
        List<Transaction> sortedIncomes = repository.getSortedTransactionsAfterTimestamp(startOfMonth, incomes);
        List<Transaction> sortedExpenses = repository.getSortedTransactionsAfterTimestamp(startOfMonth, expenses);

        double totalIncome = sortedIncomes.stream()
                .mapToDouble(Transaction::getSum)
                .sum();
        double totalExpense = sortedExpenses.stream()
                .mapToDouble(Transaction::getSum)
                .sum();

        return user.getMonthlyBudget() + totalIncome - totalExpense;
    }

    /**
     * Проверяет прогресс достижения финансовой цели пользователя.
     * Учитывает транзакции с категорией "цель".
     *
     * @return разница между целью и накоплениями (положительное — осталось накопить, отрицательное — превышение)
     */
    public double checkGoalProgress() {
        String goalCategory = "цель";

        List<Transaction> incomes = repository.getTransactionsByType(Transaction.TransactionTYPE.INCOME);
        List<Transaction> expenses = repository.getTransactionsByType(Transaction.TransactionTYPE.EXPENSE);

        List<Transaction> sortedIncomes = repository.getSortedTransactionsByCategory(goalCategory, incomes);
        List<Transaction> sortedExpenses = repository.getSortedTransactionsByCategory(goalCategory, expenses);

        double totalIncome = sortedIncomes.stream()
                .mapToDouble(Transaction::getSum)
                .sum();
        double totalExpense = sortedExpenses.stream()
                .mapToDouble(Transaction::getSum)
                .sum();

        return user.getGoal() - totalIncome + totalExpense;
    }

    /**
     * Анализирует расходы пользователя по категориям.
     * Возвращает карту, где ключ — категория, значение — сумма расходов (отрицательная).
     *
     * @return карта категорий и соответствующих расходов
     */
    public Map<String, Double> analyzeExpenseByCategories() {
        List<Transaction> transactionList = repository.getAllTransactions();
        Map<String, Double> result = new LinkedHashMap<>();
        for (Transaction transaction : transactionList) {
            String category = transaction.getCategory().toLowerCase().trim();
            double currTrans = transaction.getSum();
            if (transaction.getType() == Transaction.TransactionTYPE.EXPENSE) {
                result.put(category, result.getOrDefault(category, 0.0) - currTrans);
            }
        }
        return result;
    }

    /**
     * Подсчитывает текущий баланс пользователя на основе всех транзакций.
     *
     * @return текущий баланс (доходы минус расходы)
     */
    public double calculateBalance() {
        List<Transaction> transactionList = repository.getAllTransactions();

        double totalIncome = transactionList.stream()
                .filter(t -> t.getType() == Transaction.TransactionTYPE.INCOME)
                .mapToDouble(Transaction::getSum)
                .sum();

        double totalExpense = transactionList.stream()
                .filter(t -> t.getType() == Transaction.TransactionTYPE.EXPENSE)
                .mapToDouble(Transaction::getSum)
                .sum();

        return totalIncome - totalExpense;
    }

    /**
     * Возвращает статистику доходов и расходов за указанный период.
     *
     * @param timestamp1 начальная дата периода
     * @param timestamp2 конечная дата периода
     * @return массив: [доходы, расходы, баланс]
     */
    public double[] getIncomeExpenseForPeriod(LocalDateTime timestamp1, LocalDateTime timestamp2) {
        List<Transaction> transactionList = repository.getTransactionsBetweenTimestamps(timestamp1, timestamp2);

        double totalIncome = transactionList.stream()
                .filter(t -> t.getType() == Transaction.TransactionTYPE.INCOME)
                .mapToDouble(Transaction::getSum)
                .sum();

        double totalExpense = transactionList.stream()
                .filter(t -> t.getType() == Transaction.TransactionTYPE.EXPENSE)
                .mapToDouble(Transaction::getSum)
                .sum();

        return new double[]{totalIncome, Math.abs(totalExpense), totalIncome - totalExpense};
    }

    /**
     * Формирует финансовый отчёт пользователя за указанный период или за всё время.
     *
     * @param startTime начальная дата периода (может быть null для полного периода)
     * @param endTime   конечная дата периода (может быть null для полного периода)
     * @return объект FinancialReport с данными отчёта или null, если транзакций нет
     */
    public FinancialReport generateGeneralReport(LocalDateTime startTime, LocalDateTime endTime) {
        List<Transaction> transactions = getTransactionsForPeriod(startTime, endTime);
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
                if (transaction.getType() == Transaction.TransactionTYPE.INCOME) {
                    income = transaction.getSum();
                }
                double expense = 0.0;
                if (transaction.getType() == Transaction.TransactionTYPE.EXPENSE) {
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

            double[] goalData = calculateGoalData();

            return new FinancialReport(totalIncome, totalExpense, totalBalance, categoryReport, goalData);
        } else return null;
    }

    /**
     * Вспомогательный метод для получения транзакций за указанный период.
     *
     * @param startTime начальная дата периода (может быть null)
     * @param endTime   конечная дата периода (может быть null)
     * @return список транзакций за период или все транзакции, если период не задан
     */
    private List<Transaction> getTransactionsForPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null && endTime == null) {
            return repository.getAllTransactions();
        } else {
            if (startTime != null && endTime != null) {
                return repository.getTransactionsBetweenTimestamps(startTime, endTime);
            }
            if (startTime != null) {
                return repository.getTransactionsAfterTimestamp(startTime);
            } else {
                return repository.getTransactionsBeforeTimestamp(endTime);
            }
        }
    }

    /**
     * Вспомогательный метод для расчёта статистики по финансовой цели.
     *
     * @return массив: [цель, доходы по цели, расходы по цели, накоплено, осталось накопить], или null, если нет транзакций
     */
    private double[] calculateGoalData() {
        List<Transaction> goalTransactions = repository.getTransactionsByCategory("цель");

        if (!goalTransactions.isEmpty()) {
            double[] basicStats = getBasicStats(goalTransactions);

            assert basicStats != null;
            double goalIncome = basicStats[0];
            double goalExpense = basicStats[1];
            double saved = basicStats[2];

            double goalAmount = repository.getGoal();
            double leftToSave = goalAmount - saved;

            return new double[]{goalAmount, goalIncome, goalExpense, saved, leftToSave};
        } else return null;
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
                .filter(t -> t.getType() == Transaction.TransactionTYPE.INCOME)
                .mapToDouble(Transaction::getSum)
                .sum();
        double expense = transactionsList.stream()
                .filter(t -> t.getType() == Transaction.TransactionTYPE.EXPENSE)
                .mapToDouble(Transaction::getSum)
                .sum();
        double balance = income - expense;

        return new double[]{income, expense, balance};
    }

    /**
     * Финансовый отчёт пользователя.
     *
     * @param totalIncome   общий доход за период
     * @param totalExpense  общий расход за период
     * @param totalBalance  итоговый баланс за период
     * @param categoryReport статистика по категориям (доходы, расходы, баланс)
     * @param goalData      данные по финансовой цели (цель, доходы, расходы, накоплено, осталось)
     */
    public record FinancialReport(
            double totalIncome,
            double totalExpense,
            double totalBalance,
            Map<String, double[]> categoryReport,
            double[] goalData
    ) {
    }
}
