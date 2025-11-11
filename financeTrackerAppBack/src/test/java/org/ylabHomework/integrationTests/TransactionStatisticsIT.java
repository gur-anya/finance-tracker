package org.ylabHomework.integrationTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.*;
import org.ylabHomework.controllers.AuthController;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.repositories.UserRepository;
import org.ylabHomework.serviceClasses.enums.CategoryEnum;
import org.ylabHomework.serviceClasses.enums.RoleEnum;
import org.ylabHomework.serviceClasses.enums.TypeEnum;
import org.ylabHomework.services.KafkaProducer;
import org.ylabHomework.services.TokenService;
import org.ylabHomework.services.TransactionStatisticsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Testcontainers
public class TransactionStatisticsIT {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");
    @Autowired
    private TransactionStatisticsService transactionStatisticsService;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;
    @MockBean
    private KafkaProducer kafkaProducer;
    @MockBean
    private TokenService tokenService;
    @MockBean
    private AuthController authController;
    private User transactionsOwner;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("jwt.secret", () -> "test-secret-for-integration-tests");
        registry.add("frontend.url", () -> "http://localhost:3000");
    }

    @BeforeEach
    void setup() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();

        User transactionOwner = new User("test", "test@example.com", "somePass", RoleEnum.USER, true, BigDecimal.ZERO, "cool goal", BigDecimal.valueOf(100000), null);

        Transaction transaction1 = new Transaction(TypeEnum.INCOME, BigDecimal.valueOf(100), CategoryEnum.GIFT, "", transactionOwner, LocalDateTime.now().minusDays(5));
        Transaction transaction2 = new Transaction(TypeEnum.INCOME, BigDecimal.valueOf(50000), CategoryEnum.WAGE, "", transactionOwner, LocalDateTime.now().minusWeeks(3));
        Transaction transaction3 = new Transaction(TypeEnum.EXPENSE, BigDecimal.valueOf(1500), CategoryEnum.FOOD, "", transactionOwner, LocalDateTime.now().minusDays(3));
        Transaction transaction4 = new Transaction(TypeEnum.EXPENSE, BigDecimal.valueOf(800), CategoryEnum.OTHER, "", transactionOwner, LocalDateTime.now().minusDays(2));
        Transaction transaction5 = new Transaction(TypeEnum.EXPENSE, BigDecimal.valueOf(100), CategoryEnum.TRANSPORT, "", transactionOwner, LocalDateTime.now().minusWeeks(1));

        this.transactionsOwner = userRepository.save(transactionOwner);
        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);
        transactionRepository.save(transaction5);
    }


    @Test
    void getIncomesForPeriod_shouldReturnCorrectSum() {
        PeriodDTO period = new PeriodDTO(LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusDays(1));
        IncomesDTO result = transactionStatisticsService.getIncomesForPeriod(transactionsOwner.getId(), period);

        assertThat(result).isNotNull();
        assertThat(result.getIncomes()).isEqualByComparingTo(BigDecimal.valueOf(50100));
    }

    @Test
    void getExpensesForPeriod_shouldReturnCorrectSum() {
        PeriodDTO period = new PeriodDTO(LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusDays(1));

        ExpensesDTO result = transactionStatisticsService.getExpensesForPeriod(transactionsOwner.getId(), period);

        assertThat(result).isNotNull();
        assertThat(result.getExpenses()).isEqualByComparingTo(BigDecimal.valueOf(2400));
    }

    @Test
    void getBalanceForPeriod_shouldReturnCorrectBalance() {
        PeriodDTO period = new PeriodDTO(LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusDays(1));

        BalanceDTO result = transactionStatisticsService.getBalanceForPeriod(transactionsOwner.getId(), period);

        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(47700));
    }

    @Test
    void getBalanceForPeriod_whenNoTransactions_shouldReturnZero() {
        PeriodDTO futurePeriod = new PeriodDTO(LocalDateTime.now().plusYears(1), LocalDateTime.now().plusYears(2));

        BalanceDTO result = transactionStatisticsService.getBalanceForPeriod(transactionsOwner.getId(), futurePeriod);

        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getIncomesForPeriod_whenUserDoesNotExist_shouldReturnZero() {
        Long nonExistentUserId = transactionsOwner.getId() + 999L;
        PeriodDTO period = new PeriodDTO(LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusDays(1));

        IncomesDTO result = transactionStatisticsService.getIncomesForPeriod(nonExistentUserId, period);

        assertThat(result).isNotNull();
        assertThat(result.getIncomes()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getExpensesForPeriod_whenNoTransactions_shouldReturnZero() {
        PeriodDTO futurePeriod = new PeriodDTO(LocalDateTime.now().plusYears(1), LocalDateTime.now().plusYears(2));

        ExpensesDTO result = transactionStatisticsService.getExpensesForPeriod(transactionsOwner.getId(), futurePeriod);

        assertThat(result).isNotNull();
        assertThat(result.getExpenses()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void getReportForPeriod_whenUserDoesNotExist_shouldReturnEmptyReport() {
        Long nonExistentUserId = transactionsOwner.getId() + 999L;
        PeriodDTO period = new PeriodDTO(LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusDays(1));

        ReportDTO report = transactionStatisticsService.getReportForPeriod(nonExistentUserId, period);

        assertThat(report).isNotNull();
        assertThat(report.getIncomesGrouped()).isNotNull().isEmpty();
        assertThat(report.getExpensesGrouped()).isNotNull().isEmpty();
    }

    @Test
    void getReportForPeriod_shouldReturnGroupedData() {
        PeriodDTO period = new PeriodDTO(LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusDays(1));

        ReportDTO report = transactionStatisticsService.getReportForPeriod(transactionsOwner.getId(), period);

        assertThat(report).isNotNull();

        List<CategoryStatDTO> incomesGrouped = report.getIncomesGrouped();
        assertThat(incomesGrouped).hasSize(2);

        assertThat(incomesGrouped)
            .anySatisfy(stat -> {
                assertThat(stat.getCategory()).isEqualTo(CategoryEnum.GIFT);
                assertThat(stat.getSum()).isEqualByComparingTo(BigDecimal.valueOf(100));
            })
            .anySatisfy(stat -> {
                assertThat(stat.getCategory()).isEqualTo(CategoryEnum.WAGE);
                assertThat(stat.getSum()).isEqualByComparingTo(BigDecimal.valueOf(50000));
            });

        List<CategoryStatDTO> expensesGrouped = report.getExpensesGrouped();
        assertThat(expensesGrouped).hasSize(3);

        assertThat(expensesGrouped)
            .anySatisfy(stat -> {
                assertThat(stat.getCategory()).isEqualTo(CategoryEnum.FOOD);
                assertThat(stat.getSum()).isEqualByComparingTo(BigDecimal.valueOf(1500));
            })
            .anySatisfy(stat -> {
                assertThat(stat.getCategory()).isEqualTo(CategoryEnum.OTHER);
                assertThat(stat.getSum()).isEqualByComparingTo(BigDecimal.valueOf(800));
            })
            .anySatisfy(stat -> {
                assertThat(stat.getCategory()).isEqualTo(CategoryEnum.TRANSPORT);
                assertThat(stat.getSum()).isEqualByComparingTo(BigDecimal.valueOf(100));
            });
    }
}
