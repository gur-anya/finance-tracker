package org.ylabHomework.DTOs.TransactionsDTOs;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.ylabHomework.serviceClasses.GoalPresent;
import org.ylabHomework.serviceClasses.Unique;

@Data
@NoArgsConstructor
@AllArgsConstructor
@GoalPresent(message = "У вас еще нет установленной цели! Установить цель можно в разделе \"Статистика\".")
@JsonPropertyOrder({"type", "sum", "category", "description"})
public class BasicTransactionDTO {
    @Min(value = 1)
    @Max(value = 2)
    private int type;
    @NotEmpty(message = "Сумма не должна быть пустой!")
    private double sum;
    @NotEmpty(message = "Категория не должна быть пустой!")
    private String category;
    @Max(value = 200, message = "Описание не должно превышать 200 символов!")
    private String description;
    private String userEmail;
}
