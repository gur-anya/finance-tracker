package org.ylabHomework.controllers.financeControllers.financeStatsControllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "API расходов, доходов, баланса за период")
@Controller
@RequiredArgsConstructor
public class SummaryIncomeExpenseController {
    private final TransactionStatsService transactionStatsService;

    @Operation(
            summary = "Получить доходы, расходы и баланс за период",
            description = "Возвращает доходы, расходы и баланс за указанный период в виде карты")
    @ApiResponse(responseCode = "200", description = "Доходы, расходы и баланс за период")
    @PostMapping(value = "/summary_income_expense")
    @ResponseBody
    public ResponseEntity<Map<String, Double>> getIncomeExpenseForPeriod(
            @RequestBody Map<String, String> jsonData,
            HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        String start = jsonData.get("start");
        String end = jsonData.get("end");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime startTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endTime = LocalDateTime.parse(end, formatter);

        double[] stats = transactionStatsService.getIncomeExpenseForPeriod(user, startTime, endTime);

        Map<String, Double> responseData = new HashMap<>();
        responseData.put("income", stats[0]);
        responseData.put("expense", stats[1]);
        responseData.put("balance", stats[2]);

        return ResponseEntity.ok().body(responseData);

    }
}
