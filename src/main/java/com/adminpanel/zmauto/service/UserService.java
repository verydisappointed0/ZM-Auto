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
 * Service class for users-related operations.
 */
public class UserService {



    /**
     * Get a users by ID.
     * 
     * @param id The users ID
     * @return The users, or null if not found
     * @throws SQLException If a database error occurs
     */
    public User getUserById(Long id) throws SQLException {
        String sql = "SELECT * FROM user WHERE user_id = ?";

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
        String sql = "SELECT * FROM user";
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
     * Create a new users.
     * 
     * @param user The users to create
     * @return The created users with ID
     * @throws SQLException If a database error occurs
     */
    public User createUser(User user) throws SQLException {
        String sql = "INSERT INTO user (password, first_name, last_name, email, " +
                     "picture, birthday, phone_number, address, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(2, user.getPassword()); // Password should already be hashed
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getRole());
            stmt.setString(7, user.getPicture());
            stmt.setDate(8, user.getBirthday() != null ? new java.sql.Date(user.getBirthday().getTime()) : null);
            stmt.setString(9, user.getPhoneNumber());
            stmt.setString(10, user.getAddress());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating users failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating users failed, no ID obtained.");
                }
            }
        }

        return user;
    }

    /**
     * Update an existing users.
     * 
     * @param user The users to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateUser(User user) throws SQLException {
        String sql = "UPDATE user SET first_name = ?, last_name = ?, email = ?, " +
                     "picture = ?, birthday = ?, phone_number = ?, address = ?, updated_at = CURRENT_TIMESTAMP " +
                     "WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPicture());
            stmt.setDate(5, user.getBirthday() != null ? new java.sql.Date(user.getBirthday().getTime()) : null);
            stmt.setString(6, user.getPhoneNumber());
            stmt.setString(7, user.getAddress());
            stmt.setLong(8, user.getId());

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    /**
     * Update a users's password.
     * 
     * @param userId The users ID
     * @param newPassword The new password (will be hashed)
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updatePassword(Long userId, String newPassword) throws SQLException {
        String sql = "UPDATE user SET password = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";

        // Create a temporary users to hash the password
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
     * Delete a users.
     * 
     * @param userId The users ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteUser(Long userId) throws SQLException {
        String sql = "DELETE FROM user WHERE user_id = ?";

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
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        // Don't set the password using setPassword, as it would hash the already-hashed password
        // Instead, use setHashedPassword to set the already-hashed password directly
        user.setHashedPassword(rs.getString("password"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setRole(rs.getString("role"));
        user.setPicture(rs.getString("picture"));
        user.setBirthday(rs.getDate("birthday"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setAddress(rs.getString("address"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}
