package org.ylabHomework.controllers.financeControllers.financeManagementControllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.mappers.TransactionsMappers.TransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
@Api(value = "API удаления транзакции")
@Controller
@RequiredArgsConstructor
public class DeleteTransactionsController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;




    @ApiOperation(value = "Удалить транзакцию",
            notes = "Удаляет существующую транзакцию")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Транзакция успешно удалена: Успешно!", response = ResponseMessageDTO.class),
            @ApiResponse(code = 400, message = "Ошибка валидации входных данных. Сообщение содержит список ошибок при валидации.", response = ResponseMessageDTO.class)
    })
    @DeleteMapping(value = "/delete_transaction")
    @ResponseBody
    public ResponseEntity<ResponseMessageDTO> deleteTransaction(@RequestBody @Valid BasicTransactionDTO transactionDTO, BindingResult result, HttpSession session) {
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

            transactionDTO.setDescription(transactionDTO.getDescription().trim());
            transactionDTO.setCategory(transactionDTO.getCategory().trim().toLowerCase());

            Transaction transaction = transactionMapper.toModel(transactionDTO);
            transactionService.deleteTransaction(user, transaction);


            stateMessageBuilder.append("Успешно! ");
            stateMessage = stateMessageBuilder.toString();
            responseMessageDTO.setMessage(stateMessage);
            return ResponseEntity.ok(responseMessageDTO);
        }
    }
}