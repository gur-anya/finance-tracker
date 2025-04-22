package org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO, использующийся для передачи общего баланса.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ControllerBalanceDTO {
    private double balance;
}
