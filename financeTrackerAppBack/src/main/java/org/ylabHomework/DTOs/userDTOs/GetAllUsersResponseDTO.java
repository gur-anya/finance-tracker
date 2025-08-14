package org.ylabHomework.DTOs.userDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUsersResponseDTO {
    Page<UserDTO> users;
}
