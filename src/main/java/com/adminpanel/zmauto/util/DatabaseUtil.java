package com.adminpanel.zmauto.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class for database connection management using HikariCP connection pool.
 * Provides methods for connection management and transaction handling.
 */
public class DatabaseUtil {
    private static HikariDataSource dataSource;
    private static final ThreadLocal<Connection> transactionConnections = new ThreadLocal<>();

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

        try {
            // Load properties file
            Properties props = loadProperties();

            // Database connection settings
            config.setDriverClassName(props.getProperty("db.driver"));
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));

            // Connection pool settings
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("pool.maxSize")));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("pool.minIdle")));
            config.setIdleTimeout(Long.parseLong(props.getProperty("pool.idleTimeout")));
            config.setConnectionTimeout(Long.parseLong(props.getProperty("pool.connectionTimeout")));

            // Set pool name for easier debugging
            config.setPoolName(props.getProperty("pool.poolName"));

            System.out.println("Connecting to database with URL: " + props.getProperty("db.url"));
            System.out.println("Using username: " + props.getProperty("db.username"));
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            e.printStackTrace();

            // Fallback to default configuration
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setJdbcUrl("jdbc:mysql://localhost:3306/zm_data_base?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            config.setUsername("root");
            config.setPassword("root");
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(5);
            config.setIdleTimeout(30000);
            config.setConnectionTimeout(30000);
            config.setPoolName("ZMAutoConnectionPool");

            System.out.println("Using fallback database configuration");
            System.out.println("Connecting to database with URL: " + config.getJdbcUrl());
            System.out.println("Using username: " + config.getUsername());
        }

        try {
            dataSource = new HikariDataSource(config);
            System.out.println("Successfully connected to the database!");
        } catch (Exception e) {
            System.err.println("Failed to connect to the database: " + e.getMessage());
            System.err.println("Please check that:");
            System.err.println("1. MySQL server is running on localhost:3306");
            System.err.println("2. User '" + config.getUsername() + "' exists and has the correct password");
            System.err.println("3. User has permission to create and access the 'zm_data_base' database");
            e.printStackTrace();

            // Rethrow the exception to indicate that initialization failed
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    /**
     * Get a connection from the connection pool.
     * If a transaction is in progress, returns the transaction connection.
     * Otherwise, returns a new connection from the pool.
     * 
     * @return A database connection
     * @throws SQLException If a connection cannot be obtained
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initializeDataSource();
        }

        // If a transaction is in progress, return the transaction connection
        Connection transactionConnection = transactionConnections.get();
        if (transactionConnection != null && !transactionConnection.isClosed()) {
            return transactionConnection;
        }

        // Otherwise, return a new connection from the pool
        return dataSource.getConnection();
    }

    /**
     * Begin a transaction.
     * This method gets a connection from the pool, disables auto-commit,
     * and stores the connection in the ThreadLocal variable.
     * 
     * @return The transaction connection
     * @throws SQLException If a database error occurs
     */
    public static Connection beginTransaction() throws SQLException {
        if (dataSource == null) {
            initializeDataSource();
        }

        // Get a connection from the pool
        Connection conn = dataSource.getConnection();

        // Disable auto-commit
        conn.setAutoCommit(false);

        // Store the connection in the ThreadLocal variable
        transactionConnections.set(conn);

        return conn;
    }

    /**
     * Commit a transaction.
     * This method commits the transaction, enables auto-commit,
     * closes the connection, and removes it from the ThreadLocal variable.
     * 
     * @throws SQLException If a database error occurs
     */
    public static void commitTransaction() throws SQLException {
        Connection conn = transactionConnections.get();
        if (conn != null && !conn.isClosed()) {
            try {
                conn.commit();
                conn.setAutoCommit(true);
            } finally {
                conn.close();
                transactionConnections.remove();
            }
        }
    }

    /**
     * Rollback a transaction.
     * This method rolls back the transaction, enables auto-commit,
     * closes the connection, and removes it from the ThreadLocal variable.
     * 
     * @throws SQLException If a database error occurs
     */
    public static void rollbackTransaction() throws SQLException {
        Connection conn = transactionConnections.get();
        if (conn != null && !conn.isClosed()) {
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } finally {
                conn.close();
                transactionConnections.remove();
            }
        }
    }

    /**
     * Close the connection pool.
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Load database properties from the properties file.
     * 
     * @return Properties object containing database configuration
     * @throws IOException If the properties file cannot be loaded
     */
    private static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream is = DatabaseUtil.class.getResourceAsStream("/com/adminpanel/zmauto/config/database.properties")) {
            if (is == null) {
                throw new IOException("Could not find database.properties");
            }
            props.load(is);
        }
        return props;
    }

    /**
     * Get the current timestamp as a java.sql.Timestamp.
     * This method ensures consistent timestamp generation across the application.
     * 
     * @return The current timestamp
     */
    public static java.sql.Timestamp getCurrentTimestamp() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }

    /**
     * Execute a SQL statement with parameters that updates the updated_at timestamp.
     * This method ensures consistent timestamp updating across the application.
     * 
     * @param conn The database connection
     * @param sql The SQL statement
     * @param params The parameters for the SQL statement
     * @return The number of rows affected
     * @throws SQLException If a database error occurs
     */
    public static int executeUpdateWithTimestamp(Connection conn, String sql, Object... params) throws SQLException {
        try (java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                if (params[i] == null) {
                    stmt.setNull(i + 1, java.sql.Types.NULL);
                } else if (params[i] instanceof String) {
                    stmt.setString(i + 1, (String) params[i]);
                } else if (params[i] instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) params[i]);
                } else if (params[i] instanceof Long) {
                    stmt.setLong(i + 1, (Long) params[i]);
                } else if (params[i] instanceof Double) {
                    stmt.setDouble(i + 1, (Double) params[i]);
                } else if (params[i] instanceof Boolean) {
                    stmt.setBoolean(i + 1, (Boolean) params[i]);
                } else if (params[i] instanceof java.util.Date) {
                    stmt.setDate(i + 1, new java.sql.Date(((java.util.Date) params[i]).getTime()));
                } else if (params[i] instanceof java.sql.Date) {
                    stmt.setDate(i + 1, (java.sql.Date) params[i]);
                } else if (params[i] instanceof java.sql.Timestamp) {
                    stmt.setTimestamp(i + 1, (java.sql.Timestamp) params[i]);
                } else {
                    stmt.setObject(i + 1, params[i]);
                }
            }

            // Execute the statement
            return stmt.executeUpdate();
        }
    }
}
