package vn.edu.hust.soict.soe.assetmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Objects;

/**
 * Enables JPA auditing so that BaseEntity fields
 * (createdAt, updatedAt, createdBy) are populated automatically.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication auth = SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            String name = "system";
            if (auth != null && auth.isAuthenticated() 
                    && !"anonymousUser".equals(auth.getPrincipal())) {
                name = auth.getName();
            }

            return Objects.requireNonNull(Optional.of(name != null ? name : "system"));
        };
    }
}