package org.ylabHomework.DTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * DTO, передающий JWT-токен.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@AllArgsConstructor
public class AuthDTO {
    @NotEmpty
    String token;
}
