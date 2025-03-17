package TransactionTests;

import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.Config;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;
import org.ylabHomework.services.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Тесты для сервиса, работающего с транзакциями")
public class TransactionServicesTests {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
    static Connection connection;
    private TransactionRepository repo;
    private TransactionService service;
    private TransactionStatsService statService;
   private final UserRepository userRepo = new UserRepository();

    private User user;

    public TransactionServicesTests() throws SQLException {
    }

    @BeforeEach
    void setUp() throws SQLException {
        postgres.start();
        user = userRepo.readUserByEmail("anya@ya.ru");
        Config config = new Config();
        connection = config.establishConnection();
       repo = new TransactionRepository(user);
        service = new TransactionService(repo, user);
        statService = new TransactionStatsService(repo, user);
    }

    @AfterEach
    void clear() throws SQLException {
        connection.rollback();
        connection.close();
        postgres.stop();
    }

    @Test
    @DisplayName("Создание новой транзакции")
    public void createTransaction() throws SQLException {
        TransactionService.ParseResponseDTO result = service.createTransaction(1, "2500.0", "стипендия", "ура!");
        assertThat(result.success).isTrue();
        assertThat(result.content).isEqualTo("Транзакция успешно сохранена!");
    }

    @Test
    @DisplayName("Обновление типа транзакции с корректным типом")
    public void updateTransactionTypeValid() throws SQLException {

        TransactionService.ParseResponseDTO result = service.updateTransactionType(2, repo.getAllTransactions().get(1));
        assertThat(result.success).isTrue();
        assertThat(result.content).isEqualTo("Тип транзакции успешно обновлён!");
    }

    @Test
    @DisplayName("Обновление типа транзакции с null")
    public void updateTransactionTypeNull() {
        Transaction transaction = null;
        TransactionService.ParseResponseDTO result = service.updateTransactionType(1, transaction);
        assertThat(result.success).isFalse();
        assertThat(result.content).isEqualTo("Транзакция не найдена! Попробуйте ещё раз!");
    }

    @Test
    @DisplayName("Обновление суммы транзакции")
    public void updateTransactionSum() throws SQLException {
        TransactionService.ParseResponseDTO result = service.updateTransactionSum("250.0", repo.getAllTransactions().get(2));
        assertThat(result.success).isTrue();
        assertThat(result.content).isEqualTo("Сумма транзакции успешно обновлена!");
    }

    @Test
    @DisplayName("Обновление категории транзакции с корректной категорией")
    public void updateTransactionCategoryValid() throws SQLException {
        TransactionService.ParseResponseDTO result = service.updateTransactionCategory("Раф", repo.getAllTransactions().get(1));
        assertThat(result.success).isTrue();
        assertThat(result.content).isEqualTo("Категория транзакции успешно обновлена!");
    }

    @Test
    @DisplayName("Обновление категории транзакции с пустой категорией")
    public void updateTransactionCategoryEmpty() {
        Transaction transaction = new Transaction(2, 250.0, "карамельный макиато", "необходимость");
        TransactionService.ParseResponseDTO result = service.updateTransactionCategory("", transaction);
        assertThat(result.success).isFalse();
        assertThat(result.content).isEqualTo("Категория не может быть пустой!");
    }

    @Test
    @DisplayName("Обновление описания транзакции с корректным описанием")
    public void updateTransactionDescriptionValid() throws SQLException {
        TransactionService.ParseResponseDTO result = service.updateTransactionDescription("ЗП моя", repo.getAllTransactions().get(0));
        assertThat(result.success).isTrue();
        assertThat(result.content).isEqualTo("Описание транзакции успешно обновлено!");
    }

    @Test
    @DisplayName("Обновление описания транзакции с длинным описанием")
    public void updateTransactionDescriptionTooLong() {
        Transaction transaction = new Transaction(1, 2500.0, "стипендия", "ура!");
        String longDescription = "a".repeat(201);
        TransactionService.ParseResponseDTO result = service.updateTransactionDescription(longDescription, transaction);
        assertThat(result.success).isFalse();
        assertThat(result.content).isEqualTo("Описание не должно превышать 200 символов! Попробуйте ещё раз!");
    }

