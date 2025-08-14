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
    @NotBlank(message = "Name must not be empty")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters long")
    String name;
    @Email(message = "Incorrect email")
    @NotBlank(message = "Email must not be empty")
    String email;
    @NotBlank(message = "Password must not be empty")
    @Size(min = 8, message = "Password is too short")
    @Size(max = 128, message = "Password is too long")
    @ValidPassword
    String password;
}
