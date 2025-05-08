package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.Payment;
import com.adminpanel.zmauto.model.Reservation;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for payment-related operations.
 */
public class PaymentService {
    
    private ReservationService reservationService;
    
    /**
     * Constructor.
     */
    public PaymentService() {
        this.reservationService = new ReservationService();
    }
    
    /**
     * Get a payment by ID.
     * 
     * @param id The payment ID
     * @return The payment, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Payment getPaymentById(Long id) throws SQLException {
        String sql = "SELECT * FROM payments WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayment(rs);
                }
            }
        }
        
        return null; // Payment not found
    }
    
    /**
     * Get all payments.
     * 
     * @return A list of all payments
     * @throws SQLException If a database error occurs
     */
    public List<Payment> getAllPayments() throws SQLException {
        String sql = "SELECT * FROM payments ORDER BY payment_date DESC";
        List<Payment> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                payments.add(mapResultSetToPayment(rs));
            }
        }
        
        return payments;
    }
    
    /**
     * Get payments by reservation.
     * 
     * @param reservationId The reservation ID
     * @return A list of payments for the specified reservation
     * @throws SQLException If a database error occurs
     */
    public List<Payment> getPaymentsByReservation(Long reservationId) throws SQLException {
        String sql = "SELECT * FROM payments WHERE reservation_id = ? ORDER BY payment_date DESC";
        List<Payment> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, reservationId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        }
        
        return payments;
    }
    
    /**
     * Get payments by status.
     * 
     * @param status The payment status
     * @return A list of payments with the specified status
     * @throws SQLException If a database error occurs
     */
    public List<Payment> getPaymentsByStatus(String status) throws SQLException {
        String sql = "SELECT * FROM payments WHERE status = ? ORDER BY payment_date DESC";
        List<Payment> payments = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    payments.add(mapResultSetToPayment(rs));
                }
            }
        }
        
        return payments;
    }
    
    /**
     * Create a new payment.
     * 
     * @param payment The payment to create
     * @return The created payment with ID
     * @throws SQLException If a database error occurs
     */
    public Payment createPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payments (reservation_id, amount, payment_method, status, transaction_id, notes, payment_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, payment.getReservation().getId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getStatus());
            stmt.setString(5, payment.getTransactionId());
            stmt.setString(6, payment.getNotes());
            stmt.setTimestamp(7, Timestamp.valueOf(payment.getPaymentDate()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating payment failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating payment failed, no ID obtained.");
                }
            }
        }
        
        return payment;
    }
    
    /**
     * Update an existing payment.
     * 
     * @param payment The payment to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updatePayment(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET reservation_id = ?, amount = ?, payment_method = ?, " +
                     "status = ?, transaction_id = ?, notes = ?, payment_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, payment.getReservation().getId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getStatus());
            stmt.setString(5, payment.getTransactionId());
            stmt.setString(6, payment.getNotes());
            stmt.setTimestamp(7, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setLong(8, payment.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Update a payment's status.
     * 
     * @param paymentId The payment ID
     * @param status The new status
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updatePaymentStatus(Long paymentId, String status) throws SQLException {
        String sql = "UPDATE payments SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setLong(2, paymentId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Delete a payment.
     * 
     * @param paymentId The payment ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deletePayment(Long paymentId) throws SQLException {
        String sql = "DELETE FROM payments WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, paymentId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Map a ResultSet to a Payment object.
     * 
     * @param rs The ResultSet
     * @return The Payment object
     * @throws SQLException If a database error occurs
     */
    private Payment mapResultSetToPayment(ResultSet rs) throws SQLException {
        Payment payment = new Payment();
        payment.setId(rs.getLong("id"));
        
        // Get the reservation
        Long reservationId = rs.getLong("reservation_id");
        Reservation reservation = reservationService.getReservationById(reservationId);
        payment.setReservation(reservation);
        
        // Set other fields
        payment.setAmount(rs.getDouble("amount"));
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setStatus(rs.getString("status"));
        payment.setTransactionId(rs.getString("transaction_id"));
        payment.setNotes(rs.getString("notes"));
        payment.setPaymentDate(rs.getTimestamp("payment_date").toLocalDateTime());
        
        return payment;
    }
}