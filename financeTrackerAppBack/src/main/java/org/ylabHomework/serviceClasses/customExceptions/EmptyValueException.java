package org.ylabHomework.serviceClasses.customExceptions;

public class EmptyValueException extends IllegalArgumentException {
    public EmptyValueException(String param) {
        super("Invalid empty value (" + param + ")");
    }
}
