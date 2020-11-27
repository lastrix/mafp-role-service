package org.lastrix.mafp.roles.service.impl;

import lombok.RequiredArgsConstructor;
import org.lastrix.mafp.roles.persistence.entity.UserRole;
import org.lastrix.mafp.roles.persistence.entity.UserRoleId;
import org.lastrix.mafp.roles.persistence.repository.UserRoleRepository;
import org.lastrix.mafp.roles.service.RoleService;
import org.lastrix.mafp.roles.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository repository;
    private final RoleService roleService;

    @Transactional
    @Override
    public void addUserRoles(UUID userId, Set<String> roles) {
        Map<String, Integer> map = roleService.findRoles(roles);
        enableEach(userId, map.values());
    }

    @Transactional
    @Override
    public void setUserRoles(UUID userId, Set<String> roles) {
        var userRoles = repository.findAllUserRoles(userId);
        var r = roleService.findRoles(roles);
        enableEach(userId, r.values());
        var all = new HashSet<>(r.values());
        userRoles.stream()
                .filter(e -> !all.contains(e))
                .forEach(e -> repository.save(new UserRole(new UserRoleId(userId, e), true)));
    }

    @Transactional
    @Override
    public void removeUserRoles(UUID userId, Set<String> roles) {
        var r = roleService.findRoles(roles);
        r.values().forEach(roleId -> markDeletedIfExists(userId, roleId));
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasRoles(UUID userId, List<String> roles) {
        if (roles.isEmpty()) {
            return false;
        }
        if (roles.size() == 1) {
            return repository.userHasRole(userId, roles.iterator().next());
        }
        var r = roleService.findRoles(roles);
        return repository.countUserRoles(userId, r.values()) == roles.size();
    }

    @Transactional(readOnly = true)
    @Override
    public boolean hasAnyRole(UUID userId, List<String> roles) {
        if (roles.isEmpty()) {
            return false;
        }
        if (roles.size() == 1) {
            return repository.userHasRole(userId, roles.iterator().next());
        }
        var r = roleService.findRoles(roles);
        return repository.hasAnyRole(userId, r.values());
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getUserRoles(UUID userId) {
        return repository.findAllUserRoleNames(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Integer> getUserRoleIds(UUID userId) {
        return repository.findAllUserRoles(userId);
    }

    private void markDeletedIfExists(UUID userId, Integer roleId) {
        UserRoleId id = new UserRoleId(userId, roleId);
        repository.findById(id)
                .ifPresent(pr -> {
                    pr.setDeleted(true);
                    repository.save(pr);
                });
    }

    private void enableEach(UUID userId, java.util.Collection<Integer> roleIds) {
        roleIds.forEach(roleId -> enableRole(userId, roleId));
    }

    private void enableRole(UUID userId, Integer roleId) {
        repository.save(new UserRole(new UserRoleId(userId, roleId)));
    }
}
