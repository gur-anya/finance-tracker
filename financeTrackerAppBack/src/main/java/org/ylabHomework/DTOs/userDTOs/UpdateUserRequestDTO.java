package org.ylabHomework.DTOs.userDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO, передающий новые данные о пользователе. Содержит поля для нового имени, email, старого и нового паролей, а также
 * поле, хранящее список изменений пользователя.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters long")
    private String name;
    @Size(min = 8, message = "Password is too short")
    @Size(max = 128, message = "Password is too long")
    private String newPassword;
    private String oldPassword;
}
