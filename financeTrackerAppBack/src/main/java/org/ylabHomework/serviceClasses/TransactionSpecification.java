package org.ylabHomework.serviceClasses;

import org.springframework.data.jpa.domain.Specification;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.serviceClasses.enums.TypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionSpecification {
    public static Specification<Transaction> hasType(TypeEnum transactionType) {
        return (root, query, builder) -> {

            if (transactionType == null) {
                return null;
            }
            return builder.equal(root.get("type"), transactionType);
        };
    }

    public static Specification<Transaction> sumLessThan(BigDecimal sum) {
        return (root, query, builder) -> {

            if (sum == null) {
                return null;
            }
            return builder.lessThanOrEqualTo(root.get("sum"), sum);
        };
    }

    public static Specification<Transaction> sumMoreThan(BigDecimal sum) {
        return (root, query, builder) -> {

            if (sum == null) {
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("sum"), sum);
        };
    }

    public static Specification<Transaction> dateInPeriod(LocalDateTime start, LocalDateTime end) {
        return (root, query, builder) -> {

            if (start == null || end == null) {
                return null;
            }
            return builder.between(root.get("timestamp"), start, end);
        };
    }
    public static Specification<Transaction> hasCategory(String category) {//todo енум категорий
        return (root, query, builder) -> {

            if (category == null) {
                return null;
            }
            return builder.equal(root.get("category"), category);
        };
    }


}
