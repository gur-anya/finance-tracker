package org.ylabHomework.DTOs.transactionDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.enums.CategoryEnum;
import org.ylabHomework.serviceClasses.enums.TypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterDTO {
    private TypeEnum type;
    private BigDecimal sumMoreThan;
    private BigDecimal sumLessThan;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CategoryEnum category;
}
