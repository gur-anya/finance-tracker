package org.ylabHomework.controllers.financeControllers.financeStatsControllers.financeControllers.financeManagementControllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.ylabHomework.DTOs.ResponseMessageDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.*;
import org.ylabHomework.DTOs.TransactionsDTOs.serviceDTOs.FilterDTO;
import org.ylabHomework.mappers.TransactionsMappers.TransactionMapper;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.services.TransactionService;
import org.ylabHomework.services.UserService;

import java.util.List;
@Tag(name = "API управления транзакциями")
@RestController("/transactions/manage")
@RequiredArgsConstructor
public class FinanceManagementController {
    private final TransactionService transactionService;
    private final UserService userService;
    private final TransactionMapper transactionMapper;


    @Operation(summary = "Создать новую транзакцию",
            description = "Создает транзакцию на основе переданных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Транзакция успешно создана. Сообщение может содержать дополнительные уведомления, например, о превышении месячного бюджета."),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных. Сообщение содержит список ошибок."),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PostMapping()

    public ResponseEntity<ResponseMessageDTO> createTransaction(@RequestBody @Valid CreateTransactionDTO transactionDTO) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
        if (user != null) {
            Transaction transaction = transactionMapper.toModel(transactionDTO);
            transaction.setUserId(user.getId());
            responseMessageDTO.setMessage(transactionService.createTransaction(transaction, user));
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessageDTO);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }


    @Operation(
            summary = "Удалить транзакцию",
            description = "Удаляет существующую транзакцию")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакция успешно удалена: Успешно!"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных. Сообщение содержит список ошибок при валидации."),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "404", description = "Транзакцию не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @DeleteMapping()

    public ResponseEntity<ResponseMessageDTO> deleteTransaction(@RequestBody @Valid DeleteTransactionDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            transactionService.deleteTransaction(user.getId(), dto.getTimestamp());
            return ResponseEntity.ok()
                    .body(new ResponseMessageDTO("Успешно!"));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }


    @Operation(
            summary = "Обновляет существующую транзакцию",
            description = "Обновляет транзакцию на основе переданных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Транзакция успешно обновлена. Сообщение может содержать дополнительные уведомления, например, о превышении месячного бюджета."),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных. Сообщение содержит список ошибок при валидации."),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "404", description = "Транзакцию не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @PutMapping()

    public ResponseEntity<ResponseMessageDTO> updateTransaction(@RequestBody @Valid UpdateTransactionDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        StringBuilder message = new StringBuilder("Успешное обновление ");
        StringBuilder warningMessage = new StringBuilder();
        if (user != null) {
            if (!dto.isTypeChanged() && !dto.isSumChanged() && !dto.isCategoryChanged() && !dto.isDescriptionChanged()) {
                return ResponseEntity.badRequest().body(new ResponseMessageDTO("Вы не сделали ни одного изменения!"));
            }

            if (dto.isTypeChanged()) {
                String result = transactionService.updateTransactionType(dto.getType(), user, dto.getTimestamp());
                warningMessage.append(result);
                message.append("типа");
            }

            if (dto.isSumChanged()) {
                String result = transactionService.updateTransactionSum(dto.getSum(), user, dto.getTimestamp());
                if (!warningMessage.isEmpty()) {
                    warningMessage.append(" ").append(result);
                } else {
                    warningMessage.append(result);
                }
                if (message.toString().contains("типа")) {
                    message.append(", суммы");
                } else {
                    message.append(" суммы");
                }
            }

            if (dto.isCategoryChanged()) {
                transactionService.updateTransactionCategory(dto.getCategory(), user, dto.getTimestamp());
                if (message.toString().contains("типа") || message.toString().contains("суммы")) {
                    message.append(", категории");
                } else {
                    message.append(" категории");
                }
            }

            if (dto.isDescriptionChanged()) {
                transactionService.updateTransactionDescription(dto.getDescription(), user, dto.getTimestamp());
                if (message.toString().contains("типа") || message.toString().contains("суммы") || message.toString().contains("категории")) {
                    message.append(", описания");
                } else {
                    message.append(" описания");
                }
            }

            if (!warningMessage.isEmpty()) {
                message.append(". ").append(warningMessage);
            } else {
                message.append("!");
            }

            return ResponseEntity.ok()
                    .body(new ResponseMessageDTO(message.toString()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessageDTO("Пользователь не найден."));
        }
    }

    @Operation(
            summary = "Фильтрует транзакции",
            description = "Фильтрует транзакции по заданному параметру")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список отфильтрованных транзакций"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных. Сообщение содержит список ошибок при валидации."),
            @ApiResponse(responseCode = "403", description = "Пользователя не удалось найти"),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @GetMapping()
    public ResponseEntity<TransactionResponseDTO> filterTransactions(
            @RequestBody(required = false) FilterDTO filterDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.readUserByEmail(authentication.getName());
        if (user != null) {
            int userId = user.getId();
            List<Transaction> transactions;
            if (filterDTO == null) {
                transactions = transactionService.getAllTransactions(userId);
            } else {
                transactions = transactionService.getTransactions(filterDTO, userId);
            }
            List<TransactionDTO> responseTransactions = transactionMapper.toDTOList(transactions);

            if (responseTransactions.isEmpty()) {
                return ResponseEntity.ok()
                        .body(new TransactionResponseDTO(responseTransactions, "Транзакции не найдены!"));
            } else {
                return ResponseEntity.ok()
                        .body(new TransactionResponseDTO(responseTransactions, ""));
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new TransactionResponseDTO(List.of(), "Пользователь не найден."));
        }
    }
}
