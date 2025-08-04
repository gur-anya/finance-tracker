package org.ylabHomework.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ylabHomework.serviceClasses.enums.TypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Класс, описывающий модель для транзакций.
 * * <p>
 * * Содержит тип, сумму, категорию транзакции, дату создания и описание.
 * * </p>
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 07.03.2025
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions", schema = "main")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TypeEnum type;
    private BigDecimal sum;
    private String category;
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private LocalDateTime timestamp;


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Transaction that = (Transaction) object;
        return Objects.equals(id, that.id) && type == that.type && Objects.equals(sum, that.sum) && Objects.equals(category, that.category) && Objects.equals(description, that.description) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, sum, category, description, user);
    }
}
