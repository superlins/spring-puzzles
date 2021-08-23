package org.example.testcontainers;

import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

/**
 * @author renc
 */
public class MySQLTest {

    @Rule
    public MySQLContainer mySQLContainer = new MySQLContainer();

    @Test
    public void testMySQLContainer() throws SQLException {
        String jdbcUrl = mySQLContainer.getJdbcUrl();
        String username = mySQLContainer.getUsername();
        String password = mySQLContainer.getPassword();

        Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
        ResultSet resultSet = conn.createStatement().executeQuery("SELECT 1");
        resultSet.next();

        int result = resultSet.getInt(1);

        assertEquals(1, result);
    }
}
