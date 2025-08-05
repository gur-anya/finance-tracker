package org.ylabHomework.serviceClasses.customExceptions;

public class TokenException extends IllegalArgumentException {
    public TokenException(Throwable e) {
        super(e);
    }
}
