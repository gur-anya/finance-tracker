package org.ylabHomework.controllers;

import org.ylabHomework.models.Transaction;
import org.ylabHomework.serviceClasses.Constants;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final Scanner scanner = new Scanner(System.in);
    private final TransactionService service;
    private final TransactionStatsService statsService;
    private final UserController userController;
    private final Map<String, Command> mainMenuCommands = new HashMap<>();
    private final Map<String, Command> managementCommands = new HashMap<>();
    private final Map<String, Command> statsCommands = new HashMap<>();
    private final Map<String, Command> filterCommands = new HashMap<>();
    private final Map<String, Command> updateCommands = new HashMap<>();
    private final Map<String, Command> deleteCommands = new HashMap<>();
    private final Map<String, Command> budgetCommands = new HashMap<>();
    private final Map<String, Command> goalCommands = new HashMap<>();

    /**
     * Интерфейс паттерна Команда.
     */
    private interface Command {
        void execute();
    }

    /**
     * Конструктор для создания контроллера с заданными сервисами и пользователем.
     *
     * @param service    сервис для работы с транзакциями
     * @param statsService сервис для статистики транзакций
     * @param controller контроллер пользователя
     */
    public TransactionController(TransactionService service, TransactionStatsService statsService, UserController controller) {
        this.service = service;
        this.statsService = statsService;
        this.userController = controller;
        initializeCommands();
    }

    /**
     * Инициализация всех команд для меню.
     */
    private void initializeCommands() {
        mainMenuCommands.put("1", this::showTransactionManagement);
        mainMenuCommands.put("2", this::showStatsAndAnalysisMenu);
        mainMenuCommands.put("3", userController::showMainPageUser);
        mainMenuCommands.put("4", new ExitCommand());

        managementCommands.put("1", this::showTransactionsFilter);
        managementCommands.put("2", this::createTransaction);
        managementCommands.put("3", this::updateTransaction);
        managementCommands.put("4", this::deleteTransaction);
        managementCommands.put("5", this::showMainMenu);

        statsCommands.put("1", this::manageMonthlyBudget);
        statsCommands.put("2", this::manageGoal);
        statsCommands.put("3", this::showBalance);
        statsCommands.put("4", this::getSummary);
        statsCommands.put("5", this::showCategoryAnalysis);
        statsCommands.put("6", this::showGeneralReport);
        statsCommands.put("7", this::showMainMenu);

        filterCommands.put("1", () -> {
            showTransactionList(getTransactionsBeforeTimestamp());
            showTransactionManagement();
        });
        filterCommands.put("2", () -> {
            showTransactionList(getTransactionsAfterTimestamp());
            showTransactionManagement();
        });
        filterCommands.put("3", () -> {
            showTransactionList(getTransactionsByCategory());
            showTransactionManagement();
        });
        filterCommands.put("4", () -> {
            showTransactionList(service.getTransactionsByType(getTransactionTypeInput()));
            showTransactionManagement();
        });
        filterCommands.put("5", () -> {
            showTransactionList(service.getAllTransactions());
            showTransactionManagement();
        });
        filterCommands.put("6", this::showTransactionManagement);

        updateCommands.put("1", new UpdateTypeCommand());
        updateCommands.put("2", new UpdateSumCommand());
        updateCommands.put("3", new UpdateCategoryCommand());
        updateCommands.put("4", new UpdateDescriptionCommand());
        updateCommands.put("5", this::showTransactionManagement);

        deleteCommands.put("да", new ConfirmDeleteCommand());
        deleteCommands.put("нет", this::showTransactionManagement);

        budgetCommands.put("1", this::updateMonthlyBudget);
        budgetCommands.put("2", () -> {
            System.out.println(service.checkMonthlyBudgetLimit());
            manageMonthlyBudget();
        });
        budgetCommands.put("3", this::showStatsAndAnalysisMenu);

        goalCommands.put("1", this::updateGoal);
        goalCommands.put("2", () -> {
            System.out.println(statsService.getGoalProgress());
            manageGoal();
        });
        goalCommands.put("3", this::showStatsAndAnalysisMenu);
    }

    /**
     * Отображает главное меню управления транзакциями и обрабатывает выбор пользователя.
     */
    public void showMainMenu() {
        executeMenu(Constants.TRANSACTIONS_MAIN_PAGE, mainMenuCommands, "Пожалуйста, введите 1, 2, 3 или 4.");
    }

    /**
     * Отображает меню настроек для управления транзакциями.
     */
    public void showTransactionManagement() {
        executeMenu(Constants.TRANSACTIONS_MANAGEMENT_MENU, managementCommands, "Пожалуйста, введите 1, 2, 3, 4 или 5.");
    }

    /**
     * Отображает меню статистики и анализа финансов.
     */
    public void showStatsAndAnalysisMenu() {
        executeMenu(Constants.STATS_AND_ANALYSIS_MENU, statsCommands, "Пожалуйста, введите 1, 2, 3, 4, 5, 6 или 7.");
    }

    /**
     * Отображает меню фильтрации транзакций и выполняет выбранный фильтр.
     */
    public void showTransactionsFilter() {
        executeMenu(Constants.FILTER_CHOOSING_MENU, filterCommands, "Пожалуйста, введите 1, 2, 3, 4, 5 или 6.");
    }

    /**
     * Создаёт новую транзакцию на основе введённых пользователем данных.
     */
    public void createTransaction() {
        int type = getTransactionTypeInput();
        String sum = getValidInput("Введите сумму:", service::checkSum);
        String category = getValidInput("Введите категорию:", service::checkCategory);
        System.out.println("Введите описание (опционально):");
        String description = scanner.nextLine();

        TransactionService.ParseResponseDTO result = service.createTransaction(type, sum, category, description);
        System.out.println(result.content);
        showTransactionManagement();
    }

    /**
     * Обновляет существующую транзакцию на основе выбора пользователя.
     */
    public void updateTransaction() {
        Transaction transaction = chooseListOfTransactions();
        if (transaction != null) {
            executeTransactionMenu(Constants.UPDATE_TRANSACTIONS_MENU, updateCommands, transaction, "Пожалуйста, введите 1, 2, 3, 4 или 5.");
        }
    }

    /**
     * Удаляет существующую транзакцию по выбору пользователя.
     */
    public void deleteTransaction() {
        Transaction transaction = chooseListOfTransactions();
        if (transaction != null) {
            executeTransactionMenu("Вы действительно хотите удалить эту транзакцию? да/нет", deleteCommands, transaction, "Пожалуйста, введите да или нет.");
        }
    }

    /**
     * Управляет месячным бюджетом пользователя.
     */
    public void manageMonthlyBudget() {
        System.out.println(service.getMonthlyBudgetInfo());
        System.out.println(Constants.MONTHLY_BUDGET_MENU);
        executeMenu("", budgetCommands, "Пожалуйста, введите 1, 2 или 3.");
    }

    /**
     * Обновляет месячный бюджет пользователя.
     */
    public void updateMonthlyBudget() {
        String newBudget = getValidInput("Введите новый месячный бюджет:", service::setMonthlyBudget);
        System.out.println(service.setMonthlyBudget(newBudget).content);
        manageMonthlyBudget();
    }

    /**
     * Управляет финансовой целью пользователя.
     */
    public void manageGoal() {
        System.out.println(service.getGoalInfo());
        System.out.println(Constants.GOAL_MENU);
        executeMenu("", goalCommands, "Пожалуйста, введите 1, 2 или 3.");
    }

    /**
     * Обновляет финансовую цель пользователя.
     */
    public void updateGoal() {
        String newGoal = getValidInput("Введите новую цель сбережений:", service::setMonthlyBudget);
        System.out.println(service.setGoal(newGoal));
        manageGoal();
    }

    /**
     * Выводит текущий баланс пользователя.
     */
    public void showBalance() {
        System.out.println(statsService.calculateBalance());
        showStatsAndAnalysisMenu();
    }

    /**
     * Выводит суммарный доход, расход и баланс за указанный период.
     */
    public void getSummary() {
        LocalDateTime timestamp1 = getValidTimestamp("Введите начало периода в формате dd.MM.yyyy HH:mm:");
        LocalDateTime timestamp2 = getValidTimestamp("Введите конец периода в формате dd.MM.yyyy HH:mm:");
        if (timestamp1.isAfter(timestamp2)) {
            LocalDateTime aux = timestamp1;
            timestamp1 = timestamp2;
            timestamp2 = aux;
        }
        System.out.println(statsService.getSummary(timestamp1, timestamp2));
        showStatsAndAnalysisMenu();
    }

    /**
     * Выводит анализ транзакций по категориям.
     */
    public void showCategoryAnalysis() {
        System.out.println(statsService.getCategoryAnalysis());
        showStatsAndAnalysisMenu();
    }

    /**
     * Отображает меню для формирования финансового отчёта.
     */
    public void showGeneralReport() {
        System.out.println("Введите дату начала периода (dd.MM.yyyy HH:mm, пусто для начала):");
        LocalDateTime startTime = periodInput();
        System.out.println("Введите дату конца периода (dd.MM.yyyy HH:mm, пусто для конца):");
        LocalDateTime endTime = periodInput();
        System.out.println(statsService.generateGeneralReportFormatted(startTime, endTime));
        showStatsAndAnalysisMenu();
    }


    private interface TransactionCommand extends Command {
        void setTransaction(Transaction transaction);
    }

    private class UpdateTypeCommand implements TransactionCommand {
        private Transaction transaction;

        @Override
        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        @Override
        public void execute() {
            int newType = getTransactionTypeInput();
            System.out.println(service.updateTransactionType(newType, transaction).content);
        }
    }

    private class UpdateSumCommand implements TransactionCommand {
        private Transaction transaction;

        @Override
        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        @Override
        public void execute() {
            String sum = getValidInput("Введите сумму:", service::checkSum);
            System.out.println(service.updateTransactionSum(sum, transaction).content);
        }
    }

    private class UpdateCategoryCommand implements TransactionCommand {
        private Transaction transaction;

        @Override
        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        @Override
        public void execute() {
            String category = getValidInput("Введите новую категорию:", service::checkCategory);
            System.out.println(service.updateTransactionCategory(category, transaction).content);
        }
    }

    private class UpdateDescriptionCommand implements TransactionCommand {
        private Transaction transaction;

        @Override
        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        @Override
        public void execute() {
            System.out.println("Введите новое описание:");
            String newDescription = scanner.nextLine();
            System.out.println(service.updateTransactionDescription(newDescription, transaction).content);
        }
    }

    private class ConfirmDeleteCommand implements TransactionCommand {
        private Transaction transaction;

        @Override
        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        @Override
        public void execute() {
            System.out.println(service.deleteTransaction(transaction).content);
            showTransactionManagement();
        }
    }

    /**
     * Команда для завершения программы.
     */
    private static class ExitCommand implements Command {
        @Override
        public void execute() {
            System.exit(0);
        }
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
            if (trans.getType() == 2) sum = -sum;
            System.out.printf("%d. %.2f руб., %s, %s, %s%n",
                    i + 1, sum, trans.getCategory(), trans.getTimestamp().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")), trans.getDescription());
        }
    }

    /**
     * Запрашивает у пользователя тип транзакции.
     *
     * @return выбранный тип транзакции (INCOME или EXPENSE)
     */
    private int getTransactionTypeInput() {
        while (true) {
            System.out.println("Выберите тип транзакции (1 - доход, 2 - расход):");
            String input = scanner.nextLine().toLowerCase().trim();
            switch (input) {
                case "1": return 1;
                case "2": return 2;
                default: System.out.println("Пожалуйста, введите 1 или 2:");
            }
        }
    }

    /**
     * Запрашивает дату и время для периода отчета.
     *
     * @return введённое время или null, если ввод пустой
     */
    private LocalDateTime periodInput() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        String input = scanner.nextLine();
        if (input.isEmpty()) return null;
        try {
            return LocalDateTime.parse(input, formatter);
        } catch (Exception e) {
            System.out.println("Неверный формат! Используйте dd.MM.yyyy HH:mm");
            return periodInput();
        }
    }

    /**
     * Позволяет пользователю выбрать транзакцию из списка.
     *
     * @return выбранная транзакция или null, если список пуст
     */
    private Transaction chooseListOfTransactions() {
        List<Transaction> transactionList = service.getAllTransactions();
        showTransactionList(transactionList);
        if (transactionList.isEmpty()) return null;
        while (true) {
            System.out.println("Выберите номер транзакции:");
            try {
                int id = Integer.parseInt(scanner.nextLine());
                if (id >= 1 && id <= transactionList.size()) return transactionList.get(id - 1);
                System.out.println("Введите число от 1 до " + transactionList.size() + "!");
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число!");
            }
        }
    }

    /**
     * Выполняет цикл меню с заданным текстом и командами.
     *
     * @param menuText текст меню для отображения
     * @param commands команды для выполнения
     * @param errorMessage сообщение об ошибке при неверном вводе
     */
    private void executeMenu(String menuText, Map<String, Command> commands, String errorMessage) {
        while (true) {
            System.out.println(menuText);
            String input = scanner.nextLine();
            Command command = commands.get(input);
            if (command != null) {
                command.execute();
                break;
            } else {
                System.out.println(errorMessage);
            }
        }
    }

    /**
     * Выполняет цикл меню с передачей транзакции в команды.
     *
     * @param menuText текст меню для отображения
     * @param commands команды для выполнения
     * @param transaction транзакция для передачи в команды
     * @param errorMessage сообщение об ошибке при неверном вводе
     */
    private void executeTransactionMenu(String menuText, Map<String, Command> commands, Transaction transaction, String errorMessage) {
        while (true) {
            System.out.println(menuText);
            String input = scanner.nextLine().toLowerCase();
            Command command = commands.get(input);
            if (command != null) {
                if (command instanceof TransactionCommand) {
                    ((TransactionCommand) command).setTransaction(transaction);
                }
                command.execute();
                if (input.equals("5") || input.equals("нет")) break;
            } else {
                System.out.println(errorMessage);
            }
        }
    }

    /**
     * Запрашивает у пользователя корректный ввод с валидацией.
     *
     * @param prompt текст запроса ввода
     * @param validator функция валидации ввода
     * @return корректный введённый пользователем текст
     */
    private String getValidInput(String prompt, java.util.function.Function<String, TransactionService.ParseResponseDTO> validator) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine();
            TransactionService.ParseResponseDTO response = validator.apply(input);
            if (response.success) return input;
            System.out.println(response.content);
        }
    }

    /**
     * Запрашивает у пользователя корректную временную метку.
     *
     * @param prompt текст запроса ввода
     * @return корректная временная метка
     */
    private LocalDateTime getValidTimestamp(String prompt) {
        while (true) {
            System.out.println(prompt);
            String input = scanner.nextLine();
            TransactionService.ParseTimeResponseDTO response = service.checkTimestampInput(input);
            if (response.success) return response.time;
            System.out.println(response.content);
        }
    }

    /**
     * Получает список транзакций до указанной даты.
     *
     * @return список транзакций
     */
    private List<Transaction> getTransactionsBeforeTimestamp() {
        return service.getTransactionsBeforeTimestamp(getValidTimestamp("Введите дату в формате dd.MM.yyyy HH:mm:"));
    }

    /**
     * Получает список транзакций после указанной даты.
     *
     * @return список транзакций
     */
    private List<Transaction> getTransactionsAfterTimestamp() {
        return service.getTransactionsAfterTimestamp(getValidTimestamp("Введите дату в формате dd.MM.yyyy HH:mm:"));
    }

    /**
     * Получает список транзакций по заданной категории.
     *
     * @return список транзакций
     */
    private List<Transaction> getTransactionsByCategory() {
        return service.getTransactionsByCategory(getValidInput("Введите категорию:", service::checkCategory));
    }
}