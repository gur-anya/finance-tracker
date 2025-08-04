package org.ylabHomework.mappers.userMappers;

import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.transactionDTOs.GetAllTransactionsResponseDTO;
import org.ylabHomework.DTOs.userDTOs.GetAllUsersResponseDTO;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GetAllUsersMapper {

    GetAllUsersResponseDTO toDTO (List<User> users);
}
