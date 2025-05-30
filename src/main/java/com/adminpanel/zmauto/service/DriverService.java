package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.Driver;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for driver-related operations.
 */
public class DriverService {
    
    /**
     * Get a driver by ID.
     * 
     * @param id The driver ID
     * @return The driver, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Driver getDriverById(Long id) throws SQLException {
        String sql = "SELECT * FROM driver WHERE driver_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDriver(rs);
                }
            }
        }
        
        return null; // Driver not found
    }
    
    /**
     * Get all driver.
     * 
     * @return A list of all driver
     * @throws SQLException If a database error occurs
     */
    public List<Driver> getAllDrivers() throws SQLException {
        String sql = "SELECT * FROM driver";
        List<Driver> drivers = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                drivers.add(mapResultSetToDriver(rs));
            }
        }
        
        return drivers;
    }
    
    /**
     * Get all available driver.
     * 
     * @return A list of all available driver
     * @throws SQLException If a database error occurs
     */
    public List<Driver> getAvailableDrivers() throws SQLException {
        String sql = "SELECT * FROM driver WHERE availability = TRUE";
        List<Driver> drivers = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                drivers.add(mapResultSetToDriver(rs));
            }
        }
        
        return drivers;
    }
    
    /**
     * Create a new driver.
     * 
     * @param driver The driver to create
     * @return The created driver with ID
     * @throws SQLException If a database error occurs
     */
    public Driver createDriver(Driver driver) throws SQLException {
        String sql = "INSERT INTO driver (picture, first_name, last_name, birthday, phone_number, " +
                     "address, email, daily_wage, hourly_wage, availability, status, " +
                     "years_of_experience, car_id, rating, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, driver.getPicture());
            stmt.setString(2, driver.getFirstName());
            stmt.setString(3, driver.getLastName());
            stmt.setObject(4, driver.getBirthday());
            stmt.setString(5, driver.getPhoneNumber());
            stmt.setString(6, driver.getAddress());
            stmt.setString(7, driver.getEmail());
            stmt.setObject(8, driver.getDailyWage());
            stmt.setObject(9, driver.getHourlyWage());
            stmt.setObject(10, driver.getAvailability());
            stmt.setString(11, driver.getStatus());
            stmt.setObject(12, driver.getYearsOfExperience());
            stmt.setObject(13, driver.getCarId());
            stmt.setObject(14, driver.getRating());
            stmt.setObject(15, driver.getCreatedAt());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating driver failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    driver.setDriverId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating driver failed, no ID obtained.");
                }
            }
        }
        
        return driver;
    }
    
    /**
     * Update an existing driver.
     * 
     * @param driver The driver to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateDriver(Driver driver) throws SQLException {
        String sql = "UPDATE driver SET picture = ?, first_name = ?, last_name = ?, birthday = ?, " +
                     "phone_number = ?, address = ?, email = ?, daily_wage = ?, hourly_wage = ?, " +
                     "availability = ?, status = ?, years_of_experience = ?, car_id = ?, " +
                     "rating = ?, updated_at = ? WHERE driver_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            driver.setUpdatedAt(LocalDateTime.now());
            
            stmt.setString(1, driver.getPicture());
            stmt.setString(2, driver.getFirstName());
            stmt.setString(3, driver.getLastName());
            stmt.setObject(4, driver.getBirthday());
            stmt.setString(5, driver.getPhoneNumber());
            stmt.setString(6, driver.getAddress());
            stmt.setString(7, driver.getEmail());
            stmt.setObject(8, driver.getDailyWage());
            stmt.setObject(9, driver.getHourlyWage());
            stmt.setObject(10, driver.getAvailability());
            stmt.setString(11, driver.getStatus());
            stmt.setObject(12, driver.getYearsOfExperience());
            stmt.setObject(13, driver.getCarId());
            stmt.setObject(14, driver.getRating());
            stmt.setObject(15, driver.getUpdatedAt());
            stmt.setLong(16, driver.getDriverId());
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Update a driver's status.
     * 
     * @param driverId The driver ID
     * @param status The new status
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateDriverStatus(Long driverId, String status) throws SQLException {
        String sql = "UPDATE driver SET status = ?, updated_at = ? WHERE driver_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setObject(2, LocalDateTime.now());
            stmt.setLong(3, driverId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Delete a driver.
     * 
     * @param driverId The driver ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteDriver(Long driverId) throws SQLException {
        String sql = "DELETE FROM driver WHERE driver_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, driverId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Map a ResultSet to a Driver object.
     * 
     * @param rs The ResultSet
     * @return The Driver object
     * @throws SQLException If a database error occurs
     */
    private Driver mapResultSetToDriver(ResultSet rs) throws SQLException {
        Driver driver = new Driver();
        driver.setDriverId(rs.getLong("driver_id"));
        driver.setPicture(rs.getString("picture"));
        driver.setFirstName(rs.getString("first_name"));
        driver.setLastName(rs.getString("last_name"));
        
        // Handle date conversion
        Date birthdayDate = rs.getDate("birthday");
        if (birthdayDate != null) {
            driver.setBirthday(birthdayDate.toLocalDate());
        }
        
        driver.setPhoneNumber(rs.getString("phone_number"));
        driver.setAddress(rs.getString("address"));
        driver.setEmail(rs.getString("email"));
        driver.setDailyWage(rs.getDouble("daily_wage"));
        driver.setHourlyWage(rs.getDouble("hourly_wage"));
        driver.setAvailability(rs.getBoolean("availability"));
        driver.setStatus(rs.getString("status"));
        driver.setYearsOfExperience(rs.getInt("years_of_experience"));
        driver.setCarId(rs.getLong("car_id"));
        driver.setRating(rs.getDouble("rating"));
        
        // Handle timestamp conversion
        Timestamp createdAtTimestamp = rs.getTimestamp("created_at");
        if (createdAtTimestamp != null) {
            driver.setCreatedAt(createdAtTimestamp.toLocalDateTime());
        }
        
        Timestamp updatedAtTimestamp = rs.getTimestamp("updated_at");
        if (updatedAtTimestamp != null) {
            driver.setUpdatedAt(updatedAtTimestamp.toLocalDateTime());
        }
        
        return driver;
    }
}