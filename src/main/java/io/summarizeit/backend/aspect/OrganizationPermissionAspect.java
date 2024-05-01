package io.summarizeit.backend.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import io.summarizeit.backend.dto.AdminPermissions;
import io.summarizeit.backend.entity.User;
import io.summarizeit.backend.service.MessageSourceService;
import io.summarizeit.backend.service.PermissionService;
import io.summarizeit.backend.service.UserService;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.List;
import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
public class OrganizationPermissionAspect {
        private final MessageSourceService messageSourceService;

        private final PermissionService permissionService;

        private final UserService userService;

        @Before(value = "@annotation(io.summarizeit.backend.aspect.OrganizationPermission)")
        public void getOrganizationPermission(JoinPoint joinPoint) {
                MethodSignature signature = (MethodSignature) joinPoint.getSignature();
                Method method = signature.getMethod();
                OrganizationPermission organizationPermission = method.getAnnotation(OrganizationPermission.class);
                UUID organizationId = findOrganizationId(joinPoint, signature);
                AdminPermissions[] neededPerms = organizationPermission.permissions();
                User user = userService.getUser();

                if (permissionService.isAdmin(user, organizationId))
                        return;

                if (neededPerms.length != 0) {
                        List<AdminPermissions> perms = permissionService.getAdminPermissions(user, organizationId);
                        if (CollectionUtils.containsAny(perms, List.of(neededPerms)))
                                return;
                }

                throw new AccessDeniedException(messageSourceService.get("permission-error"));
        }

        private UUID findOrganizationId(JoinPoint joinPoint, MethodSignature signature) {
                Object[] args = joinPoint.getArgs();
                String[] parameterNames = signature.getParameterNames();
                for (int i = 0; i < args.length; i++) {
                        if (args[i] instanceof UUID && "organizationId".equals(parameterNames[i])) {
                                return (UUID) args[i];
                        }
                }
                throw new UnsupportedOperationException(messageSourceService.get("permission-aspect"));
        }
}
