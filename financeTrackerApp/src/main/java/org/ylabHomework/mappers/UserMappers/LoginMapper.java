package org.ylabHomework.mappers.UserMappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylabHomework.DTOs.UserDTOs.LoginDTO;
import org.ylabHomework.models.User;

/**
 * * Маппер, преобразующий JSON-данные логина в модель пользователя.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */
@Mapper(componentModel = "spring")
public interface LoginMapper {

    /**
     * Преобразует DTO логина в модель пользователя.
     *
     * @param loginDTO DTO, содержащий данные, переданные при логине
     * @return модель пользователя
     */
    @Mapping(target = "monthlyBudget", ignore = true)
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toModel(LoginDTO loginDTO);
}
