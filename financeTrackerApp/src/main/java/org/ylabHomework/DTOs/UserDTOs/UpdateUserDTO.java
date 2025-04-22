package org.ylabHomework.DTOs.UserDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
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
public class UpdateUserDTO {
    @Size(min = 2, message = "Слишком короткое имя!")
    @Size(max = 50, message = "Слишком длинное имя!")
    private String name;
    private boolean updateName = false;

    @Size(min = 5, message = "Слишком короткий email!")
    @Size(max = 100, message = "Слишком длинный email!")
    @Email(message = "Введите корректный email.")
    private String email;
    private boolean updateEmail = false;

    @Size(min = 8, message = "Слишком короткий пароль!")
    @Size(max = 128, message = "Слишком длинный пароль!")
    private String password;
    private String oldPassword;
    private boolean updatePassword = false;
}
