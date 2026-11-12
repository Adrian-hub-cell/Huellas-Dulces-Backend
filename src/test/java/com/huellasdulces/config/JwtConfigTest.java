package com.huellasdulces.config;

import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtConfigTest {

    private final JwtConfig jwtConfig = new JwtConfig();

    @Test
    void shouldCreateSecretKeyWhenSecretHasAtLeast64Bytes() {
        String secret = "A".repeat(64);

        SecretKey key = jwtConfig.jwtSecretKey(secret);

        assertThat(key).isNotNull();
        assertThat(key.getAlgorithm()).isEqualTo("HmacSHA512");
        assertThat(key.getEncoded()).hasSize(64);
    }

    @Test
    void shouldRejectSecretShorterThan64Bytes() {
        String secret = "clave-demasiado-corta";

        assertThatThrownBy(() -> jwtConfig.jwtSecretKey(secret))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La clave JWT debe tener al menos 64 bytes para HS512");
    }

    @Test
    void shouldCreateJwtEncoder() {
        SecretKey key = jwtConfig.jwtSecretKey("A".repeat(64));

        assertThat(jwtConfig.jwtEncoder(key)).isNotNull();
    }

    @Test
    void shouldCreateJwtDecoder() {
        SecretKey key = jwtConfig.jwtSecretKey("A".repeat(64));

        assertThat(jwtConfig.jwtDecoder(key)).isNotNull();
    }
}