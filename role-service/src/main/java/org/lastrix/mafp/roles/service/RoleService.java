package org.lastrix.mafp.roles.service;

import org.lastrix.mafp.roles.persistence.entity.Role;
import org.lastrix.rest.Pagination;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.Map;

public interface RoleService {
    Role create(Role role);

    Role update(Role role);

    Role getById(Integer roleId);

    Page<Role> page(Pagination pagination);

    Map<String, Integer> findRoles(Collection<String> roles);

    Role getByName(String roleName);
}
