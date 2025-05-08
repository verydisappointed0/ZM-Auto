package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.User;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for user-related operations.
 */
public class UserService {

    /**
     * Authenticate a user with the given username and password.
     * 
     * @param username The username
     * @param password The password
     * @return The authenticated user, or null if authentication fails
     * @throws SQLException If a database error occurs
     */
    public User authenticate(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);

                    // Verify the password
                    if (user.verifyPassword(password)) {
                        return user;
                    }
                }
            }
        }

        return null; // Authentication failed
    }

    /**
     * Get a user by ID.
     * 
     * @param id The user ID
     * @return The user, or null if not found
     * @throws SQLException If a database error occurs
     */
    public User getUserById(Long id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        }

        return null; // User not found
    }

    /**
     * Get all users.
     * 
     * @return A list of all users
     * @throws SQLException If a database error occurs
     */
    public List<User> getAllUsers() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }

        return users;
    }

    /**
     * Create a new user.
     * 
     * @param user The user to create
     * @return The created user with ID
     * @throws SQLException If a database error occurs
     */
    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword()); // Password should already be hashed
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        }

        return user;
    }

    /**
     * Update an existing user.
     * 
     * @param user The user to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET username = ?, full_name = ?, email = ?, role = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFullName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setLong(5, user.getId());

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    /**
     * Update a user's password.
     * 
     * @param userId The user ID
     * @param newPassword The new password (will be hashed)
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updatePassword(Long userId, String newPassword) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        // Create a temporary user to hash the password
        User tempUser = new User();
        tempUser.setPassword(newPassword);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tempUser.getPassword());
            stmt.setLong(2, userId);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    /**
     * Delete a user.
     * 
     * @param userId The user ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteUser(Long userId) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    /**
     * Map a ResultSet to a User object.
     * 
     * @param rs The ResultSet
     * @return The User object
     * @throws SQLException If a database error occurs
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        // Don't set the password using setPassword, as it would hash the already-hashed password
        // Instead, use setHashedPassword to set the already-hashed password directly
        user.setHashedPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        return user;
    }
}
