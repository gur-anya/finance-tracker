package org.ylabHomework.mappers.transactionMappers;

import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.ylabHomework.DTOs.transactionDTOs.GetAllTransactionsResponseDTO;
import org.ylabHomework.DTOs.transactionDTOs.TransactionDTO;
import org.ylabHomework.models.Transaction;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {TransactionMapper.class})
public abstract class GetAllTransactionsMapper {
    @Autowired
    protected TransactionMapper transactionMapper;

    public GetAllTransactionsResponseDTO toDTO(List<Transaction> transactions) {
        if (transactions == null) {
            return null;
        }

        List<TransactionDTO> transactionDTOs = transactions.stream()
            .map(transactionMapper::toDTO)
            .collect(Collectors.toList());

        return new GetAllTransactionsResponseDTO(transactionDTOs);
    }
}