package org.ylabHomework.DTOs.transactionStatisticsDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReportDTO {
    List<CategoryStatDTO> incomesGrouped;
    List<CategoryStatDTO> expensesGrouped;
}
