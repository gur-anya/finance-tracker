package org.ylabHomework.DTOs.UserDTOs;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.Unique;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"email", "password"})
public class LoginDTO {
    @Email(message = "Введите корректный email.")
    @NotEmpty(message = "Поле email не должно быть пустым.")
    @Unique(reversed = true, message = "Пользователь с таким email не найден, повторите попытку.")
    private String email;

    @NotEmpty(message = "Пароль не должен быть пустым.")
    private String password;
}