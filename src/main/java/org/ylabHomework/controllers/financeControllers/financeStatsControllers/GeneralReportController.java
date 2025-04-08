package org.ylabHomework.controllers.financeControllers.financeStatsControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionStatsService;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
@Api(value = "API финансового отчета")
@Controller
@RequiredArgsConstructor
public class GeneralReportController {
    private final TransactionStatsService transactionStatsService;


    @ApiOperation(value = "Демонстрирует финансовый отчет")
    @ApiResponse(code = 200, message = "Финансовый отчет, содержащий данные об общих доходах, расходах, балансе, расходах по категориям и цели за период.", response = Map.class)
    @PostMapping(value = "/general_report")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> generateGeneralReport(@RequestBody Map<String, String> jsonData, HttpSession session) {
            User user = (User) session.getAttribute("loggedUser");
            String start = jsonData.get("start");
            String end = jsonData.get("end");

            LocalDateTime startTime = null;
            LocalDateTime endTime = null;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            if (!start.isEmpty()) {
                startTime = LocalDateTime.parse(start, formatter);
            }
            if (!end.isEmpty()) {
                endTime = LocalDateTime.parse(end, formatter);
            }

            Map<String, Object> responseData = new HashMap<>();
            TransactionStatsService.FinancialReport report = transactionStatsService.generateGeneralReport(user, startTime, endTime);

            if (report == null) {
                Map<String, Double> basicStats = new HashMap<>();
                basicStats.put("totalIncome", 0.0);
                basicStats.put("totalExpense", 0.0);
                basicStats.put("totalBalance", 0.0);
                responseData.put("basicStats", basicStats);
                responseData.put("categoryReport", new HashMap<>());
                Map<String, Double> goalData = new HashMap<>();
                goalData.put("goalSum", 0.0);
                goalData.put("goalIncome", 0.0);
                goalData.put("goalExpense", 0.0);
                goalData.put("saved", 0.0);
                goalData.put("left", 0.0);
                responseData.put("goalData", goalData);
            } else {
                Map<String, Double> basicStats = new HashMap<>();
                basicStats.put("totalIncome", report.totalIncome);
                basicStats.put("totalExpense", report.totalExpense);
                basicStats.put("totalBalance", report.totalBalance);

                responseData.put("basicStats", basicStats);
                responseData.put("categoryReport", report.categoryReport);

                Map<String, Double> goalData = new HashMap<>();
                goalData.put("goalSum", report.goalData[0]);
                goalData.put("goalIncome", report.goalData[1]);
                goalData.put("goalExpense", report.goalData[2]);
                goalData.put("saved", report.goalData[3]);
                goalData.put("left", report.goalData[4]);

                responseData.put("goalData", goalData);
            }

            return ResponseEntity.ok().body(responseData);
        }
    }
