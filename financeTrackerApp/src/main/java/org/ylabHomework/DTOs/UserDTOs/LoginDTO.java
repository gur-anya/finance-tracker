package org.ylabHomework.DTOs.UserDTOs;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.ValidPassword;

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
    @NotBlank(message = "Поле email не должно быть пустым.")
    @Size(min = 5, message = "Слишком короткий email!")
    @Size(max = 100, message = "Слишком длинный email!")
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым.")
    @Size(min = 8, message = "Слишком короткий пароль!")
    @Size(max = 128, message = "Слишком длинный пароль!")
    @ValidPassword
    private String password;
}