package org.lastrix.mafp.roles.client.api;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public interface RoleClientService {

    default List<String> getUserRoles(UUID userId, Set<String> whiteList) {
        return getUserRoles(userId).stream()
                .filter(whiteList::contains)
                .collect(Collectors.toList());
    }

    List<String> getUserRoles(UUID userId);

    boolean hasRoles(UUID userId, List<String> roles);

    boolean hasAnyRole(UUID userId, List<String> roles);
}
