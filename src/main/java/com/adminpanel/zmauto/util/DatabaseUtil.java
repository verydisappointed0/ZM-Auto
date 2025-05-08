package com.adminpanel.zmauto.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Utility class for database connection management using HikariCP connection pool.
 */
public class DatabaseUtil {
    private static HikariDataSource dataSource;

    static {
        try {
            initializeDataSource();
        } catch (Exception e) {
            System.err.println("Error initializing database connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initializeDataSource() {
        HikariConfig config = new HikariConfig();

        // Use H2 embedded database instead of MySQL
        config.setJdbcUrl("jdbc:h2:mem:zmauto;DB_CLOSE_DELAY=-1;MODE=MySQL");
        config.setUsername("sa");
        config.setPassword("");
        config.setDriverClassName("org.h2.Driver");

        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);

        // Set pool name for easier debugging
        config.setPoolName("ZMAutoConnectionPool");

        dataSource = new HikariDataSource(config);
    }

    /**
     * Get a connection from the connection pool.
     * 
     * @return A database connection
     * @throws SQLException If a connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initializeDataSource();
        }
        return dataSource.getConnection();
    }

    /**
     * Close the connection pool.
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
