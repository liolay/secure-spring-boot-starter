package com.howuc.framework.safe.filter;

import com.howuc.framework.safe.SessionManager;
import com.howuc.framework.safe.filter.annotation.RequiresAuthentication;
import com.howuc.framework.safe.filter.exception.AuthorizingException;
import com.howuc.framework.safe.filter.exception.UnauthenticatedException;
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
