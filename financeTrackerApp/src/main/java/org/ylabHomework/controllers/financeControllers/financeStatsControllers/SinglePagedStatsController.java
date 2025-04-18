package org.ylabHomework.controllers.financeControllers.financeStatsControllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ylabHomework.DTOs.TransactionsDTOs.SingleParamDTO;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;

import java.util.Map;

@Tag(name = "API статистик, отображемых на едиственной странице без возможности совершения дополнительных действий (баланс, общие расходы по категориям)")
@Controller
@RequiredArgsConstructor
public class SinglePagedStatsController {
    private final TransactionStatsService transactionStatsService;

    @Operation(
            summary = "Получить текущий баланс пользователя",
            description = "Возвращает баланс пользователя")
    @ApiResponse(responseCode = "200", description = "Полученный баланс")
    @GetMapping(value = "/get_balance")
    @ResponseBody
    public ResponseEntity<SingleParamDTO> getBalance(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        SingleParamDTO paramDTO = new SingleParamDTO();
        String balance = transactionStatsService.calculateBalance(user);
        paramDTO.setParam(balance);
        return ResponseEntity.ok().body(paramDTO);
    }

    @Operation(
            summary = "Получить расходы по категориям",
            description = "Возвращает карту расходов пользователя, разбитых по категориям")
    @ApiResponse(responseCode = "200", description = "Расходы по категориям")
    @GetMapping(value = "/get_expenses_by_category")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getExpensesByCategory(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        Map<String, Double> responseData = transactionStatsService.analyzeExpenseByCategories(user);
        return ResponseEntity.ok().body(responseData);
    }
}
