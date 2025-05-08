package com.adminpanel.zmauto.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for initializing the database.
 */
public class DatabaseInitializer {

    /**
     * Initialize the database with the SQL script.
     */
    public static void initialize() {
        try {
            // Read the SQL script
            String sqlScript = readSqlScript();

            // Split the script into individual statements
            List<String> statements = splitSqlStatements(sqlScript);

            // Execute each statement
            executeStatements(statements);

            System.out.println("Database initialized successfully.");

            // Test the password hash
            testPasswordHash();

        } catch (IOException | SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Test if the password hash in the database matches the expected hash for "admin123".
     */
    private static void testPasswordHash() {
        String password = "admin123";
        String expectedHash = "JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=";
        String actualHash = hashPassword(password);

        System.out.println("Password: " + password);
        System.out.println("Expected Hash: " + expectedHash);
        System.out.println("Actual Hash: " + actualHash);
        System.out.println("Passwords match: " + actualHash.equals(expectedHash));
    }

    /**
     * Hash a password using SHA-256.
     * 
     * @param plainPassword The plain text password to hash
     * @return The hashed password
     */
    private static String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Read the SQL script from the resources.
     * 
     * @return The SQL script as a string
     * @throws IOException If an I/O error occurs
     */
    private static String readSqlScript() throws IOException {
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream("/com/adminpanel/zmauto/db/init.sql")) {
            if (is == null) {
                throw new IOException("SQL script not found");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }

    /**
     * Split the SQL script into individual statements.
     * 
     * @param sqlScript The SQL script
     * @return A list of SQL statements
     */
    private static List<String> splitSqlStatements(String sqlScript) {
        List<String> statements = new ArrayList<>();
        StringBuilder currentStatement = new StringBuilder();

        for (String line : sqlScript.split("\n")) {
            // Skip comments and empty lines
            if (line.trim().startsWith("--") || line.trim().isEmpty()) {
                continue;
            }

            currentStatement.append(line).append("\n");

            // If the line ends with a semicolon, it's the end of a statement
            if (line.trim().endsWith(";")) {
                statements.add(currentStatement.toString());
                currentStatement = new StringBuilder();
            }
        }

        return statements;
    }

    /**
     * Execute the SQL statements.
     * 
     * @param statements The SQL statements to execute
     * @throws SQLException If a database error occurs
     */
    private static void executeStatements(List<String> statements) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Disable auto-commit to execute all statements in a transaction
            conn.setAutoCommit(false);

            try (Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    stmt.execute(sql);
                }

                // Commit the transaction
                conn.commit();
            } catch (SQLException e) {
                // Rollback the transaction if an error occurs
                conn.rollback();
                throw e;
            } finally {
                // Restore auto-commit
                conn.setAutoCommit(true);
            }
        }
    }
}
