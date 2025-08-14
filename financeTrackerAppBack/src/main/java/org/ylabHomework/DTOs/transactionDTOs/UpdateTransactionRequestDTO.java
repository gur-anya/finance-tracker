package org.ylabHomework.DTOs.transactionDTOs;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.enums.CategoryEnum;
import org.ylabHomework.serviceClasses.enums.TypeEnum;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransactionRequestDTO {

    @Positive(message = "Sum must be greater than 0")
    private BigDecimal sum;


    private CategoryEnum category;

    @Size(max = 200, message = "Description must not have more than 200 symbols!")
    private String description;

    private TypeEnum type;
}