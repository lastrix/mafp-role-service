package org.lastrix.mafp.roles.web.v1.srv;

import lombok.RequiredArgsConstructor;
import org.lastrix.jwt.UserType;
import org.lastrix.mafp.roles.client.api.RequireAnyRole;
import org.lastrix.mafp.roles.model.dto.RoleDto;
import org.lastrix.mafp.roles.persistence.mapper.RoleMapper;
import org.lastrix.mafp.roles.service.RoleService;
import org.lastrix.rest.Rest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/srv/v1/role")
public class SrvRoleController {
    private final RoleService service;
    private final RoleMapper mapper;

    @RequireAnyRole(value = "ServiceUser", userTypes = UserType.SRV)
    @GetMapping("/{roleName}")
    public Rest<RoleDto> getRole(@PathVariable String roleName) {
        return Rest.of(service.getByName(roleName), mapper);
    }
}
