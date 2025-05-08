package com.adminpanel.zmauto.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Utility class to hash passwords using SHA-256.
 */
public class PasswordHasher {

    /**
     * Main method to test password hashing.
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        // Test the admin password with the correct hash
        testPassword("admin123", "JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=");

        // Test some other passwords to see what their hashes would be
        String[] testPasswords = {"password", "admin", "123456", "admin1234"};
        for (String password : testPasswords) {
            System.out.println("\nTesting password: " + password);
            System.out.println("Hash: " + hashPassword(password));
        }
    }

    /**
     * Test if a password matches an expected hash.
     * 
     * @param password The password to test
     * @param expectedHash The expected hash
     */
    private static void testPassword(String password, String expectedHash) {
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
}
