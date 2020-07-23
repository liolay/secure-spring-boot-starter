package com.howuc.framework.secure.filter.exception;

public class UnauthenticatedException extends AuthorizingException {
    public UnauthenticatedException(String message) {
        super(message);
    }
}
