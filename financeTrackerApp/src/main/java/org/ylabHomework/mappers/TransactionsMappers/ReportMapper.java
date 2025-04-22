package org.ylabHomework.mappers.TransactionsMappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.ylabHomework.DTOs.TransactionsDTOs.controllerDTOs.ReportDTO;
import org.ylabHomework.services.TransactionStatsService;

/**
 * Маппер, преобразующий данные финансового отчета в DTO финансового отчета.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 10.04.2025
 */
@Mapper(componentModel = "spring")
public interface ReportMapper {

    /**
     * Преобразует объект финансового отчета в DTO финансового отчета
     *
     * @param report финансовый отчет
     * @return DTO финансового отчета
     */
    @Mapping(target = "basicStats", source = ".")
    @Mapping(target = "categoryReport", source = "categoryReport")
    @Mapping(target = "goalData", source = "goalData")
    ReportDTO toReportResponseDTO(TransactionStatsService.FinancialReport report);

    /**
     * Получает DTO базовой статистики (доход, расход, баланс) из объекта финансового отчета
     *
     * @param report объект финансового отчета
     * @return DTO базовой статистики
     */
    ReportDTO.BasicStatsDTO toBasicStatsDTO(TransactionStatsService.FinancialReport report);

    /**
     * Преобразует массив с данными о доходе, расходе и балансе по категориям в DTO.
     *
     * @param stats массив с данными о доходе, расходе и балансе по категориям
     * @return DTO статистики по категориям
     */
    @Mapping(target = "income", expression = "java(stats[0])")
    @Mapping(target = "expense", expression = "java(stats[1])")
    @Mapping(target = "balance", expression = "java(stats[2])")
    ReportDTO.CategoryStatsDTO toCategoryStatsDTO(double[] stats);

    /**
     * Преобразует массив с данными по цели в DTO.
     *
     * @param goalData массив с данными о сумме цели, доходе, расходе и оставшихся для накопления средствах
     * @return DTO данных по цели
     */
    @Mapping(target = "goalSum", expression = "java(goalData[0])")
    @Mapping(target = "goalIncome", expression = "java(goalData[1])")
    @Mapping(target = "goalExpense", expression = "java(goalData[2])")
    @Mapping(target = "saved", expression = "java(goalData[3])")
    @Mapping(target = "left", expression = "java(goalData[4])")
    ReportDTO.GoalDataDTO toGoalDataDTO(double[] goalData);
}