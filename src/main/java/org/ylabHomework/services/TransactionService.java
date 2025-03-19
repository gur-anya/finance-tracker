package org.ylabHomework.services;

import lombok.Getter;
import lombok.Setter;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
     * @param user       пользователь, с транзакциями которого ведётся работа
     */
    public TransactionService(TransactionRepository repository, User user) {
        this.repository = repository;
        this.statsService = new TransactionStatsService(repository, user);
        this.user = user;
    }

    public final TransactionRepository repository;
    @Getter
    @Setter
    public TransactionStatsService statsService;
    public final User user;

    /**
     * Проверяет корректность категории транзакции.
     *
     * @param category категория для проверки
     * @return результат проверки с сообщением
     */
    public ParseResponseDTO checkCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ParseResponseDTO(false, "Категория не может быть пустой!");
        } else if (category.trim().equalsIgnoreCase("цель") && getGoal() == 0) {
            System.out.println("Вы еще не установили цель! Вы можете сделать это в меню статистики и анализа. Введите другую категорию!");
        }
        return new ParseResponseDTO(true, category);
    }

    /**
     * Проверяет корректность суммы транзакции.
     *
     * @param sumInput сумма в виде строки для проверки
     * @return результат проверки с сообщением
     */
    public ParseResponseDTO checkSum(String sumInput) {
        if (sumInput == null || sumInput.trim().isEmpty()) {
            return new ParseResponseDTO(false, "Сумма не может быть пустой!");
        }
        try {
            double sum = Double.parseDouble(sumInput.trim());
            if (Double.isNaN(sum) || Double.isInfinite(sum)) {
                return new ParseResponseDTO(false, "Сумма должна быть корректным числом!");
            }
            if (sum <= 0) {
                return new ParseResponseDTO(false, "Сумма должна быть больше нуля!");
            }
            return new ParseResponseDTO(true, sumInput);
        } catch (NumberFormatException e) {
            return new ParseResponseDTO(false, "Пожалуйста, введите корректное число!");
        }
    }

    /**
     * Создает новую транзакцию с заданным типом, суммой, категорией и описанием для пользователя.
     *
     * @param type        тип транзакции (доход/расход)
     * @param sum         сумма транзакции в виде строки
     * @param category    категория транзакции
     * @param description описание транзакции
     * @return сообщение об успешном создании или ошибке
     */
    public ParseResponseDTO createTransaction(int type, String sum, String category, String description) {
        if (type != 1 && type != 2) {
            return new ParseResponseDTO(false, "Тип транзакции должен быть 1 (доход) или 2 (расход)! Попробуйте ещё раз!");
        }
        ParseResponseDTO sumCheck = checkSum(sum);
        if (!sumCheck.success) return sumCheck;
        ParseResponseDTO categoryCheck = checkCategory(category);
        if (!categoryCheck.success) return categoryCheck;

        double parsedSum = Double.parseDouble(sum);
        try {
            repository.createTransaction(new Transaction(type, parsedSum, category, description));
            if (type == 2 && getMonthlyBudget() != 0) {
                System.out.println(checkExpenseLimitReminder());
            }
            return new ParseResponseDTO(true, "Транзакция успешно сохранена!");
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            String errorMessage;
            if (sqlState.equals("22001")) {
                errorMessage = "Слишком длинная категория или описание: " + message + " Попробуйте ещё раз!";
                return new ParseResponseDTO(false, errorMessage);
            } else {
                errorMessage = "Ошибка базы данных: " + message + " Попробуйте ещё раз!";
                return new ParseResponseDTO(false, errorMessage);
            }
        }
    }

    /**
     * Обновляет тип транзакции.
     *
     * @param newType     новый тип транзакции
     * @param transaction объект транзакции для обновления
     * @return сообщение о результате обновления типа
     */
    public ParseResponseDTO updateTransactionType(int newType, Transaction transaction) {
        if (transaction == null) {
            return new ParseResponseDTO(false, "Транзакция не найдена! Попробуйте ещё раз!");
        }
        if (newType != 1 && newType != 2) {
            return new ParseResponseDTO(false, "Тип транзакции должен быть 1 (доход) или 2 (расход)! Попробуйте ещё раз!");
        }
        try {
            repository.updateTransactionType(newType, transaction);
            if (newType == 2 && getMonthlyBudget() != 0) {
                System.out.println(checkExpenseLimitReminder());
            }
            return new ParseResponseDTO(true, "Тип транзакции успешно обновлён!");
        } catch (SQLException e) {
                return new ParseResponseDTO(false, "Ошибка!  " + e.getMessage());
        }
    }

    /**
     * Обновляет сумму транзакции.
     *
     * @param newSum      новая сумма транзакции в виде строки
     * @param transaction объект транзакции для обновления
     * @return сообщение о результате обновления суммы
     */
    public ParseResponseDTO updateTransactionSum(String newSum, Transaction transaction) {
        if (transaction == null) {
            return new ParseResponseDTO(false, "Транзакция не найдена! Попробуйте ещё раз!");
        }
        ParseResponseDTO sumCheck = checkSum(newSum);
        if (!sumCheck.success) return sumCheck;
        double parsedSum = Double.parseDouble(newSum);
        try {
            repository.updateTransactionSum(parsedSum, transaction);
            if (transaction.getType() == 2 && getMonthlyBudget() != 0) {
                System.out.println(checkExpenseLimitReminder());
            }
            return new ParseResponseDTO(true, "Сумма транзакции успешно обновлена!");
        } catch (SQLException e) {
            return new ParseResponseDTO(false, "Ошибка! " + e.getMessage());
        }


    }

    /**
     * Обновляет категорию транзакции.
     *
     * @param newCategory новая категория транзакции
     * @param transaction объект транзакции для обновления
     * @return сообщение о результате обновления категории
     */
    public ParseResponseDTO updateTransactionCategory(String newCategory, Transaction transaction) {
        if (transaction == null) {
            return new ParseResponseDTO(false, "Транзакция не найдена! Попробуйте ещё раз!");
        }
        ParseResponseDTO categoryCheck = checkCategory(newCategory);
        if (!categoryCheck.success) return categoryCheck;
        try {
            repository.updateTransactionCategory(newCategory, transaction);
            return new ParseResponseDTO(true, "Категория транзакции успешно обновлена!");
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            if ("22001".equals(sqlState)) {
                return new ParseResponseDTO(false, "Слишком длинная категория: " + message + " Попробуйте ещё раз!");
            } else {
                return new ParseResponseDTO(false, "Ошибка!  " + e.getMessage());
            }

        }
    }

    /**
     * Обновляет описание транзакции.
     *
     * @param newDescription новое описание транзакции
     * @param transaction    объект транзакции для обновления
     * @return сообщение о результате обновления описания
     */
    public ParseResponseDTO updateTransactionDescription(String newDescription, Transaction transaction) {
        if (transaction == null) {
            return new ParseResponseDTO(false, "Транзакция не найдена! Попробуйте ещё раз!");
        }
        if (newDescription != null && newDescription.length() > 200) {
            return new ParseResponseDTO(false, "Описание не должно превышать 200 символов! Попробуйте ещё раз!");
        }
        try {
            repository.updateTransactionDescription(newDescription, transaction);
            return new ParseResponseDTO(true, "Описание транзакции успешно обновлено!");
        } catch (SQLException e) {
            return new ParseResponseDTO(false, "Ошибка!  " + e.getMessage());
        }
    }


    /**
     * Устанавливает месячный бюджет пользователя.
     *
     * @param budgetInput новое значение бюджета в виде строки
     * @return уведомление об успешном обновлении бюджета или сообщение об ошибке
     */
    public ParseResponseDTO setMonthlyBudget(String budgetInput) {
        ParseResponseDTO budgetCheck = checkSum(budgetInput);
        if (!budgetCheck.success) return budgetCheck;
        double budget = Double.parseDouble(budgetInput);
        try {
            repository.setMonthlyBudget(budget);
            return new ParseResponseDTO(true, String.format("Новый месячный бюджет %.2f руб. успешно установлен!", budget));
        } catch (SQLException e) {
            return new ParseResponseDTO(false, "Ошибка! " + e.getMessage());
        }


    }

    /**
     * Получает список транзакций, совершенных до указанной даты и времени.
     *
     * @param timestamp временная метка
     * @return список транзакций
     */
    public List<Transaction> getTransactionsBeforeTimestamp(LocalDateTime timestamp) {
        try {
            return transactionsFilterUpperCase(repository.getTransactionsBeforeTimestamp(timestamp));
        } catch (SQLException e) {
            System.out.println("Ошибка! " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Получает список транзакций, совершенных после указанной даты и времени.
     *
     * @param timestamp временная метка
     * @return список транзакций
     */
    public List<Transaction> getTransactionsAfterTimestamp(LocalDateTime timestamp) {
        try {
            return transactionsFilterUpperCase(repository.getTransactionsAfterTimestamp(timestamp));
        } catch (SQLException e) {
            System.out.println("Ошибка! " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * Проверяет корректность введённой временной метки.
     *
     * @param timestampInput строка с временной меткой в формате dd.MM.yyyy HH:mm
     * @return результат проверки с временной меткой или сообщением об ошибке
     */
    public ParseTimeResponseDTO checkTimestampInput(String timestampInput) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        ParseTimeResponseDTO unsuccess = new ParseTimeResponseDTO();
        try {
            LocalDateTime timestamp = LocalDateTime.parse(timestampInput, formatter);
            return new ParseTimeResponseDTO(true, timestamp);
        } catch (Exception e) {
            unsuccess.success = false;
            unsuccess.content = "Неверный формат! Используйте dd.MM.yyyy HH:mm";
            return unsuccess;
        }
    }


    /**
     * Получает список транзакций по заданной категории.
     *
     * @param category категория для фильтрации
     * @return список транзакций
     */
    public List<Transaction> getTransactionsByCategory(String category) {
        try {
            return transactionsFilterUpperCase(repository.getTransactionsByCategory(category));
        } catch (SQLException e) {
            String sqlState = e.getSQLState();
            String message = e.getMessage();
            if ("22001".equals(sqlState)) {
                System.out.println("Слишком длинная категория: " + message + " Попробуйте ещё раз!");
            } else {
                System.out.println(databaseError(e));
            }
            return new ArrayList<>();
        }
    }

    /**
     * Получает список транзакций по заданному типу (доход/расход).
     *
     * @param type тип транзакции для фильтрации
     * @return список транзакций
     */
    public List<Transaction> getTransactionsByType(int type) {
        try {
            return transactionsFilterUpperCase(repository.getTransactionsByType(type));
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return new ArrayList<>();
        }
    }

    /**
     * Получает список всех транзакций пользователя.
     *
     * @return список всех транзакций
     */
    public List<Transaction> getAllTransactions() {
        try {
            return transactionsFilterUpperCase(repository.getAllTransactions());
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return new ArrayList<>();
        }
    }

    /**
     * Форматирует список транзакций - выводит категории с большой буквы.
     *
     * @param transactions список транзакций
     * @return отформатированный список транзакций
     */
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

    /**
     * Удаляет указанную транзакцию.
     *
     * @param transaction транзакция для удаления
     * @return сообщение о результате удаления
     */
    public ParseResponseDTO deleteTransaction(Transaction transaction) {
        if (transaction == null) {
            return new ParseResponseDTO(false, "Транзакция не найдена! Попробуйте ещё раз!");
        }
        try {
            if (repository.deleteTransaction(transaction)) {
                return new ParseResponseDTO(true, "Транзакция успешно удалена!");
            }
            return new ParseResponseDTO(false, "Транзакция не найдена! Попробуйте ещё раз!");
        } catch (SQLException e) {
            return new ParseResponseDTO(false, "Ошибка! " + e.getMessage());
        }


    }

    /**
     * Получает месячный бюджет пользователя.
     *
     * @return значение месячного бюджета
     */
    public double getMonthlyBudget() {
        try {
            return repository.getMonthlyBudget();
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return 0;
        }
    }

    /**
     * Получает финансовую цель пользователя.
     *
     * @return значение финансовой цели
     */
    public double getGoal() {
        try {
            return repository.getGoal();
        } catch (SQLException e) {
            System.out.println(databaseError(e));
            return 0;
        }
    }

    /**
     * Устанавливает финансовую цель пользователя.
     *
     * @param goalInput новое значение цели в виде строки
     * @return уведомление об успешной установке цели или сообщение об ошибке
     */
    public String setGoal(String goalInput) {
        ParseResponseDTO goalCheck = checkSum(goalInput);
        if (!goalCheck.success) {
            return goalCheck.content;
        } else {
            try {
                repository.setGoal(Double.parseDouble(goalInput));
                return "Новая цель " + String.format("%.2f", Double.parseDouble(goalInput)) + " руб. успешно установлена!";
            } catch (SQLException e) {
                return "Ошибка! " + e.getMessage();
            }
        }
    }

    /**
     * Возвращает информацию о месячном бюджете пользователя.
     *
     * @return строка с информацией о бюджете
     */
    public String getMonthlyBudgetInfo() {
        double monthlyBudget = getMonthlyBudget();
        if (monthlyBudget != 0) {
            return "Ваш месячный бюджет: " + String.format("%.2f", monthlyBudget) + " руб.";
        }
        return "Вы пока не установили месячный бюджет. Сделайте это прямо сейчас!";
    }

    /**
     * Проверяет остаток месячного бюджета и возвращает сообщение о состоянии.
     *
     * @return сообщение о состоянии бюджета
     */
    public String checkMonthlyBudgetLimit() {
        try {
            if (repository.getMonthlyBudget() == 0) {
                return "";
            }
        } catch (SQLException e) {
            System.out.println("Ошибка! " + e);
        }
        double balance = statsService.checkMonthlyBudgetLimit();
        if (balance < 0) {
            return "Вы превысили лимит на месяц на " + String.format("%.2f", Math.abs(balance)) + " руб.!";
        }
        if (balance == 0) {
            return "Ваш остаток: " + String.format("%.2f", balance) + " руб.";
        }
        return "Ваш остаток: " + String.format("%.2f", balance) + " руб. Продолжайте в том же духе!";
    }

    /**
     * Проверяет лимит расходов и выводит уведомление, если он близок к исчерпанию.
     */
    public String checkExpenseLimitReminder() {
        if (getMonthlyBudget() == 0) {
            return "";
        }
        double remainingBudget = statsService.checkMonthlyBudgetLimit();
        if (remainingBudget <= 0) {
            return notifyAboutMonthlyLimit(Math.abs(remainingBudget));
        } else if (remainingBudget <= getMonthlyBudget() * 0.1) {
            return  "Осторожно! Остаток бюджета составляет " + String.format("%.2f", remainingBudget) +
                    " руб. (менее 10% от лимита).";
        }
        return "";
    }

    /**
     * Уведомляет о превышении месячного бюджета.
     *
     * @param overgo сумма превышения
     * @return
     */
    public String notifyAboutMonthlyLimit(double overgo) {
        return "Внимание! Вы превысили установленный месячный бюджет на " + String.format("%.2f", overgo) +
                " руб. " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) + ". Подробности отправлены" +
                "по адресу " + user.getEmail();
    }


    /**
     * Возвращает информацию о финансовой цели пользователя.
     *
     * @return строка с информацией о цели
     */
    public String getGoalInfo() {
        double goal = getGoal();
        if (goal != 0) {
            return "Ваша установленная цель: " + String.format("%.2f", goal) + " руб.";
        }
        return "Вы пока не установили цель. Сделайте это прямо сейчас!";
    }

    public String databaseError(Exception e) {
        return "Ошибка базы данных: " + e.getMessage() + " Попробуйте ещё раз!";
    }

    public static class ParseResponseDTO {
        public boolean success;
        public String content;

        public ParseResponseDTO() {
        }

        ParseResponseDTO(boolean success, String content) {
            this.success = success;
            this.content = content;
        }
    }

    public static class ParseTimeResponseDTO extends ParseResponseDTO {
        public LocalDateTime time;

        ParseTimeResponseDTO() {
        }

        ParseTimeResponseDTO(boolean success, LocalDateTime time) {
            super(success, null);
            this.time = time;
        }
    }
}