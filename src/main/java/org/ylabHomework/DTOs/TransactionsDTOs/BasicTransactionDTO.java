package org.ylabHomework.DTOs.TransactionsDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;

import org.ylabHomework.serviceClasses.GoalPresent;
import org.ylabHomework.serviceClasses.Unique;

import java.time.LocalDateTime;


@GoalPresent(message = "У вас еще нет установленной цели! Установить цель можно в разделе \"Статистика\".")
@JsonPropertyOrder({"type", "sum", "category", "description"})
public class BasicTransactionDTO implements TransactionDTO{
    public BasicTransactionDTO() {

    }

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

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    @Override
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BasicTransactionDTO(int type, double sum, String category, String userEmail, String description, LocalDateTime timestamp) {
        this.type = type;
        this.sum = sum;
        this.category = category;
        this.userEmail = userEmail;
        this.description = description;
        this.timestamp = timestamp;
    }
}
