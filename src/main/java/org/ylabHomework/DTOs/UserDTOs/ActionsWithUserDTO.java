package org.ylabHomework.DTOs.UserDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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
