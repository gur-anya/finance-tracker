package org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
/**
 * DTO, использующийся для передачи списка транзакций и дополнительного сообщения при необходимости.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {
    private List<TransactionDTO> transactions;
    private String message;
}
