package org.lastrix.mafp.roles.client.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public abstract class AbstractCachedRoleClientService implements RoleClientService {
    public AbstractCachedRoleClientService(Duration lifetime, Long maxSize) {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(lifetime)
                .maximumSize(maxSize)
                .build();
    }

    private final Cache<UUID, Set<Integer>> cache;
    private final Map<String, Integer> roleIdMap = new ConcurrentHashMap<>();
    private final AtomicInteger roleIdCounter = new AtomicInteger(0);

    @Override
    public final boolean hasRoles(UUID userId, List<String> roles) {
        var set = toUserRoleIdSet(userId);
        var rs = roles.stream().map(this::toRoleId).collect(Collectors.toSet());
        return set.containsAll(rs);
    }

    @Override
    public final boolean hasAnyRole(UUID userId, List<String> roles) {
        var set = toUserRoleIdSet(userId);
        var rs = roles.stream().map(this::toRoleId).collect(Collectors.toSet());
        return rs.stream().anyMatch(set::contains);
    }

    private Set<Integer> toUserRoleIdSet(UUID userId) {
        try {
            return cache.get(userId, () -> getUserRoleIds(userId));
        } catch (ExecutionException e) {
            throw new IllegalStateException("Unable to resolve user roles: " + userId, e);
        }
    }

    protected final Integer toRoleId(String roleName) {
        return roleIdMap.computeIfAbsent(roleName, ignored -> roleIdCounter.incrementAndGet());
    }

    protected abstract Set<Integer> getUserRoleIds(UUID userId);
}
