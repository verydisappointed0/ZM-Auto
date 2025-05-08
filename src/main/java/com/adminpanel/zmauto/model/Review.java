package com.adminpanel.zmauto.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity class representing a customer review or feedback.
 */
@Entity
@Table(name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;
    
    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;
    
    @Column(nullable = false)
    private Integer rating; // 1-5 stars
    
    @Column(nullable = false, length = 1000)
    private String comment;
    
    @Column(nullable = false)
    private String reviewType; // VEHICLE, SERVICE, GENERAL
    
    @Column(nullable = false)
    private Boolean approved;
    
    @Column
    private String adminResponse;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // Default constructor required by JPA
    public Review() {
        this.approved = false;
        this.createdAt = LocalDateTime.now();
    }
    
    public Review(User user, Vehicle vehicle, Reservation reservation, Integer rating, 
                 String comment, String reviewType, Boolean approved, String adminResponse) {
        this.user = user;
        this.vehicle = vehicle;
        this.reservation = reservation;
        this.rating = rating;
        this.comment = comment;
        this.reviewType = reviewType;
        this.approved = approved != null ? approved : false;
        this.adminResponse = adminResponse;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    public Reservation getReservation() {
        return reservation;
    }
    
    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }
    
    public Integer getRating() {
        return rating;
    }
    
    public void setRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getReviewType() {
        return reviewType;
    }
    
    public void setReviewType(String reviewType) {
        this.reviewType = reviewType;
    }
    
    public Boolean getApproved() {
        return approved;
    }
    
    public void setApproved(Boolean approved) {
        this.approved = approved;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAdminResponse() {
        return adminResponse;
    }
    
    public void setAdminResponse(String adminResponse) {
        this.adminResponse = adminResponse;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Get the rating as stars (e.g., "★★★☆☆" for a rating of 3).
     * 
     * @return The rating as stars
     */
    public String getRatingAsStars() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            stars.append(i < rating ? "★" : "☆");
        }
        return stars.toString();
    }
    
    /**
     * Approve the review.
     */
    public void approve() {
        this.approved = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Reject the review.
     */
    public void reject() {
        this.approved = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Add an admin response to the review.
     * 
     * @param response The admin response
     */
    public void respond(String response) {
        this.adminResponse = response;
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", vehicle=" + (vehicle != null ? vehicle.getLicensePlate() : "null") +
                ", rating=" + rating +
                ", reviewType='" + reviewType + '\'' +
                ", approved=" + approved +
                ", createdAt=" + createdAt +
                '}';
    }
}