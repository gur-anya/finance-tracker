package org.ylabHomework.DTOs.userDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.serviceClasses.ValidPassword;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {
    @NotBlank(message = "Имя не должно быть пустым или состоять только из пробелов!")
    @Size(min = 2, max = 50, message = "Имя должно содержать от 2 до 50 символов!")
    String name;
    @Email(message = "Введите корректный email.")
    @NotBlank(message = "Поле email не должно быть пустым.")
    String email;
    @NotBlank(message = "Пароль не должен быть пустым.")
    @Size(min = 8, message = "Слишком короткий пароль!")
    @Size(max = 128, message = "Слишком длинный пароль!")
    @ValidPassword
    String password;
}
