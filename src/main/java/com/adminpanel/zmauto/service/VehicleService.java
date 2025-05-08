package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.Vehicle;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for vehicle-related operations.
 */
public class VehicleService {
    
    /**
     * Get a vehicle by ID.
     * 
     * @param id The vehicle ID
     * @return The vehicle, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Vehicle getVehicleById(Long id) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        }
        
        return null; // Vehicle not found
    }
    
    /**
     * Get all vehicles.
     * 
     * @return A list of all vehicles
     * @throws SQLException If a database error occurs
     */
    public List<Vehicle> getAllVehicles() throws SQLException {
        String sql = "SELECT * FROM vehicles";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        
        return vehicles;
    }
    
    /**
     * Get all available vehicles.
     * 
     * @return A list of all available vehicles
     * @throws SQLException If a database error occurs
     */
    public List<Vehicle> getAvailableVehicles() throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE status = 'AVAILABLE'";
        List<Vehicle> vehicles = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }
        
        return vehicles;
    }
    
    /**
     * Create a new vehicle.
     * 
     * @param vehicle The vehicle to create
     * @return The created vehicle with ID
     * @throws SQLException If a database error occurs
     */
    public Vehicle createVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (make, model, year, license_plate, color, status, description, daily_rate) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, vehicle.getMake());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getLicensePlate());
            stmt.setString(5, vehicle.getColor());
            stmt.setString(6, vehicle.getStatus());
            stmt.setString(7, vehicle.getDescription());
            stmt.setDouble(8, vehicle.getDailyRate());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating vehicle failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehicle.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating vehicle failed, no ID obtained.");
                }
            }
        }
        
        return vehicle;
    }
    
    /**
     * Update an existing vehicle.
     * 
     * @param vehicle The vehicle to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET make = ?, model = ?, year = ?, license_plate = ?, " +
                     "color = ?, status = ?, description = ?, daily_rate = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vehicle.getMake());
            stmt.setString(2, vehicle.getModel());
            stmt.setInt(3, vehicle.getYear());
            stmt.setString(4, vehicle.getLicensePlate());
            stmt.setString(5, vehicle.getColor());
            stmt.setString(6, vehicle.getStatus());
            stmt.setString(7, vehicle.getDescription());
            stmt.setDouble(8, vehicle.getDailyRate());
            stmt.setLong(9, vehicle.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Update a vehicle's status.
     * 
     * @param vehicleId The vehicle ID
     * @param status The new status
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateVehicleStatus(Long vehicleId, String status) throws SQLException {
        String sql = "UPDATE vehicles SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setLong(2, vehicleId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Delete a vehicle.
     * 
     * @param vehicleId The vehicle ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteVehicle(Long vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, vehicleId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Map a ResultSet to a Vehicle object.
     * 
     * @param rs The ResultSet
     * @return The Vehicle object
     * @throws SQLException If a database error occurs
     */
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(rs.getLong("id"));
        vehicle.setMake(rs.getString("make"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setYear(rs.getInt("year"));
        vehicle.setLicensePlate(rs.getString("license_plate"));
        vehicle.setColor(rs.getString("color"));
        vehicle.setStatus(rs.getString("status"));
        vehicle.setDescription(rs.getString("description"));
        vehicle.setDailyRate(rs.getDouble("daily_rate"));
        return vehicle;
    }
}