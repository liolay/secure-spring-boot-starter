package com.howuc.framework.secure.filter;

import com.howuc.framework.secure.SessionManager;
import com.howuc.framework.secure.filter.annotation.RequiresUser;
import com.howuc.framework.secure.filter.exception.AuthorizingException;
import com.howuc.framework.secure.filter.exception.LacksIdentifyException;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;

public class UserHandlerMethodInterceptor extends AbstractHandlerMethodInterceptor {
    public UserHandlerMethodInterceptor() {
        super(RequiresUser.class);
    }

    public UserHandlerMethodInterceptor(Class<? extends Annotation> supportedAnnotation) {
        super(supportedAnnotation);
    }

    @Override
    public void checkPermission(HandlerMethod handlerMethod) throws AuthorizingException {

        if (SessionManager.getSubjectId() == null) {
            throw new LacksIdentifyException("lacks identify exception");
        }
    }
}
