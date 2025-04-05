package org.ylabHomework.controllers.financeControllers.financeManagementControllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ylabHomework.mappers.TransactionsMappers.TransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "API демонстрации транзакций с фильтром")
@Controller
@RequiredArgsConstructor
public class FilterTransactionsController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;


    @Operation(
            summary = "Просмотреть транзакции с фильтром",
            description = "Показывает транзакции, отфильтраванные по одному из параметров (до/после даты, по типу, по катории, без фильтра")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Отфильтрованные транзакции. Содержит список транзакций или сообщение о том, что транзакции с этим фильтром не найдены."),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных. Сообщение содержит описание ошибки при валидации.")
    })
    @PostMapping(value = "/show_transactions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> filterTransactions(@RequestBody Map<String, String> jsonData, HttpSession session) {


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String filter = jsonData.get("filter");
        User user = (User) session.getAttribute("loggedUser");
        List<Transaction> transactions;
        String stateMessage;
        Map<String, Object> response = new HashMap<>();
        switch (filter) {
            case "1":
                String beforeDateStr = jsonData.get("beforeDate");
                if (beforeDateStr != null && !beforeDateStr.isEmpty()) {
                    LocalDateTime beforeDate = LocalDateTime.parse(beforeDateStr, formatter);
                    transactions = transactionService.getTransactionsBeforeTimestamp(user, beforeDate);
                    if (transactions.isEmpty()) {
                        stateMessage = "Транзакции до указанной даты не найдены!";
                        response.put("message", stateMessage);
                        return ResponseEntity.ok().body(response);
                    }
                } else {
                    stateMessage = "Укажите дату для фильтрации!";
                    response.put("message", stateMessage);
                    return ResponseEntity.badRequest().body(response);
                }

                break;
            case "2":
                String afterDateStr = jsonData.get("afterDate");
                if (afterDateStr != null && !afterDateStr.isEmpty()) {
                    LocalDateTime afterDate = LocalDateTime.parse(afterDateStr, formatter);
                    transactions = transactionService.getTransactionsAfterTimestamp(user, afterDate);
                    if (transactions.isEmpty()) {
                        stateMessage = "Транзакции после указанной даты не найдены!";
                        response.put("message", stateMessage);
                        return ResponseEntity.ok().body(response);
                    }
                } else {
                    stateMessage = "Укажите дату для фильтрации!";
                    response.put("message", stateMessage);
                    return ResponseEntity.badRequest().body(response);
                }
                break;
            case "3":
                String category = jsonData.get("category");
                if (category != null && !category.isEmpty()) {
                    transactions = transactionService.getTransactionsByCategory(user, category);
                    if (transactions.isEmpty()) {
                        if (category.trim().equalsIgnoreCase("цель") && transactionService.getGoal(user) == 0.0) {
                            stateMessage = "Транзакции по категории '" + category + "' не найдены! Установить цель можно в разделе Статистика.";
                        } else {
                            stateMessage = "Транзакции по категории '" + category + "' не найдены!";
                        }
                        response.put("message", stateMessage);
                        return ResponseEntity.ok().body(response);
                    }
                } else {
                    stateMessage = "Укажите категорию для фильтрации!";
                    response.put("message", stateMessage);
                    return ResponseEntity.badRequest().body(response);
                }
                break;
            case "41":
                transactions = transactionService.getTransactionsByType(user, 1);
                if (transactions.isEmpty()) {
                    response.put("message", "Доходы не найдены!");
                    return ResponseEntity.ok().body(response);
                }
                break;
            case "42":
                transactions = transactionService.getTransactionsByType(user, 2);
                if (transactions.isEmpty()) {
                    response.put("message", "Расходы не найдены!");
                    return ResponseEntity.ok().body(response);
                }
                break;
            case "5":
                transactions = transactionService.getAllTransactions(user);
                if (transactions.isEmpty()) {
                    response.put("message", "Транзакции отсутствуют!");
                    return ResponseEntity.ok().body(response);
                }
                break;
            default:
                response.put("message", "Неверный фильтр!");
                return ResponseEntity.badRequest().body(response);
        }

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("transactions", transactionMapper.toDTOList(transactions));

        return ResponseEntity.ok().body(responseData);
    }
}
