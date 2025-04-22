package org.ylabHomework.DTOs.TransactionsDTOs.serviceDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO, использующийся для передачи из контроллера баланса.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceBalanceDTO {
    private double balance;
    private boolean isEmpty;
}
