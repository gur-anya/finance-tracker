package org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
/**
 * DTO, использующийся для передачи финансового отчета.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private BasicStatsDTO basicStats;
    private Map<String, CategoryStatsDTO> categoryReport;
    private GoalDataDTO goalData;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicStatsDTO {
        private double totalIncome;
        private double totalExpense;
        private double totalBalance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryStatsDTO {
        private double income;
        private double expense;
        private double balance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalDataDTO {
        private double goalSum;
        private double goalIncome;
        private double goalExpense;
        private double saved;
        private double left;
    }
}