package org.ylabHomework.mappers;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylabHomework.DTOs.UserDTOs.BasicUserDTO;
import org.ylabHomework.models.User;
/**
 * * Маппер, преобразующий JSON-данные пользователя в модель пользователя.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 21.03.2025
 */

@Mapper
public interface UserMapper {
    /**
     * Преобразует DTO пользователя в модель пользователя.
     *
     * @param basicUserDTO DTO, содержащий данные пользователя
     * @return модель пользователя
     */
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "monthlyBudget", ignore = true)
    @Mapping(target = "goal", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toModel(BasicUserDTO basicUserDTO);
}
