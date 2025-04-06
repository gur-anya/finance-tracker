package org.ylabHomework.controllers.financeControllers.financeManagementControllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.mappers.TransactionsMappers.TransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Tag(name = "API создания транзакции")
@Controller
@RequiredArgsConstructor
public class CreateTransactionsController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;
    private final TransactionStatsService transactionStatsService;

    @Operation(summary = "Создать новую транзакцию",
            description = "Создает транзакцию на основе переданных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакция успешно создана. Сообщение может содержать дополнительные уведомления, например, о превышении месячного бюджета."),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных. Сообщение содержит список ошибок при валидации.")
    })
    @PostMapping(value = "/create_transaction")
    @ResponseBody
    public ResponseEntity<ResponseMessageDTO> createTransaction(@RequestBody @Valid BasicTransactionDTO transactionDTO, BindingResult result, HttpSession session) {
        User user = (User) session.getAttribute("loggedUser");

        StringBuilder stateMessageBuilder = new StringBuilder();
        String stateMessage;
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        if (result.hasErrors()) {
            for (ObjectError error : result.getAllErrors()) {
                stateMessageBuilder.append(error.getDefaultMessage()).append(" ");
            }
            stateMessage = stateMessageBuilder.toString();
            responseMessageDTO.setMessage(stateMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessageDTO);
        } else {
            Transaction transaction = transactionMapper.toModel(transactionDTO);
            transactionService.createTransaction(user,
                    transaction.getType(),
                    String.valueOf(transaction.getSum()),
                    transaction.getCategory(),
                    transaction.getDescription()
            );
           stateMessageBuilder.append("Успешно! ");

            if (transaction.getType() == 2 && transactionStatsService.getMonthlyBudget(user) > 0) {
                double budgetLimit = transactionStatsService.checkMonthlyBudgetLimit(user);
                if (budgetLimit < 0) {
                    stateMessageBuilder.append(transactionService.notifyAboutMonthlyLimit(budgetLimit));
                }
            }
            stateMessage = stateMessageBuilder.toString();
            responseMessageDTO.setMessage(stateMessage);
            return ResponseEntity.ok(responseMessageDTO);
        }
    }
}
