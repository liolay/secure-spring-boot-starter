package com.howuc.framework.secure.filter;

import com.howuc.framework.secure.filter.annotation.RequiresGuest;
import com.howuc.framework.secure.filter.exception.AuthorizingException;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;

public class GuestHandlerMethodInterceptor extends AbstractHandlerMethodInterceptor {
    public GuestHandlerMethodInterceptor() {
        super(RequiresGuest.class);
    }

    public GuestHandlerMethodInterceptor(Class<? extends Annotation> supportedAnnotation) {
        super(supportedAnnotation);
    }

    @Override
    public void checkPermission(HandlerMethod handlerMethod) throws AuthorizingException {
    }
}
