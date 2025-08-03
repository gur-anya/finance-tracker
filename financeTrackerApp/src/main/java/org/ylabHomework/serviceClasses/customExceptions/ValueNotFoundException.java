package org.ylabHomework.serviceClasses.customExceptions;

public class ValueNotFoundException extends RuntimeException {
    public ValueNotFoundException(String value) {
        super("Failed to get " + value);
    }
}
