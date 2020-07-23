package com.howuc.framework.secure.filter;

import com.google.common.base.Joiner;
import com.howuc.framework.secure.SessionManager;
import com.howuc.framework.secure.filter.annotation.Logical;
import com.howuc.framework.secure.filter.annotation.RequiresRoles;
import com.howuc.framework.secure.filter.exception.AuthorizingException;
import com.howuc.framework.secure.filter.exception.LackPermissionException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

public class RoleHandlerMethodInterceptor extends AuthenticationHandlerMethodInterceptor {
    public RoleHandlerMethodInterceptor() {
        super(RequiresRoles.class);
    }

    @Override
    public void checkPermission(HandlerMethod handlerMethod) throws AuthorizingException {
        super.checkPermission(handlerMethod);

        RequiresRoles permissionAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequiresRoles.class);
        permissionAnnotation = permissionAnnotation != null ? permissionAnnotation : AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RequiresRoles.class);

        if (permissionAnnotation.logical() == Logical.AND) {
            if (!SessionManager.hasRole(permissionAnnotation.value())) {
                throw new LackPermissionException("lacks role,require:" + Joiner.on(",").join(permissionAnnotation.value()));
            }
            return;
        }

        if (!SessionManager.hasAnyRole(permissionAnnotation.value())) {
            throw new LackPermissionException("lacks role,require:" + Joiner.on(",").join(permissionAnnotation.value()));
        }
    }
}
