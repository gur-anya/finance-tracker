package org.ylabHomework.mappers.transactionMappers;

import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.transactionDTOs.TransactionDTO;
import org.ylabHomework.models.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    /**
     * Преобразует DTO транзакции в модель транзации.
     *
     * @param transactionDTO DTO, содержащий переданные данные о транзакции
     * @return модель транкзакции
     */
    Transaction toModel(TransactionDTO transactionDTO);

    /**
     * Преобразует модель транзакции в DTO транзакции.
     *
     * @param transaction модель транзакции
     * @return DTO транзакции
     */
    TransactionDTO toDTO(Transaction transaction);
}
