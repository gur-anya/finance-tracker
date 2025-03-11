package org.ylabHomework.controllers;

import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
/**
 * Контроллер для управления транзакциями пользователя через консольный интерфейс.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 09.03.2025
 * </p>
 */
public class TransactionController {

    /**
     * Конструктор для создания контроллера с заданными сервисами и пользователем.
     *
     * @param service    сервис для работы с транзакциями
     * @param controller контроллер пользователя
     * @param user       пользователь, с транзакциями которого ведётся работа
     */
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

    /**
     * Отображает главное меню управления транзакциями и обрабатывает выбор пользователя.
     * <p>Позволяет перейти к управлению транзакциями, статистике, вернуться в меню пользователя или выйти из программы.</p>
     */
    public void showMainMenu() {
        mainLoop:
        while (true) {
            System.out.println(Constants.TRANSACTIONS_MAIN_PAGE);
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> {
                    showTransactionManagement();
                    break mainLoop;
                }
                case "2" -> {
                    showStatsAndAnalysisMenu();
                    break mainLoop;
                }
                case "3" -> {
                    userController.showMainPageUser();
                    break mainLoop;
                }
                case "4" -> {
                    exitApp();
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1, 2, 3 или 4.");
            }
        }
    }

    /**
     * Отображает меню настроек для управления транзакциями.
     * <p>Предоставляет пользователю возможность просмотра, добавления, редактирования и удаления транзакций.</p>
     */
    public void showTransactionManagement() {
        mainLoop:
        while (true) {
            System.out.println(Constants.TRANSACTIONS_MANAGEMENT_MENU);
            String input = scanner.nextLine();
            switch (input) {
                case "1" -> {
                    showTransactionsFilter();
                    break mainLoop;
                }
                case "2" -> {
                    createTransaction();
                    break mainLoop;
                }
                case "3" -> {
                    updateTransaction();
                    break mainLoop;
                }
                case "4" -> {
                    deleteTransaction();
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
     * Отображает меню статистики и анализа финансов.
     * <p>Позволяет управлять бюджетом, целями, просматривать баланс, сводку за период, анализ по категориям и общий отчет.</p>
     */
    public void showStatsAndAnalysisMenu() {
        mainLoop:
        while (true) {
            System.out.println(Constants.STATS_AND_ANALYSIS_MENU);
            String input = scanner.nextLine();
            switch (input) {
                case "1" -> {
                    manageMonthlyBudget();
                    break mainLoop;
                }
                case "2" -> {
                    manageGoal();
                    break mainLoop;
                }
                case "3" -> {
                    showBalance();
                    break mainLoop;
                }
                case "4" -> {
                    getSummary();
                    break mainLoop;
                }
                case "5" -> {
                    showCategoryAnalysis();
                    break mainLoop;
                }
                case "6" -> {
                    showGeneralReport();
                    break mainLoop;
                }
                case "7" -> {
                    showMainMenu();
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1, 2, 3, 4, 5, 6 или 7.");
            }
        }
    }

    /**
     * Отображает меню фильтрации транзакций и выполняет выбранный фильтр.
     * <p>Предоставляет фильтры по дате, категории, типу транзакции или вывод всех транзакций.</p>
     */
    public void showTransactionsFilter() {
        mainLoop:
        while (true) {
            System.out.println(Constants.FILTER_CHOOSING_MENU);
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
                    showTransactionManagement();
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1, 2, 3, 4, 5 или 6.");
            }
        }
    }

    /**
     * Создаёт новую транзакцию на основе введённых пользователем данных.
     * <p>Запрашивает тип, сумму, категорию и описание, затем сохраняет транзакцию через сервис.</p>
     */
    public void createTransaction() {
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
        if (type == Transaction.TransactionTYPE.EXPENSE && service.getMonthlyBudget() != 0) {
            checkExpenseLimitReminder();
        }
        showTransactionManagement();
    }

    /**
     * Получает ввод категории от пользователя с проверкой на корректность.
     *
     * @return введённая категория
     */
    public String getCategoryInput() {
        String category;
        while (true) {
            category = scanner.nextLine();
            if (Objects.equals(category.toLowerCase().trim(), "цель") && service.getGoal() == 0) {
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

    /**
     * Обновляет существующую транзакцию на основе выбора пользователя.
     * <p>Позволяет изменить тип, сумму, категорию или описание выбранной транзакции.</p>
     */
    public void updateTransaction() {
        Transaction transaction = chooseListOfTransactions();
        if (transaction != null) {
            mainLoop:
            while (true) {
                System.out.println(Constants.UPDATE_TRANSACTIONS_MENU);
                String input = scanner.nextLine();
                switch (input) {
                    case "1" -> {
                        Transaction.TransactionTYPE newType = getTransactionTypeInput();
                        if (newType == Transaction.TransactionTYPE.EXPENSE && service.getMonthlyBudget() != 0) {
                           checkMonthlyBudgetLimit();
                        }
                        System.out.println(service.updateTransactionType(newType, transaction));
                    }
                    case "2" -> {
                        System.out.println("Введите новую сумму:");
                        double newSum = getDoubleInput();
                        System.out.println(service.updateTransactionSum(newSum, transaction));
                    }
                    case "3" -> {
                        System.out.println("Введите новую категорию:");
                        String newCategory = scanner.nextLine();
                        System.out.println(service.updateTransactionCategory(newCategory, transaction));
                    }
                    case "4" -> {
                        System.out.println("Введите новое описание:");
                        String newDescription = scanner.nextLine();
                        if (newDescription.isEmpty()) {
                            newDescription = "-";
                        }
                        System.out.println(service.updateTransactionDescription(newDescription, transaction));
                    }
                    case "5" -> {
                        showTransactionManagement();
                        break mainLoop;
                    }
                    default -> System.out.println("Пожалуйста, введите 1, 2, 3, 4 или 5.");
                }
            }
        }
    }

    /**
     * Удаляет существующую транзакцию по выбору пользователя.
     * <p>Запрашивает подтверждение перед удалением выбранной транзакции.</p>
     */
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
                        showTransactionManagement();
                        break mainLoop;
                    }
                    case "нет" -> {
                        showTransactionManagement();
                        break mainLoop;
                    }
                    default -> System.out.println("Пожалуйста, введите да или нет.");
                }
            }
        }
    }

    /**
     * Управляет месячным бюджетом пользователя.
     * <p>Позволяет изменить бюджет или проверить остаток.</p>
     */
    public void manageMonthlyBudget() {
        double monthlyBudget = service.getMonthlyBudget();
        mainLoop:
        while (true) {
            if (monthlyBudget != 0) {
                System.out.println("Ваш месячный бюджет: " + String.format("%.2f", monthlyBudget) + " руб.");
            } else {
                System.out.println("Вы пока не установили месячный бюджет. Сделайте это прямо сейчас!");
            }
            System.out.println(Constants.MONTHLY_BUDGET_MENU);
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
                    showStatsAndAnalysisMenu();
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1, 2 или 3.");
            }
        }
    }

    /**
     * Обновляет месячный бюджет пользователя.
     * <p>Запрашивает новый бюджет и сохраняет его для пользователя.</p>
     */
    public void updateMonthlyBudget() {
        while (true) {
            System.out.println("Введите новый месячный бюджет:");
            String input = scanner.nextLine();
            if (isDoublePos(input)) {
                double newBudget = Double.parseDouble(input);
                service.setMonthlyBudget(newBudget);
                System.out.println("Новый месячный бюджет " + String.format("%.2f", newBudget) + " руб. успешно установлен!");
                break;
            } else {
                System.out.println("Некорректный ввод! Введите положительное число.");
            }
        }
        manageMonthlyBudget();
    }

    /**
     * Проверяет остаток месячного бюджета.
     * <p>Выводит информацию об остатке или превышении бюджета.</p>
     */
    public void checkMonthlyBudgetLimit() {
        double balance = statsService.checkMonthlyBudgetLimit();
        if (balance < 0) {
            System.out.println("Вы превысили лимит на месяц на " + String.format("%.2f", Math.abs(balance)) + " руб.!");
        } else if (balance == 0) {
            System.out.println("Ваш остаток: " + String.format("%.2f", balance) + " руб.");
        } else {
            System.out.println("Ваш остаток: " + String.format("%.2f", balance) + " руб. Продолжайте в том же духе!");
        }
        manageMonthlyBudget();
    }

    /**
     * Уведомляет пользователя о превышении месячного бюджета.
     *
     * @param overgo сумма превышения
     */
    public void notifyAboutMonthlyLimit(double overgo) {
        String message = "Внимание! Вы превысили установленный месячный бюджет на " + String.format("%.2f", overgo) + " руб." + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        sendEmailNotification(message);
    }

    /**
     * Проверяет, достиг ли пользователь лимита расходов, и отправляет напоминание.
     */
    public void checkExpenseLimitReminder() {
        if (service.getMonthlyBudget() == 0) {
            return;
        }
        double remainingBudget = statsService.checkMonthlyBudgetLimit();
        if (remainingBudget <= 0) {
            notifyAboutMonthlyLimit(Math.abs(remainingBudget));
        } else if (remainingBudget <= user.getMonthlyBudget() * 0.1) {
            System.out.println("Осторожно! Остаток бюджета составляет " + String.format("%.2f", remainingBudget) +
                    " руб. (менее 10% от лимита).");
        }
    }

    /**
     * Имитирует отправку email-уведомления пользователю через консоль.
     *
     * @param message сообщение для отправки
     */
    public void sendEmailNotification(String message) {
        System.out.println("Отправлено письмо на почту " + user.getEmail() + " с содержанием: " + message);
    }

    /**
     * Управляет финансовой целью пользователя.
     * <p>Позволяет изменить цель или проверить прогресс по ней.</p>
     */
    public void manageGoal() {
        double goal = service.getGoal();
        mainLoop:
        while (true) {
            if (goal != 0) {
                System.out.println("Ваша установленная цель: " + String.format("%.2f", goal) + " руб.");
            } else {
                System.out.println("Вы пока не установили цель. Сделайте это прямо сейчас!");
            }
            System.out.println(Constants.GOAL_MENU);
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
                    showStatsAndAnalysisMenu();
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1, 2 или 3.");
            }
        }
    }

