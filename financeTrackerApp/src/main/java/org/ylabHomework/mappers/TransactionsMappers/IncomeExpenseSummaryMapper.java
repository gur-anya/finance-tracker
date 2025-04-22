package org.ylabHomework.mappers.TransactionsMappers;

import org.mapstruct.Mapper;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.IncomeExpenseSummaryDTO;
/**
 * Маппер, преобразующий массив с данными о сумммарном доходе,
 * расходе и балансе за период в DTO.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 10.04.2025
 */
@Mapper(componentModel = "spring")
public interface IncomeExpenseSummaryMapper {
    /**
     * Преобразует массив с данными о расходе, доходе, балансе за период в DTO.
     *
     * @param incomeExpenseArr входной массив с данными о расходе, доходе и балансе за период
     * @return DTO расходов по категориям
     */

    IncomeExpenseSummaryDTO toDTO(double[] incomeExpenseArr);
}
