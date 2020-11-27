package org.lastrix.mafp.roles.client.impl;

import feign.RequestInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.lastrix.http.client.api.AbstractHttpClientAutoConfiguration;
import org.lastrix.mafp.roles.client.api.RoleClientService;
import org.lastrix.rest.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@ComponentScan("org.lastrix.mafp.roles.client.api")
public class RoleAutoConfiguration extends AbstractHttpClientAutoConfiguration {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    @ConditionalOnProperty(name = "mafp.role.enable", havingValue = "true", matchIfMissing = true)
    public RoleClient roleClient(@Value("${mafp.role.url}") String url) {
        return buildClient(url, RoleClient.class, "RoleService");
    }

    @Override
    protected RequestInterceptor jwtAuthInterceptor() {
        return t -> {
            t.removeHeader(HttpHeaders.AUTHORIZATION);
            t.header(HttpHeaders.AUTHORIZATION, jwtTokenProvider.getToken());
        };
    }

    @Bean
    @ConditionalOnBean(RoleClient.class)
    public RoleClientService roleService(
            RoleClient client,
            @Value("${mafp.role.cache.enable:false}") Boolean cached,
            @Value("${mafp.role.cache.white-list:}") String roleWhiteList,
            @Value("${mafp.role.cache.lifetime:300000}") Long lifetime,
            @Value("${mafp.role.cache.max-size:100000}") Long maxSize) {
        return cached
                ? new CachedRestRoleClientService(client, parseCommaSet(roleWhiteList), Duration.ofSeconds(lifetime), maxSize)
                : new RestRoleClientService(client);
    }

    private Set<String> parseCommaSet(String roleWhiteList) {
        if (StringUtils.isBlank(roleWhiteList)) return Collections.emptySet();
        var parts = roleWhiteList.split(",");
        if (parts.length == 0) return Collections.emptySet();
        return Arrays.stream(parts).filter(e -> !StringUtils.isBlank(e))
                .collect(Collectors.toSet());
    }

    @Bean
    @ConditionalOnMissingBean(RoleClient.class)
    public RoleClientService roleServiceMock() {
        return new RoleClientServiceMock();
    }
}
