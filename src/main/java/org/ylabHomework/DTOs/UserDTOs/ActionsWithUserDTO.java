package org.ylabHomework.DTOs.UserDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
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
public class ActionsWithUserDTO {
    @NotEmpty(message = "Имя не должно быть пустым.")
    private String name;

    @Email(message = "Введите корректный email.")
    @NotEmpty(message = "Поле email не должно быть пустым.")
    private String email;
    private String newPassword;
    private String oldPassword;
    @NotEmpty(message = "Вы не сделали ни одного изменения!")
    private String updatedValues;

    @AssertTrue(message = "При изменении пароля пожалуйста, подтвердите старый пароль.")
    public boolean isOldPasswordValid() {
        return newPassword == null || newPassword.isEmpty() || oldPassword != null && !oldPassword.isEmpty();
    }
}
