package org.ylabHomework.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.*;
import org.ylabHomework.serviceClasses.springConfigs.security.UserDetailsImpl;
import org.ylabHomework.services.TransactionStatisticsService;

@RestController
@RequestMapping("/api/v1/transactionStatistics")
@RequiredArgsConstructor
public class TransactionStatisticsController {
    private final TransactionStatisticsService transactionStatisticsService;

    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BalanceDTO> getBalanceForPeriod(@Valid PeriodDTO periodDTO, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        BalanceDTO balanceDTO = transactionStatisticsService.getBalanceForPeriod(currentUser.getId(), periodDTO);
        return ResponseEntity.ok(balanceDTO);
    }

    @GetMapping("/incomes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<IncomesDTO> getIncomesForPeriod(@Valid PeriodDTO periodDTO, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        IncomesDTO incomesDTO = transactionStatisticsService.getIncomesForPeriod(currentUser.getId(), periodDTO);
        return ResponseEntity.ok(incomesDTO);
    }

    @GetMapping("/expenses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExpensesDTO> getExpensesForPeriod(@Valid PeriodDTO periodDTO, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        ExpensesDTO expensesDTO = transactionStatisticsService.getExpensesForPeriod(currentUser.getId(), periodDTO);
        return ResponseEntity.ok(expensesDTO);
    }

    @GetMapping("/report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReportDTO> getReportForPeriod(@Valid PeriodDTO periodDTO, @AuthenticationPrincipal UserDetailsImpl currentUser) {
        ReportDTO reportDTO = transactionStatisticsService.getReportForPeriod(currentUser.getId(), periodDTO);
        return ResponseEntity.ok(reportDTO);
    }
}