package com.agiletour.config;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

@Configuration
@Profile("dev")
@ConditionalOnProperty(prefix = "spring.flyway", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DevFlywayResetConfig {

    private final Flyway flyway;

    public DevFlywayResetConfig(Flyway flyway) {
        this.flyway = flyway;
    }

    @PostConstruct
    public void resetSchemaAndMigrate() {
        try {
            flyway.clean();
        } catch (Exception ignored) {
            // ignore clean failures in dev
        }
        flyway.migrate();
    }
}
