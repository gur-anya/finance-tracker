package org.ylabHomework.DTOs.userDTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    Long id;
    @NotEmpty
    String token;
}