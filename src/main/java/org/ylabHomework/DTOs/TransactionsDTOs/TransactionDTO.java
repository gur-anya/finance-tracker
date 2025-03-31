package org.ylabHomework.DTOs.TransactionsDTOs;

public interface TransactionDTO {
    int getType();
    double getSum();
    String getCategory();
    String getDescription();
    String getUserEmail();
}