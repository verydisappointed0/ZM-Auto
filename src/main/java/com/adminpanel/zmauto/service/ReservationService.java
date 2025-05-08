package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.Reservation;
import com.adminpanel.zmauto.model.User;
import com.adminpanel.zmauto.model.Vehicle;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for reservation-related operations.
 */
public class ReservationService {
    
    private UserService userService;
    private VehicleService vehicleService;
    
    /**
     * Constructor.
     */
    public ReservationService() {
        this.userService = new UserService();
        this.vehicleService = new VehicleService();
    }
    
    /**
     * Get a reservation by ID.
     * 
     * @param id The reservation ID
     * @return The reservation, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Reservation getReservationById(Long id) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        }
        
        return null; // Reservation not found
    }
    
    /**
     * Get all reservations.
     * 
     * @return A list of all reservations
     * @throws SQLException If a database error occurs
     */
    public List<Reservation> getAllReservations() throws SQLException {
        String sql = "SELECT * FROM reservations ORDER BY created_at DESC";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        }
        
        return reservations;
    }
    
    /**
     * Get reservations by status.
     * 
     * @param status The reservation status
     * @return A list of reservations with the specified status
     * @throws SQLException If a database error occurs
     */
    public List<Reservation> getReservationsByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE status = ? ORDER BY created_at DESC";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        }
        
        return reservations;
    }
    
    /**
     * Get reservations by user.
     * 
     * @param userId The user ID
     * @return A list of reservations for the specified user
     * @throws SQLException If a database error occurs
     */
    public List<Reservation> getReservationsByUser(Long userId) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE user_id = ? ORDER BY created_at DESC";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        }
        
        return reservations;
    }
    
    /**
     * Get reservations by vehicle.
     * 
     * @param vehicleId The vehicle ID
     * @return A list of reservations for the specified vehicle
     * @throws SQLException If a database error occurs
     */
    public List<Reservation> getReservationsByVehicle(Long vehicleId) throws SQLException {
        String sql = "SELECT * FROM reservations WHERE vehicle_id = ? ORDER BY created_at DESC";
        List<Reservation> reservations = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        }
        
        return reservations;
    }
    
    /**
     * Create a new reservation.
     * 
     * @param reservation The reservation to create
     * @return The created reservation with ID
     * @throws SQLException If a database error occurs
     */
    public Reservation createReservation(Reservation reservation) throws SQLException {
        String sql = "INSERT INTO reservations (user_id, vehicle_id, start_date, end_date, status, notes, total_cost, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, reservation.getUser().getId());
            stmt.setLong(2, reservation.getVehicle().getId());
            stmt.setDate(3, Date.valueOf(reservation.getStartDate()));
            stmt.setDate(4, Date.valueOf(reservation.getEndDate()));
            stmt.setString(5, reservation.getStatus());
            stmt.setString(6, reservation.getNotes());
            stmt.setDouble(7, reservation.getTotalCost());
            stmt.setTimestamp(8, Timestamp.valueOf(reservation.getCreatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating reservation failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating reservation failed, no ID obtained.");
                }
            }
        }
        
        return reservation;
    }
    
    /**
     * Update an existing reservation.
     * 
     * @param reservation The reservation to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateReservation(Reservation reservation) throws SQLException {
        String sql = "UPDATE reservations SET user_id = ?, vehicle_id = ?, start_date = ?, end_date = ?, " +
                     "status = ?, notes = ?, total_cost = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, reservation.getUser().getId());
            stmt.setLong(2, reservation.getVehicle().getId());
            stmt.setDate(3, Date.valueOf(reservation.getStartDate()));
            stmt.setDate(4, Date.valueOf(reservation.getEndDate()));
            stmt.setString(5, reservation.getStatus());
            stmt.setString(6, reservation.getNotes());
            stmt.setDouble(7, reservation.getTotalCost());
            stmt.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(9, reservation.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Update a reservation's status.
     * 
     * @param reservationId The reservation ID
     * @param status The new status
     * @param notes Optional notes about the status change
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateReservationStatus(Long reservationId, String status, String notes) throws SQLException {
        String sql = "UPDATE reservations SET status = ?, notes = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setString(2, notes);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(4, reservationId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Approve a reservation.
     * 
     * @param reservationId The reservation ID
     * @param notes Optional notes about the approval
     * @return true if the approval was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean approveReservation(Long reservationId, String notes) throws SQLException {
        return updateReservationStatus(reservationId, "APPROVED", notes);
    }
    
    /**
     * Reject a reservation.
     * 
     * @param reservationId The reservation ID
     * @param notes Optional notes about the rejection
     * @return true if the rejection was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean rejectReservation(Long reservationId, String notes) throws SQLException {
        return updateReservationStatus(reservationId, "REJECTED", notes);
    }
    
    /**
     * Delete a reservation.
     * 
     * @param reservationId The reservation ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteReservation(Long reservationId) throws SQLException {
        String sql = "DELETE FROM reservations WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, reservationId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Map a ResultSet to a Reservation object.
     * 
     * @param rs The ResultSet
     * @return The Reservation object
     * @throws SQLException If a database error occurs
     */
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        Reservation reservation = new Reservation();
        reservation.setId(rs.getLong("id"));
        
        // Get the user
        Long userId = rs.getLong("user_id");
        User user = userService.getUserById(userId);
        reservation.setUser(user);
        
        // Get the vehicle
        Long vehicleId = rs.getLong("vehicle_id");
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        reservation.setVehicle(vehicle);
        
        // Set other fields
        reservation.setStartDate(rs.getDate("start_date").toLocalDate());
        reservation.setEndDate(rs.getDate("end_date").toLocalDate());
        reservation.setStatus(rs.getString("status"));
        reservation.setNotes(rs.getString("notes"));
        reservation.setTotalCost(rs.getDouble("total_cost"));
        reservation.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            reservation.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return reservation;
    }
}