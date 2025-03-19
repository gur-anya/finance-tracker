package org.ylabHomework.services;

import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

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
        List<Transaction> incomes;
        List<Transaction> expenses;
        try {
            incomes = repository.getTransactionsByType(1);
            expenses = repository.getTransactionsByType(2);
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
            List<Transaction>   sortedExpenses = repository.getSortedTransactionsAfterTimestamp(startOfMonth, expenses);
            double totalIncome = sortedIncomes.stream().mapToDouble(Transaction::getSum).sum();
            double totalExpense = sortedExpenses.stream().mapToDouble(Transaction::getSum).sum();

            return repository.getMonthlyBudget() + totalIncome - totalExpense;
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
    public double checkGoalProgress() {
        String goalCategory = "цель";
        List<Transaction> incomes;
        List<Transaction> expenses;
        try {
            incomes = repository.getTransactionsByType(1);
            expenses = repository.getTransactionsByType(2);
        } catch (SQLException e) {
             System.out.println(databaseError(e));
            return 0;
        }

        try {
            List<Transaction>   sortedIncomes = repository.getSortedTransactionsByCategory(goalCategory, incomes);
            List<Transaction>  sortedExpenses = repository.getSortedTransactionsByCategory(goalCategory, expenses);

            double totalIncome = sortedIncomes.stream().mapToDouble(Transaction::getSum).sum();
            double totalExpense = sortedExpenses.stream().mapToDouble(Transaction::getSum).sum();

            return repository.getGoal() - totalIncome + totalExpense;
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
    public Map<String, Double> analyzeExpenseByCategories() {
        List<Transaction> transactionList;
        try {
            transactionList = repository.getAllTransactions();
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
     * @return уведомление текущем балансе (доходы минус расходы)
     */
    public String calculateBalance() {
        List<Transaction> transactionList;
        try {
            transactionList = repository.getAllTransactions();
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
    public double[] getIncomeExpenseForPeriod(LocalDateTime timestamp1, LocalDateTime timestamp2) {
        List<Transaction> transactionList;
        try {
            transactionList = repository.getTransactionsBetweenTimestamps(timestamp1, timestamp2);
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

            double[] goalData = calculateGoalData();

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
    private List<Transaction> getTransactionsForPeriod(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            if (startTime == null && endTime == null) {
                return repository.getAllTransactions();
            }
            if (startTime != null && endTime != null) {
                return repository.getTransactionsBetweenTimestamps(startTime, endTime);
            }
            if (startTime != null) {
                return repository.getTransactionsAfterTimestamp(startTime);
            }
            return repository.getTransactionsBeforeTimestamp(endTime);
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
    public double[] calculateGoalData() {
        List<Transaction> goalTransactions;
        try {
            goalTransactions = repository.getTransactionsByCategory("цель");
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            if ("22001".equals(sqlState)) {
                System.out.println("Слишком длинная категория: " + message + " Попробуйте ещё раз!");
            } else  System.out.println(databaseError(e));
            return null;
        }

        if (!goalTransactions.isEmpty()) {
            double[] basicStats = getBasicStats(goalTransactions);
            if (basicStats == null) return null;
            double goalIncome = basicStats[0];
            double goalExpense = basicStats[1];
            double saved = basicStats[2];

            double goalAmount;
            try {
                goalAmount = repository.getGoal();
            } catch (SQLException e) {
                System.out.println(databaseError(e));
                return null;
            }
            double leftToSave = goalAmount - saved;

            return new double[]{goalAmount, goalIncome, goalExpense, saved, leftToSave};
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

    /**
     * Возвращает прогресс достижения финансовой цели в виде строки.
     *
     * @return сообщение о прогрессе цели
     */
    public String getGoalProgress() {
        try {
            if (repository.getGoal() == 0){
                return "";
            } else {
                double leftToGoal = checkGoalProgress();
                if (leftToGoal < 0) {
                    return "Поздравляем! Вы превысили цель на " + String.format("%.2f", Math.abs(leftToGoal)) + " руб.!";
                }
                if (leftToGoal == 0) {
                    return "Поздравляем! Вы достигли своей цели!";
                }
                return "До цели осталось накопить " + String.format("%.2f", leftToGoal) + " руб. Отличный результат!";
            }
        } catch (SQLException e) {
            System.out.println("Ошибка! " + e.getMessage());
        }
        return "";
    }

    /**
     * Возвращает анализ расходов по категориям в виде строки.
     *
     * @return отформатированный анализ категорий
     */
    public String getCategoryAnalysis() {
        Map<String, Double> categoryAnalysis = analyzeExpenseByCategories();
        if (categoryAnalysis.isEmpty()) {
            return "Нет транзакций для анализа!";
        }
        StringBuilder sb = new StringBuilder("Анализ расходов по категориям:\n");
        sb.append(String.format("%-20s %-15s%n", "Категория", "Расходы")).append("-".repeat(35)).append("\n");
        for (Map.Entry<String, Double> entry : categoryAnalysis.entrySet()) {
            String category = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
            double expense = Math.abs(entry.getValue());
            sb.append(String.format("%-20s %15.2f руб.%n", category, expense));
        }
        return sb.toString();
    }

    /**
     * Возвращает сводку доходов и расходов за период в виде строки.
     *
     * @param timestamp1 начальная дата периода
     * @param timestamp2 конечная дата периода
     * @return отформатированная сводка
     */
    public String getSummary(LocalDateTime timestamp1, LocalDateTime timestamp2) {
        if (timestamp1.isAfter(timestamp2)) {
            LocalDateTime aux = timestamp1;
            timestamp1 = timestamp2;
            timestamp2 = aux;
        }
        double[] stats = getIncomeExpenseForPeriod(timestamp1, timestamp2);
        return String.format("Период: %s - %s%nДоходы: %15.2f руб.%nРасходы: %15.2f руб.%nИтоговый баланс: %15.2f руб.",
                timestamp1.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                timestamp2.format(java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                stats[0], stats[1], stats[2]);
    }

    /**
     * Генерирует отформатированный финансовый отчёт за указанный период.
     *
     * @param startTime начальная дата периода (может быть null)
     * @param endTime   конечная дата периода (может быть null)
     * @return строка с отчётом или сообщение об отсутствии данных
     */
    public String generateGeneralReportFormatted(LocalDateTime startTime, LocalDateTime endTime) {
        FinancialReport report = generateGeneralReport(startTime, endTime);
        if (report == null) {
            return "Транзакции за период не найдены!";
        }

        StringBuilder sb = new StringBuilder("ФИНАНСОВЫЙ ОТЧЁТ\n");
        sb.append("-".repeat(50)).append("\n");
        sb.append("Общие данные:\n");
        sb.append(String.format("Доходы: %15.2f руб.%n", report.totalIncome()));
        sb.append(String.format("Расходы: %15.2f руб.%n", report.totalExpense()));
        sb.append(String.format("Баланс: %15.2f руб.%n", report.totalBalance()));
        sb.append("-".repeat(50)).append("\n");

        sb.append("По категориям:\n");
        sb.append(String.format("%-20s %-15s %-15s %-15s%n", "Категория", "Доходы", "Расходы", "Баланс"));
        sb.append("-".repeat(65)).append("\n");
        for (Map.Entry<String, double[]> entry : report.categoryReport().entrySet()) {
            String category = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
            double[] stats = entry.getValue();
            sb.append(String.format("%-20s %15.2f %15.2f %15.2f%n", category, stats[0], stats[1], stats[2]));
        }
        sb.append("-".repeat(65)).append("\n");

        if (report.goalData() != null) {
            sb.append("Прогресс цели:\n");
            sb.append(String.format("Цель: %15.2f руб.%n", report.goalData()[0]));
            sb.append(String.format("Доходы: %15.2f руб.%n", report.goalData()[1]));
            sb.append(String.format("Расходы: %15.2f руб.%n", report.goalData()[2]));
            sb.append(String.format("Накоплено: %15.2f руб.%n", report.goalData()[3]));
            sb.append(String.format("Осталось: %15.2f руб.%n", report.goalData()[4]));
            sb.append("-".repeat(50)).append("\n");
        }

        return sb.toString();
    }

    public String databaseError(Exception e) {
        return "Ошибка базы данных: " + e.getMessage() + " Попробуйте ещё раз!";
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