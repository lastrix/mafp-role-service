package org.lastrix.mafp.roles.web.v1.api;

import lombok.RequiredArgsConstructor;
import org.lastrix.mafp.roles.client.api.RequireAnyRole;
import org.lastrix.mafp.roles.client.api.RequireRoles;
import org.lastrix.mafp.roles.model.dto.RoleDto;
import org.lastrix.mafp.roles.persistence.mapper.RoleMapper;
import org.lastrix.mafp.roles.service.RoleService;
import org.lastrix.rest.Pagination;
import org.lastrix.rest.Rest;
import org.lastrix.rest.ValidGroup;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
public class RoleController {
    private final RoleService service;
    private final RoleMapper mapper;

    @RequireRoles("RoleManager")
    @PostMapping
    public Rest<RoleDto> create(@RequestBody @Validated(ValidGroup.Create.class) RoleDto role) {
        return Rest.of(service.create(mapper.fromDto(role)), mapper);
    }

    @RequireRoles("RoleManager")
    @PutMapping
    public Rest<RoleDto> update(@RequestBody @Validated(ValidGroup.Create.class) RoleDto role) {
        return Rest.of(service.update(mapper.fromDto(role)), mapper);
    }

    @RequireAnyRole("RoleManager")
    @GetMapping("/{roleName}")
    public Rest<RoleDto> getRole(@PathVariable String roleName) {
        return Rest.of(service.getByName(roleName), mapper);
    }

    @RequireRoles("RoleManager")
    @GetMapping("/page")
    public Rest<RoleDto> page(@ModelAttribute @Validated(ValidGroup.Read.class) Pagination pagination) {
        return Rest.of(service.page(pagination),
                pagination,
                mapper
        );
    }

    @RequireRoles("RoleManager")
    @PostMapping("/page")
    public Rest<RoleDto> pagePost(@RequestBody @Validated(ValidGroup.Read.class) Pagination pagination) {
        return Rest.of(service.page(pagination),
                pagination,
                mapper
        );
    }
}