    /**
     * Обновляет финансовую цель пользователя.
     * <p>Запрашивает новую цель и сохраняет её для пользователя.</p>
     */
    public void updateGoal() {
        while (true) {
            System.out.println("Введите новую цель сбережений:");
            String input = scanner.nextLine();
            if (isDoublePos(input)) {
                double newGoal = Double.parseDouble(input);
                service.setGoal(newGoal);
                System.out.println("Новая цель " + String.format("%.2f", newGoal) + " руб. успешно установлена! Управляйте сбережениями по цели, добавляя транзакции в категорию \"Цель\".");
                break;
            } else {
                System.out.println("Некорректный ввод! Введите положительное число.");
            }
        }
        manageGoal();
    }

    /**
     * Проверяет прогресс по финансовой цели пользователя.
     * <p>Выводит информацию о накоплениях относительно установленной цели.</p>
     */
    public void getGoalProgress() {
        double leftToGoal = statsService.checkGoalProgress();
        if (leftToGoal < 0) {
            System.out.println("Поздравляем! Вы превысили цель на " + String.format("%.2f", Math.abs(leftToGoal)) + " руб.! Может, пора установить новую? ;)");
        } else if (leftToGoal == 0) {
            System.out.println("Поздравляем! Вы достигли своей цели! Может, пора установить новую? ;)");
        } else {
            System.out.println("До цели осталось накопить " + String.format("%.2f", leftToGoal) + " руб. Отличный результат!");
        }
        manageGoal();
    }

