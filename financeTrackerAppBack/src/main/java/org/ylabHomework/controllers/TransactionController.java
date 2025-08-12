package org.ylabHomework.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.ylabHomework.DTOs.transactionDTOs.*;
import org.ylabHomework.serviceClasses.springConfigs.security.UserDetailsImpl;
import org.ylabHomework.services.TransactionService;

@RestController
@Tag(name = "API для работы с транзакциями: создание, чтение, изменение и удаление")
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(
        summary = "Создает транзакцию с переданным типом, суммой, описанием")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Успешное создание - ответ с созданной транзакцией"),
        @ApiResponse(responseCode = "400", description = "Невалидные параметры транзакции"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CreateTransactionResponseDTO> createTransaction(@Valid @RequestBody CreateTransactionRequestDTO createTransactionRequestDTO,
                                                                          @AuthenticationPrincipal UserDetailsImpl currentUser) {
        CreateTransactionResponseDTO transactionResponseDTO = transactionService.createTransaction(createTransactionRequestDTO, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDTO);
    }


    @Operation(
        summary = "Обновляет параметры транзакции: тип/сумма/описание")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешное обновление - ответ с обновленной транзакцией"),
        @ApiResponse(responseCode = "400", description = "Невалидные параметры для обновления транзакции"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "404", description = "Транзакция не найдена"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UpdateTransactionResponseDTO> updateTransaction(@Valid @RequestBody UpdateTransactionRequestDTO updateTransactionRequestDTO,
                                                                          @PathVariable Long transactionId,
                                                                          @AuthenticationPrincipal UserDetailsImpl currentUser) {
        UpdateTransactionResponseDTO transactionResponseDTO = transactionService.updateTransaction(updateTransactionRequestDTO, transactionId, currentUser.getId());
        return ResponseEntity.ok(transactionResponseDTO);
    }

    @Operation(
        summary = "Удалает транзакцию")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Успешное удаление"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "404", description = "Транзакция не найдена"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long transactionId,
                                               @AuthenticationPrincipal UserDetailsImpl currentUser) {
        transactionService.deleteTransaction(transactionId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(
        summary = "Возвращает все транзакции пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный ответ - трназакции пользователя"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован (JWT-токен отсутствует или невалиден)"),
        @ApiResponse(responseCode = "404", description = "Транзакция не найдена"),
        @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetAllTransactionsResponseDTO> getAllTransactionsByUser (@AuthenticationPrincipal UserDetailsImpl currentUser) {
       GetAllTransactionsResponseDTO allTransactions = transactionService.getAllTransactionsByUser(currentUser.getId());
       return ResponseEntity.ok(allTransactions);
    }
}
