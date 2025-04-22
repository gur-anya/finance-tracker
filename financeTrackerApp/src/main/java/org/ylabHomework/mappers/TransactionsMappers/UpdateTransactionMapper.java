package org.ylabHomework.mappers.TransactionsMappers;

import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.UpdateTransactionDTO;
import org.ylabHomework.models.Transaction;

/**
 * Маппер, преобразующий JSON, переданный при изменении данных транзакций,
 * в модель обновленной транзакций.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
@Mapper(componentModel = "spring")
public interface UpdateTransactionMapper {
    /**
     * Преобразует DTO обновленных данных в модель транзакции.
     *
     * @param updateTransactionDTO DTO, содержащий данные, переданные при обновлении транзакции
     * @return модель транзакции
     */
    Transaction toModel(UpdateTransactionDTO updateTransactionDTO);
}
