package org.ylabHomework.DTOs.transactionDTOs;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.enums.TypeEnum;

import java.math.BigDecimal;

@JsonPropertyOrder({"type", "sum", "category", "description"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransactionRequestDTO {
    private TypeEnum type;

    @NotNull(message = "Sum must not be empty")
    @Positive(message = "Sum must be greater than 0")
    private BigDecimal sum;

    @NotBlank(message = "Category must not be empty")
    private String category;

    @Size(max = 200, message = "Description must not have more than 200 symbols!")
    private String description;
}