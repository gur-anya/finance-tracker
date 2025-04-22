package org.ylabHomework.controllers.financeControllers.financeStatsControllers.financeControllers.financeStatsControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.CategoryExpensesDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.ControllerBalanceDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.IncomeExpenseSummaryDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.ReportDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.serviceDTOs.PeriodDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.serviceDTOs.ServiceBalanceDTO;
import org.ylabHomework.mappers.TransactionsMappers.CategoryExpensesMapper;
import org.ylabHomework.mappers.TransactionsMappers.IncomeExpenseSummaryMapper;
import org.ylabHomework.mappers.TransactionsMappers.ReportMapper;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;
import org.ylabHomework.services.UserService;

import java.util.Map;
@Tag(name = "API получения статистики: финансовый отчет, баланс, расходы по категориям, расходы и доходы за период")
@RestController("/transactions/stats")
@RequiredArgsConstructor
public class GenericStatsController {
    private final UserService userService;
    private final ReportMapper reportMapper;
    private final CategoryExpensesMapper categoryExpensesMapper;
    private final IncomeExpenseSummaryMapper incomeExpenseSummaryMapper;
    private final TransactionStatsService transactionStatsService;

    @Operation(
            summary = "Демонстрирует финансовый отчет")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Финансовый отчет, содержащий данные об общих доходах, расходах, балансе, расходах по категориям и цели за период."),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping("/report")
    public ResponseEntity<?> getGeneralReport(@RequestBody PeriodDTO periodDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            int userId = user.getId();

            TransactionStatsService.FinancialReport report = transactionStatsService.buildFinancialReport(userId, periodDTO);
            ReportDTO responseDTO = reportMapper.toReportResponseDTO(report);

            if (report.totalIncome == 0 && report.totalExpense == 0) {
                return ResponseEntity.ok()
                        .body(new ResponseMessageDTO("Транзакции за период не найдены!"));
            }

            return ResponseEntity.ok().body(responseDTO);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }


    @Operation(
            summary = "Получить текущий баланс пользователя",
            description = "Возвращает баланс пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Полученный баланс"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(value = "/get/balance")
    public ResponseEntity<?> getBalance() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            int userId = user.getId();
            ServiceBalanceDTO serviceBalanceDTO = transactionStatsService.getCurrentBalance(userId);
            if (serviceBalanceDTO.isEmpty()) {
                return ResponseEntity.ok()
                        .body(new ResponseMessageDTO("Транзакции не найдены!"));
            }
            return ResponseEntity.ok().body(new ControllerBalanceDTO(serviceBalanceDTO.getBalance()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }


    @Operation(
            summary = "Получить расходы по категориям",
            description = "Возвращает карту расходов пользователя, разбитых по категориям")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Расходы по категориям"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping(value = "/get/categoryExpenses")
    public ResponseEntity<?> getExpensesByCategory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            int userId = user.getId();
            Map<String, Double> result = transactionStatsService.getExpensesByCategory(userId);
            CategoryExpensesDTO categoryExpensesDTO = categoryExpensesMapper.toDTO(result);
            if (categoryExpensesDTO.getCategoryExpenses() == null) {
                return ResponseEntity.ok()
                        .body(new ResponseMessageDTO("Расходы не найдены!"));
            }
            return ResponseEntity.ok().body(new CategoryExpensesDTO(
                    categoryExpensesDTO.getCategoryExpenses()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }


    @Operation(
            summary = "Получить доходы, расходы и баланс за период",
            description = "Возвращает доходы, расходы и баланс за указанный период")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Доходы, расходы и баланс за период"),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping(value = "/incomeExpenseSummary")
    public ResponseEntity<?> getIncomeExpenseForPeriod(@RequestBody PeriodDTO periodDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            int userId = user.getId();
            double[] result = transactionStatsService.getPeriodIncomeExpenseSummary(userId, periodDTO);
            IncomeExpenseSummaryDTO incomeExpenseSummaryDTO = incomeExpenseSummaryMapper.toDTO(result);
            if (incomeExpenseSummaryDTO.getIncome() == 0.0 &&
                    incomeExpenseSummaryDTO.getExpense() == 0.0 &&
                    incomeExpenseSummaryDTO.getBalance() == 0.0) {
                return ResponseEntity.ok()
                        .body(new ResponseMessageDTO("Транзакции за период не найдены!"));
            }
            return ResponseEntity.ok().body(incomeExpenseSummaryDTO);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }
}
