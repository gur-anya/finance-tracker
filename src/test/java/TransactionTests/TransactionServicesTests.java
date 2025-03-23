package TransactionTests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.Config;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Тесты для сервиса, работающего с транзакциями")
public class TransactionServicesTests {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");
    static Connection connection;
    private TransactionRepository repo;
    private TransactionService service;
    private TransactionStatsService statService;
    private final UserRepository userRepo = new UserRepository();

    private User user;

    public TransactionServicesTests() {
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
    public void createTransaction() {
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
    @DisplayName("Обновление типа транзакции с null-транзакцией")
    public void updateTransactionTypeNull() {
        TransactionService.ParseResponseDTO result = service.updateTransactionType(1, null);
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
    public void getTransactionsBeforeTimestamp() {
        LocalDateTime now = LocalDateTime.now();

        List<Transaction> result = service.getTransactionsBeforeTimestamp(now);
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("Получение транзакций после указанной даты")
    public void getTransactionsAfterTimestamp() {
        List<Transaction> result = service.getTransactionsAfterTimestamp(LocalDateTime.of(LocalDate.of(2000, 1, 1), LocalTime.of(0, 0, 0, 0)));
        assertThat(result).hasSize(3);
        assertThat(result.get(0).getSum()).isEqualTo(250.0);
    }

    @Test
    @DisplayName("Получение транзакций по категории")
    public void getTransactionsByCategory() {
        List<Transaction> result = service.getTransactionsByCategory("стипендия");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSum()).isEqualTo(2500.0);
    }

    @Test
    @DisplayName("Получение транзакций по типу")
    public void getTransactionsByType() {
        List<Transaction> result = service.getTransactionsByType(2);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getSum()).isEqualTo(250.0);
    }

    @Test
    @DisplayName("Получение всех транзакций")
    public void getAllTransactions() {
        List<Transaction> result = service.getAllTransactions();
        assertThat(result).hasSize(3);
        assertThat(result).extracting(Transaction::getSum).containsExactlyInAnyOrder(2500.0, 50.0, 250.0);
    }

    @Test
    @DisplayName("Удаление существующей транзакции")
    public void deleteTransactionExists() {
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
    public void checkMonthlyBudgetLimitWithTransactions() {
        double result = statService.checkMonthlyBudgetLimit();
        assertThat(result).isEqualTo(3200.0);
    }

    @Test
    @DisplayName("Проверка прогресса цели без транзакций")
    public void checkGoalProgressNoTransactions() {
        service.setGoal("1000.0");
        double result = statService.checkGoalProgress();
        assertThat(result).isEqualTo(1000.0);
    }


    @Test
    @DisplayName("Анализ расходов по категориям с транзакциями")
    public void analyzeExpenseByCategoriesWithTransactions() {
        Map<String, Double> result = statService.analyzeExpenseByCategories();
        assertThat(result).hasSize(2);
        assertThat(result).containsEntry("карамельный макиато", 250.0);
        assertThat(result).containsEntry("булочка с сосиской", 50.0);
    }


    @Test
    @DisplayName("Подсчёт баланса с транзакциями")
    public void calculateBalanceWithTransactions() {
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
    public void getIncomeExpenseForPeriodWithTransactions() {
        LocalDateTime start = (LocalDateTime.of(2000, 1, 1, 0, 0));
        LocalDateTime end = (LocalDateTime.of(2050, 1, 1, 0, 0));

        double[] result = statService.getIncomeExpenseForPeriod(start, end);
        assertThat(result).containsExactly(2500.0, 300.0, 2200.0);
    }

    @Test
    @DisplayName("Генерация финансового отчёта за полный период")
    public void generateGeneralReportFullPeriod() {
        TransactionStatsService.FinancialReport result = statService.generateGeneralReport(null, null);
        assertThat(result.totalIncome()).isEqualTo(2500.0);
        assertThat(result.totalExpense()).isEqualTo(300.0);
        assertThat(result.totalBalance()).isEqualTo(2200.0);
        assertThat(result.categoryReport()).hasSize(3);
        assertThat(result.categoryReport().get("стипендия")[0]).isEqualTo(2500.0);
        assertThat(result.categoryReport().get("карамельный макиато")[1]).isEqualTo(250.0);
    }

    @Test
    @DisplayName("Попытка создать транзакцию с описанием длиннее 200 символов")
    public void createTransactionWithTooLongDescription() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);
        TransactionService service = new TransactionService(repositoryMock, user);

        String longDescription = "a".repeat(201);

        doThrow(new SQLException("String data right truncation", "22001"))
                .when(repositoryMock).createTransaction(any(Transaction.class));

        TransactionService.ParseResponseDTO result = service.createTransaction(1, "1000", "подарок", longDescription);
        assertThat(result.success).isFalse();
        assertThat(result.content).startsWith("Слишком длинная категория или описание: ");
        assertThat(result.content).endsWith("Попробуйте ещё раз!");
    }

    @Test
    @DisplayName("Попытка обновить транзакцию с описанием длиннее 200 символов")
    public void updateTransactionWithTooLongDescription() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);
        TransactionService service = new TransactionService(repositoryMock, user);

        String longDescription = "a".repeat(201);

        doThrow(new SQLException("String data right truncation", "22001"))
                .when(repositoryMock).updateTransactionDescription(any(String.class), any(Transaction.class));
        when(repositoryMock.getAllTransactions()).thenReturn(new ArrayList<>(List.of(new Transaction(2, 300.0, "placeholderCateg", ""))));

        TransactionService.ParseResponseDTO result = service.updateTransactionDescription(longDescription, service.getAllTransactions().get(0));
        assertThat(result.success).isFalse();
        assertThat(result.content).isEqualTo("Описание не должно превышать 200 символов! Попробуйте ещё раз!");
    }

