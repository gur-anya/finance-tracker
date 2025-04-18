package org.ylabHomework.mappers.UserMappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylabHomework.DTOs.UserDTOs.ActionsWithUserDTO;
import org.ylabHomework.models.User;
/**
 * Маппер, преобразующий JSON, переданный при изменении данных пользователя,
 * в модель обновленного пользователя.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */
@Mapper(componentModel = "spring")
public interface ActionsWithUserMapper {

    /**
     * Преобразует DTO обновленных данных в модель пользователя.
     *
     * @param actionsWithUserDTO DTO, содержащий данные, переданные при обновлении пользователя
     * @return модель пользователя
     */
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "monthlyBudget", ignore = true)
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "role", ignore = true)
    User toModel (ActionsWithUserDTO actionsWithUserDTO);
}
