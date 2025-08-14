package org.ylabHomework.mappers.transactionMappers;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.ylabHomework.DTOs.transactionDTOs.GetAllTransactionsResponseDTO;
import org.ylabHomework.DTOs.transactionDTOs.TransactionDTO;
import org.ylabHomework.models.Transaction;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class})
public abstract class GetAllTransactionsMapper {
    @Autowired
    protected TransactionMapper transactionMapper;

    public GetAllTransactionsResponseDTO toDTO(Page<Transaction> transactions) {
        if (transactions == null) {
            return null;
        }

        Page<TransactionDTO> transactionDTOs = transactions.map(transactionMapper::toDTO);
        return new GetAllTransactionsResponseDTO(transactionDTOs);
    }
}