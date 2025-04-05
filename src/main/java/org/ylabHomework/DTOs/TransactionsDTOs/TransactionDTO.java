package org.ylabHomework.DTOs.TransactionsDTOs;
/**
 * Интерфейс DTO для транзакций. Содержит основные поля транзакции и дополнительно email пользователя, которому
 * принадлежит эта транзакция.
 *
 * @author Gureva Anna
 * @version 1.0
 * @since 30.03.2025
 */
public interface TransactionDTO {
    int getType();
    double getSum();
    String getCategory();
    String getDescription();
    String getUserEmail();
}