package org.ylabHomework.DTOs.TransactionsDTOs;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * DTO, использующийся для получения и проверки нового значения цели/бюджета.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
public class NewValueDTO {
    @NotNull(message = "Новое значение не должно быть пустым!")
    @Positive(message = "Новое значение должно быть положительным!")
    private double newValue;
}
