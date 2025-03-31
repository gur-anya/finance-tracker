package org.ylabHomework.DTOs.UserDTOs;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionsWithUserDTO {
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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getUpdatedValues() {
        return updatedValues;
    }

    public void setUpdatedValues(String updatedValues) {
        this.updatedValues = updatedValues;
    }

    public ActionsWithUserDTO() {
    }

    public ActionsWithUserDTO(String name, String email, String newPassword, String oldPassword, String updatedValues) {
        this.name = name;
        this.email = email;
        this.newPassword = newPassword;
        this.oldPassword = oldPassword;
        this.updatedValues = updatedValues;
    }

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
