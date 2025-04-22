package org.ylabHomework.serviceClasses.customExceptions;

public class CustomDatabaseException extends RuntimeException {
    public CustomDatabaseException(Throwable cause) {
        super("Произошла ошибка при создании пользователя. Подробная информация - в логах");
    }
}
