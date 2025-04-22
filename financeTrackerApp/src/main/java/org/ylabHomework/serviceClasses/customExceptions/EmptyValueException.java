package org.ylabHomework.serviceClasses.customExceptions;

public class EmptyValueException extends IllegalArgumentException {
    public EmptyValueException(String param) {
        super("Недопустимое пустое значение (" + param + ")");
    }
}
