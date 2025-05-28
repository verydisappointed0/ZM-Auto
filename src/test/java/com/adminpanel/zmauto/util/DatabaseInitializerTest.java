package com.adminpanel.zmauto.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * A simple test class to verify that the database initialization works correctly.
 */
public class DatabaseInitializerTest {

    @BeforeEach
    public void setUp() {
        System.out.println("Starting database initialization test...");
    }

    @AfterEach
    public void tearDown() {
        // Close the database connection pool
        DatabaseUtil.closePool();
        System.out.println("Database connection pool closed.");
    }

    @Test
    public void testDatabaseInitialization() {
        try {
            // Initialize the database
            DatabaseInitializer.initialize();

            System.out.println("Database initialization completed successfully.");
            System.out.println("Test completed successfully.");
        } catch (Exception e) {
            System.err.println("[DEBUG_LOG] Error during database initialization test: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database initialization failed", e);
        }
    }
}
