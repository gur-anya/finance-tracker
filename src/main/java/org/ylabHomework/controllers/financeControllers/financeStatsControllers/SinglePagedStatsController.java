package org.ylabHomework.controllers.financeControllers.financeStatsControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ylabHomework.DTOs.TransactionsDTOs.SingleParamDTO;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Api(value = "API статистик, отображемых на едиственной странице без возможности совершения дополнительных действий (баланс, общие расходы по категориям)")
@Controller
@RequiredArgsConstructor
public class SinglePagedStatsController {
    private final TransactionStatsService transactionStatsService;

    @ApiOperation(value = "Получить текущий баланс пользователя",
            notes = "Возвращает баланс пользователя")
    @ApiResponse(code = 200, message = "Полученный баланс", response = SingleParamDTO.class)
    @GetMapping(value = "/get_balance")
    @ResponseBody
    public ResponseEntity<SingleParamDTO> getBalance(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        SingleParamDTO paramDTO = new SingleParamDTO();
        String balance = transactionStatsService.calculateBalance(user);
        paramDTO.setParam(balance);
        return ResponseEntity.ok().body(paramDTO);
    }

    @ApiOperation(value = "Получить расходы по категориям",
            notes = "Возвращает карту расходов пользователя, разбитых по категориям")
    @ApiResponse(code = 200, message = "Расходы по категориям", response = Map.class)
    @GetMapping(value = "/get_expenses_by_category")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getExpensesByCategory(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        Map<String, Double> responseData = transactionStatsService.analyzeExpenseByCategories(user);
        return ResponseEntity.ok().body(responseData);
    }
}
