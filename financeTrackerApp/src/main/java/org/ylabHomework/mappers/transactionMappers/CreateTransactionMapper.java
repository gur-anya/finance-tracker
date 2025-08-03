package org.ylabHomework.mappers.transactionMappers;

import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.transactionDTOs.CreateTransactionRequestDTO;
import org.ylabHomework.models.Transaction;

@Mapper(componentModel = "spring")
public interface CreateTransactionMapper {

    Transaction toModel(CreateTransactionRequestDTO createRequest);

    CreateTransactionRequestDTO toDTO(Transaction transaction);
}
