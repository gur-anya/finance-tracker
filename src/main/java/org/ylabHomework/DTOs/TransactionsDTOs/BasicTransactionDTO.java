package org.ylabHomework.DTOs.TransactionsDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import javax.validation.constraints.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.GoalPresent;

import java.time.LocalDateTime;

/**
 * DTO, передающий поля транзакции. Дополнительно содержит email пользователя, которому принадлежит данная транзакция.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@GoalPresent(message = "У вас еще нет установленной цели! Установить цель можно в разделе \"Статистика\".")
@JsonPropertyOrder({"type", "sum", "category", "description"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicTransactionDTO implements TransactionDTO{
    @Min(value = 1, message = "Тип должен быть равен 1 (доход) или 2 (расход)!")
    @Max(value = 2, message = "Тип должен быть равен 1 (доход) или 2 (расход)!")
    private int type;
    @NotNull(message = "Сумма не должна быть пустой!")
    @Positive(message = "Сумма должна быть больше нуля!")
    private double sum;
    @NotEmpty(message = "Категория не должна быть пустой!")
    private String category;
    private String userEmail;
    private String description;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime timestamp;
}
