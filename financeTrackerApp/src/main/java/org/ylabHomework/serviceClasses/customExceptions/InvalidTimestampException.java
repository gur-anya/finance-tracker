package org.ylabHomework.serviceClasses.customExceptions;

public class InvalidTimestampException extends IllegalArgumentException {
    public InvalidTimestampException(String s) {
        super(s);
    }
}
