package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.Review;
import com.adminpanel.zmauto.model.User;
import com.adminpanel.zmauto.model.Vehicle;
import com.adminpanel.zmauto.model.Reservation;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for review-related operations.
 */
public class ReviewService {
    
    private UserService userService;
    private VehicleService vehicleService;
    private ReservationService reservationService;
    
    /**
     * Constructor.
     */
    public ReviewService() {
        this.userService = new UserService();
        this.vehicleService = new VehicleService();
        this.reservationService = new ReservationService();
    }
    
    /**
     * Get a review by ID.
     * 
     * @param id The review ID
     * @return The review, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Review getReviewById(Long id) throws SQLException {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReview(rs);
                }
            }
        }
        
        return null; // Review not found
    }
    
    /**
     * Get all reviews.
     * 
     * @return A list of all reviews
     * @throws SQLException If a database error occurs
     */
    public List<Review> getAllReviews() throws SQLException {
        String sql = "SELECT * FROM reviews ORDER BY created_at DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        }
        
        return reviews;
    }
    
    /**
     * Get approved reviews.
     * 
     * @return A list of approved reviews
     * @throws SQLException If a database error occurs
     */
    public List<Review> getApprovedReviews() throws SQLException {
        String sql = "SELECT * FROM reviews WHERE approved = TRUE ORDER BY created_at DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        }
        
        return reviews;
    }
    
