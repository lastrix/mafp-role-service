package org.lastrix.mafp.roles.client.impl;

import lombok.RequiredArgsConstructor;
import org.lastrix.http.client.api.AbstractRestService;
import org.lastrix.mafp.roles.client.api.RoleClientService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class RestRoleClientService extends AbstractRestService implements RoleClientService {
    private final RoleClient client;

    @Override
    public List<String> getUserRoles(UUID userId, Set<String> whiteList) {
        return allResults(() -> client.getUserRoles(userId, whiteList));
    }

    @Override
    public List<String> getUserRoles(UUID userId) {
        return allResults(() -> client.getUserRoles(userId));
    }

    @Override
    public boolean hasRoles(UUID userId, List<String> roles) {
        return singleResult(() -> client.hasRoles(userId, roles));
    }

    @Override
    public boolean hasAnyRole(UUID userId, List<String> roles) {
        return singleResult(() -> client.hasAnyRole(userId, roles));
    }
}
