package org.ylabHomework.mappers.TransactionsMappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylabHomework.DTOs.TransactionsDTOs.BasicTransactionDTO;
import org.ylabHomework.models.Transaction;

import java.util.List;
/**
 *  Маппер, преобразующий JSON-данные транзакции в модель транзакции, модель транзакции в JSON,
 *  список DTO в список транзакций.
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 30.03.2025
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    /**
     * Преобразует DTO транзакции в модель транзации.
     *
     * @param transactionDTO DTO, содержащий переданные данные о транзакции
     * @return модель транкзакции
     */
    Transaction toModel(BasicTransactionDTO transactionDTO);
    /**
     * Преобразует модель транзакции в DTO транзакции.
     *
     * @param transaction модель транзакции
     * @return DTO транзакции
     */
    BasicTransactionDTO toDTO(Transaction transaction);

    /**
     * Преобразует список моделей транзации в список DTO транзакций.
     *
     * @param transactions список моделей транзакций
     * @return список DTO транзакций
     */
    List<BasicTransactionDTO> toDTOList(List<Transaction> transactions);
}
