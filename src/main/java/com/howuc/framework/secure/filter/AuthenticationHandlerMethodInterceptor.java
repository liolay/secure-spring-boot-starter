package com.howuc.framework.secure.filter;

import com.howuc.framework.secure.SessionManager;
import com.howuc.framework.secure.filter.annotation.RequiresAuthentication;
import com.howuc.framework.secure.filter.exception.AuthorizingException;
import com.howuc.framework.secure.filter.exception.UnauthenticatedException;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;

public class AuthenticationHandlerMethodInterceptor extends UserHandlerMethodInterceptor {
    public AuthenticationHandlerMethodInterceptor() {
        super(RequiresAuthentication.class);
    }

    public AuthenticationHandlerMethodInterceptor(Class<? extends Annotation> supportedAnnotation) {
        super(supportedAnnotation);
    }

    @Override
    public void checkPermission(HandlerMethod handlerMethod) throws AuthorizingException {
        super.checkPermission(handlerMethod);

        if (!SessionManager.isLogin()) throw new UnauthenticatedException("session is not authenticated!");
    }
}
