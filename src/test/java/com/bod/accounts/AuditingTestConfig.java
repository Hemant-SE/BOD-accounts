package com.bod.accounts;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@TestConfiguration
public class AuditingTestConfig {
    @Bean("auditAwareImpl")
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("test"); // Mock the auditor as empty
    }
}

