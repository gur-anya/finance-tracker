package org.ylabHomework.serviceClasses.customExceptions;

public class NoUpdatesException extends IllegalArgumentException {
    public NoUpdatesException(String param) {
        super("В " + param + " не внесено ни одного изменения!");
    }
}
