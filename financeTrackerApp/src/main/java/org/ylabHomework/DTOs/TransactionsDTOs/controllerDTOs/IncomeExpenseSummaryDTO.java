package org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncomeExpenseSummaryDTO {
    private double income;
    private double expense;
    private double balance;
}
