package org.ylabHomework.serviceClasses.customExceptions;

public class TransactionNotFoundException extends IllegalArgumentException {
    public TransactionNotFoundException() {
        super("Транзакция не найдена!");
    }
}
