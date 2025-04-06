package org.ylabHomework.controllers.financeControllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.ylabHomework.mappers.TransactionsMappers.TransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "API получения всех транзакций")
@Controller
@RequiredArgsConstructor
public class GetAllTransactionsController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @Operation(
            summary = "Получить все транзакции")
    @ApiResponse(responseCode = "200", description = "Все транзакции пользователя")
    @GetMapping(value = "/get_all")
    public ResponseEntity<Map<String, Object>> getAllTransactions(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        Map<String, Object> responseData = new HashMap<>();
        List<Transaction> transactions = transactionService.getAllTransactions(user);
        responseData.put("transactions", transactionMapper.toDTOList(transactions));
        return ResponseEntity.ok().body(responseData);
    }
}
