package org.lastrix.mafp.role.perf;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.lastrix.jwt.JwtSecret;
import org.lastrix.perf.tester.PerfSuite;
import org.lastrix.rest.DefaultJwtTokenProvider;
import org.springframework.http.HttpHeaders;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.UUID;

@Slf4j
public class RolePerfSuite implements PerfSuite {
    private final JwtSecret secret = new JwtSecret();
    private final OkHttpClient client = new OkHttpClient();
    private final String authToken = new DefaultJwtTokenProvider(secret, "7fffffff-ffff-ffff-0000-000000000002", 24 * 3600L).getToken();
    private final SecureRandom random = new SecureRandom();

    @Override
    public void init() {
        log.info("Testing...");
    }

    @Override
    public void next() throws Exception {
        var userId = new UUID(0L, random.nextInt(10_000));
        var role = "Role" + random.nextInt(10_000);
        try {
            var result = hasRoles(userId, role);
            log.trace("Got result: {}", result);
        } catch (Exception e) {
            log.info("Failed to create request", e);
        }
    }

    @Override
    public void close() {

    }

    private String hasRoles(UUID userId, String role) {
        var url = new HttpUrl.Builder()
                .scheme("http")
                .host("localhost")
                .port(8080)
                .encodedPath("/srv/v1/user/" + userId + "/check")
                .addQueryParameter("roles", role)
                .build();

        var request = new Request.Builder()
                .url(url)
                .header(HttpHeaders.AUTHORIZATION, authToken)
                .build();
        try (Response response = client.newCall(request).execute()) {
            try (InputStream is = response.body().byteStream()) {
                return IOUtils.toString(is, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.info("Failed to request resource", e);
            return "";
        }
    }
}
