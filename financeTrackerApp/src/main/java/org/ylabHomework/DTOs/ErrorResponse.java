package org.ylabHomework.DTOs;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/**
 * DTO, передающий сообщение о статусе действия.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@JsonPropertyOrder({"message"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    @NotEmpty
    String message;
    LocalDateTime timestamp;
}
