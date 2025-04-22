package org.ylabHomework.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private int id;
    private String name;
    private String email;
    private String password;
    private int role;
    private boolean isActive;
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

}
