package org.ylabHomework.serviceClasses.customExceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("User with this email is already registered in the system");
    }
}