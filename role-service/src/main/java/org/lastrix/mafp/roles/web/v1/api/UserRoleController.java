package org.lastrix.mafp.roles.web.v1.api;

import lombok.RequiredArgsConstructor;
import org.lastrix.jwt.Jwt;
import org.lastrix.mafp.roles.client.api.RequireAnyRole;
import org.lastrix.mafp.roles.client.api.RequireRoles;
import org.lastrix.mafp.roles.service.UserRoleService;
import org.lastrix.rest.Rest;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Size;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserRoleController {
    private final UserRoleService service;
    private final Jwt jwt;

    @RequireRoles("RoleManager")
    @PostMapping("/{userId}/roles")
    public Rest<Boolean> setUserRoles(@PathVariable UUID userId, @RequestParam @Size(min = 1, max = 512) Set<String> roles) {
        service.setUserRoles(userId, roles);
        return Rest.ok();
    }

    @RequireRoles("RoleManager")
    @PutMapping("/{userId}/roles")
    public Rest<Boolean> addUserRoles(@PathVariable UUID userId, @RequestParam @Size(min = 1, max = 512) Set<String> roles) {
        service.addUserRoles(userId, roles);
        return Rest.ok();
    }

    @RequireRoles("RoleManager")
    @DeleteMapping("/{userId}/roles")
    public Rest<Boolean> removeUserRoles(@PathVariable UUID userId, @RequestParam @Size(min = 1, max = 512) Set<String> roles) {
        service.removeUserRoles(userId, roles);
        return Rest.ok();
    }

    @RequireRoles("RoleManager")
    @GetMapping("/{userId}/roles")
    public Rest<String> getUserRoles(@PathVariable UUID userId) {
        return Rest.of(service.getUserRoles(userId));
    }

    @RequireAnyRole("User")
    @GetMapping("/roles")
    public Rest<String> getMyRoles() {
        return Rest.of(service.getUserRoles(jwt.getUserId()));
    }
}
