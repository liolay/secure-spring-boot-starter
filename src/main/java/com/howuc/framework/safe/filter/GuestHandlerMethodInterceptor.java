package com.howuc.framework.safe.filter;

import com.howuc.framework.safe.SessionManager;
import com.howuc.framework.safe.filter.annotation.RequiresGuest;
import com.howuc.framework.safe.filter.annotation.RequiresUser;
import com.howuc.framework.safe.filter.exception.AuthorizingException;
import com.howuc.framework.safe.filter.exception.LacksIdentifyException;
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
