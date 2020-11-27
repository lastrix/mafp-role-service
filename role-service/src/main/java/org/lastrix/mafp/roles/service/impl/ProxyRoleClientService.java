package org.lastrix.mafp.roles.service.impl;

import lombok.RequiredArgsConstructor;
import org.lastrix.mafp.roles.client.api.RoleClientService;
import org.lastrix.mafp.roles.service.UserRoleService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "mafp.role.cache.enable", havingValue = "false")
public class ProxyRoleClientService implements RoleClientService {
    private final UserRoleService service;

    @Override
    public List<String> getUserRoles(UUID userId) {
        return service.getUserRoles(userId);
    }

    @Override
    public boolean hasRoles(UUID userId, List<String> roles) {
        return service.hasRoles(userId, roles);
    }

    @Override
    public boolean hasAnyRole(UUID userId, List<String> roles) {
        return service.hasAnyRole(userId, roles);
    }
}
