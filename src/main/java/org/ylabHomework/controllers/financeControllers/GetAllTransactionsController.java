package org.ylabHomework.controllers.financeControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.ylabHomework.mappers.TransactionsMappers.TransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionService;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "API получения всех транзакций")
@Controller
@RequiredArgsConstructor
public class GetAllTransactionsController {
    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @ApiOperation(value = "Получить все транзакции")
    @ApiResponse(code = 200, message = "Все транзакции пользователя", response = Map.class)
    @GetMapping(value = "/get_all")
    public ResponseEntity<Map<String, Object>> getAllTransactions(HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");
        Map<String, Object> responseData = new HashMap<>();
        List<Transaction> transactions = transactionService.getAllTransactions(user);
        responseData.put("transactions", transactionMapper.toDTOList(transactions));
        return ResponseEntity.ok().body(responseData);
    }
}
