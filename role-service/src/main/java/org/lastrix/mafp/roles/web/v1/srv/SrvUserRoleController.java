package org.lastrix.mafp.roles.web.v1.srv;

import lombok.RequiredArgsConstructor;
import org.lastrix.jwt.UserType;
import org.lastrix.mafp.roles.client.api.RequireRoles;
import org.lastrix.mafp.roles.client.api.RoleClientService;
import org.lastrix.rest.Rest;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/srv/v1/user")
public class SrvUserRoleController {
    private final RoleClientService service;

    @RequireRoles(value = "ServiceUser", userTypes = UserType.SRV)
    @GetMapping("/{userId}/roles")
    public Rest<String> getUserRoles(@PathVariable UUID userId) {
        return Rest.of(service.getUserRoles(userId));
    }

    @RequireRoles(value = "ServiceUser", userTypes = UserType.SRV)
    @GetMapping("/{userId}/roles/filter")
    public Rest<String> getUserRolesFilter(@PathVariable UUID userId, @RequestParam @Size(min = 1, max = 512) Set<String> whiteList) {
        if (whiteList.isEmpty()) return Rest.of(Collections.emptyList());
        return Rest.of(service.getUserRoles(userId).stream().filter(whiteList::contains).collect(Collectors.toSet()));
    }

    @RequireRoles(value = "ServiceUser", userTypes = UserType.SRV)
    @GetMapping("/{userId}/check")
    public Rest<Boolean> hasRoles(@PathVariable UUID userId, @RequestParam @Size(min = 1, max = 512) List<String> roles) {
        return Rest.of(!roles.isEmpty() && service.hasRoles(userId, roles));
    }

    @RequireRoles(value = "ServiceUser", userTypes = UserType.SRV)
    @GetMapping("/{userId}/check/any")
    public Rest<Boolean> hasAnyRole(@PathVariable UUID userId, @RequestParam @Size(min = 1, max = 512) List<String> roles) {
        return Rest.of(!roles.isEmpty() && service.hasAnyRole(userId, roles));
    }

    @GetMapping("echo")
    public Rest<Boolean> echo() {
        return Rest.ok();
    }
}
