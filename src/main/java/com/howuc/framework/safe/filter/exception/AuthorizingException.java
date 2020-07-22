package com.howuc.framework.safe.filter.exception;

public class AuthorizingException extends Exception {
    public AuthorizingException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return null;
    }
}
