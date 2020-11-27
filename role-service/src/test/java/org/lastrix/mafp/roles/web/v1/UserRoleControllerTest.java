package org.lastrix.mafp.roles.web.v1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.lastrix.mafp.roles.persistence.entity.Role;
import org.lastrix.mafp.roles.persistence.entity.UserRole;
import org.lastrix.mafp.roles.persistence.entity.UserRoleId;
import org.lastrix.mafp.roles.persistence.repository.RoleRepository;
import org.lastrix.mafp.roles.persistence.repository.UserRoleRepository;
import org.lastrix.rest.test.ControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled
@Slf4j
@SpringBootTest
class UserRoleControllerTest extends ControllerTest {
    private static final int ROLE_COUNT = 100;
    private static final int PERSON_COUNT = 100;
    private static final int MIN_PERSON_ROLES = 30;
    private static final int MAX_PERSON_ROLES = 60;
    private static final int TEST_ROLES = 100;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRoleRepository userRoleRepository;
    private final List<Integer> roleIds = new ArrayList<>();
    private final Map<UUID, Set<Integer>> userIds = new HashMap<>();
    private final SecureRandom random = new SecureRandom();

    @Transactional
    @BeforeEach
    public void beforeAll() {
        for (int i = 0; i < ROLE_COUNT; i++) {
            var r = new Role();
            r.setName("Role #" + i);
            r.setDescription("Test role #" + i);
            r = roleRepository.save(r);
            roleIds.add(r.getId());
        }
        for (int i = 0; i < PERSON_COUNT; i++) {
            var userId = nextUserId();
            var roles = generateRoles(MIN_PERSON_ROLES, MAX_PERSON_ROLES);
            userIds.put(userId, roles);
            roles.stream()
                    .map(e -> new UserRoleId(userId, e))
                    .map(UserRole::new)
                    .forEach(e -> userRoleRepository.save(e));
        }
    }

    private UUID nextUserId() {
        UUID userId;
        do {
            userId = UUID.randomUUID();
        } while (userIds.containsKey(userId));
        return userId;
    }

    @Test
    public void testCheckUserRoles() throws Exception {
        var testSuites = new HashMap<UUID, Set<Integer>>();
        for (UUID userId : userIds.keySet()) {
            testSuites.put(userId, generateRoles(TEST_ROLES, TEST_ROLES));
        }
        for (Map.Entry<UUID, Set<Integer>> e : testSuites.entrySet()) {
            var userId = e.getKey();
            for (Integer roleId : e.getValue()) {
                singleResult(Boolean.class, MockMvcRequestBuilders.get("/api/v1/user/{userId}/check", userId).param("roleIds", roleId.toString()));
            }
        }
        var start = Instant.now();
        var requests = 0;
        for (Map.Entry<UUID, Set<Integer>> e : testSuites.entrySet()) {
            var userId = e.getKey();
            for (Integer roleId : e.getValue()) {
                var res = singleResult(Boolean.class, MockMvcRequestBuilders.get("/api/v1/user/{userId}/check", userId).param("roleIds", roleId.toString()));
                assertEquals(userIds.getOrDefault(userId, Collections.emptySet()).contains(roleId), res);
                requests++;
            }
        }
        var stop = Instant.now();
        var elapsed = Duration.between(start, stop);
        var perRequest = (double) elapsed.toMillis() / requests;
        log.warn("Test finished, total time {}s for {} requests with {} ms each", elapsed.getSeconds(), requests, perRequest);
    }

    private Set<Integer> generateRoles(int min, int max) {
        var count = min == max ? min : random.nextInt(max - min) + min;
        var set = new HashSet<Integer>();
        for (int i = 0; i < count; i++) {
            set.add(nextRole(set));
        }
        return set;
    }

    private Integer nextRole(Set<Integer> set) {
        Integer roleId;
        do {
            roleId = roleIds.get(random.nextInt(roleIds.size()));
        } while (set.contains(roleId));
        return roleId;
    }

}
