package com.tfg.backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@TestConfiguration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "tests.use-testcontainers", havingValue = "true", matchIfMissing = true)
public class TestContainersConfig {

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:16")
                    .withReuse(true);

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        POSTGRES_CONTAINER.start();
        return POSTGRES_CONTAINER;
    }
}
