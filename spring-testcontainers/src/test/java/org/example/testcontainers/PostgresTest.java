package org.example.testcontainers;

import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;

/**
 * @author renc
 */
public class PostgresTest {

    @Rule
    public PostgreSQLContainer postgresContainer = new PostgreSQLContainer();

    @Test
    public void testPostgresContainers() throws Exception {

        String jdbcUrl = postgresContainer.getJdbcUrl();
        String username = postgresContainer.getUsername();
        String password = postgresContainer.getPassword();

        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        ResultSet resultSet = conn.createStatement().executeQuery("SELECT 1");
        resultSet.next();

        int result = resultSet.getInt(1);

        assertEquals(1, result);
    }
}
