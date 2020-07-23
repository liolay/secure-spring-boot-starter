package com.howuc.framework.secure.filter.exception;

public class LacksIdentifyException extends AuthorizingException {
    public LacksIdentifyException(String message) {
        super(message);
    }
}
