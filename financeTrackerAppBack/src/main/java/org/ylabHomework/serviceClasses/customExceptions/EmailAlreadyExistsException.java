package org.ylabHomework.serviceClasses.customExceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("Пользователь с таким email уже зарегистрирован в системе!");
    }
}