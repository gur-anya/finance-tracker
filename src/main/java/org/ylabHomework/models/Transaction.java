package org.ylabHomework.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
/**
 * Класс, описывающий модель для транзакций.
 *  * <p>
 *  * Содержит тип, сумму, категорию транзакции, дату создания и описание.
 *  * </p>
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 07.03.2025
 */
@Getter
@Setter
public class Transaction {
    private TransactionTYPE type;
    private double sum;
    private String category;
    private LocalDateTime timestamp;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Double.compare(sum, that.sum) == 0 && type == that.type &&
                Objects.equals(category, that.category) && Objects.equals(timestamp, that.timestamp)
                && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, sum, category, timestamp, description);
    }

    public Transaction(TransactionTYPE type, double sum, String category, String description) {
        this.type = type;
        this.sum = sum;
        this.category = category;
        this.timestamp = LocalDateTime.now();
        this.description = description;
    }

    public enum TransactionTYPE {
        INCOME,
        EXPENSE
    }
}
