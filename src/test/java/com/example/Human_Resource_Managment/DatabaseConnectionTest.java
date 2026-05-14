package com.example.Human_Resource_Managment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Test
    void testDatabaseConnection() {
        assertNotNull(dataSource, "DataSource should not be null");
        
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertFalse(connection.isClosed(), "Connection should be open");
            
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            String databaseProductVersion = connection.getMetaData().getDatabaseProductVersion();
            String url = connection.getMetaData().getURL();
            
            System.out.println("=== Database Connection Test ===");
            System.out.println("Database Product: " + databaseProductName);
            System.out.println("Database Version: " + databaseProductVersion);
            System.out.println("Database URL: " + url);
            System.out.println("Connection successful!");
            System.out.println("================================");
            
            assertTrue(connection.isValid(5), "Connection should be valid");
            
        } catch (SQLException e) {
            fail("Failed to connect to database: " + e.getMessage());
        }
    }

    @Test
    void testJdbcTemplate() {
        if (jdbcTemplate != null) {
            try {
                Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                assertEquals(1, result, "Query should return 1");
                System.out.println("JdbcTemplate test successful!");
            } catch (Exception e) {
                fail("JdbcTemplate query failed: " + e.getMessage());
            }
        }
    }
}
