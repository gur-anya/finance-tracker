package org.ylabHomework.mappers.transactionMappers;

import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.transactionDTOs.UpdateTransactionResponseDTO;
import org.ylabHomework.models.Transaction;

@Mapper(componentModel = "spring")
public interface UpdateTransactionMapper {

    Transaction toModel(UpdateTransactionResponseDTO updateResponse);

    UpdateTransactionResponseDTO toDTO(Transaction transaction);
}
