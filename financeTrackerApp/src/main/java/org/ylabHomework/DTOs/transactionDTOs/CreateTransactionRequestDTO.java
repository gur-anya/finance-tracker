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

    @NotNull(message = "Сумма не должна быть пустой!")
    @Positive(message = "Сумма должна быть больше 0!")
    @Digits(integer = 15, fraction = 2, message = "Сумма должна содержать не более 15 цифр до запятой и 2 после!")
    private BigDecimal sum;

    @NotBlank(message = "Категория не должна быть пустой или состоять только из пробелов!")
    @Size(min = 2, max = 50, message = "Категория должна содержать от 2 до 50 символов!")
    private String category;

    @Size(max = 200, message = "Описание не должно превышать 200 символов!")
    private String description;
}