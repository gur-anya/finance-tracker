package org.ylabHomework.DTOs.transactionDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GetAllTransactionsResponseDTO {
    List<TransactionDTO> transactions;
}
