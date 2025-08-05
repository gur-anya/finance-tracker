package org.ylabHomework.DTOs.transactionStatisticsDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CheckGoalResponseDTO {
    BigDecimal gotToGoal;
}
