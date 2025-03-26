package org.ylabHomework.DTOs.UserDTOs;


import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.*;

import org.ylabHomework.serviceClasses.Unique;


@JsonPropertyOrder({"name", "email", "password", "role"})
public class BasicUserDTO {
    @NotEmpty (message = "Имя не должно быть пустым.")
    private String name;
    @Email (message = "Введите корректный email.")
    @NotEmpty (message = "Поле email не должно быть пустым.")
    @Unique(message = "Пользователь с таким email уже зарегистрирован!")
    private String email;
    @NotEmpty(message = "Пароль не должен быть пустым.")
    //  @Size(min = 8, message = "Пароль должен состоять из 8 и более символов.")
    private String password;
    @Max(value = 1)
    private int role;
    @NotEmpty (message = "Пожалуйста, повторите пароль.")
    private String repeatedPassword;
    @AssertTrue(message = "Пароли должны совпадать.")
    public boolean isPasswordsMatch() {
        return password != null && password.equals(repeatedPassword);
    }

    public BasicUserDTO(String name, String email, String password, int role, String repeatedPassword) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.repeatedPassword = repeatedPassword;
    }

    public BasicUserDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getRepeatedPassword() {
        return repeatedPassword;
    }

    public void setRepeatedPassword(String repeatedPassword) {
        this.repeatedPassword = repeatedPassword;
    }
}
