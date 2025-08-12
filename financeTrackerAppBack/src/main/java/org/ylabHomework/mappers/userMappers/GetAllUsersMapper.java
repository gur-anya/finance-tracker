package org.ylabHomework.mappers.userMappers;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.ylabHomework.DTOs.userDTOs.GetAllUsersResponseDTO;
import org.ylabHomework.DTOs.userDTOs.UserDTO;
import org.ylabHomework.models.User;

import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class GetAllUsersMapper {
    @Autowired
    protected UserMapper userMapper;

    public GetAllUsersResponseDTO toDTO(List<User> users) {
        if (users == null) {
            return null;
        }

        List<UserDTO> userDTOs = users.stream()
            .map(userMapper::toDTO)
            .collect(Collectors.toList());

        return new GetAllUsersResponseDTO(userDTOs);
    }
}