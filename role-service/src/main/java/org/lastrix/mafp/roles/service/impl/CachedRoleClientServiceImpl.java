package org.lastrix.mafp.roles.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.lastrix.mafp.roles.client.api.AbstractCachedRoleClientService;
import org.lastrix.mafp.roles.service.UserRoleService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@ConditionalOnProperty(value = "mafp.role.cache.enable", matchIfMissing = true)
@Service
public class CachedRoleClientServiceImpl extends AbstractCachedRoleClientService {
    public CachedRoleClientServiceImpl(
            UserRoleService service,
            @Value("${mafp.role.cache.lifetime:300000}") Long lifetime,
            @Value("${mafp.role.cache.max-size:100000}") Long maxSize
    ) {
        super(Duration.ofSeconds(lifetime), maxSize);
        this.service = service;
    }

    private final UserRoleService service;

    public List<String> getUserRoles(UUID userId) {
        return service.getUserRoles(userId);
    }

    @Override
    protected Set<Integer> getUserRoleIds(UUID userId) {
        return getUserRoles(userId).stream()
                .map(this::toRoleId)
                .collect(Collectors.toSet());
    }
}
