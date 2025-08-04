package org.ylabHomework.DTOs.userDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.ylabHomework.models.User;

import java.util.List;
@Data
@AllArgsConstructor
public class GetAllUsersResponseDTO {
    List<UserDTO> users;
}
