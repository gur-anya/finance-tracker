package org.ylabHomework.serviceClasses;

import org.springframework.data.jpa.domain.Specification;
import org.ylabHomework.models.Transaction;
import org.ylabHomework.models.User;
import org.ylabHomework.serviceClasses.enums.CategoryEnum;
import org.ylabHomework.serviceClasses.enums.TypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionSpecification {
    public static Specification<Transaction> hasUser(User user) {
        return (root, query, builder) -> builder.equal(root.get("user"), user);
    }

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

    public static Specification<Transaction> dateAfter(LocalDateTime start) {
        return (root, query, builder) -> {

            if (start == null) {
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("timestamp"), start);
        };
    }

    public static Specification<Transaction> dateBefore(LocalDateTime end) {
        return (root, query, builder) -> {

            if (end == null) {
                return null;
            }
            return builder.lessThanOrEqualTo(root.get("timestamp"), end);
        };
    }
    public static Specification<Transaction> hasCategory(CategoryEnum category) {
        return (root, query, builder) -> {

            if (category == null) {
                return null;
            }
            return builder.equal(root.get("category"), category);
        };
    }


}
