package org.ylabHomework.DTOs.transactionDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequestDTO {
    @NotNull
    BigDecimal goal;
}
