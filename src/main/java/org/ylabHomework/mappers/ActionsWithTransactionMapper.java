package org.ylabHomework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylabHomework.DTOs.TransactionsDTOs.ActionsWithTransactionDTO;
import org.ylabHomework.DTOs.UserDTOs.ActionsWithUserDTO;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
@Mapper
public interface ActionsWithTransactionMapper {

    Transaction toModel (ActionsWithTransactionDTO actionsWithTransactionDTO);
}
