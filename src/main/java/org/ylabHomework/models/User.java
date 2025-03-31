package org.ylabHomework.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.hash;

/**
 * Класс, описывающий модель для пользователя.
 * * <p>
 * * Содержит имя пользователя, адрес электронной почты, пароль в закодированном виде,
 * обозначение роли, обозначение, активен ли аккаунт или заблокирован (true/false)
 * и Set пользовательских транзакций.
 * * </p>
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 07.03.2025
 */

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

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public int getRole() {
        return this.role;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public double getMonthlyBudget() {
        return this.monthlyBudget;
    }

    public double getGoal() {
        return this.goal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void setMonthlyBudget(double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public String toString() {
        return "User(name=" + this.getName() + ", email=" + this.getEmail() + ", password=" + this.getPassword() + ", role=" + this.getRole() + ", isActive=" + this.isActive() + ", transactions=" + this.getTransactions() + ", monthlyBudget=" + this.getMonthlyBudget() + ", goal=" + this.getGoal() + ")";
    }
}