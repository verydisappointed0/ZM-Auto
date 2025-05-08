package com.adminpanel.zmauto.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a reservation request in the system.
 */
@Entity
@Table(name = "reservations")
public class Reservation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @Column(nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED
    
    @Column
    private String notes;
    
    @Column(nullable = false)
    private Double totalCost;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // Default constructor required by JPA
    public Reservation() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Reservation(User user, Vehicle vehicle, LocalDate startDate, LocalDate endDate, 
                      String status, String notes, Double totalCost) {
        this.user = user;
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.notes = notes;
        this.totalCost = totalCost;
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
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public Double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
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
     * Approve this reservation request.
     * 
     * @param notes Optional notes about the approval
     */
    public void approve(String notes) {
        this.status = "APPROVED";
        if (notes != null && !notes.isEmpty()) {
            this.notes = notes;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Reject this reservation request.
     * 
     * @param notes Optional notes about the rejection
     */
    public void reject(String notes) {
        this.status = "REJECTED";
        if (notes != null && !notes.isEmpty()) {
            this.notes = notes;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", vehicle=" + (vehicle != null ? vehicle.getLicensePlate() : "null") +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", status='" + status + '\'' +
                ", totalCost=" + totalCost +
                ", createdAt=" + createdAt +
                '}';
    }
}