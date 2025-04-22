package org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO, передающий новые данные о транзакции. Содержит предыдущие и новые значения, а также список изменений в транзакций.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class UpdateTransactionDTO {

    @Min(value = 1, message = "Тип должен быть равен 1 (доход) или 2 (расход)!")
    @Max(value = 2, message = "Тип должен быть равен 1 (доход) или 2 (расход)!")
    private int type;
    private boolean typeChanged;

    @NotNull(message = "Сумма не должна быть пустой!")
    @Positive(message = "Сумма должна быть больше 0!")
    @Digits(integer = 15, fraction = 2, message = "Сумма должна содержать не более 15 цифр до запятой и 2 после!")
    private double sum;
    private boolean sumChanged;

    @Size(min = 2, max = 50, message = "Категория должна содержать от 2 до 50 символов!")
    private String category;
    private boolean categoryChanged;

    @Size(max = 200, message = "Описание не должно превышать 200 символов!")
    private String description;
    private boolean descriptionChanged;


    @NotNull(message = "Время транзакции не должно быть null!")
    @PastOrPresent(message = "Время транзакции не может быть в будущем!")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSSSSS")
    private LocalDateTime timestamp;
}
