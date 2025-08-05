package org.ylabHomework.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.*;
import org.ylabHomework.serviceClasses.springConfigs.security.UserDetailsImpl;
import org.ylabHomework.services.TransactionStatisticsService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/transactionStatistics")
@RequiredArgsConstructor
public class TransactionStatisticsController {
    private final TransactionStatisticsService transactionStatisticsService;

    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BalanceDTO> getBalanceForPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimestamp,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimestamp,
                                                          @AuthenticationPrincipal UserDetailsImpl currentUser) {
        BalanceDTO balanceDTO = transactionStatisticsService.getBalanceForPeriod(currentUser.getId(), new PeriodDTO(startTimestamp, endTimestamp));
        return ResponseEntity.ok(balanceDTO);
    }

    @GetMapping("/incomes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<IncomesDTO> getIncomesForPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimestamp,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimestamp,
                                                          @AuthenticationPrincipal UserDetailsImpl currentUser) {
        IncomesDTO incomesDTO = transactionStatisticsService.getIncomesForPeriod(currentUser.getId(), new PeriodDTO(startTimestamp, endTimestamp));
        return ResponseEntity.ok(incomesDTO);
    }

    @GetMapping("/expenses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExpensesDTO> getExpensesForPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimestamp,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimestamp,
                                                            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        ExpensesDTO expensesDTO = transactionStatisticsService.getExpensesForPeriod(currentUser.getId(), new PeriodDTO(startTimestamp, endTimestamp));
        return ResponseEntity.ok(expensesDTO);
    }

    @GetMapping("/report")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReportDTO> getReportForPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimestamp,
                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimestamp,
                                                        @AuthenticationPrincipal UserDetailsImpl currentUser) {
        ReportDTO reportDTO = transactionStatisticsService.getReportForPeriod(currentUser.getId(), new PeriodDTO(startTimestamp, endTimestamp));
        return ResponseEntity.ok(reportDTO);
    }
}