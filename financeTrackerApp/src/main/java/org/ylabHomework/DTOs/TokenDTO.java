package org.ylabHomework.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO, передающий JWT-токен.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 01.08.2025
 */
@Data
@AllArgsConstructor
public class TokenDTO {
    @NotEmpty
    String token;
}
