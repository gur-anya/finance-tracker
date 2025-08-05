package org.ylabHomework.mappers.userMappers;

import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.userDTOs.UpdateUserResponseDTO;
import org.ylabHomework.models.User;

@Mapper(componentModel = "spring")
public interface UpdateUserMapper {
    User toModel(UpdateUserResponseDTO updateResponse);

    UpdateUserResponseDTO toDTO(User user);
}
