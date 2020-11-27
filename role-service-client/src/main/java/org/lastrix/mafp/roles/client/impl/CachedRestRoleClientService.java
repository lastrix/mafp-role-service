package org.lastrix.mafp.roles.client.impl;

import org.lastrix.mafp.roles.client.api.AbstractCachedRoleClientService;
import org.lastrix.mafp.roles.client.api.RoleClientService;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CachedRestRoleClientService extends AbstractCachedRoleClientService implements RoleClientService {
    public CachedRestRoleClientService(RoleClient client, Set<String> roleWhiteList, Duration lifetime, Long maxSize) {
        super(lifetime, maxSize);
        this.clientService = new RestRoleClientService(client);
        this.roleWhiteList = roleWhiteList;
    }

    private final RoleClientService clientService;
    private final Set<String> roleWhiteList;

    @Override
    public List<String> getUserRoles(UUID userId, Set<String> whiteList) {
        return clientService.getUserRoles(userId, whiteList);
    }

    @Override
    public List<String> getUserRoles(UUID userId) {
        return clientService.getUserRoles(userId);
    }

    protected Set<Integer> getUserRoleIds(UUID userId) {
        if (roleWhiteList.isEmpty()) {
            return getUserRoles(userId)
                    .stream()
                    .map(this::toRoleId)
                    .collect(Collectors.toSet());
        }
        return getUserRoles(userId, roleWhiteList)
                .stream()
                .filter(roleWhiteList::contains)
                .map(this::toRoleId)
                .collect(Collectors.toSet());
    }
}
