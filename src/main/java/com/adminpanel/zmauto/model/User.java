package com.adminpanel.zmauto.model;

import jakarta.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Entity class representing a user in the system.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String role; // ADMIN, USER

    // Default constructor required by JPA
    public User() {
    }

    public User(String username, String password, String fullName, String email, String role) {
        this.username = username;
        setPassword(password); // Hash the password
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        // Hash the password before storing it
        this.password = hashPassword(password);
    }

    /**
     * Set an already-hashed password directly.
     * This method is used when loading a user from the database to avoid double-hashing.
     * 
     * @param hashedPassword The already-hashed password
     */
    public void setHashedPassword(String hashedPassword) {
        this.password = hashedPassword;
    }

    /**
     * Hash a password using SHA-256.
     * 
     * @param plainPassword The plain text password to hash
     * @return The hashed password
     */
    private String hashPassword(String plainPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Verify if the provided password matches the stored hashed password.
     * 
     * @param plainPassword The plain text password to check
     * @return true if the password matches, false otherwise
     */
    public boolean verifyPassword(String plainPassword) {
        String hashedPassword = hashPassword(plainPassword);
        return hashedPassword.equals(this.password);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
