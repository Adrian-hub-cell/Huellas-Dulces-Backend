
package com.huellasdulces.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class JpaAuditingConfigTest {

    private JpaAuditingConfig config;
    private AuditorAware<String> auditorAware;

    @BeforeEach
    void setUp() {
        config = new JpaAuditingConfig();
        auditorAware = config.auditorAware();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReturnSystemWhenAuthenticationIsNull() {
        SecurityContextHolder.getContext().setAuthentication(null);

        assertThat(auditorAware.getCurrentAuditor())
                .isEqualTo(Optional.of("sistema"));
    }

    @Test
    void shouldReturnSystemWhenUserIsNotAuthenticated() {
        var authentication =
                UsernamePasswordAuthenticationToken.unauthenticated(
                        "usuario", "password"
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        assertThat(auditorAware.getCurrentAuditor())
                .isEqualTo(Optional.of("sistema"));
    }

    @Test
    void shouldReturnSystemWhenAuthenticationIsAnonymous() {
        var authentication = new AnonymousAuthenticationToken(
                "test-key",
                "anonymousUser",
                List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        assertThat(auditorAware.getCurrentAuditor())
                .isEqualTo(Optional.of("sistema"));
    }

    @Test
    void shouldReturnAuthenticatedUsername() {
        var authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        "adrian",
                        "password",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        assertThat(auditorAware.getCurrentAuditor())
                .isEqualTo(Optional.of("adrian"));
    }
}
