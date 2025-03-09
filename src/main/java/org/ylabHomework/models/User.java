package org.ylabHomework.models;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Класс, описывающий модель для пользователя.
 *  * <p>
 *  * Содержит имя пользователя, адрес электронной почты, пароль в закодированном виде,
 *  обозначение роли, обозначение, активен ли аккаунт или заблокирован (true/false)
 *  и Set пользовательских транзакций.
 *  * </p>
 *
 *   @author Gureva Anna
 *   @version 1.0
 *   @since 07.03.2025
 */
@Getter
@Setter
public class User {
    private String name;
    private String email;
    private String password;
    private int role;
    private boolean isActive;
    private List<Transaction> transactions;
    private double monthlyBudget;
    private double goal;

    /**
     * Переопределение метода equals для корректного сравнения пользователей.
     * @param obj пользователь, с которым производится сравнение
     * @return true, если пользователи имеют одинаковое имя, адрес почты и пароль (являются одним и тем же пользователем), false - иначе.
     */
    @Override
    public boolean equals(Object obj) {
        User toWork = (User) obj;
        return (Objects.equals(this.email, toWork.email) &
                Objects.equals(this.name, toWork.name) &
                Objects.equals(this.password, toWork.password));
    }

    /**
     * Конструктор для создания нового пользователя. List для хранения транзакций создается в конструкторе.
     * @param name имя пользователя
     * @param email адрес электронной почты пользователя
     * @param password закодированный пароль
     * @param role роль пользователя (0 - администратор, 1 - обычный пользователь)
     */
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
}