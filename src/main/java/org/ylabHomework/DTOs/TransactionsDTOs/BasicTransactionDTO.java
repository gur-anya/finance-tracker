package org.ylabHomework.DTOs.TransactionsDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.ylabHomework.serviceClasses.GoalPresent;
import org.ylabHomework.serviceClasses.Unique;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@GoalPresent(message = "У вас еще нет установленной цели! Установить цель можно в разделе \"Статистика\".")
@JsonPropertyOrder({"type", "sum", "category", "description"})
public class BasicTransactionDTO implements TransactionDTO{
    @Min(value = 1)
    @Max(value = 2)
    private int type;
    @NotNull(message = "Сумма не должна быть пустой!")
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
