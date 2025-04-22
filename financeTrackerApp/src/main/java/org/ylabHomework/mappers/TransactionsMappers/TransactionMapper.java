package org.ylabHomework.mappers.TransactionsMappers;

import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.CreateTransactionDTO;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.TransactionDTO;
import org.ylabHomework.models.Transaction;

import java.util.List;

/**
 * Маппер, преобразующий JSON-данные транзакции в модель транзакции, модель транзакции в JSON,
 * список DTO в список транзакций.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

     /**
     * Преобразует DTO транзакции в модель транзации.
     *
     * @param transactionDTO DTO, содержащий переданные данные о транзакции
     * @return модель транкзакции
     */
    Transaction toModel(CreateTransactionDTO transactionDTO);

    /**
     * Преобразует модель транзакции в DTO транзакции.
     *
     * @param transaction модель транзакции
     * @return DTO транзакции
     */
    CreateTransactionDTO toDTO(Transaction transaction);

    /**
     * Преобразует список моделей транзации в список DTO транзакций.
     *
     * @param transactions список моделей транзакций
     * @return список DTO транзакций
     */
    List<TransactionDTO> toDTOList(List<Transaction> transactions);
}
