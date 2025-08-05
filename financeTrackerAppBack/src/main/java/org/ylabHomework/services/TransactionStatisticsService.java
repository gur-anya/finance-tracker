package org.ylabHomework.services;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.*;
import org.ylabHomework.repositories.TransactionRepository;
import org.ylabHomework.serviceClasses.customExceptions.ValueNotFoundException;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionStatisticsService {
    private final TransactionRepository transactionRepository;

    public BalanceDTO getBalanceForPeriod(Long userId, PeriodDTO period) {
        BigDecimal balance = transactionRepository.getBalanceForPeriod(userId, period.getStartTimestamp(), period.getEndTimestamp())
            .orElse(BigDecimal.ZERO);
        return new BalanceDTO(balance);
    }

    public IncomesDTO getIncomesForPeriod(Long userId, PeriodDTO period) {
        BigDecimal incomes = transactionRepository.getIncomesForPeriod(userId, period.getStartTimestamp(), period.getEndTimestamp())
            .orElse(BigDecimal.ZERO);
        return new IncomesDTO(incomes);
    }

    public ExpensesDTO getExpensesForPeriod(Long userId, PeriodDTO period) {
        BigDecimal expenses = transactionRepository.getExpensesForPeriod(userId, period.getStartTimestamp(), period.getEndTimestamp())
            .orElse(BigDecimal.ZERO);
        return new ExpensesDTO(expenses);
    }

    public ReportDTO getReportForPeriod(Long userId, PeriodDTO period) {
        List<CategoryStatDTO> incomesGrouped = transactionRepository.getIncomesForPeriodGroupByCategories(userId, period.getStartTimestamp(), period.getEndTimestamp());
        List<CategoryStatDTO> expensesGrouped = transactionRepository.getExpensesForPeriodGroupByCategories(userId, period.getStartTimestamp(), period.getEndTimestamp());
        return new ReportDTO(incomesGrouped, expensesGrouped);
    }
}

//TODO: понять, почему транзакции цели не влияют на цель; заинверсить баланс (старт=лимит, меньше 0 - плохо); понять почему доходы и расходы не вностся, использовать репорт