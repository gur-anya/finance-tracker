package org.ylabHomework.DTOs.UserDTOs;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.Unique;
/**
 * DTO, передающий поля пользователя. Дополнительно содержит поле с повторенным паролем.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"name", "email", "password", "role"})
public class BasicUserDTO {
    @NotEmpty (message = "Имя не должно быть пустым.")
    private String name;
    @Email(message = "Введите корректный email.")
    @NotEmpty (message = "Поле email не должно быть пустым.")
    @Unique(message = "Пользователь с таким email уже зарегистрирован!")
    private String email;
    @NotEmpty(message = "Пароль не должен быть пустым.")
    private String password;
    @Max(value = 1)
    private int role;
    @NotEmpty(message = "Пожалуйста, повторите пароль.")
    private String repeatedPassword;
    @AssertTrue(message = "Пароли должны совпадать.")
    public boolean isPasswordsMatch() {
        return password != null && password.equals(repeatedPassword);
    }

}
