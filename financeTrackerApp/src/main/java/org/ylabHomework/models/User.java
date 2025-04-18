package org.ylabHomework.models;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String name;
    private String email;
    private String password;
    private int role;
    private boolean isActive;
    private List<Transaction> transactions;
    private double monthlyBudget;
    private double goal;


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

    public User(String name, String email, String password, int role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.isActive = true;
        this.role = role;
        this.transactions = new ArrayList<>();
        this.monthlyBudget = 0;
        this.goal = 0;
    }

    public String toString() {
        return "User(name=" + this.getName() + ", email=" + this.getEmail() + ", password=" + this.getPassword() + ", role=" + this.getRole() + ", isActive=" + this.isActive() + ", transactions=" + this.getTransactions() + ", monthlyBudget=" + this.getMonthlyBudget() + ", goal=" + this.getGoal() + ")";
    }
}