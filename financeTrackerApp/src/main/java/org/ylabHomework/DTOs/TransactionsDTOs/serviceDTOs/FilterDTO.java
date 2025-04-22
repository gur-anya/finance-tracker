package org.ylabHomework.DTOs.TransactionsDTOs.serviceDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.customExceptions.EmptyValueException;

import java.time.LocalDateTime;
/**
 * DTO, использующийся для передачи фильтра транзакций и дополнительного параметра при необходимости.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterDTO {
    @NotNull(message = "Фильтр обязателен!")
    private String filter;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime beforeTimestamp;


    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime afterTimestamp;

    @Size(min = 2, max = 50, message = "Категория должна содержать от 2 до 50 символов!")
    private String category;


    public void validate() {
        switch (filter) {
            case "1":
                if (beforeTimestamp == null) {
                    throw new EmptyValueException("дата до в фильтре");
                }
                break;
            case "2":
                if (afterTimestamp == null) {
                    throw new EmptyValueException("дата после в фильтре");
                }
                break;
            case "3":
                if (category == null || category.trim().isEmpty()) {
                    throw new EmptyValueException("категория в фильтре");
                }
                break;
            case "41":
            case "42":
            case "5":
                break;
            default:
                throw new IllegalArgumentException("Неверный фильтр!");
        }
    }
}
