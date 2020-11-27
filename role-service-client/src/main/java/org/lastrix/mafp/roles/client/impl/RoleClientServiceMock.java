package org.lastrix.mafp.roles.client.impl;

import lombok.extern.slf4j.Slf4j;
import org.lastrix.mafp.roles.client.api.RoleClientService;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
public class RoleClientServiceMock implements RoleClientService {
    @Override
    public List<String> getUserRoles(UUID userId, Set<String> whiteList) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getUserRoles(UUID userId) {
        return Collections.emptyList();
    }

    @Override
    public boolean hasRoles(UUID userId, List<String> roles) {
        return true;
    }

    @Override
    public boolean hasAnyRole(UUID userId, List<String> roles) {
        return true;
    }
}
