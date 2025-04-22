package org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
/**
 * DTO, использующийся для передачи расходов по категориям.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryExpensesDTO {
    private Map<String, Double> categoryExpenses;
}