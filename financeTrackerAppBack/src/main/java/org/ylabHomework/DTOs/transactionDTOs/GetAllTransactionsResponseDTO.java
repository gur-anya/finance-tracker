package org.ylabHomework.DTOs.transactionDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllTransactionsResponseDTO {
    Page<TransactionDTO> transactions;
}
