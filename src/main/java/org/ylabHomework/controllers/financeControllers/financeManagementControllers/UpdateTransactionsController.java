package org.ylabHomework.controllers.financeControllers.financeManagementControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.ActionsWithTransactionDTO;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.TransactionStatsService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
@Api(value = "API обновления транзакции")
@Controller
@RequiredArgsConstructor
public class UpdateTransactionsController {

    private final TransactionService transactionService;
    private final TransactionStatsService transactionStatsService;





    @ApiOperation(value = "Обновляет существующую транзакцию",
            notes = "Обновляет транзакцию транзакцию на основе переданных данных")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Транзакция успешно обновлена. Сообщение может содержать дополнительные уведомления, например, о превышении месячного бюджета.", response = ResponseMessageDTO.class),
            @ApiResponse(code = 400, message = "Ошибка валидации входных данных. Сообщение содержит список ошибок при валидации.", response = ResponseMessageDTO.class)
    })
    @PostMapping(value = "/update_transaction")
    @ResponseBody
    public ResponseEntity<ResponseMessageDTO> updateTransaction(@RequestBody @Valid ActionsWithTransactionDTO transactionDTO, BindingResult result, HttpSession session) {
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
            return ResponseEntity.badRequest().body(responseMessageDTO);
        } else {
            Transaction updatedTransaction = new Transaction(
                    transactionDTO.getOriginalType(),
                    transactionDTO.getOriginalSum(),
                    transactionDTO.getOriginalCategory().toLowerCase().trim(),
                    transactionDTO.getDescription().trim()
            );
            updatedTransaction.setTimestamp(transactionDTO.getOriginalTimestamp());

            String updatedValues = transactionDTO.getUpdatedValues();
            if (updatedValues.contains("type")) {
                transactionService.updateTransactionType(user,transactionDTO.getType(), updatedTransaction);
            }
            if (updatedValues.contains("sum")) {
                transactionService.updateTransactionSum(user, String.valueOf(transactionDTO.getSum()), updatedTransaction);
            }
            if (updatedValues.contains("category")) {
                transactionService.updateTransactionCategory(user, transactionDTO.getCategory(), updatedTransaction);
            }
            if (updatedValues.contains("description")) {
                transactionService.updateTransactionDescription(user, transactionDTO.getDescription(), updatedTransaction);
            }


           stateMessageBuilder.append("Успешно! ");

            if (updatedValues.contains("type") && transactionDTO.getType() == 2 &&
                    transactionStatsService.getMonthlyBudget(user) > 0) {
                double budgetLimit = transactionStatsService.checkMonthlyBudgetLimit(user);
                if (budgetLimit < 0) {
                    stateMessageBuilder.append(transactionService.notifyAboutMonthlyLimit(budgetLimit));
                }
            }
            responseMessageDTO.setMessage(stateMessageBuilder.toString());
            return ResponseEntity.ok().body(responseMessageDTO);
        }
    }
}