    /**
     * Get pending reviews.
     * 
     * @return A list of pending reviews
     * @throws SQLException If a database error occurs
     */
    public List<Review> getPendingReviews() throws SQLException {
        String sql = "SELECT * FROM reviews WHERE approved = FALSE ORDER BY created_at DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                reviews.add(mapResultSetToReview(rs));
            }
        }
        
        return reviews;
    }
    
    /**
     * Get reviews by user.
     * 
     * @param userId The user ID
     * @return A list of reviews by the specified user
     * @throws SQLException If a database error occurs
     */
    public List<Review> getReviewsByUser(Long userId) throws SQLException {
        String sql = "SELECT * FROM reviews WHERE user_id = ? ORDER BY created_at DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        
        return reviews;
    }
    
    /**
     * Get reviews by vehicle.
     * 
     * @param vehicleId The vehicle ID
     * @param approvedOnly Whether to return only approved reviews
     * @return A list of reviews for the specified vehicle
     * @throws SQLException If a database error occurs
     */
    public List<Review> getReviewsByVehicle(Long vehicleId, boolean approvedOnly) throws SQLException {
        String sql = "SELECT * FROM reviews WHERE vehicle_id = ?";
        if (approvedOnly) {
            sql += " AND approved = TRUE";
        }
        sql += " ORDER BY created_at DESC";
        
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        
        return reviews;
    }
    
    /**
     * Get reviews by reservation.
     * 
     * @param reservationId The reservation ID
     * @return A list of reviews for the specified reservation
     * @throws SQLException If a database error occurs
     */
    public List<Review> getReviewsByReservation(Long reservationId) throws SQLException {
        String sql = "SELECT * FROM reviews WHERE reservation_id = ? ORDER BY created_at DESC";
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, reservationId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        
        return reviews;
    }
    
    /**
     * Get reviews by type.
     * 
     * @param reviewType The review type
     * @param approvedOnly Whether to return only approved reviews
     * @return A list of reviews of the specified type
     * @throws SQLException If a database error occurs
     */
    public List<Review> getReviewsByType(String reviewType, boolean approvedOnly) throws SQLException {
        String sql = "SELECT * FROM reviews WHERE review_type = ?";
        if (approvedOnly) {
            sql += " AND approved = TRUE";
        }
        sql += " ORDER BY created_at DESC";
        
        List<Review> reviews = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, reviewType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reviews.add(mapResultSetToReview(rs));
                }
            }
        }
        
        return reviews;
    }
    
    /**
     * Create a new review.
     * 
     * @param review The review to create
     * @return The created review with ID
     * @throws SQLException If a database error occurs
     */
    public Review createReview(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (user_id, vehicle_id, reservation_id, rating, comment, " +
                     "review_type, approved, admin_response, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, review.getUser().getId());
            
            if (review.getVehicle() != null) {
                stmt.setLong(2, review.getVehicle().getId());
            } else {
                stmt.setNull(2, Types.BIGINT);
            }
            
            if (review.getReservation() != null) {
                stmt.setLong(3, review.getReservation().getId());
            } else {
                stmt.setNull(3, Types.BIGINT);
            }
            
            stmt.setInt(4, review.getRating());
            stmt.setString(5, review.getComment());
            stmt.setString(6, review.getReviewType());
            stmt.setBoolean(7, review.getApproved());
            stmt.setString(8, review.getAdminResponse());
            stmt.setTimestamp(9, Timestamp.valueOf(review.getCreatedAt()));
            
            if (review.getUpdatedAt() != null) {
                stmt.setTimestamp(10, Timestamp.valueOf(review.getUpdatedAt()));
            } else {
                stmt.setNull(10, Types.TIMESTAMP);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating review failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    review.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating review failed, no ID obtained.");
                }
            }
        }
        
        return review;
    }
    
    /**
     * Update an existing review.
     * 
     * @param review The review to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateReview(Review review) throws SQLException {
        String sql = "UPDATE reviews SET rating = ?, comment = ?, review_type = ?, " +
                     "approved = ?, admin_response = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getComment());
            stmt.setString(3, review.getReviewType());
            stmt.setBoolean(4, review.getApproved());
            stmt.setString(5, review.getAdminResponse());
            
            LocalDateTime now = LocalDateTime.now();
            review.setUpdatedAt(now);
            stmt.setTimestamp(6, Timestamp.valueOf(now));
            
            stmt.setLong(7, review.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Approve a review.
     * 
     * @param reviewId The review ID
     * @param adminResponse Optional admin response
     * @return true if the approval was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean approveReview(Long reviewId, String adminResponse) throws SQLException {
        String sql = "UPDATE reviews SET approved = TRUE, admin_response = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, adminResponse);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(3, reviewId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Reject a review.
     * 
     * @param reviewId The review ID
     * @param adminResponse Optional admin response
     * @return true if the rejection was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean rejectReview(Long reviewId, String adminResponse) throws SQLException {
        String sql = "UPDATE reviews SET approved = FALSE, admin_response = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, adminResponse);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(3, reviewId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Delete a review.
     * 
     * @param reviewId The review ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteReview(Long reviewId) throws SQLException {
        String sql = "DELETE FROM reviews WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, reviewId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Calculate the average rating for a vehicle.
     * 
     * @param vehicleId The vehicle ID
     * @return The average rating, or 0 if no reviews exist
     * @throws SQLException If a database error occurs
     */
    public double getAverageRatingForVehicle(Long vehicleId) throws SQLException {
        String sql = "SELECT AVG(rating) AS avg_rating FROM reviews WHERE vehicle_id = ? AND approved = TRUE";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, vehicleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avgRating = rs.getDouble("avg_rating");
                    return rs.wasNull() ? 0 : avgRating;
                }
            }
        }
        
        return 0; // No reviews or error
    }
    
    /**
     * Map a ResultSet to a Review object.
     * 
     * @param rs The ResultSet
     * @return The Review object
     * @throws SQLException If a database error occurs
     */
    private Review mapResultSetToReview(ResultSet rs) throws SQLException {
        Review review = new Review();
        review.setId(rs.getLong("id"));
        
        // Get the user
        Long userId = rs.getLong("user_id");
        User user = userService.getUserById(userId);
        review.setUser(user);
        
        // Get the vehicle (if any)
        Long vehicleId = rs.getLong("vehicle_id");
        if (!rs.wasNull()) {
            Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
            review.setVehicle(vehicle);
        }
        
        // Get the reservation (if any)
        Long reservationId = rs.getLong("reservation_id");
        if (!rs.wasNull()) {
            Reservation reservation = reservationService.getReservationById(reservationId);
            review.setReservation(reservation);
        }
        
        // Set other fields
        review.setRating(rs.getInt("rating"));
        review.setComment(rs.getString("comment"));
        review.setReviewType(rs.getString("review_type"));
        review.setApproved(rs.getBoolean("approved"));
        review.setAdminResponse(rs.getString("admin_response"));
        review.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            review.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return review;
    }
}