    @Test
    @DisplayName("Проверка месячного остатка с отрицательным остатком")
    public void checkMonthlyBudgetBelow0() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);
        TransactionStatsService statsServiceMock = mock(TransactionStatsService.class);
        TransactionService service = new TransactionService(repositoryMock, user);


        doReturn(-200.0).when(statsServiceMock).checkMonthlyBudgetLimit();

        when(repositoryMock.getMonthlyBudget()).thenReturn(100.0);
        when(repositoryMock.getTransactionsByType(2)).thenReturn(new ArrayList<>(List.of(new Transaction(2, 300.0, "placeholderCateg", ""))));
        when(repositoryMock.getTransactionsByType(1)).thenReturn(new ArrayList<>(List.of()));

        service.setStatsService(statsServiceMock);
        String result = service.checkMonthlyBudgetLimit();

        assertThat(result).isEqualTo("Вы превысили лимит на месяц на 200,00 руб.!");
    }

    @Test
    @DisplayName("Проверка месячного остатка с нулевым остатком")
    public void checkMonthlyBudget0() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);
        TransactionStatsService statsServiceMock = mock(TransactionStatsService.class);
        TransactionService service = new TransactionService(repositoryMock, user);


        doReturn(0.0).when(statsServiceMock).checkMonthlyBudgetLimit();

        when(repositoryMock.getMonthlyBudget()).thenReturn(1000.0);
        when(repositoryMock.getTransactionsByType(2)).thenReturn(new ArrayList<>(List.of(new Transaction(2, 1000.0, "placeholderCateg", ""))));
        when(repositoryMock.getTransactionsByType(1)).thenReturn(new ArrayList<>(List.of()));

        service.setStatsService(statsServiceMock);
        String result = service.checkMonthlyBudgetLimit();

        assertThat(result).isEqualTo("Ваш остаток: 0,00 руб.");
    }

    @Test
    @DisplayName("Проверка месячного остатка с ненулевым остатком")
    public void checkMonthlyBudgetPositive() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);
        TransactionStatsService statsServiceMock = mock(TransactionStatsService.class);
        TransactionService service = new TransactionService(repositoryMock, user);


        doReturn(200.0).when(statsServiceMock).checkMonthlyBudgetLimit();

        when(repositoryMock.getMonthlyBudget()).thenReturn(1200.0);
        when(repositoryMock.getTransactionsByType(2)).thenReturn(new ArrayList<>(List.of(new Transaction(2, 1000.0, "placeholderCateg", ""))));
        when(repositoryMock.getTransactionsByType(1)).thenReturn(new ArrayList<>(List.of()));

        service.setStatsService(statsServiceMock);
        String result = service.checkMonthlyBudgetLimit();
        assertThat(result).isEqualTo("Ваш остаток: 200,00 руб. Продолжайте в том же духе!");
    }

    @Test
    @DisplayName("Пустой список транзакций по цели")
    void calculateGoalDataEmptyTransactions() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);

        when(repositoryMock.getTransactionsByCategory("цель")).thenReturn(new ArrayList<>(List.of()));
        double[] result = statService.calculateGoalData();
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Непустой список транзакций по цели")
    void calculateGoalDataSuccess() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);
        TransactionStatsService statsServiceMock = new TransactionStatsService(repositoryMock, user);
        when(repositoryMock.getGoal()).thenReturn(1200.0);
        when(repositoryMock.getTransactionsByCategory("цель")).thenReturn(new ArrayList<>(List.of(new Transaction(1, 1000.0, "цель", ""),
                new Transaction(2, 200.0, "цель", ""))));
        double[] result = statsServiceMock.calculateGoalData();

        assertThat(result).hasSize(5);
        assertThat(result[0]).isEqualTo(1200.0);
        assertThat(result[1]).isEqualTo(1000.0);
        assertThat(result[2]).isEqualTo(200.0);
        assertThat(result[3]).isEqualTo(800.0);
        assertThat(result[4]).isEqualTo(400.0);
    }

    @Test
    @DisplayName("Прогресс по неустановленной цели")
    void getGoalProgressNoGoal() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);
        TransactionStatsService statsServiceMock = new TransactionStatsService(repositoryMock, user);


        when(repositoryMock.getGoal()).thenReturn(0.0);

        String result = statsServiceMock.getGoalProgress();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Достижение цели")
    void getGoalProgressGoalReached() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);
        TransactionStatsService statsServiceMock = spy(new TransactionStatsService(repositoryMock, user));

        doReturn(500.0).when(repositoryMock).getGoal();
        doReturn(0.0).when(statsServiceMock).checkGoalProgress();

        String result = statsServiceMock.getGoalProgress();
        assertThat(result).isEqualTo("Поздравляем! Вы достигли своей цели!");
    }

    @Test
    @DisplayName("Превышение цели")
    void getGoalProgressGoalExceeded() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);
        TransactionStatsService statsServiceMock = spy(new TransactionStatsService(repositoryMock, user));

        when(repositoryMock.getGoal()).thenReturn(500.0);
        when(statsServiceMock.checkGoalProgress()).thenReturn(-200.0);

        String result = statsServiceMock.getGoalProgress();
        assertThat(result).isEqualTo("Поздравляем! Вы превысили цель на 200,00 руб.!");
    }

    @Test
    @DisplayName("Получение остатка до цели")
    void getGoalProgressLeftToSave() throws SQLException {
        TransactionRepository repositoryMock = mock(TransactionRepository.class);
        TransactionStatsService statsServiceMock = spy(new TransactionStatsService(repositoryMock, user));

        when(repositoryMock.getGoal()).thenReturn(500.0);
        when(statsServiceMock.checkGoalProgress()).thenReturn(300.0);

        String result = statsServiceMock.getGoalProgress();
        assertThat(result).isEqualTo("До цели осталось накопить 300,00 руб. Отличный результат!");
    }

    @Test
    @DisplayName("Превышение остатка на месяц")
    void overgoMonthlyBudget() {
        String result = service.notifyAboutMonthlyLimit(500.0);
        assertThat(result).contains("Внимание! Вы превысили установленный месячный бюджет на 500,00 руб.");
        assertThat(result).contains("Подробности отправлены по адресу anya@ya.ru");
    }
}