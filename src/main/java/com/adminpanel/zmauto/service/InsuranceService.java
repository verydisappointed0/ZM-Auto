package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.Insurance;
import com.adminpanel.zmauto.model.Vehicle;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for insurance-related operations.
 */
public class InsuranceService {
    
    private VehicleService vehicleService;
    
    /**
     * Constructor.
     */
    public InsuranceService() {
        this.vehicleService = new VehicleService();
    }
    
    /**
     * Get an insurance record by ID.
     * 
     * @param id The insurance ID
     * @return The insurance record, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Insurance getInsuranceById(Long id) throws SQLException {
        String sql = "SELECT * FROM insurance WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInsurance(rs);
                }
            }
        }
        
        return null; // Insurance not found
    }
    
    /**
     * Get all insurance records.
     * 
     * @return A list of all insurance records
     * @throws SQLException If a database error occurs
     */
    public List<Insurance> getAllInsurance() throws SQLException {
        String sql = "SELECT * FROM insurance ORDER BY expiry_date";
        List<Insurance> insuranceList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                insuranceList.add(mapResultSetToInsurance(rs));
            }
        }
        
        return insuranceList;
    }
    
    /**
     * Get insurance records by vehicle.
     * 
     * @param vehicleId The vehicle ID
     * @return A list of insurance records for the specified vehicle
     * @throws SQLException If a database error occurs
     */
    public List<Insurance> getInsuranceByVehicle(Long vehicleId) throws SQLException {
        String sql = "SELECT * FROM insurance WHERE vehicle_id = ? ORDER BY expiry_date";
        List<Insurance> insuranceList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    insuranceList.add(mapResultSetToInsurance(rs));
                }
            }
        }
        
        return insuranceList;
    }
    
    /**
     * Get current insurance for a vehicle.
     * 
     * @param vehicleId The vehicle ID
     * @return The current insurance record, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Insurance getCurrentInsurance(Long vehicleId) throws SQLException {
        LocalDate today = LocalDate.now();
        String sql = "SELECT * FROM insurance WHERE vehicle_id = ? AND start_date <= ? AND expiry_date >= ? ORDER BY expiry_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, vehicleId);
            stmt.setDate(2, Date.valueOf(today));
            stmt.setDate(3, Date.valueOf(today));
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInsurance(rs);
                }
            }
        }
        
        return null; // No current insurance found
    }
    
    /**
     * Get expired insurance records.
     * 
     * @return A list of expired insurance records
     * @throws SQLException If a database error occurs
     */
    public List<Insurance> getExpiredInsurance() throws SQLException {
        LocalDate today = LocalDate.now();
        String sql = "SELECT * FROM insurance WHERE expiry_date < ? ORDER BY expiry_date DESC";
        List<Insurance> insuranceList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(today));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    insuranceList.add(mapResultSetToInsurance(rs));
                }
            }
        }
        
        return insuranceList;
    }
    
    /**
     * Get insurance records expiring soon.
     * 
     * @param days The number of days to look ahead
     * @return A list of insurance records expiring within the specified number of days
     * @throws SQLException If a database error occurs
     */
    public List<Insurance> getInsuranceExpiringSoon(int days) throws SQLException {
        LocalDate today = LocalDate.now();
        LocalDate futureDate = today.plusDays(days);
        
        String sql = "SELECT * FROM insurance WHERE expiry_date BETWEEN ? AND ? ORDER BY expiry_date";
        List<Insurance> insuranceList = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(today));
            stmt.setDate(2, Date.valueOf(futureDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    insuranceList.add(mapResultSetToInsurance(rs));
                }
            }
        }
        
        return insuranceList;
    }
    
    /**
     * Create a new insurance record.
     * 
     * @param insurance The insurance record to create
     * @return The created insurance record with ID
     * @throws SQLException If a database error occurs
     */
    public Insurance createInsurance(Insurance insurance) throws SQLException {
        String sql = "INSERT INTO insurance (vehicle_id, policy_number, provider, coverage_type, " +
                     "start_date, expiry_date, premium, notes, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, insurance.getVehicle().getId());
            stmt.setString(2, insurance.getPolicyNumber());
            stmt.setString(3, insurance.getProvider());
            stmt.setString(4, insurance.getCoverageType());
            stmt.setDate(5, Date.valueOf(insurance.getStartDate()));
            stmt.setDate(6, Date.valueOf(insurance.getExpiryDate()));
            stmt.setDouble(7, insurance.getPremium());
            stmt.setString(8, insurance.getNotes());
            stmt.setTimestamp(9, Timestamp.valueOf(insurance.getCreatedAt()));
            
            if (insurance.getUpdatedAt() != null) {
                stmt.setTimestamp(10, Timestamp.valueOf(insurance.getUpdatedAt()));
            } else {
                stmt.setNull(10, Types.TIMESTAMP);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating insurance record failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    insurance.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating insurance record failed, no ID obtained.");
                }
            }
        }
        
        return insurance;
    }
    
    /**
     * Update an existing insurance record.
     * 
     * @param insurance The insurance record to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateInsurance(Insurance insurance) throws SQLException {
        String sql = "UPDATE insurance SET vehicle_id = ?, policy_number = ?, provider = ?, " +
                     "coverage_type = ?, start_date = ?, expiry_date = ?, premium = ?, " +
                     "notes = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, insurance.getVehicle().getId());
            stmt.setString(2, insurance.getPolicyNumber());
            stmt.setString(3, insurance.getProvider());
            stmt.setString(4, insurance.getCoverageType());
            stmt.setDate(5, Date.valueOf(insurance.getStartDate()));
            stmt.setDate(6, Date.valueOf(insurance.getExpiryDate()));
            stmt.setDouble(7, insurance.getPremium());
            stmt.setString(8, insurance.getNotes());
            
            LocalDateTime now = LocalDateTime.now();
            insurance.setUpdatedAt(now);
            stmt.setTimestamp(9, Timestamp.valueOf(now));
            
            stmt.setLong(10, insurance.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Delete an insurance record.
     * 
     * @param insuranceId The insurance ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteInsurance(Long insuranceId) throws SQLException {
        String sql = "DELETE FROM insurance WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, insuranceId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Check if a vehicle has valid insurance.
     * 
     * @param vehicleId The vehicle ID
     * @return true if the vehicle has valid insurance, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean hasValidInsurance(Long vehicleId) throws SQLException {
        Insurance insurance = getCurrentInsurance(vehicleId);
        return insurance != null;
    }
    
    /**
     * Map a ResultSet to an Insurance object.
     * 
     * @param rs The ResultSet
     * @return The Insurance object
     * @throws SQLException If a database error occurs
     */
    private Insurance mapResultSetToInsurance(ResultSet rs) throws SQLException {
        Insurance insurance = new Insurance();
        insurance.setId(rs.getLong("id"));
        
        // Get the vehicle
        Long vehicleId = rs.getLong("vehicle_id");
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        insurance.setVehicle(vehicle);
        
        // Set other fields
        insurance.setPolicyNumber(rs.getString("policy_number"));
        insurance.setProvider(rs.getString("provider"));
        insurance.setCoverageType(rs.getString("coverage_type"));
        insurance.setStartDate(rs.getDate("start_date").toLocalDate());
        insurance.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
        insurance.setPremium(rs.getDouble("premium"));
        insurance.setNotes(rs.getString("notes"));
        insurance.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            insurance.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return insurance;
    }
}