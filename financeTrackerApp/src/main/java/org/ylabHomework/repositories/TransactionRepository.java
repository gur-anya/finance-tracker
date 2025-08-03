package org.ylabHomework.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.ylabHomework.DTOs.transactionStatisticsDTOs.CategoryStatDTO;
import org.ylabHomework.models.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с сущностью Transaction через базу данных.
 * <p>
 * * @author Gureva Anna
 * * @version 1.0
 * * @since 01.08.2025
 * </p>
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT SUM(CASE WHEN trans.type = 'INCOME' THEN trans.sum ELSE -trans.sum END) FROM Transaction AS trans WHERE " +
        "trans.timestamp >= :startTimestamp AND trans.timestamp <= :endTimestamp AND trans.category != 'цель' AND trans.user = :userId")
    Optional<BigDecimal> getBalanceForPeriod(Long userId, LocalDateTime startTimestamp, LocalDateTime endTimestamp);

    @Query("SELECT SUM(trans.sum) FROM Transaction AS trans WHERE" +
        " trans.timestamp >= :startTimestamp AND trans.timestamp <= :endTimestamp AND trans.type = 'INCOME' AND trans.category != 'цель' AND trans.user = :userId")
    Optional<BigDecimal> getIncomesForPeriod(Long userId, LocalDateTime startTimestamp, LocalDateTime endTimestamp);

    @Query("SELECT SUM(trans.sum) FROM Transaction AS trans WHERE" +
        " trans.timestamp >= :startTimestamp AND trans.timestamp <= :endTimestamp AND trans.type = 'EXPENSE' AND trans.category != 'цель' AND trans.user = :userId")
    Optional<BigDecimal> getExpensesForPeriod(Long userId, LocalDateTime startTimestamp, LocalDateTime endTimestamp);

    @Query("SELECT new org.ylabHomework.DTOs.transactionStatisticsDTOs.CategoryStatDTO(trans.category, SUM(trans.sum)) FROM Transaction AS trans WHERE" +
        " trans.timestamp >= :startTimestamp AND trans.timestamp <= :endTimestamp AND trans.type = 'INCOME' AND trans.category != 'цель' AND trans.user = :userId GROUP BY trans.category")
    List<CategoryStatDTO> getIncomesForPeriodGroupByCategories(Long userId, LocalDateTime startTimestamp, LocalDateTime endTimestamp);

    @Query("SELECT new org.ylabHomework.DTOs.transactionStatisticsDTOs.CategoryStatDTO(trans.category, SUM(trans.sum)) FROM Transaction AS trans WHERE" +
        " trans.timestamp >= :startTimestamp AND trans.timestamp <= :endTimestamp AND trans.type = 'EXPENSE' AND trans.category != 'цель' AND trans.user = :userId GROUP BY trans.category")
    List<CategoryStatDTO> getExpensesForPeriodGroupByCategories(Long userId, LocalDateTime startTimestamp, LocalDateTime endTimestamp);

    @Query("SELECT  CASE WHEN us.budgetLimit > 0 THEN  (100.0 - SUM(CASE WHEN trans.type = 'INCOME' THEN trans.sum ELSE -trans.sum END)/us.budgetLimit*100) ELSE 0.0 END " +
        "FROM Transaction AS trans " +
        "JOIN User AS us ON trans.user.id=us.id WHERE trans.user = :userId AND trans.category != 'цель' ")
    BigDecimal getPercentageLeftToReachBudgetLimit(Long userId);

    @Modifying
    @Query("DELETE FROM Transaction trans WHERE trans.category = :category AND trans.user.id = :userId")
    void deleteByCategoryAndUserId(String category, Long userId);

    @Query("SELECT  CASE WHEN us.goal > 0 THEN  (100.0 - SUM(CASE WHEN trans.type = 'INCOME' THEN trans.sum ELSE -trans.sum END)/us.goal*100) ELSE 0.0 END " +
        "FROM Transaction AS trans " +
        "JOIN User AS us ON trans.user.id=us.id WHERE trans.user = :userId AND trans.category = 'цель' ")
    BigDecimal checkGoal(Long userId);
}
