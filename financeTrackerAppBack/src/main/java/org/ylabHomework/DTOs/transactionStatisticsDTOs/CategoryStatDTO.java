package org.ylabHomework.DTOs.transactionStatisticsDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ylabHomework.serviceClasses.enums.CategoryEnum;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class CategoryStatDTO {
    private CategoryEnum category;
    private BigDecimal sum;
}

