package org.lastrix.mafp.roles.service.impl;

import lombok.RequiredArgsConstructor;
import org.lastrix.mafp.roles.persistence.entity.Role;
import org.lastrix.mafp.roles.persistence.repository.RoleRepository;
import org.lastrix.mafp.roles.service.RoleService;
import org.lastrix.rest.Pagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repository;

    @Transactional(readOnly = true)
    @Override
    public Map<String, Integer> findRoles(Collection<String> roles) {
        if (roles.isEmpty()) return Collections.emptyMap();
        var list = repository.findAllByNames(roles);
        var map = list.stream().collect(Collectors.toMap(Role::getName, Role::getId));
        if (list.size() != roles.size())
            throw new IllegalArgumentException("Some roles are not exist: " + roles.stream().filter(e -> !map.containsKey(e)).collect(Collectors.joining(", ")));
        return map;
    }

    @Transactional
    @Override
    public Role create(Role role) {
        var r = new Role();
        r.setName(role.getName());
        r.setDescription(role.getDescription());
        return repository.save(r);
    }

    @Transactional
    @Override
    public Role update(Role role) {
        var r = getByName(role.getName());
        if (role.getDescription() != null) {
            r.setDescription(role.getDescription());
        }
        return repository.save(r);
    }

    @Transactional(readOnly = true)
    @Override
    public Role getById(Integer roleId) {
        return repository.findById(roleId).orElseThrow(() -> new IllegalArgumentException("No role: " + roleId));
    }

    @Transactional(readOnly = true)
    @Override
    public Role getByName(String roleName) {
        return repository.findByName(roleName).orElseThrow(() -> new IllegalArgumentException("No role: " + roleName));
    }

    @Transactional(readOnly = true)
    @Override
    public Page<Role> page(Pagination pagination) {
        return repository.findAll(pagination.toPageable(Sort.by("name")));
    }
}
