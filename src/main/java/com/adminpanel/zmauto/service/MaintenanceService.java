package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.Maintenance;
import com.adminpanel.zmauto.model.Vehicle;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for maintenance-related operations.
 */
public class MaintenanceService {
    
    private VehicleService vehicleService;
    
    /**
     * Constructor.
     */
    public MaintenanceService() {
        this.vehicleService = new VehicleService();
    }
    
    /**
     * Get a maintenance record by ID.
     * 
     * @param id The maintenance ID
     * @return The maintenance record, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Maintenance getMaintenanceById(Long id) throws SQLException {
        String sql = "SELECT * FROM maintenance WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMaintenance(rs);
                }
            }
        }
        
        return null; // Maintenance not found
    }
    
    /**
     * Get all maintenance records.
     * 
     * @return A list of all maintenance records
     * @throws SQLException If a database error occurs
     */
    public List<Maintenance> getAllMaintenance() throws SQLException {
        String sql = "SELECT * FROM maintenance ORDER BY maintenance_date DESC";
        List<Maintenance> maintenanceList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                maintenanceList.add(mapResultSetToMaintenance(rs));
            }
        }
        
        return maintenanceList;
    }
    
    /**
     * Get maintenance records by vehicle.
     * 
     * @param vehicleId The vehicle ID
     * @return A list of maintenance records for the specified vehicle
     * @throws SQLException If a database error occurs
     */
    public List<Maintenance> getMaintenanceByVehicle(Long vehicleId) throws SQLException {
        String sql = "SELECT * FROM maintenance WHERE vehicle_id = ? ORDER BY maintenance_date DESC";
        List<Maintenance> maintenanceList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    maintenanceList.add(mapResultSetToMaintenance(rs));
                }
            }
        }
        
        return maintenanceList;
    }
    
    /**
     * Get maintenance records by status.
     * 
     * @param status The maintenance status
     * @return A list of maintenance records with the specified status
     * @throws SQLException If a database error occurs
     */
    public List<Maintenance> getMaintenanceByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM maintenance WHERE status = ? ORDER BY maintenance_date DESC";
        List<Maintenance> maintenanceList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    maintenanceList.add(mapResultSetToMaintenance(rs));
                }
            }
        }
        
        return maintenanceList;
    }
    
    /**
     * Get upcoming maintenance records.
     * 
     * @param days The number of days to look ahead
     * @return A list of maintenance records scheduled within the specified number of days
     * @throws SQLException If a database error occurs
     */
    public List<Maintenance> getUpcomingMaintenance(int days) throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        
        String sql = "SELECT * FROM maintenance WHERE maintenance_date BETWEEN ? AND ? AND status = 'SCHEDULED' ORDER BY maintenance_date";
        List<Maintenance> maintenanceList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(today));
            stmt.setDate(2, Date.valueOf(futureDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    maintenanceList.add(mapResultSetToMaintenance(rs));
                }
            }
        }
        
        return maintenanceList;
    }
    
    /**
     * Create a new maintenance record.
     * 
     * @param maintenance The maintenance record to create
     * @return The created maintenance record with ID
     * @throws SQLException If a database error occurs
     */
    public Maintenance createMaintenance(Maintenance maintenance) throws SQLException {
        String sql = "INSERT INTO maintenance (vehicle_id, maintenance_type, description, maintenance_date, " +
                     "next_maintenance_date, cost, performed_by, status, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, maintenance.getVehicle().getId());
            stmt.setString(2, maintenance.getMaintenanceType());
            stmt.setString(3, maintenance.getDescription());
            stmt.setDate(4, Date.valueOf(maintenance.getMaintenanceDate()));
            
            if (maintenance.getNextMaintenanceDate() != null) {
                stmt.setDate(5, Date.valueOf(maintenance.getNextMaintenanceDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setDouble(6, maintenance.getCost());
            stmt.setString(7, maintenance.getPerformedBy());
            stmt.setString(8, maintenance.getStatus());
            stmt.setTimestamp(9, Timestamp.valueOf(maintenance.getCreatedAt()));
            
            if (maintenance.getUpdatedAt() != null) {
                stmt.setTimestamp(10, Timestamp.valueOf(maintenance.getUpdatedAt()));
            } else {
                stmt.setNull(10, Types.TIMESTAMP);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating maintenance record failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    maintenance.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating maintenance record failed, no ID obtained.");
                }
            }
            
            // If the maintenance is completed and the vehicle is in MAINTENANCE status, update it to AVAILABLE
            if ("COMPLETED".equals(maintenance.getStatus())) {
                Vehicle vehicle = maintenance.getVehicle();
                if ("MAINTENANCE".equals(vehicle.getStatus())) {
                    vehicle.setStatus("AVAILABLE");
                    vehicleService.updateVehicle(vehicle);
                }
            }
        }
        
        return maintenance;
    }
    
    /**
     * Update an existing maintenance record.
     * 
     * @param maintenance The maintenance record to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateMaintenance(Maintenance maintenance) throws SQLException {
        String sql = "UPDATE maintenance SET vehicle_id = ?, maintenance_type = ?, description = ?, " +
                     "maintenance_date = ?, next_maintenance_date = ?, cost = ?, performed_by = ?, " +
                     "status = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, maintenance.getVehicle().getId());
            stmt.setString(2, maintenance.getMaintenanceType());
            stmt.setString(3, maintenance.getDescription());
            stmt.setDate(4, Date.valueOf(maintenance.getMaintenanceDate()));
            
            if (maintenance.getNextMaintenanceDate() != null) {
                stmt.setDate(5, Date.valueOf(maintenance.getNextMaintenanceDate()));
            } else {
                stmt.setNull(5, Types.DATE);
            }
            
            stmt.setDouble(6, maintenance.getCost());
            stmt.setString(7, maintenance.getPerformedBy());
            stmt.setString(8, maintenance.getStatus());
            
            LocalDateTime now = LocalDateTime.now();
            maintenance.setUpdatedAt(now);
            stmt.setTimestamp(9, Timestamp.valueOf(now));
            
            stmt.setLong(10, maintenance.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                // If the maintenance is completed and the vehicle is in MAINTENANCE status, update it to AVAILABLE
                if ("COMPLETED".equals(maintenance.getStatus())) {
                    Vehicle vehicle = maintenance.getVehicle();
                    if ("MAINTENANCE".equals(vehicle.getStatus())) {
                        vehicle.setStatus("AVAILABLE");
                        vehicleService.updateVehicle(vehicle);
                    }
                }
                return true;
            }
            
            return false;
        }
    }
    
    /**
     * Update a maintenance record's status.
     * 
     * @param maintenanceId The maintenance ID
     * @param status The new status
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateMaintenanceStatus(Long maintenanceId, String status) throws SQLException {
        String sql = "UPDATE maintenance SET status = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(3, maintenanceId);
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0 && "COMPLETED".equals(status)) {
                // If the maintenance is completed, check if the vehicle is in MAINTENANCE status
                Maintenance maintenance = getMaintenanceById(maintenanceId);
                if (maintenance != null) {
                    Vehicle vehicle = maintenance.getVehicle();
                    if ("MAINTENANCE".equals(vehicle.getStatus())) {
                        vehicle.setStatus("AVAILABLE");
                        vehicleService.updateVehicle(vehicle);
                    }
                }
            }
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Delete a maintenance record.
     * 
     * @param maintenanceId The maintenance ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteMaintenance(Long maintenanceId) throws SQLException {
        String sql = "DELETE FROM maintenance WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, maintenanceId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Map a ResultSet to a Maintenance object.
     * 
     * @param rs The ResultSet
     * @return The Maintenance object
     * @throws SQLException If a database error occurs
     */
    private Maintenance mapResultSetToMaintenance(ResultSet rs) throws SQLException {
        Maintenance maintenance = new Maintenance();
        maintenance.setId(rs.getLong("id"));
        
        // Get the vehicle
        Long vehicleId = rs.getLong("vehicle_id");
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        maintenance.setVehicle(vehicle);
        
        // Set other fields
        maintenance.setMaintenanceType(rs.getString("maintenance_type"));
        maintenance.setDescription(rs.getString("description"));
        maintenance.setMaintenanceDate(rs.getDate("maintenance_date").toLocalDate());
        
        Date nextMaintenanceDate = rs.getDate("next_maintenance_date");
        if (nextMaintenanceDate != null) {
            maintenance.setNextMaintenanceDate(nextMaintenanceDate.toLocalDate());
        }
        
        maintenance.setCost(rs.getDouble("cost"));
        maintenance.setPerformedBy(rs.getString("performed_by"));
        maintenance.setStatus(rs.getString("status"));
        maintenance.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            maintenance.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return maintenance;
    }
}