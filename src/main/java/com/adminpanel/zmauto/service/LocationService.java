package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.Location;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for location-related operations.
 */
public class LocationService {
    
    /**
     * Get a location by ID.
     * 
     * @param id The location ID
     * @return The location, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Location getLocationById(Long id) throws SQLException {
        String sql = "SELECT * FROM locations WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLocation(rs);
                }
            }
        }
        
        return null; // Location not found
    }
    
    /**
     * Get all locations.
     * 
     * @return A list of all locations
     * @throws SQLException If a database error occurs
     */
    public List<Location> getAllLocations() throws SQLException {
        String sql = "SELECT * FROM locations ORDER BY name";
        List<Location> locations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                locations.add(mapResultSetToLocation(rs));
            }
        }
        
        return locations;
    }
    
    /**
     * Get active locations.
     * 
     * @return A list of active locations
     * @throws SQLException If a database error occurs
     */
    public List<Location> getActiveLocations() throws SQLException {
        String sql = "SELECT * FROM locations WHERE active = TRUE ORDER BY name";
        List<Location> locations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                locations.add(mapResultSetToLocation(rs));
            }
        }
        
        return locations;
    }
    
    /**
     * Get locations by city.
     * 
     * @param city The city name
     * @return A list of locations in the specified city
     * @throws SQLException If a database error occurs
     */
    public List<Location> getLocationsByCity(String city) throws SQLException {
        String sql = "SELECT * FROM locations WHERE city = ? ORDER BY name";
        List<Location> locations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, city);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    locations.add(mapResultSetToLocation(rs));
                }
            }
        }
        
        return locations;
    }
    
    /**
     * Create a new location.
     * 
     * @param location The location to create
     * @return The created location with ID
     * @throws SQLException If a database error occurs
     */
    public Location createLocation(Location location) throws SQLException {
        String sql = "INSERT INTO locations (name, address, city, state, zip_code, country, " +
                     "phone_number, email, manager_name, active, opening_hours) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, location.getName());
            stmt.setString(2, location.getAddress());
            stmt.setString(3, location.getCity());
            stmt.setString(4, location.getState());
            stmt.setString(5, location.getZipCode());
            stmt.setString(6, location.getCountry());
            stmt.setString(7, location.getPhoneNumber());
            stmt.setString(8, location.getEmail());
            stmt.setString(9, location.getManagerName());
            stmt.setBoolean(10, location.getActive());
            stmt.setString(11, location.getOpeningHours());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating location failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    location.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating location failed, no ID obtained.");
                }
            }
        }
        
        return location;
    }
    
    /**
     * Update an existing location.
     * 
     * @param location The location to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateLocation(Location location) throws SQLException {
        String sql = "UPDATE locations SET name = ?, address = ?, city = ?, state = ?, " +
                     "zip_code = ?, country = ?, phone_number = ?, email = ?, " +
                     "manager_name = ?, active = ?, opening_hours = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, location.getName());
            stmt.setString(2, location.getAddress());
            stmt.setString(3, location.getCity());
            stmt.setString(4, location.getState());
            stmt.setString(5, location.getZipCode());
            stmt.setString(6, location.getCountry());
            stmt.setString(7, location.getPhoneNumber());
            stmt.setString(8, location.getEmail());
            stmt.setString(9, location.getManagerName());
            stmt.setBoolean(10, location.getActive());
            stmt.setString(11, location.getOpeningHours());
            stmt.setLong(12, location.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Update a location's active status.
     * 
     * @param locationId The location ID
     * @param active The new active status
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateLocationStatus(Long locationId, boolean active) throws SQLException {
        String sql = "UPDATE locations SET active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBoolean(1, active);
            stmt.setLong(2, locationId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Delete a location.
     * 
     * @param locationId The location ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteLocation(Long locationId) throws SQLException {
        String sql = "DELETE FROM locations WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, locationId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Map a ResultSet to a Location object.
     * 
     * @param rs The ResultSet
     * @return The Location object
     * @throws SQLException If a database error occurs
     */
    private Location mapResultSetToLocation(ResultSet rs) throws SQLException {
        Location location = new Location();
        location.setId(rs.getLong("id"));
        location.setName(rs.getString("name"));
        location.setAddress(rs.getString("address"));
        location.setCity(rs.getString("city"));
        location.setState(rs.getString("state"));
        location.setZipCode(rs.getString("zip_code"));
        location.setCountry(rs.getString("country"));
        location.setPhoneNumber(rs.getString("phone_number"));
        location.setEmail(rs.getString("email"));
        location.setManagerName(rs.getString("manager_name"));
        location.setActive(rs.getBoolean("active"));
        location.setOpeningHours(rs.getString("opening_hours"));
        return location;
    }
}