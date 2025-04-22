package org.ylabHomework.mappers.UserMappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylabHomework.DTOs.UserDTOs.UpdateUserDTO;
import org.ylabHomework.models.User;

/**
 * Маппер, преобразующий JSON, переданный при изменении данных пользователя,
 * в модель обновленного пользователя.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 21.03.2025
 */
@Mapper(componentModel = "spring")
public interface UpdateUserMapper {

    /**
     * Преобразует DTO обновленных данных в модель пользователя.
     *
     * @param updateUserDTO DTO, содержащий данные, переданные при обновлении пользователя
     * @return модель пользователя
     */

    @Mapping(target = "monthlyBudget", ignore = true)
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toModel(UpdateUserDTO updateUserDTO);
}
