package com.howuc.framework.secure.filter;

import com.howuc.framework.secure.filter.exception.AuthorizingException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AbstractHandlerMethodInterceptor {
    private final Class<? extends Annotation> supportedAnnotation;

    protected AbstractHandlerMethodInterceptor(Class<? extends Annotation> supportedAnnotation) {
        this.supportedAnnotation = supportedAnnotation;
    }

    public boolean supported(HandlerMethod handlerMethod) {
        if (supportedAnnotation == null) return false;

        return containsClassAnnotation(handlerMethod.getBeanType(), supportedAnnotation) || containsMethodAnnotation(handlerMethod.getMethod(), supportedAnnotation);
    }

    private boolean containsClassAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return AnnotationUtils.findAnnotation(clazz, annotationClass) != null;
    }

    private boolean containsMethodAnnotation(Method method, Class<? extends Annotation> annotationClass) {
        return AnnotationUtils.findAnnotation(method, annotationClass) != null;
    }

    public abstract void checkPermission(HandlerMethod handlerMethod) throws AuthorizingException;
}
