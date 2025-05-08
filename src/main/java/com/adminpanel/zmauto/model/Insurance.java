package com.adminpanel.zmauto.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing insurance information for a vehicle.
 */
@Entity
@Table(name = "insurance")
public class Insurance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @Column(nullable = false)
    private String policyNumber;
    
    @Column(nullable = false)
    private String provider;
    
    @Column(nullable = false)
    private String coverageType; // BASIC, COMPREHENSIVE, THIRD_PARTY
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate expiryDate;
    
    @Column(nullable = false)
    private Double premium;
    
    @Column
    private String notes;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // Default constructor required by JPA
    public Insurance() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Insurance(Vehicle vehicle, String policyNumber, String provider, 
                    String coverageType, LocalDate startDate, LocalDate expiryDate, 
                    Double premium, String notes) {
        this.vehicle = vehicle;
        this.policyNumber = policyNumber;
        this.provider = provider;
        this.coverageType = coverageType;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.premium = premium;
        this.notes = notes;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Vehicle getVehicle() {
        return vehicle;
    }
    
    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    
    public String getPolicyNumber() {
        return policyNumber;
    }
    
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public void setProvider(String provider) {
        this.provider = provider;
    }
    
    public String getCoverageType() {
        return coverageType;
    }
    
    public void setCoverageType(String coverageType) {
        this.coverageType = coverageType;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public Double getPremium() {
        return premium;
    }
    
    public void setPremium(Double premium) {
        this.premium = premium;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
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
     * Check if the insurance is currently valid.
     * 
     * @return true if the insurance is valid, false otherwise
     */
    public boolean isValid() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(startDate) && !today.isAfter(expiryDate);
    }
    
    /**
     * Calculate the number of days until the insurance expires.
     * 
     * @return the number of days until expiry, or 0 if already expired
     */
    public long daysUntilExpiry() {
        LocalDate today = LocalDate.now();
        if (today.isAfter(expiryDate)) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);
    }
    
    @Override
    public String toString() {
        return "Insurance{" +
                "id=" + id +
                ", vehicle=" + (vehicle != null ? vehicle.getLicensePlate() : "null") +
                ", policyNumber='" + policyNumber + '\'' +
                ", provider='" + provider + '\'' +
                ", coverageType='" + coverageType + '\'' +
                ", startDate=" + startDate +
                ", expiryDate=" + expiryDate +
                ", premium=" + premium +
                '}';
    }
}