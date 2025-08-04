package org.ylabHomework.controllers;

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
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/{userId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CreateTransactionResponseDTO> createTransaction(@Valid @RequestBody CreateTransactionRequestDTO createTransactionRequestDTO,
                                                                          @PathVariable Long userId) {
        CreateTransactionResponseDTO transactionResponseDTO = transactionService.createTransaction(createTransactionRequestDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseDTO);
    }

    @PatchMapping("/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UpdateTransactionResponseDTO> updateTransaction(@Valid @RequestBody UpdateTransactionRequestDTO updateTransactionRequestDTO,
                                                                          @PathVariable Long transactionId,
                                                                          @AuthenticationPrincipal UserDetailsImpl currentUser) {
        UpdateTransactionResponseDTO transactionResponseDTO = transactionService.updateTransaction(updateTransactionRequestDTO, transactionId, currentUser.getId());
        return ResponseEntity.ok(transactionResponseDTO);
    }

    @DeleteMapping("/{transactionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long transactionId,
                                               @AuthenticationPrincipal UserDetailsImpl currentUser) {
        transactionService.deleteTransaction(transactionId, currentUser.getId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GetAllTransactionsResponseDTO> getAllTransactionsByUser (@AuthenticationPrincipal UserDetailsImpl currentUser) {
       GetAllTransactionsResponseDTO allTransactions = transactionService.getAllTransactionsByUser(currentUser.getId());
       return ResponseEntity.ok(allTransactions);
    }
}
