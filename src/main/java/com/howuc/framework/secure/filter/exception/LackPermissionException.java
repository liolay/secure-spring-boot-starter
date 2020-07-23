package com.howuc.framework.secure.filter.exception;

public class LackPermissionException extends AuthorizingException {
    public LackPermissionException(String message) {
        super(message);
    }
}
