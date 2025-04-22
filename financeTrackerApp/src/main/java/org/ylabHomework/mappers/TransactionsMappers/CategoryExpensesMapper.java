package org.ylabHomework.mappers.TransactionsMappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.CategoryExpensesDTO;

import java.util.Map;
/**
 * * Маппер, преобразующий Map расходов по категориям в DTO расходов по категориям.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 10.04.2025
 */
@Mapper(componentModel = "spring")
public interface CategoryExpensesMapper {
    /**
     * Преобразует карту расходов по категориям в DTO.
     *
     * @param map расходы по категориям
     * @return DTO расходов по категориям
     */
    @Mapping(target = "categoryExpenses", source = "map")
    CategoryExpensesDTO toDTO(Map<String, Double> map);
}
