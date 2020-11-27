package org.lastrix.mafp.roles.persistence.repository;

import org.lastrix.mafp.roles.persistence.entity.UserRole;
import org.lastrix.mafp.roles.persistence.entity.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    @Query("SELECT r.name FROM UserRole p JOIN Role r ON r.id = p.id.roleId WHERE p.id.userId = ?1 AND p.deleted = false")
    List<String> findAllUserRoleNames(UUID userId);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM role_service.role r JOIN role_service.user_role pr ON pr.role_id = r.role_id WHERE pr.user_id = ?1 AND r.name = ?2 AND pr.is_deleted = false)", nativeQuery = true)
    boolean userHasRole(UUID userId, String next);

    @Query("SELECT count(pr) FROM UserRole pr WHERE pr.id.userId = ?1 AND pr.id.roleId IN (?2) AND pr.deleted = false")
    int countUserRoles(UUID userId, Collection<Integer> values);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM role_service.user_role pr WHERE pr.user_id = ?1 AND pr.role_id IN (?2) AND pr.is_deleted = false)", nativeQuery = true)
    boolean hasAnyRole(UUID userId, Collection<Integer> values);

    @Query("SELECT r.id FROM Role r JOIN UserRole ur ON ur.id.roleId = r.id WHERE ur.id.userId = ?1")
    Set<Integer> findAllUserRoles(UUID userId);
}