    /**
     * Выводит анализ транзакций по категориям в виде таблицы.
     * <p>Показывает суммарные расходы по каждой категории.</p>
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
            String category = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
            double expense = Math.abs(entry.getValue());
            System.out.printf("%-20s %15.2f руб.%n", category, expense);
        }

        showStatsAndAnalysisMenu();
    }

    /**
     * Отображает меню для формирования финансового отчёта.
     * <p>Запрашивает временные рамки и выводит детализированный отчет.</p>
     */
    public void showGeneralReport() {
        LocalDateTime startTime, endTime;
        System.out.println("Введите дату начала периода (dd.MM.yyyy HH:mm). Оставьте поле пустым, если не хотите задавать нижнюю границу: ");
        startTime = periodInput();
        System.out.println("Введите дату конца периода (dd.MM.yyyy HH:mm). Оставьте поле пустым, если не хотите задавать верхнюю границу: ");
        endTime = periodInput();

        if (startTime != null && endTime != null) {
            if (startTime.isAfter(endTime)) {
                System.out.println("Начало периода не должно быть позже конца периода. Изменен порядок временных рамок.");
                LocalDateTime aux = endTime;
                endTime = startTime;
                startTime = aux;
            }
        }

        printGeneralReport(startTime, endTime);
    }

    /**
     * Выводит финансовый отчёт за указанный период.
     *
     * @param startTimestamp начальная дата периода (может быть null)
     * @param endTimestamp   конечная дата периода (может быть null)
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
            String category = entry.getKey().substring(0, 1).toUpperCase() + entry.getKey().substring(1);
            double[] stats = entry.getValue();
            System.out.printf("%-20s %-15.2f %-15.2f  %-15.2f %n",
                    category,
                    stats[0],
                    stats[1],
                    stats[2]
            );
        }

        System.out.println("\nСтатистика по финансовой цели:");
        double[] goalData = report.goalData();
        if (goalData == null) {
            System.out.println("Цель на данном периоде не поставлена!");
        } else {
            System.out.printf("%-25s %-15.2f руб.%n", "Цель:", goalData[0]);
            System.out.printf("%-25s %-15.2f руб.%n", "Доход по цели:", goalData[1]);
            System.out.printf("%-25s %-15.2f руб.%n", "Расход по цели:", goalData[2]);
            System.out.printf("%-25s %-15.2f руб.%n", "Накоплено:", goalData[3]);
            System.out.printf("%-25s %-15.2f руб.%n", "Осталось накопить:", goalData[4]);
        }

        showStatsAndAnalysisMenu();
    }

    /**
     * Выводит текущий баланс пользователя.
     * <p>Рассчитывает и отображает итоговый баланс на основе всех транзакций.</p>
     */
    public void showBalance() {
        double balance = statsService.calculateBalance();
        System.out.print("Ваш баланс: ");
        System.out.printf("%15.2f руб.%n", balance);
        showStatsAndAnalysisMenu();
    }

