package org.lastrix.mafp.roles.client.impl;

import org.lastrix.mafp.roles.model.dto.RoleDto;
import org.lastrix.rest.Rest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequestMapping("/srv/v1")
public interface RoleClient {
    @GetMapping(value = "/user/{userId}/roles/filter")
    Rest<String> getUserRoles(@PathVariable UUID userId, Set<String> whiteList);

    @GetMapping(value = "/user/{userId}/roles")
    Rest<String> getUserRoles(@PathVariable UUID userId);

    @GetMapping(value = "/user/{userId}/check")
    Rest<Boolean> hasRoles(@PathVariable UUID userId, @RequestParam @Size(min = 1, max = 512) List<String> roles);

    @GetMapping(value = "/user/{userId}/check/any")
    Rest<Boolean> hasAnyRole(@PathVariable UUID userId, @RequestParam @Size(min = 1, max = 512) List<String> roles);

    @GetMapping("/role/{roleName}")
    Rest<RoleDto> getRole(@PathVariable String roleName);
}
