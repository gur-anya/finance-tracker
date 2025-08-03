package org.ylabHomework.mappers.userMappers;

import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.userDTOs.CreateUserRequestDTO;
import org.ylabHomework.models.User;

@Mapper(componentModel = "spring")
public interface CreateUserMapper {

    User toModel(CreateUserRequestDTO createRequest);

    CreateUserRequestDTO toDTO(User user);
}
