package org.ylabHomework.DTOs.TransactionsDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.ylabHomework.serviceClasses.GoalPresent;

import java.time.LocalDateTime;


@GoalPresent(message = "У вас еще нет установленной цели! Установить цель можно в разделе \"Статистика\".")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionsWithTransactionDTO implements TransactionDTO{
    @Min(value = 1)
    @Max(value = 2)
    private int type;
    @NotNull(message = "Сумма не должна быть пустой!")
    private double sum;
    @NotEmpty(message = "Категория не должна быть пустой!")
    private String category;
    private String userEmail;
    private String description;
    @NotEmpty(message = "Вы не сделали ни одного изменения!")
    private String updatedValues;
    private int originalType;
    private double originalSum;
    private String originalCategory;
    private String originalDescription;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime originalTimestamp;

    public ActionsWithTransactionDTO() {
    }

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

    public String getUpdatedValues() {
        return updatedValues;
    }

    public void setUpdatedValues(String updatedValues) {
        this.updatedValues = updatedValues;
    }

    public int getOriginalType() {
        return originalType;
    }

    public void setOriginalType(int originalType) {
        this.originalType = originalType;
    }

    public double getOriginalSum() {
        return originalSum;
    }

    public void setOriginalSum(double originalSum) {
        this.originalSum = originalSum;
    }

    public String getOriginalCategory() {
        return originalCategory;
    }

    public void setOriginalCategory(String originalCategory) {
        this.originalCategory = originalCategory;
    }

    public String getOriginalDescription() {
        return originalDescription;
    }

    public void setOriginalDescription(String originalDescription) {
        this.originalDescription = originalDescription;
    }

    public LocalDateTime getOriginalTimestamp() {
        return originalTimestamp;
    }

    public void setOriginalTimestamp(LocalDateTime originalTimestamp) {
        this.originalTimestamp = originalTimestamp;
    }

    public ActionsWithTransactionDTO(int type, double sum, String category, String userEmail, String description, String updatedValues, int originalType, double originalSum, String originalCategory, String originalDescription, LocalDateTime originalTimestamp) {
        this.type = type;
        this.sum = sum;
        this.category = category;
        this.userEmail = userEmail;
        this.description = description;
        this.updatedValues = updatedValues;
        this.originalType = originalType;
        this.originalSum = originalSum;
        this.originalCategory = originalCategory;
        this.originalDescription = originalDescription;
        this.originalTimestamp = originalTimestamp;
    }
}
