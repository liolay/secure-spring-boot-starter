package com.howuc.framework.safe.filter.exception;

public class NotExposedException extends AuthorizingException {
    public NotExposedException(String message) {
        super(message);
    }
}
