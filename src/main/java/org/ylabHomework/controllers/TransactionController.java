package org.ylabHomework.controllers;

import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class TransactionController {

    public TransactionController(TransactionService service, UserController controller, User user) {
        this.service = service;
        this.userController = controller;
        this.user = user;
        this.statsService = service.getStatsService();
    }

    private final Scanner scanner = new Scanner(System.in);
    private final User user;
    private final TransactionService service;
    private final UserController userController;
    private final TransactionStatsService statsService;

    public void showMainMenu() {
        while (true) {
            System.out.println("""
                    Выберите действие:
                    1. Просмотреть транзакции
                    2. Добавить транзакцию
                    3. Редактировать транзакции
                    4. Удалить транзакцию
                    5. Управлять бюджетом на месяц
                    6. Управлять целью
                    7. Перейти в меню анализа
                    8. Назад
                    9. Выход из программы
                    """);
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> showTransactions();
                case "2" -> createTransaction();
                case "3" -> showSettings();
                case "4" -> deleteTransaction();
                case "5" -> manageMonthlyBudget();
                case "6" -> manageGoal();
                case "7" -> doAnalysis();
                case "8" -> userController.showMainPageUser();
                case "9" -> System.exit(0);
                default -> System.out.println("Пожалуйста, введите 1, 2, 3, 4 или 5.");
            }
        }
    }

    public void showTransactions() {
        mainLoop:
        while (true) {
            System.out.println("""
                    Выберите фильтр для транзакций:
                    1. До даты
                    2. После даты
                    3. По категории
                    4. По типу (доход/расход)
                    5. Все (без фильтра)
                    6. Назад
                    """);
            String show = scanner.nextLine();
            switch (show) {
                case "1" -> {
                    LocalDateTime timestamp = getTimestampInput();
                    List<Transaction> transactions = service.getTransactionsBeforeTimestamp(timestamp);
                    showTransactionList(transactions);
                }
                case "2" -> {
                    LocalDateTime timestamp = getTimestampInput();
                    List<Transaction> transactions = service.getTransactionsAfterTimestamp(timestamp);
                    showTransactionList(transactions);
                }
                case "3" -> {
                    System.out.println("Введите категорию:");
                    String category = scanner.nextLine();
                    List<Transaction> transactions = service.getTransactionsByCategory(category);
                    showTransactionList(transactions);
                }
                case "4" -> {
                    Transaction.TransactionTYPE type = getTransactionTypeInput();
                    List<Transaction> transactions = service.getTransactionsByType(type);
                    showTransactionList(transactions);
                }
                case "5" -> {
                    List<Transaction> transactions = service.getAllTransactions();
                    showTransactionList(transactions);
                }
                case "6" -> {
                    showMainMenu();
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1, 2, 3, 4, 5 или 6.");
            }
        }
    }

    public void showSettings() {
        System.out.println("""
                Выберите действие:
                1. Добавить новую транзакцию
                2. Отредактировать существующую
                3. Удалить транзакцию
                4. В главное меню
                """);

        mainLoop:
        while (true) {
            String input = scanner.nextLine();
            switch (input) {
                case "1" -> {
                    createTransaction();
                    break mainLoop;
                }
                case "2" -> {
                    updateTransaction();
                    break mainLoop;
                }
                case "3" -> {
                    deleteTransaction();
                    break mainLoop;
                }
                case "4" -> {
                    showMainMenu();
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1, 2, 3 или 4.");
            }
        }
    }

    public void createTransaction() {
        System.out.println("Введите тип транзакции (доход/расход):");
        Transaction.TransactionTYPE type = getTransactionTypeInput();

        System.out.println("Введите сумму:");
        double sum = getDoubleInput();

        System.out.println("Введите категорию. Для добавления транзакции по цели введите категорию \"Цель\":");
        String category = getCategoryInput();

        System.out.println("Введите описание (опционально):");
        String description = scanner.nextLine();
        if (description.isEmpty()) {
            description = "-";
        }

        System.out.println(service.createTransaction(type, sum, category, description));
        if (type == Transaction.TransactionTYPE.EXPENSE && user.getMonthlyBudget() != 0) {
            double monthlyBudget = statsService.checkMonthlyBudgetLimit();
            if (monthlyBudget < 0) {
                notifyAboutMonthlyLimit(Math.abs(monthlyBudget));
            }
        }
    }

    public String getCategoryInput() {
        String category;
        while (true) {
            category = scanner.nextLine();
            if (Objects.equals(category.toLowerCase().trim(), "цель") && user.getGoal() == 0) {
                System.out.println("Вы еще не установили цель! Установите цель в меню управления.");
                System.out.println("Хотите прервать создание транзакции и создать цель прямо сейчас? да/нет");
                String input = scanner.nextLine();
                choiceLoop:
                while (true) {
                    switch (input.toLowerCase()) {
                        case "да" -> updateGoal();
                        case "нет" -> {
                            break choiceLoop;
                        }
                        default -> System.out.println("Пожалуйста, введите да или нет.");
                    }
                }
            } else if (category.isEmpty()) {
                System.out.println("Категория не может быть пустой!");
            } else {
                break;
            }
            System.out.println("Введите категорию:");
        }
        return category;
    }

    public void updateTransaction() {
        Transaction transaction = chooseListOfTransactions();
        if (transaction != null) {
            System.out.println("""
                    Что хотите отредактировать:
                    1. Тип
                    2. Сумму
                    3. Категорию
                    4. Описание
                    5. В главное меню
                    """);

            mainLoop:
            while (true) {
                String input = scanner.nextLine();
                switch (input) {
                    case "1" -> {
                        Transaction.TransactionTYPE newType = getTransactionTypeInput();
                        if (newType == Transaction.TransactionTYPE.EXPENSE && user.getMonthlyBudget() != 0) {
                            double monthlyBudget = statsService.checkMonthlyBudgetLimit();
                            if (monthlyBudget < 0) {
                                notifyAboutMonthlyLimit(Math.abs(monthlyBudget));
                            }
                        }
                        System.out.println(service.updateTransactionType(newType, transaction));
                        break mainLoop;
                    }
                    case "2" -> {
                        System.out.println("Введите новую сумму:");
                        double newSum = getDoubleInput();
                        System.out.println(service.updateTransactionSum(newSum, transaction));
                        break mainLoop;
                    }
                    case "3" -> {
                        System.out.println("Введите новую категорию:");
                        String newCategory = scanner.nextLine();
                        System.out.println(service.updateTransactionCategory(newCategory, transaction));
                        break mainLoop;
                    }
                    case "4" -> {
                        System.out.println("Введите новое описание:");
                        String newDescription = scanner.nextLine();
                        if (newDescription.isEmpty()) {
                            newDescription = "-";
                        }
                        System.out.println(service.updateTransactionDescription(newDescription, transaction));
                        break mainLoop;
                    }
                    case "5" -> {
                        showSettings();
                        break mainLoop;
                    }
                    default -> System.out.println("Пожалуйста, введите 1, 2, 3, 4 или 5.");
                }
            }
        }
    }

    public void deleteTransaction() {
        Transaction transaction = chooseListOfTransactions();
        if (transaction != null) {
            mainLoop:
            while (true) {
                System.out.println("Вы действительно хотите удалить эту транзакцию? Это действие нельзя отменить! да/нет");
                String input = scanner.nextLine();
                switch (input.toLowerCase()) {
                    case "да" -> {
                        System.out.println(service.deleteTransaction(transaction));
                        break mainLoop;
                    }
                    case "нет" -> {
                        showSettings();
                        break mainLoop;
                    }
                    default -> System.out.println("Пожалуйста, введите да или нет.");
                }
            }
        }
    }

    public void manageMonthlyBudget() {
        double monthlyBudget = user.getMonthlyBudget();
        mainLoop:
        while (true) {
            if (monthlyBudget != 0) {
                System.out.println("Ваш месячный бюджет: " + String.format("%.2f", monthlyBudget) + " рублей.");
            } else {
                System.out.println("Вы пока не установили месячный бюджет. Сделайте это прямо сейчас!");
            }
            System.out.println("""
                    Выберите действие:
                    1. Изменить бюджет на месяц
                    2. Проверить остаток на месяц
                    3. В меню управления бюджетом
                    """);
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    updateMonthlyBudget();
                    break mainLoop;
                }
                case "2" -> {
                    checkMonthlyBudgetLimit();
                    break mainLoop;
                }
                case "3" -> {
                    showMainMenu();
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1, 2 или 3.");
            }
        }
    }

    public void updateMonthlyBudget() {
        while (true) {
            System.out.println("Введите новый месячный бюджет:");
            String input = scanner.nextLine();
            if (isDoublePos(input)) {
                double newBudget = Double.parseDouble(input);
                user.setMonthlyBudget(newBudget);
                System.out.println("Новый месячный бюджет " + String.format("%.2f", newBudget) + " рублей успешно установлен!");
                break;
            } else {
                System.out.println("Некорректный ввод! Введите положительное число.");
            }
        }
        manageMonthlyBudget();
    }

    public void checkMonthlyBudgetLimit() {
        double balance = statsService.checkMonthlyBudgetLimit();
        if (balance < 0) {
            System.out.println("Вы превысили лимит на месяц на " + String.format("%.2f", Math.abs(balance)) + " рублей!");
        } else if (balance == 0) {
            System.out.println("Ваш остаток: " + String.format("%.2f", balance) + ".");
        } else {
            System.out.println("Ваш остаток: " + String.format("%.2f", balance) + ". Продолжайте в том же духе!");
        }
        manageMonthlyBudget();
    }

    public void notifyAboutMonthlyLimit(double overgo) {
        System.out.println("Внимание! Вы превысили установленный месячный бюджет на " + String.format("%.2f", overgo) + " рублей.");
    }

    public void manageGoal() {
        double goal = user.getGoal();
        mainLoop:
        while (true) {
            if (goal != 0) {
                System.out.println("Ваша установленная цель: " + String.format("%.2f", goal) + " рублей.");
            } else {
                System.out.println("Вы пока не установили цель. Сделайте это прямо сейчас!");
            }
            System.out.println("""
                    Выберите действие:
                    1. Изменить цель
                    2. Проверить прогресс по цели
                    3. В меню управления целью
                    """);
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    updateGoal();
                    break mainLoop;
                }
                case "2" -> {
                    getGoalProgress();
                    break mainLoop;
                }
                case "3" -> {
                    showMainMenu();
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1, 2 или 3.");
            }
        }
    }

    public void updateGoal() {
        while (true) {
            System.out.println("Введите новую цель сбережений:");
            String input = scanner.nextLine();
            if (isDoublePos(input)) {
                double newGoal = Double.parseDouble(input);
                user.setGoal(newGoal);
                System.out.println("Новая цель " + String.format("%.2f", newGoal) + " рублей успешно установлена! Управляйте сбережениями по цели, добавляя транзакции в категорию \"Цель\".");
                break;
            } else {
                System.out.println("Некорректный ввод! Введите положительное число.");
            }
        }
        manageMonthlyBudget();
    }

    public void getGoalProgress() {
        double leftToGoal = statsService.checkGoalProgress();
        if (leftToGoal < 0) {
            System.out.println("Поздравляем! Вы превысили цель на " + String.format("%.2f", Math.abs(leftToGoal)) + " рублей! Может, пора установить новую? ;)");
        } else if (leftToGoal == 0) {
            System.out.println("Поздравляем! Вы достигли своей цели! Может, пора установить новую? ;)");
        } else {
            System.out.println("До цели осталось накопить " + String.format("%.2f", leftToGoal) + ". Отличный результат!");
        }
        manageGoal();
    }

    public void doAnalysis() {
        mainLoop:
        while (true) {
            System.out.println("""
                    Выберите действие:
                    1. Вывести текущий баланс с учетом всех транзакций
                    2. Рассчитать суммарный доход и расход за период
                    3. Проанализировать общие расходы по категориям
                    4. Сформировать отчет по финансовому состоянию
                    5. Назад
                    """);
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    showBalance();
                    break mainLoop;
                }
                case "2" -> {
                    getSummary();
                    break mainLoop;
                }
                case "3" -> {
                    showCategoryAnalysis();
                    break mainLoop;
                }
                case "4" -> {
                    showGeneralReport();
                    break mainLoop;
                }
                case "5" -> {
                    showMainMenu();
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1, 2, 3, 4 или 5.");
            }
        }
    }

    /**
     * Выводит анализ транзакций по категориям в виде таблицы.
     */
    public void showCategoryAnalysis() {
        Map<String, Double> categoryAnalysis = statsService.analyzeExpenseByCategories();

        if (categoryAnalysis.isEmpty()) {
            System.out.println("Нет транзакций для анализа!");
            return;
        }

        System.out.println("Анализ расходов по категориям:");
        System.out.printf("%-20s %-15s%n", "Категория", "Расходы");
        System.out.println("-".repeat(35));

        for (Map.Entry<String, Double> entry : categoryAnalysis.entrySet()) {
            String category = entry.getKey();
            double expense = Math.abs(entry.getValue());
            System.out.printf("%-20s %15.2f%n", category, expense);
        }
    }

    public void showGeneralReport() {
        LocalDateTime startTime, endTime;
        System.out.println("Введите дату начала периода (dd.MM.yyyy HH:mm). Оставьте поле пустым, если не хотите задавать нижнюю границу: ");
        startTime = periodInput();
        System.out.println("Введите дату конца периода (dd.MM.yyyy HH:mm). Оставьте поле пустым, если не хотите задавать верхнюю границу: ");
        endTime = periodInput();
        printGeneralReport(startTime, endTime);
    }

    /**
     * Выводит финансовый отчёт за указанный период.
     *
     * @param startTimestamp начальная дата периода
     * @param endTimestamp   конечная дата периода
     */
    public void printGeneralReport(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
        TransactionStatsService.FinancialReport report = statsService.generateGeneralReport(startTimestamp, endTimestamp);

        if (report == null) {
            System.out.println("Транзакции за период не найдены!");
            return;
        }

        System.out.println("=ФИНАНСОВЫЙ ОТЧЁТ=");
        System.out.println("Период: " +
                (startTimestamp != null ? startTimestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "начало") +
                " - " +
                (endTimestamp != null ? endTimestamp.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) : "конец")
        );

        System.out.println("Общий доход: " + String.format("%.2f", report.totalIncome()) + " руб.");
        System.out.println("Общий расход: " + String.format("%.2f", report.totalExpense()) + " руб.");
        System.out.println("Итоговый баланс: " + String.format("%.2f", report.totalBalance()) + " руб.");

        System.out.println("\nСтатистика по категориям:");
        System.out.printf("%-20s %-15s %-15s %-15s%n", "Категория", "Доход, руб.", "Расход, руб.", "Баланс, руб.");
        System.out.println("-".repeat(65));
        for (Map.Entry<String, double[]> entry : report.categoryReport().entrySet()) {
            String category = entry.getKey();
            double[] stats = entry.getValue();
            System.out.printf("%-20s %-15.2f %-15.2f %-15.2f%n",
                    category,
                    stats[0],
                    stats[1],
                    stats[2]
            );
        }

        System.out.println("\nСтатистика по финансовой цели:");
        double[] goalData = report.goalData();
        if (goalData == null) {
            System.out.println("Цель не поставлена!");
        } else {
            System.out.printf("%-25s %-15.2f%n", "Цель:", goalData[0]);
            System.out.printf("%-25s %-15.2f%n", "Доход по цели:", goalData[1]);
            System.out.printf("%-25s %-15.2f%n", "Расход по цели:", goalData[2]);
            System.out.printf("%-25s %-15.2f%n", "Накоплено:", goalData[3]);
            System.out.printf("%-25s %-15.2f%n", "Осталось накопить:", goalData[4]);
        }
    }

    /**
     * Выводит текущий баланс пользователя.
     */
    public void showBalance() {
        double balance = statsService.calculateBalance();
        System.out.print("Ваш баланс: ");
        System.out.printf("%15.2f%n", balance);
        System.out.println(" рублей.");
    }

    public void getSummary() {
        LocalDateTime timestamp1, timestamp2;
        System.out.println("Введите начало периода");
        timestamp1 = getTimestampInput();
        System.out.println("Введите конец периода");
        timestamp2 = getTimestampInput();
        if (timestamp1.isAfter(timestamp2)) {
            System.out.println("Начало периода не должно быть позже конца периода. Изменен порядок временных рамок.");
            LocalDateTime aux = timestamp1;
            timestamp1 = timestamp2;
            timestamp2 = aux;
        }
        double[] stats = statsService.getIncomeExpenseForPeriod(timestamp1, timestamp2);
        double totalIncome = stats[0];
        double totalExpense = stats[1];
        double balance = stats[2];
        System.out.println("Период: " + timestamp1 + "-" + timestamp2);
        System.out.print("Доходы: ");
        System.out.printf("%15.2f%n", totalIncome);
        System.out.println(" рублей;");
        System.out.print("Расходы: ");
        System.out.printf("%15.2f%n", totalExpense);
        System.out.println(" рублей;");
        System.out.print("Итоговый баланс: ");
        System.out.printf("%15.2f%n", balance);
        System.out.println(" рублей.");
    }

    private void showTransactionList(List<Transaction> transactions) {
        if (transactions.isEmpty()) {
            System.out.println("Транзакции не найдены!");
            return;
        }
        for (int i = 0; i < transactions.size(); i++) {
            Transaction trans = transactions.get(i);
            double sum = trans.getSum();
            if (trans.getType() == Transaction.TransactionTYPE.EXPENSE) {
                sum = 0 - sum;
            }
            System.out.printf("%d. %.2f, %s, %s, %s%n",
                    i + 1,
                    sum,
                    trans.getCategory(),
                    trans.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    trans.getDescription()
            );
        }
    }

    private Transaction.TransactionTYPE getTransactionTypeInput() {
        Transaction.TransactionTYPE type;
        mainLoop:
        while (true) {
            String input = scanner.nextLine().toLowerCase().trim();
            switch (input) {
                case "доход" -> {
                    type = Transaction.TransactionTYPE.INCOME;
                    break mainLoop;
                }
                case "расход" -> {
                    type = Transaction.TransactionTYPE.EXPENSE;
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите \"доход\" или \"расход\":");
            }
        }
        return type;
    }

    private double getDoubleInput() {
        while (true) {
            String input = scanner.nextLine();
            if (isDoublePos(input)) {
                return Double.parseDouble(input);
            } else {
                System.out.println("Пожалуйста, введите корректное число:");
            }
        }
    }

    private LocalDateTime getTimestampInput() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        while (true) {
            try {
                System.out.println("Введите дату (dd.MM.yyyy HH:mm):");
                String input = scanner.nextLine();
                return LocalDateTime.parse(input, formatter);
            } catch (Exception e) {
                System.out.println("Неверный формат! Используйте dd.MM.yyyy HH:mm");
            }
        }
    }

    private LocalDateTime periodInput() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String startInput = scanner.nextLine();
        if (!startInput.isEmpty()) {
            while (true) {
                try {
                    System.out.println("Введите дату (dd.MM.yyyy HH:mm):");
                    return LocalDateTime.parse(startInput, formatter);
                } catch (Exception e) {
                    System.out.println("Неверный формат! Используйте dd.MM.yyyy HH:mm");
                    startInput = scanner.nextLine();
                }
            }
        } else {
            return null;
        }
    }

    private Transaction chooseListOfTransactions() {
        List<Transaction> transactionList = service.getAllTransactions();
        showTransactionList(transactionList);

        if (transactionList.isEmpty()) {
            return null;
        }

        Transaction transaction = null;

        while (transaction == null) {
            System.out.println("Выберите номер транзакции из списка выше:");
            try {
                int transactionId = Integer.parseInt(scanner.nextLine());
                if (transactionId >= 1 && transactionId <= transactionList.size()) {
                    transaction = transactionList.get(transactionId - 1);
                } else {
                    System.out.println("Некорректный ввод! Пожалуйста, введите число от 1 до " + transactionList.size() + "!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число от 1 до " + transactionList.size() + "!");
            }
        }
        return transaction;
    }

    private boolean isDoublePos(String str) {
        try {
            double parsedDouble = Double.parseDouble(str);
            return parsedDouble > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}