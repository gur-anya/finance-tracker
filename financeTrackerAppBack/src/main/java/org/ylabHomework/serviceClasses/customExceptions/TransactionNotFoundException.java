package org.ylabHomework.serviceClasses.customExceptions;

public class TransactionNotFoundException extends IllegalArgumentException {
    public TransactionNotFoundException() {
        super("Transaction not found");
    }
}