    @Test
    @DisplayName("Получение транзакций до указанной даты")
    public void getTransactionsBeforeTimestamp() throws SQLException {
        LocalDateTime now = LocalDateTime.now();

        List<Transaction> result = service.getTransactionsBeforeTimestamp(now);
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Получение транзакций после указанной даты")
    public void getTransactionsAfterTimestamp() throws SQLException {
        List<Transaction> result = service.getTransactionsAfterTimestamp(LocalDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(0,0,0,0)));
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getSum()).isEqualTo(250.0);
    }

    @Test
    @DisplayName("Получение транзакций по категории")
    public void getTransactionsByCategory() throws SQLException {
        List<Transaction> result = service.getTransactionsByCategory("стипендия");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSum()).isEqualTo(2500.0);
    }

    @Test
    @DisplayName("Получение транзакций по типу")
    public void getTransactionsByType() throws SQLException {
        List<Transaction> result = service.getTransactionsByType(2);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSum()).isEqualTo(250.0);
    }

    @Test
    @DisplayName("Получение всех транзакций")
    public void getAllTransactions() throws SQLException {
       List<Transaction> result = service.getAllTransactions();
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Transaction::getSum).containsExactlyInAnyOrder(2500.0, 50.0, 250.0);
    }

    @Test
    @DisplayName("Удаление существующей транзакции")
    public void deleteTransactionExists() throws SQLException {
        Transaction transaction = service.getAllTransactions().get(0);
        TransactionService.ParseResponseDTO result = service.deleteTransaction(transaction);
        assertThat(result.content).isEqualTo("Транзакция успешно удалена!");
    }

    @Test
    @DisplayName("Удаление несуществующей транзакции")
    public void deleteTransactionNotExists() {
        Transaction transaction = new Transaction(1, 2500.0, "стипендия", "ура!");
        TransactionService.ParseResponseDTO result = service.deleteTransaction(transaction);
        assertThat(result.success).isFalse();
        assertThat(result.content).isEqualTo("Транзакция не найдена! Попробуйте ещё раз!");
    }

    @Test
    @DisplayName("Проверка остатка месячного бюджета с транзакциями")
    public void checkMonthlyBudgetLimitWithTransactions() throws SQLException {
        double result = statService.checkMonthlyBudgetLimit();
        assertThat(result).isEqualTo(3200.0);
    }

    @Test
    @DisplayName("Проверка прогресса цели без транзакций")
    public void checkGoalProgressNoTransactions() throws SQLException {
        service.setGoal("1000.0");
        double result = statService.checkGoalProgress();
        assertThat(result).isEqualTo(1000.0);
    }


    @Test
    @DisplayName("Анализ расходов по категориям с транзакциями")
    public void analyzeExpenseByCategoriesWithTransactions() throws SQLException {
       Map<String, Double> result = statService.analyzeExpenseByCategories();
        assertThat(result).hasSize(2);
        assertThat(result).containsEntry("карамельный макиато", 250.0);
        assertThat(result).containsEntry("булочка с сосиской", 50.0);
    }


    @Test
    @DisplayName("Подсчёт баланса с транзакциями")
    public void calculateBalanceWithTransactions() throws SQLException {
        String result = statService.calculateBalance();
        assertThat(result).isEqualTo("Ваш баланс: 2200,00 руб.");
    }

    @Test
    @DisplayName("Получение доходов и расходов за период без транзакций")
    public void getIncomeExpenseForPeriodNoTransactions() {
        LocalDateTime start = LocalDateTime.now().minusDays(1);
        LocalDateTime end = LocalDateTime.now();
        double[] result = statService.getIncomeExpenseForPeriod(start, end);
        assertThat(result).containsExactly(0.0, 0.0, 0.0);
    }

    @Test
    @DisplayName("Получение доходов и расходов за период с транзакциями")
    public void getIncomeExpenseForPeriodWithTransactions() throws SQLException {
        LocalDateTime start = (LocalDateTime.of(2000, 1, 1, 0, 0));
        LocalDateTime end = (LocalDateTime.of(2050, 1, 1, 0, 0));

        double[] result = statService.getIncomeExpenseForPeriod(start, end);
        assertThat(result).containsExactly(2500.0, 300.0, 2200.0);
    }

    @Test
    @DisplayName("Генерация финансового отчёта за полный период")
    public void generateGeneralReportFullPeriod() throws SQLException {
        TransactionStatsService.FinancialReport result = statService.generateGeneralReport(null, null);
        assertThat(result.totalIncome()).isEqualTo(2500.0);
        assertThat(result.totalExpense()).isEqualTo(300.0);
        assertThat(result.totalBalance()).isEqualTo(2200.0);
        assertThat(result.categoryReport()).hasSize(3);
        assertThat(result.categoryReport().get("стипендия")[0]).isEqualTo(2500.0);
        assertThat(result.categoryReport().get("карамельный макиато")[1]).isEqualTo(250.0);
    }
}