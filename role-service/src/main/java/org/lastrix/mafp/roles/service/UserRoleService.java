package org.lastrix.mafp.roles.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserRoleService {

    void addUserRoles(UUID userId, Set<String> roles);

    void setUserRoles(UUID userId, Set<String> roles);

    void removeUserRoles(UUID userId, Set<String> roles);

    List<String> getUserRoles(UUID userId);

    Set<Integer> getUserRoleIds(UUID userId);

    boolean hasRoles(UUID userId, List<String> roles);

    boolean hasAnyRole(UUID userId, List<String> roles);
}
