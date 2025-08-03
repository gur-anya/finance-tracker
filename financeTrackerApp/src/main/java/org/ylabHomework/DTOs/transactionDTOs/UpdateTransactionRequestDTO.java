package org.ylabHomework.DTOs.transactionDTOs;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.enums.TypeEnum;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTransactionRequestDTO {

    @Positive(message = "Сумма должна быть больше 0!")
    @Digits(integer = 15, fraction = 2)
    private BigDecimal sum;

    @NotBlank
    @Size(min = 2, max = 50)
    private String category;

    @Size(max = 200)
    private String description;

    private TypeEnum type;
}