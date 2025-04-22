package org.ylabHomework.mappers.UserMappers;


import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.UserDTOs.CreateUserDTO;
import org.ylabHomework.models.User;

/**
 * * Маппер, преобразующий JSON-данные пользователя в модель пользователя.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */

@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Преобразует DTO пользователя в модель пользователя.
     *
     * @param createUserDTO DTO, содержащий данные пользователя
     * @return модель пользователя
     */

    User toModel(CreateUserDTO createUserDTO);
}
