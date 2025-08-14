package org.ylabHomework.mappers.userMappers;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.ylabHomework.DTOs.userDTOs.GetAllUsersResponseDTO;
import org.ylabHomework.DTOs.userDTOs.UserDTO;
import org.ylabHomework.models.User;


@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class GetAllUsersMapper {
    @Autowired
    protected UserMapper userMapper;

    public GetAllUsersResponseDTO toDTO(Page<User> users) {
        if (users == null) {
            return null;
        }

        Page<UserDTO> userDTOs = users.map(userMapper::toDTO);
        return new GetAllUsersResponseDTO(userDTOs);
    }
}