package org.ylabHomework.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.ylabHomework.serviceClasses.security.UserDetailsImpl;
import org.ylabHomework.services.TransactionStatisticsService;

import java.time.LocalDateTime;

@RestController
@Tag(name = "API для получения статистики: финансовый отчет, баланс, расходы и доходы за период")
@RequestMapping("/api/v1/transactionStatistics")
@RequiredArgsConstructor
public class TransactionStatisticsController {
    private final TransactionStatisticsService transactionStatisticsService;


    @Operation(
        summary = "Возвращает баланс (лимит на месяц+доходы-расходы) за период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ с балансом за период"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса - неверный формат даты"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BalanceDTO> getBalanceForPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimestamp,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimestamp,
                                                          @AuthenticationPrincipal UserDetailsImpl currentUser) {
        BalanceDTO balanceDTO = transactionStatisticsService.getBalanceForPeriod(currentUser.getId(), new PeriodDTO(startTimestamp, endTimestamp));
        return ResponseEntity.ok(balanceDTO);
    }

    @Operation(
        summary = "Возвращает сумму доходов за период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ с доходы за период"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса - неверный формат даты"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/incomes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<IncomesDTO> getIncomesForPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimestamp,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimestamp,
                                                          @AuthenticationPrincipal UserDetailsImpl currentUser) {
        IncomesDTO incomesDTO = transactionStatisticsService.getIncomesForPeriod(currentUser.getId(), new PeriodDTO(startTimestamp, endTimestamp));
        return ResponseEntity.ok(incomesDTO);
    }

    @Operation(
        summary = "Возвращает сумму расходов за период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Расходы за период"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса - неверный формат даты"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/expenses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ExpensesDTO> getExpensesForPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimestamp,
                                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimestamp,
                                                            @AuthenticationPrincipal UserDetailsImpl currentUser) {
        ExpensesDTO expensesDTO = transactionStatisticsService.getExpensesForPeriod(currentUser.getId(), new PeriodDTO(startTimestamp, endTimestamp));
        return ResponseEntity.ok(expensesDTO);
    }

    @Operation(
        summary = "Возвращает финансовый отчет (доходы и расходы по категориям) за период")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Финансовый отчет за период"),
        @ApiResponse(responseCode = "400", description = "Некорректные параметры запроса - неверный формат даты"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping("/report")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReportDTO> getReportForPeriod(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTimestamp,
                                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTimestamp,
                                                        @AuthenticationPrincipal UserDetailsImpl currentUser) {
        ReportDTO reportDTO = transactionStatisticsService.getReportForPeriod(currentUser.getId(), new PeriodDTO(startTimestamp, endTimestamp));
        return ResponseEntity.ok(reportDTO);
    }
}