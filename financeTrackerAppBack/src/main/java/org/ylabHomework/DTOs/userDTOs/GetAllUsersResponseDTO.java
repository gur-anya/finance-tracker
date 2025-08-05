package org.ylabHomework.DTOs.userDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ylabHomework.models.User;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUsersResponseDTO {
    List<UserDTO> users;
}
