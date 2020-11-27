package org.lastrix.mafp.roles.persistence.repository;

import org.lastrix.mafp.roles.persistence.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("SELECT r FROM Role r WHERE r.name IN (?1)")
    List<Role> findAllByNames(Collection<String> roles);

    @Query("SELECT r FROM Role r WHERE r.name = ?1")
    Optional<Role> findByName(String roleName);
}
