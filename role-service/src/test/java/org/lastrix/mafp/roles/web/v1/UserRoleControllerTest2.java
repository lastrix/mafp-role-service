package org.lastrix.mafp.roles.web.v1;

import com.auth0.jwt.JWT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.lastrix.jwt.Jwt;
import org.lastrix.jwt.JwtSecret;
import org.lastrix.jwt.UserType;
import org.lastrix.mafp.roles.service.UserRoleService;
import org.lastrix.rest.JwtAutoConfiguration;
import org.lastrix.rest.test.ControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

@Disabled
@SpringBootTest
@Slf4j
public class UserRoleControllerTest2 extends ControllerTest {
    private static final int WARM_UP_REQ_COUNT = 10_000;
    private static final int REQ_COUNT = 10_000;
    private static final int THREAD_COUNT = 8;
    private static final long PERSON_COUNT = 100_000;

    @Autowired
    private JwtSecret jwtSecret;
    @Autowired
    private UserRoleService userRoleService;

    @Test
    public void testAll() throws Exception {
        var roles = Set.of("User", "RoleManager");
        for (long i = 0; i < PERSON_COUNT; i++) {
            if (i % 50_000 == 0) log.info("Processed {} users", i);
            userRoleService.setUserRoles(new UUID(0L, i), roles);
        }
        var pool = new ForkJoinPool(THREAD_COUNT);
        var userId = new UUID(0L, 2L);
        var list = new CopyOnWriteArrayList<Duration>();
        var monitor = new Object();
        AtomicInteger counter = new AtomicInteger();
        var expiration = Instant.now().plus(5, ChronoUnit.HOURS);
        var token = "Bearer " + JWT.create()
                .withExpiresAt(Date.from(expiration))
                .withIssuer(JwtAutoConfiguration.MAFP_ISSUER)
                .withClaim(Jwt.CLAIM_USER_TYPE, UserType.SRV.name())
                .withClaim(Jwt.CLAIM_USER_ID, UUID.fromString("7fffffff-ffff-ffff-0000-000000000001").toString())
                .sign(jwtSecret.getAlgorithm());

        log.info("Warming up...");
        for (int k = 0; k < WARM_UP_REQ_COUNT; k++) {
            MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/srv/v1/user/{userId}/check", userId)
                    .param("roles", "RoleManager")
                    .header(HttpHeaders.AUTHORIZATION, token);
            singleResult(Boolean.class, builder);
        }
        log.info("Testing...");
        for (int i = 0; i < THREAD_COUNT; i++) {
            counter.incrementAndGet();
            pool.submit(() -> {
                try {
                    var start = Instant.now();
                    for (int k = 0; k < REQ_COUNT; k++) {
                        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/srv/v1/user/{userId}/check", UUID.randomUUID())
                                .param("roles", "RoleManager")
                                .header(HttpHeaders.AUTHORIZATION, token);
                        singleResult(Boolean.class, builder);
                    }
                    var elapsed = Duration.between(start, Instant.now());
                    list.add(elapsed);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                } finally {
                    counter.decrementAndGet();
                    synchronized (monitor) {
                        monitor.notifyAll();
                    }
                }
            });
        }
        while (counter.get() > 0) {
            synchronized (monitor) {
                monitor.wait();
            }
        }
        list.forEach(e -> log.info("Taken: {}", e));
        var est = list.stream().mapToDouble(e -> e.toMillis() + (double) e.toNanosPart() * 10e-9).average().orElse(0d) / REQ_COUNT;
        log.info("Finished! {} ms, {} rps", est, 1000 / est * THREAD_COUNT);
    }
}
