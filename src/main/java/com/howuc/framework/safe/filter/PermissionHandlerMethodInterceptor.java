package com.howuc.framework.safe.filter;

import com.google.common.base.Joiner;
import com.howuc.framework.safe.SessionManager;
import com.howuc.framework.safe.filter.exception.AuthorizingException;
import com.howuc.framework.safe.filter.exception.LackPermissionException;
import com.howuc.framework.safe.filter.annotation.Logical;
import com.howuc.framework.safe.filter.annotation.RequiresPermissions;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

public class PermissionHandlerMethodInterceptor extends AuthenticationHandlerMethodInterceptor {
    public PermissionHandlerMethodInterceptor() {
        super(RequiresPermissions.class);
    }

    @Override
    public void checkPermission(HandlerMethod handlerMethod) throws AuthorizingException {
        super.checkPermission(handlerMethod);

        RequiresPermissions permissionAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequiresPermissions.class);
        permissionAnnotation = permissionAnnotation != null ? permissionAnnotation : AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RequiresPermissions.class);

        String[] requiredPmsCode = permissionAnnotation.value();

        if (ArrayUtils.isEmpty(requiredPmsCode)) {
            String defaultPmsCode = "";
            RequestMapping controllerMapping = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RequestMapping.class);
            if (controllerMapping != null && ArrayUtils.isNotEmpty(controllerMapping.value())) {
                defaultPmsCode += Joiner.on(",").join(controllerMapping.value());
            }

            RequestMapping methodMapping = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RequestMapping.class);
            if (methodMapping != null && ArrayUtils.isNotEmpty(methodMapping.value())) {
                defaultPmsCode += Joiner.on(",").join(methodMapping.value());
            }

            if (StringUtils.isNotBlank(defaultPmsCode)) {
                requiredPmsCode = new String[]{defaultPmsCode};
            }
        }

        if (permissionAnnotation.logical() == Logical.AND) {
            if (!SessionManager.hasPermission(requiredPmsCode)) {
                throw new LackPermissionException("lacks permission,require:" + Joiner.on(",").join(requiredPmsCode));
            }
            return;
        }

        if (!SessionManager.hasAnyPermission(requiredPmsCode)) {
            throw new LackPermissionException("lacks permission,require:" + Joiner.on(",").join(requiredPmsCode));
        }
    }
}
