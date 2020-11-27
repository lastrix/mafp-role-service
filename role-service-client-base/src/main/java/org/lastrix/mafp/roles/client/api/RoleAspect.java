package org.lastrix.mafp.roles.client.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.lastrix.jwt.Jwt;
import org.lastrix.jwt.UserType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
@Slf4j
@Aspect
@RequiredArgsConstructor
@ConditionalOnProperty(name = "mafp.role.enable", havingValue = "true", matchIfMissing = true)
public class RoleAspect {
    private final RoleClientService roleClientService;
    private final Jwt jwt;
    @Value("${mafp.role.jwt.use:true}")
    private Boolean allowJwtRoles;

    @Around(value = "@annotation(requireRoles)", argNames = "joinPoint,requireRoles")
    public Object intercept(ProceedingJoinPoint joinPoint, RequireRoles requireRoles) throws Throwable {
        return checkedAccessFor(joinPoint, requireRoles.userTypes(), requireRoles.value(), false);
    }

    @Around(value = "@annotation(requireAnyRole)", argNames = "joinPoint,requireAnyRole")
    public Object interceptAny(ProceedingJoinPoint joinPoint, RequireAnyRole requireAnyRole) throws Throwable {
        return checkedAccessFor(joinPoint, requireAnyRole.userTypes(), requireAnyRole.value(), true);
    }

    private Object checkedAccessFor(ProceedingJoinPoint joinPoint, UserType[] userTypes, String[] roles, boolean any) throws Throwable {
        if (roles == null || roles.length == 0) throw new IllegalArgumentException("No roles supplied");
        UserType userType = jwt.getUserType();
        if (userTypes.length != 0 && !hasAccountType(userTypes, userType))
            throw new IllegalStateException("Not access for account type '" + userType + "' to: " + joinPoint.getSignature());

        var id = resolveUserId(joinPoint, userType);
        List<String> roleList = Arrays.asList(roles);
        boolean allow = allowJwtRoles && isAllowedByJwt(any, roleList) || isAllowedByRest(id, any, roleList);
        if (!allow) throw new IllegalStateException("No access to:  " + joinPoint.getSignature());
        return joinPoint.proceed();
    }

    private boolean isAllowedByJwt(boolean any, List<String> roleList) {
        var roles = jwt.getRoles();
        return any ? roleList.stream().anyMatch(roles::contains)
                : roles.containsAll(roleList);
    }

    private boolean isAllowedByRest(UUID id, boolean any, List<String> roleList) {
        return any ? roleClientService.hasAnyRole(id, roleList)
                : roleClientService.hasRoles(id, roleList);
    }

    private boolean hasAccountType(UserType[] types, UserType type) {
        for (UserType t : types) {
            if (t == type) return true;
        }
        return false;
    }

    private UUID resolveUserId(ProceedingJoinPoint joinPoint, UserType type) {
        UUID id = jwt.getUserId();
        log.trace("Processing access for {}:{} to method {}", type, id, joinPoint.getSignature());
        return id;
    }
}
