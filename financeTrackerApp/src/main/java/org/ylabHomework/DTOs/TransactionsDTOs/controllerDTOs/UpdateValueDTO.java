package org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO, использующийся для передачи нового значения (баланс, цель).
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateValueDTO {
    @NotNull(message = "Новое значение не должно быть пустым!")
    @Positive(message = "Новое значение должно быть положительным!")
    private double newValue;
}
