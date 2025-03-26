package org.ylabHomework.DTOs.UserDTOs;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import org.ylabHomework.serviceClasses.Unique;


@JsonPropertyOrder({"email", "password"})
public class LoginDTO {
    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public LoginDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Email(message = "Введите корректный email.")
    @NotEmpty(message = "Поле email не должно быть пустым.")
    @Unique(reversed = true, message = "Пользователь с таким email не найден, повторите попытку.")
    private String email;

    @NotEmpty(message = "Пароль не должен быть пустым.")
    private String password;
}