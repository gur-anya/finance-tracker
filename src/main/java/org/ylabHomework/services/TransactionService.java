package org.ylabHomework.services;

import lombok.Getter;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     * @param user пользователь, с транзакциями которого ведётся работа
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
     * Проверяет корректность категории транзакции.
     *
     * @param category категория для проверки
     * @return результат проверки с сообщением
     */
    public ParseResponseDTO checkCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            return new ParseResponseDTO(false, "Категория не может быть пустой!");
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
     * @param type тип транзакции (доход/расход)
     * @param sum сумма транзакции в виде строки
     * @param category категория транзакции
     * @param description описание транзакции
     * @return сообщение об успешном создании или ошибке
     */
    public ParseResponseDTO createTransaction(int type, String sum, String category, String description) {
        if (type != 1 && type != 2) {
            return new ParseResponseDTO(false, "Тип транзакции должен быть INCOME или EXPENSE!");
        }
        ParseResponseDTO sumCheck = checkSum(sum);
        if (!sumCheck.success) return sumCheck;
        ParseResponseDTO categoryCheck = checkCategory(category);
        if (!categoryCheck.success) return categoryCheck;

        double parsedSum = Double.parseDouble(sum);
        repository.createTransaction(new Transaction(type, parsedSum, category, description));
        if (type == 2 && getMonthlyBudget() != 0) {
            checkExpenseLimitReminder();
        }
        return new ParseResponseDTO(true, "Транзакция успешно сохранена!");
    }

    /**
     * Обновляет тип транзакции.
     *
     * @param newType новый тип транзакции
     * @param transaction объект транзакции для обновления
     * @return сообщение о результате обновления типа
     */
    public ParseResponseDTO updateTransactionType(int newType, Transaction transaction) {
        repository.updateTransactionType(newType, transaction);
        if (newType == 2 && getMonthlyBudget() != 0) {
            checkExpenseLimitReminder();
        }
        return new ParseResponseDTO(true, "Успешно обновлено!");
    }

    /**
     * Обновляет сумму транзакции.
     *
     * @param newSum новая сумма транзакции в виде строки
     * @param transaction объект транзакции для обновления
     * @return сообщение о результате обновления суммы
     */
    public ParseResponseDTO updateTransactionSum(String newSum, Transaction transaction) {
        ParseResponseDTO sumCheck = checkSum(newSum);
        if (!sumCheck.success) return sumCheck;
        double parsedSum = Double.parseDouble(newSum);

        repository.updateTransactionSum(parsedSum, transaction);
        if (transaction.getType() == 2 && getMonthlyBudget() != 0) {
            checkExpenseLimitReminder();
        }
        return new ParseResponseDTO(true, "Сумма обновлена!");
    }

    /**
     * Обновляет категорию транзакции.
     *
     * @param newCategory новая категория транзакции
     * @param transaction объект транзакции для обновления
     * @return сообщение о результате обновления категории
     */
    public ParseResponseDTO updateTransactionCategory(String newCategory, Transaction transaction) {
        ParseResponseDTO categoryCheck = checkCategory(newCategory);
        if (!categoryCheck.success) return categoryCheck;
        repository.updateTransactionCategory(newCategory, transaction);
        return new ParseResponseDTO(true, "Категория обновлена!");
    }

    /**
     * Обновляет описание транзакции.
     *
     * @param newDescription новое описание транзакции
     * @param transaction объект транзакции для обновления
     * @return сообщение о результате обновления описания
     */
    public ParseResponseDTO updateTransactionDescription(String newDescription, Transaction transaction) {
        if (newDescription != null && newDescription.length() > 200) {
            return new ParseResponseDTO(false, "Описание не должно превышать 200 символов!");
        }
        repository.updateTransactionDescription(newDescription, transaction);
        return new ParseResponseDTO(true, "Описание обновлено!");
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
        double budget = Double.parseDouble(budgetCheck.content);

        repository.setMonthlyBudget(budget);
        return new ParseResponseDTO(true, String.format("Новый месячный бюджет %.2f руб. успешно установлен!", budget));
    }

    /**
     * Получает список транзакций, совершенных до указанной даты и времени.
     *
     * @param timestamp временная метка
     * @return список транзакций
     */
    public List<Transaction> getTransactionsBeforeTimestamp(LocalDateTime timestamp) {
        return transactionsFilterUpperCase(repository.getTransactionsBeforeTimestamp(timestamp));
    }

    /**
     * Получает список транзакций, совершенных после указанной даты и времени.
     *
     * @param timestamp временная метка
     * @return список транзакций
     */
    public List<Transaction> getTransactionsAfterTimestamp(LocalDateTime timestamp) {
        return transactionsFilterUpperCase(repository.getTransactionsAfterTimestamp(timestamp));
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
        return transactionsFilterUpperCase(repository.getTransactionsByCategory(category));
    }

    /**
     * Получает список транзакций по заданному типу (доход/расход).
     *
     * @param type тип транзакции для фильтрации
     * @return список транзакций
     */
    public List<Transaction> getTransactionsByType(int type) {
        return transactionsFilterUpperCase(repository.getTransactionsByType(type));
    }

    /**
     * Получает список всех транзакций пользователя.
     *
     * @return список всех транзакций
     */
    public List<Transaction> getAllTransactions() {
        return transactionsFilterUpperCase(repository.getAllTransactions());
    }

    /**
     * Форматирует список транзакций - выводит категории с большой буквы.
     * @param transactions список транзакций
     * @return отформатированный список транзакций
     */
    private List<Transaction> transactionsFilterUpperCase(List<Transaction> transactions){
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
        if (repository.deleteTransaction(transaction)) {
            return new ParseResponseDTO(true, "Удаление прошло успешно");
        }
        return new ParseResponseDTO(false, "Транзакция не найдена!");
    }

    /**
     * Получает месячный бюджет пользователя.
     *
     * @return значение месячного бюджета
     */
    public double getMonthlyBudget() {
        return repository.getMonthlyBudget();
    }

    /**
     * Получает финансовую цель пользователя.
     *
     * @return значение финансовой цели
     */
    public double getGoal() {
        return repository.getGoal();
    }

    /**
     * Устанавливает финансовую цель пользователя.
     *
     * @param goalInput новое значение цели в виде строки
     * @return уведомление об успешной установке цели или сообщение об ошибке
     */
    public String setGoal(String goalInput) {
        try {
            double goal = Double.parseDouble(goalInput);
            if (Double.isNaN(goal) || Double.isInfinite(goal)) {
                return "Цель должна быть корректным числом!";
            }
            if (goal <= 0) {
                return "Цель должна быть больше нуля!";
            }
            repository.setGoal(goal);
            return "Новая цель " + String.format("%.2f", goal) + " руб. успешно установлена!";
        } catch (NumberFormatException e) {
            return "Пожалуйста, введите корректное число!";
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
        if (user.getMonthlyBudget() == 0){
            return "";
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
    public void checkExpenseLimitReminder() {
        if (getMonthlyBudget() == 0) return;
        double remainingBudget = statsService.checkMonthlyBudgetLimit();
        if (remainingBudget <= 0) {
            notifyAboutMonthlyLimit(Math.abs(remainingBudget));
        } else if (remainingBudget <= getMonthlyBudget() * 0.1) {
            System.out.println("Осторожно! Остаток бюджета составляет " + String.format("%.2f", remainingBudget) +
                    " руб. (менее 10% от лимита).");
        }
    }

    /**
     * Уведомляет о превышении месячного бюджета.
     *
     * @param overgo сумма превышения
     */
    public void notifyAboutMonthlyLimit(double overgo) {
        String message = "Внимание! Вы превысили установленный месячный бюджет на " + String.format("%.2f", overgo) +
                " руб. " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        sendEmailNotification(message);
    }

    /**
     * Имитирует отправку email-уведомления о превышении бюджета.
     *
     * @param message текст уведомления
     */
    public void sendEmailNotification(String message) {
        System.out.println("Отправлено письмо на почту " + user.getEmail() + " с содержанием: " + message);
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

    public static class ParseResponseDTO {
        public boolean success;
        public String content;

        public ParseResponseDTO() {}
        ParseResponseDTO(boolean success, String content) {
            this.success = success;
            this.content = content;
        }
    }

    public static class ParseTimeResponseDTO extends ParseResponseDTO {
        public LocalDateTime time;

        ParseTimeResponseDTO() {}
        ParseTimeResponseDTO(boolean success, LocalDateTime time) {
            super(success, null);
            this.time = time;
        }
    }
}