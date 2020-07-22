package com.howuc.framework.safe.filter.exception;

public class LackPermissionException extends AuthorizingException {
    public LackPermissionException(String message) {
        super(message);
    }
}