    /**
     * Выводит суммарный доход, расход и баланс за указанный период.
     * <p>Запрашивает временные рамки и выводит сводку.</p>
     */
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
        System.out.println("Период: " + timestamp1.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + " - " + timestamp2.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
        System.out.print("Доходы: ");
        System.out.printf("%15.2f руб.%n", totalIncome);
        System.out.print("Расходы: ");
        System.out.printf("%15.2f руб.%n", totalExpense);
        System.out.print("Итоговый баланс: ");
        System.out.printf("%15.2f руб.%n", balance);

        showStatsAndAnalysisMenu();
    }

    /**
     * Выводит список транзакций в консоль.
     *
     * @param transactions список транзакций для отображения
     */
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
            System.out.printf("%d. %.2f руб., %s, %s, %s%n",
                    i + 1,
                    sum,
                    trans.getCategory(),
                    trans.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")),
                    trans.getDescription()
            );
        }
    }

    /**
     * Запрашивает у пользователя тип транзакции (доход или расход).
     *
     * @return выбранный тип транзакции
     */
    private Transaction.TransactionTYPE getTransactionTypeInput() {
        Transaction.TransactionTYPE type;
        System.out.println("Выберите тип транзакции (1 - доход, 2 - расход):");
        mainLoop:
        while (true) {
            String input = scanner.nextLine().toLowerCase().trim();
            switch (input) {
                case "1" -> {
                    type = Transaction.TransactionTYPE.INCOME;
                    break mainLoop;
                }
                case "2" -> {
                    type = Transaction.TransactionTYPE.EXPENSE;
                    break mainLoop;
                }
                default -> System.out.println("Пожалуйста, введите 1 или 2:");
            }
        }
        return type;
    }

    /**
     * Запрашивает у пользователя положительное число для суммы транзакции.
     *
     * @return введённая сумма
     */
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

    /**
     * Запрашивает у пользователя дату и время в формате dd.MM.yyyy HH:mm.
     *
     * @return введённая дата и время
     */
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

    /**
     * Запрашивает у пользователя дату и время для периода отчета, позволяя оставить поле пустым.
     *
     * @return введённая дата и время или null, если поле пустое
     */
    private LocalDateTime periodInput() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String startInput = scanner.nextLine();
        if (!startInput.isEmpty()) {
            while (true) {
                try {
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

    /**
     * Позволяет пользователю выбрать транзакцию из списка для редактирования или удаления.
     *
     * @return выбранная транзакция или null, если список пуст
     */
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

    /**
     * Проверяет, является ли строка положительным числом.
     *
     * @param str строка для проверки
     * @return true, если строка — положительное число, иначе false
     */
    private boolean isDoublePos(String str) {
        try {
            double parsedDouble = Double.parseDouble(str);
            return parsedDouble > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Завершает выполнение программы.
     */
    public void exitApp() {
        System.exit(0);
    }
}