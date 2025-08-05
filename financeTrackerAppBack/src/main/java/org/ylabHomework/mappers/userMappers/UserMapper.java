package org.ylabHomework.mappers.userMappers;


import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.userDTOs.UserDTO;
import org.ylabHomework.models.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    /**
     * Преобразует DTO транзакции в модель транзации.
     *
     * @param userDTO DTO, содержащий переданные данные о транзакции
     * @return модель транкзакции
     */
    User toModel(UserDTO userDTO);

    /**
     * Преобразует модель транзакции в DTO транзакции.
     *
     * @param user модель транзакции
     * @return DTO транзакции
     */
    UserDTO toDTO(User user);
}
