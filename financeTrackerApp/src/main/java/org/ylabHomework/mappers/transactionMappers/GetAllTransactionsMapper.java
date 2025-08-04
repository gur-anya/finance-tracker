package org.ylabHomework.mappers.transactionMappers;

import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.transactionDTOs.GetAllTransactionsResponseDTO;
import org.ylabHomework.models.Transaction;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GetAllTransactionsMapper {
    
    GetAllTransactionsResponseDTO toDTO (List<Transaction> transactions);
}
