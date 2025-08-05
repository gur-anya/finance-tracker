package org.ylabHomework.DTOs.transactionStatisticsDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequestDTO {
    String goalName;
    @NotNull
    BigDecimal goalSum;
}
