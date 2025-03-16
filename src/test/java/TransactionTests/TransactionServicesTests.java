//package TransactionTests;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.ylabHomework.models.Transaction;
//import org.ylabHomework.models.User;
//import org.ylabHomework.repositories.TransactionRepository;
//import org.ylabHomework.services.TransactionService;
//import org.ylabHomework.services.TransactionStatsService;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//import java.util.List;
//import java.util.Map;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DisplayName("Тесты для сервиса, работающего с транзакциями")
//public class TransactionServicesTests {
//    private TransactionRepository repo;
//    private TransactionService service;
//    private TransactionStatsService statService;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        user = new User("anya", "anya@ya.ru", "1234", 1);
//        repo = new TransactionRepository(user);
//        service = new TransactionService(repo, user);
//        statService = new TransactionStatsService(repo, user);
//    }
//
//    @Test
//    @DisplayName("Создание новой транзакции")
//    public void createTransaction() {
//        String result = service.createTransaction(1, 2500.0, "стипендия", "ура! стипендия");
//        assertThat(result).isEqualTo("Транзакция успешно сохранена!");
//        List<Transaction> transactions = service.getAllTransactions();
//        assertThat(transactions).hasSize(1);
//        assertThat(transactions.getFirst().getSum()).isEqualTo(2500.0);
//    }
//
//    @Test
//    @DisplayName("Обновление типа транзакции с корректным типом")
//    public void updateTransactionTypeValid() {
//        Transaction transaction = new Transaction(1, 40.0, "проезд", "до вуза");
//        service.repository.createTransaction(transaction);
//        String result = service.updateTransactionType(2, transaction);
//        assertThat(result).isEqualTo("Успешно обновлено!");
//        assertThat(transaction.getType()).isEqualTo(2);
//    }
//
//    @Test
//    @DisplayName("Обновление типа транзакции с null")
//    public void updateTransactionTypeNull() {
//        Transaction transaction = new Transaction(1, 40.0, "проезд", "из вуза");
//        String result = service.updateTransactionType(null, transaction);
//        assertThat(result).isEqualTo("Тип транзакции не может быть пустым!");
//    }
//
//    @Test
//    @DisplayName("Обновление суммы транзакции")
//    public void updateTransactionSum() {
//        Transaction transaction = new Transaction(2, 20.0, "карамельный макиато", "необходимость");
//        service.repository.createTransaction(transaction);
//        String result = service.updateTransactionSum(250.0, transaction);
//        assertThat(result).isEqualTo("Сумма обновлена!");
//        assertThat(transaction.getSum()).isEqualTo(250.0);
//    }
//
//    @Test
//    @DisplayName("Обновление категории транзакции с корректной категорией")
//    public void updateTransactionCategoryValid() {
//        Transaction transaction = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        service.repository.createTransaction(transaction);
//        String result = service.updateTransactionCategory("Раф", transaction);
//        assertThat(result).isEqualTo("Категория обновлена!");
//        assertThat(transaction.getCategory()).isEqualTo("Раф");
//    }
//
//    @Test
//    @DisplayName("Обновление категории транзакции с пустой категорией")
//    public void updateTransactionCategoryEmpty() {
//        Transaction transaction = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        String result = service.updateTransactionCategory("", transaction);
//        assertThat(result).isEqualTo("Категория не может быть пустой!");
//    }
//
//    @Test
//    @DisplayName("Обновление описания транзакции с корректным описанием")
//    public void updateTransactionDescriptionValid() {
//        Transaction transaction = new Transaction(1, 2500.0, "стипендия", "ура!");
//        service.repository.createTransaction(transaction);
//        String result = service.updateTransactionDescription("ЗП моя", transaction);
//        assertThat(result).isEqualTo("Описание обновлено!");
//        assertThat(transaction.getDescription()).isEqualTo("ЗП моя");
//    }
//
//    @Test
//    @DisplayName("Обновление описания транзакции с длинным описанием")
//    public void updateTransactionDescriptionTooLong() {
//        Transaction transaction = new Transaction(1, 2500.0, "стипендия", "ура!");
//        String longDescription = "a".repeat(201);
//        String result = service.updateTransactionDescription(longDescription, transaction);
//        assertThat(result).isEqualTo("Описание не должно превышать 200 символов!");
//    }
//
//    @Test
//    @DisplayName("Получение транзакций до указанной даты")
//    public void getTransactionsBeforeTimestamp() {
//        LocalDateTime now = LocalDateTime.now();
//        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        t1.setTimestamp(now.minusDays(1));
//        service.repository.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        t2.setTimestamp(now.plusDays(1));
//        service.repository.createTransaction(t2);
//
//        List<Transaction> result = service.getTransactionsBeforeTimestamp(now);
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst().getSum()).isEqualTo(2500.0);
//    }
//
//    @Test
//    @DisplayName("Получение транзакций после указанной даты")
//    public void getTransactionsAfterTimestamp() {
//        LocalDateTime now = LocalDateTime.now();
//        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        t1.setTimestamp(now.minusDays(1));
//        service.repository.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        t2.setTimestamp(now.plusDays(1));
//        service.repository.createTransaction(t2);
//
//        List<Transaction> result = service.getTransactionsAfterTimestamp(now);
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst().getSum()).isEqualTo(250.0);
//    }
//
//    @Test
//    @DisplayName("Получение транзакций между датами")
//    public void getTransactionsBetweenTimestamps() {
//        LocalDateTime now = LocalDateTime.now();
//        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        t1.setTimestamp(now.minusDays(2));
//        service.repository.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        t2.setTimestamp(now);
//        service.repository.createTransaction(t2);
//        Transaction t3 = new Transaction(1, 5000.0, "матпомощь", "деньги от вуза");
//        t3.setTimestamp(now.plusDays(2));
//        service.repository.createTransaction(t3);
//
//        List<Transaction> result = service.getTransactionsBetweenTimestamps(now.minusDays(1), now.plusDays(1));
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst().getSum()).isEqualTo(250.0);
//    }
//
//    @Test
//    @DisplayName("Получение транзакций по категории")
//    public void getTransactionsByCategory() {
//        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        service.repository.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        service.repository.createTransaction(t2);
//
//        List<Transaction> result = service.getTransactionsByCategory("стипендия");
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst().getSum()).isEqualTo(2500.0);
//    }
//
//    @Test
//    @DisplayName("Получение транзакций по типу")
//    public void getTransactionsByType() {
//        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        service.repository.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        service.repository.createTransaction(t2);
//
//        List<Transaction> result = service.getTransactionsByType(2);
//        assertThat(result).hasSize(1);
//        assertThat(result.getFirst().getSum()).isEqualTo(250.0);
//    }
//
//    @Test
//    @DisplayName("Получение всех транзакций")
//    public void getAllTransactions() {
//        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        service.repository.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        service.repository.createTransaction(t2);
//
//        List<Transaction> result = service.getAllTransactions();
//        assertThat(result).hasSize(2);
//        assertThat(result).extracting(Transaction::getSum).containsExactlyInAnyOrder(2500.0, 250.0);
//    }
//
//    @Test
//    @DisplayName("Удаление существующей транзакции")
//    public void deleteTransactionExists() {
//        Transaction transaction = new Transaction(1, 2500.0, "стипендия", "ура!");
//        service.repository.createTransaction(transaction);
//        String result = service.deleteTransaction(transaction);
//        assertThat(result).isEqualTo("Удаление прошло успешно");
//        assertThat(service.getAllTransactions()).isEmpty();
//    }
//
//    @Test
//    @DisplayName("Удаление несуществующей транзакции")
//    public void deleteTransactionNotExists() {
//        Transaction transaction = new Transaction(1, 2500.0, "стипендия", "ура!");
//        String result = service.deleteTransaction(transaction);
//        assertThat(result).isEqualTo("Транзакция не найдена!");
//    }
//
//    @Test
//    @DisplayName("Проверка остатка месячного бюджета без транзакций")
//    public void checkMonthlyBudgetLimitNoTransactions() {
//        service.setMonthlyBudget(1000.0); // Изменено: используем метод сервиса вместо прямого доступа
//        double result = statService.checkMonthlyBudgetLimit();
//        assertThat(result).isEqualTo(1000.0);
//    }
//
//    @Test
//    @DisplayName("Проверка остатка месячного бюджета с транзакциями")
//    public void checkMonthlyBudgetLimitWithTransactions() {
//        service.setMonthlyBudget(1000.0); // Изменено: используем метод сервиса вместо прямого доступа
//        LocalDateTime startOfMonth = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);
//        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        t1.setTimestamp(startOfMonth.plusDays(1));
//        repo.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        t2.setTimestamp(startOfMonth.plusDays(2));
//        repo.createTransaction(t2);
//
//        double result = statService.checkMonthlyBudgetLimit();
//        assertThat(result).isEqualTo(3250.0);
//    }
//
//    @Test
//    @DisplayName("Проверка прогресса цели без транзакций")
//    public void checkGoalProgressNoTransactions() {
//        service.setGoal(1000.0); // Изменено: используем метод сервиса вместо прямого доступа
//        double result = statService.checkGoalProgress();
//        assertThat(result).isEqualTo(1000.0);
//    }
//
//    @Test
//    @DisplayName("Проверка прогресса цели с транзакциями")
//    public void checkGoalProgressWithTransactions() {
//        service.setGoal(1000.0); // Изменено: используем метод сервиса вместо прямого доступа
//        Transaction t1 = new Transaction(1, 2500.0, "Цель", "стипендия");
//        repo.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "Цель", "хочу карамельный макиато");
//        repo.createTransaction(t2);
//
//        double result = statService.checkGoalProgress();
//        assertThat(result).isEqualTo(-1250.0);
//    }
//
//    @Test
//    @DisplayName("Анализ расходов по категориям без транзакций")
//    public void analyzeExpenseByCategoriesEmpty() {
//        Map<String, Double> result = statService.analyzeExpenseByCategories();
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    @DisplayName("Анализ расходов по категориям с транзакциями")
//    public void analyzeExpenseByCategoriesWithTransactions() {
//        Transaction t1 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        repo.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 50.0, "булочка", "с сосиской");
//        repo.createTransaction(t2);
//        Transaction t3 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        repo.createTransaction(t3);
//
//        Map<String, Double> result = statService.analyzeExpenseByCategories();
//        assertThat(result).hasSize(2);
//        assertThat(result).containsEntry("карамельный макиато", -250.0);
//        assertThat(result).containsEntry("булочка", -50.0);
//    }
//
////    @Test
////    @DisplayName("Подсчёт баланса без транзакций")
////    public void calculateBalanceNoTransactions() {
////        double result = statService.calculateBalance();
////        assertThat(result).isEqualTo(0.0);
////    }
//
////    @Test
////    @DisplayName("Подсчёт баланса с транзакциями")
////    public void calculateBalanceWithTransactions() {
////        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
////        repo.createTransaction(t1);
////        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
////        repo.createTransaction(t2);
////
////        double result = statService.calculateBalance();
////        assertThat(result).isEqualTo(2250.0);
////    }
//
//    @Test
//    @DisplayName("Получение доходов и расходов за период без транзакций")
//    public void getIncomeExpenseForPeriodNoTransactions() {
//        LocalDateTime start = LocalDateTime.now().minusDays(1);
//        LocalDateTime end = LocalDateTime.now();
//        double[] result = statService.getIncomeExpenseForPeriod(start, end);
//        assertThat(result).containsExactly(0.0, 0.0, 0.0);
//    }
//
//    @Test
//    @DisplayName("Получение доходов и расходов за период с транзакциями")
//    public void getIncomeExpenseForPeriodWithTransactions() {
//        LocalDateTime start = LocalDateTime.now().minusDays(1);
//        LocalDateTime end = LocalDateTime.now().plusDays(1);
//        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        t1.setTimestamp(start.plusHours(1));
//        repo.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        t2.setTimestamp(start.plusHours(2));
//        repo.createTransaction(t2);
//
//        double[] result = statService.getIncomeExpenseForPeriod(start, end);
//        assertThat(result).containsExactly(2500.0, 250.0, 2250.0);
//    }
//
//    @Test
//    @DisplayName("Генерация финансового отчёта без транзакций")
//    public void generateGeneralReportNoTransactions() {
//        TransactionStatsService.FinancialReport result = statService.generateGeneralReport(null, null);
//        assertThat(result).isNull();
//    }
//
//    @Test
//    @DisplayName("Генерация финансового отчёта за полный период")
//    public void generateGeneralReportFullPeriod() {
//        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        repo.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
//        repo.createTransaction(t2);
//        Transaction t3 = new Transaction(1, 5000.0, "цель", "на десять карамельных макиато и пятнадцать булочек с сосиской");
//        repo.createTransaction(t3);
//        service.setGoal(200.0); // Изменено: используем метод сервиса вместо прямого доступа
//
//        TransactionStatsService.FinancialReport result = statService.generateGeneralReport(null, null);
//        assertThat(result.totalIncome()).isEqualTo(7500.0);
//        assertThat(result.totalExpense()).isEqualTo(250.0);
//        assertThat(result.totalBalance()).isEqualTo(7250.0);
//        assertThat(result.categoryReport()).hasSize(3);
//        assertThat(result.categoryReport().get("стипендия")[0]).isEqualTo(2500.0);
//        assertThat(result.categoryReport().get("карамельный макиато")[1]).isEqualTo(250.0);
//        assertThat(result.goalData()).containsExactly(200.0, 5000.0, 0.0, 5000.0, -4800.0);
//    }
//
//    @Test
//    @DisplayName("Генерация финансового отчёта за заданный период")
//    public void generateGeneralReportSpecificPeriod() {
//        LocalDateTime start = LocalDateTime.now().minusDays(1);
//        LocalDateTime end = LocalDateTime.now().plusDays(1);
//        Transaction t1 = new Transaction(1, 2500.0, "стипендия", "ура!");
//        t1.setTimestamp(start.plusHours(1));
//        repo.createTransaction(t1);
//        Transaction t2 = new Transaction(2, 250.0, "карамельный макиато", "единственная причина ходить в вуз");
//        t2.setTimestamp(end.plusHours(1));
//        repo.createTransaction(t2);
//
//        TransactionStatsService.FinancialReport result = statService.generateGeneralReport(start, end);
//        assertThat(result.totalIncome()).isEqualTo(2500.0);
//        assertThat(result.totalExpense()).isEqualTo(0.0);
//        assertThat(result.totalBalance()).isEqualTo(2500.0);
//        assertThat(result.categoryReport()).hasSize(1);
//    }
//}