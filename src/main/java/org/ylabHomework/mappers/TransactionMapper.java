package org.ylabHomework.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.models.Transaction;

import java.util.List;

@Mapper
public interface TransactionMapper {
    @Mapping(target = "timestamp", source = "timestamp")
    Transaction toModel(BasicTransactionDTO transactionDTO);

    @Mapping(target = "userEmail", ignore = true)
    BasicTransactionDTO toDTO(Transaction transaction);

    List<BasicTransactionDTO> toDTOList(List<Transaction> transactions);
}
