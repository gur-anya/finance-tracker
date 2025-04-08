package org.ylabHomework.DTOs.UserDTOs;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.Unique;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * DTO, передающий объект логина пользователя. Содержит email и пароль, переданные при входе.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@JsonPropertyOrder({"email", "password"})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    @Email(message = "Введите корректный email.")
    @NotEmpty(message = "Поле email не должно быть пустым.")
    @Unique(uniqueRequired = true, message = "Пользователь с таким email не найден, повторите попытку.")
    private String email;

    @NotEmpty(message = "Пароль не должен быть пустым.")
    private String password;
}