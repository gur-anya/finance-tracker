package org.ylabHomework.DTOs.userDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.ValidPassword;

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
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов!")
    private String name;
    @Size(min = 8, message = "Новый пароль слишком короткий!")
    @Size(max = 128, message = "Новый пароль слишком длинный!")
    @ValidPassword
    private String newPassword;
    private String oldPassword;
}
