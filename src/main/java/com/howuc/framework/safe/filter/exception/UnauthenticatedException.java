package com.howuc.framework.safe.filter.exception;

public class UnauthenticatedException extends AuthorizingException {
    public UnauthenticatedException(String message) {
        super(message);
    }
}
