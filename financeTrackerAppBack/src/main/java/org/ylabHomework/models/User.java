package org.ylabHomework.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.ylabHomework.serviceClasses.enums.BudgetNotificationStatus;
import org.ylabHomework.serviceClasses.enums.RoleEnum;

import java.math.BigDecimal;
import java.util.Objects;

import static java.util.Objects.hash;

/**
 * Класс, описывающий модель для пользователя.
 * * <p>
 * * Содержит имя пользователя, адрес электронной почты, пароль в закодированном виде,
 * обозначение роли, обозначение, активен ли аккаунт или заблокирован (true/false)
 * и List пользовательских транзакций.
 * * </p>
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 07.03.2025
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "main")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private RoleEnum role;
    private boolean isActive;
    private BigDecimal budgetLimit;
    private String goalName;
    private BigDecimal goalSum;
    @Enumerated(EnumType.STRING)
    private BudgetNotificationStatus budgetNotificationStatus;


    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        User toWork = (User) obj;
        return (Objects.equals(this.email, toWork.email) &
            Objects.equals(this.name, toWork.name) &
            Objects.equals(this.password, toWork.password));
    }


    @Override
    public int hashCode() {
        return hash(this.email, this.name, this.password);
    }

    public User(String name, String email, String password, RoleEnum role, boolean isActive,
                BigDecimal budgetLimit, String goalName, BigDecimal goalSum, BudgetNotificationStatus budgetNotificationStatus) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.isActive = isActive;
        this.budgetLimit = budgetLimit;
        this.goalName = goalName;
        this.goalSum = goalSum;
        this.budgetNotificationStatus = budgetNotificationStatus;
    }
}
