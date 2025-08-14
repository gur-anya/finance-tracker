package org.ylabHomework.DTOs.transactionDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.enums.CategoryEnum;
import org.ylabHomework.serviceClasses.enums.TypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO транзакции. Содержит тип, сумму, категорию, описание.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long id;
    private TypeEnum type;
    private BigDecimal sum;
    private CategoryEnum category;
    private String description;
    private LocalDateTime timestamp;
}
