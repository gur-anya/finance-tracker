package org.ylabHomework.unitTests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.*;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.services.TransactionStatisticsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionStatisticsServiceTest {
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private TransactionStatisticsService transactionStatisticsService;

    @Test
    public void getBalanceForPeriod_shouldReturnZeroBalance_whenRepositoryReturnsEmptyOptional() {
        Long userId = 1L;
        PeriodDTO period = new PeriodDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(transactionRepository.getBalanceForPeriod(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Optional.empty());


        BalanceDTO result = transactionStatisticsService.getBalanceForPeriod(userId, period);

        assertThat(result).isNotNull();
        assertThat(result.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    public void getIncomesForPeriod_shouldReturnZeroIncomes_whenRepositoryReturnsEmptyOptional() {
        Long userId = 1L;
        PeriodDTO period = new PeriodDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(transactionRepository.getIncomesForPeriod(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Optional.empty());


        IncomesDTO result = transactionStatisticsService.getIncomesForPeriod(userId, period);

        assertThat(result).isNotNull();
        assertThat(result.getIncomes()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    public void getExpensesForPeriod_shouldReturnZeroExpenses_whenRepositoryReturnsEmptyOptional() {
        Long userId = 1L;
        PeriodDTO period = new PeriodDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(transactionRepository.getExpensesForPeriod(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
            .thenReturn(Optional.empty());


        ExpensesDTO result = transactionStatisticsService.getExpensesForPeriod(userId, period);

        assertThat(result).isNotNull();
        assertThat(result.getExpenses()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    public void getReportForPeriod_shouldReturnEmptyLists_whenRepositoryReturnsEmptyOptional() {
        Long userId = 1L;
        LocalDateTime start = LocalDateTime.now().minusWeeks(2);
        LocalDateTime end = LocalDateTime.now();
        PeriodDTO period = new PeriodDTO(start, end);

        List<CategoryStatDTO> mockedIncomes = List.of();
        List<CategoryStatDTO> mockedExpenses = List.of();


        when(transactionRepository.getIncomesForPeriodGroupByCategories(userId, start, end))
            .thenReturn(mockedIncomes);
        when(transactionRepository.getExpensesForPeriodGroupByCategories(userId, start, end))
            .thenReturn(mockedExpenses);

        ReportDTO actualReport = transactionStatisticsService.getReportForPeriod(userId, period);
        assertThat(actualReport).isNotNull();

        assertThat(actualReport.getIncomesGrouped()).hasSize(0);
        assertThat(actualReport.getExpensesGrouped()).hasSize(0);

        assertThat(actualReport.getIncomesGrouped()).containsExactlyInAnyOrderElementsOf(mockedIncomes);
        assertThat(actualReport.getExpensesGrouped()).containsExactlyInAnyOrderElementsOf(mockedExpenses);
    }

    @Test
    public void getBalanceForPeriod_shouldPropagateException_whenRepositoryThrowsException() {
        Long userId = 1L;
        PeriodDTO period = new PeriodDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(transactionRepository.getBalanceForPeriod(anyLong(), any(), any()))
            .thenThrow(new DataAccessException("Database connection failed") {
            });

        assertThrows(DataAccessException.class, () -> transactionStatisticsService.getBalanceForPeriod(userId, period));
    }

    @Test
    public void getIncomesForPeriod_shouldPropagateException_whenRepositoryThrowsException() {
        Long userId = 1L;
        PeriodDTO period = new PeriodDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(transactionRepository.getIncomesForPeriod(anyLong(), any(), any()))
            .thenThrow(new DataAccessException("Database connection failed") {
            });

        assertThrows(DataAccessException.class, () -> transactionStatisticsService.getIncomesForPeriod(userId, period));
    }

    @Test
    public void getExpensesForPeriod_shouldPropagateException_whenRepositoryThrowsException() {
        Long userId = 1L;
        PeriodDTO period = new PeriodDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(transactionRepository.getExpensesForPeriod(anyLong(), any(), any()))
            .thenThrow(new DataAccessException("Database connection failed") {
            });

        assertThrows(DataAccessException.class, () -> transactionStatisticsService.getExpensesForPeriod(userId, period));
    }

    @Test
    public void getReportForPeriod_shouldPropagateException_whenRepositoryThrowsException() {
        Long userId = 1L;
        PeriodDTO period = new PeriodDTO(LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        when(transactionRepository.getIncomesForPeriodGroupByCategories(anyLong(), any(), any()))
            .thenThrow(new DataAccessException("Database connection failed") {
            });

        assertThrows(DataAccessException.class, () -> transactionStatisticsService.getReportForPeriod(userId, period));
    }
}

