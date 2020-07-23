package com.howuc.framework.safe.filter;

import com.google.common.collect.Lists;
import com.howuc.framework.safe.filter.exception.NotExposedException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SecureHandlerMethodInterceptorAdapter extends HandlerInterceptorAdapter {
    private static final List<AbstractHandlerMethodInterceptor> HANDLER_METHOD_INTERCEPTORS = Lists.newArrayList(
            new AuthenticationHandlerMethodInterceptor(),
            new PermissionHandlerMethodInterceptor(),
            new RoleHandlerMethodInterceptor(),
            new UserHandlerMethodInterceptor(),
            new GuestHandlerMethodInterceptor()
    );
    private boolean hideNotExposedHandler;

    public SecureHandlerMethodInterceptorAdapter(boolean hideNotExposedHandler) {
        this.hideNotExposedHandler = hideNotExposedHandler;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) return true;

        int supportedInterceptor = 0;

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        for (AbstractHandlerMethodInterceptor handlerMethodInterceptor : HANDLER_METHOD_INTERCEPTORS) {
            if (handlerMethodInterceptor.supported(handlerMethod)) {
                supportedInterceptor++;
                handlerMethodInterceptor.checkPermission(handlerMethod);
            }
        }

        if (hideNotExposedHandler && supportedInterceptor == 0) {
            throw new NotExposedException("request URI is not exposed");
        }

        return true;
    }
}
