
package com.huellasdulces.integration;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class PostgreSQLMigrationTest {

    @Container
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:16-alpine")
                    .withDatabaseName("huellas_dulces_test")
                    .withUsername("test")
                    .withPassword("test");

    @Test
    void shouldExecuteAllFlywayMigrationsAndCreateTables()
            throws Exception {

        Flyway flyway = Flyway.configure()
                .dataSource(
                        postgres.getJdbcUrl(),
                        postgres.getUsername(),
                        postgres.getPassword()
                )
                .locations("classpath:db/migration")
                .load();

        var migrationResult = flyway.migrate();

        assertEquals(5, migrationResult.migrationsExecuted);

        List<String> expectedTables = List.of(
                "products",
                "suppliers",
                "customers",
                "orders",
                "user_profiles"
        );

        try (Connection connection = DriverManager.getConnection(
                    postgres.getJdbcUrl(),
                    postgres.getUsername(),
                    postgres.getPassword());
             Statement statement = connection.createStatement()) {

            for (String table : expectedTables) {
                try (ResultSet result = statement.executeQuery(
                        "SELECT COUNT(*) FROM information_schema.tables " +
                        "WHERE table_schema = 'public' " +
                        "AND table_name = '" + table + "'")) {

                    assertTrue(result.next());
                    assertEquals(
                            1,
                            result.getInt(1),
                            "No se encontró la tabla: " + table
                    );
                }
            }
        }
    }
}
