package com.adminpanel.zmauto.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a maintenance record for a vehicle.
 */
@Entity
@Table(name = "maintenance")
public class Maintenance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @Column(nullable = false)
    private String maintenanceType; // REGULAR, REPAIR, INSPECTION
    
    @Column(nullable = false)
    private String description;
    
    @Column(nullable = false)
    private LocalDate maintenanceDate;
    
    @Column
    private LocalDate nextMaintenanceDate;
    
    @Column(nullable = false)
    private Double cost;
    
    @Column
    private String performedBy;
    
    @Column(nullable = false)
    private String status; // SCHEDULED, IN_PROGRESS, COMPLETED
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // Default constructor required by JPA
    public Maintenance() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Maintenance(Vehicle vehicle, String maintenanceType, String description, 
                      LocalDate maintenanceDate, LocalDate nextMaintenanceDate, 
                      Double cost, String performedBy, String status) {
        this.vehicle = vehicle;
        this.maintenanceType = maintenanceType;
        this.description = description;
        this.maintenanceDate = maintenanceDate;
        this.nextMaintenanceDate = nextMaintenanceDate;
        this.cost = cost;
        this.performedBy = performedBy;
        this.status = status;
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
    
    public String getMaintenanceType() {
        return maintenanceType;
    }
    
    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getMaintenanceDate() {
        return maintenanceDate;
    }
    
    public void setMaintenanceDate(LocalDate maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }
    
    public LocalDate getNextMaintenanceDate() {
        return nextMaintenanceDate;
    }
    
    public void setNextMaintenanceDate(LocalDate nextMaintenanceDate) {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }
    
    public Double getCost() {
        return cost;
    }
    
    public void setCost(Double cost) {
        this.cost = cost;
    }
    
    public String getPerformedBy() {
        return performedBy;
    }
    
    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    @Override
    public String toString() {
        return "Maintenance{" +
                "id=" + id +
                ", vehicle=" + (vehicle != null ? vehicle.getLicensePlate() : "null") +
                ", maintenanceType='" + maintenanceType + '\'' +
                ", description='" + description + '\'' +
                ", maintenanceDate=" + maintenanceDate +
                ", status='" + status + '\'' +
                ", cost=" + cost +
                '}';
    }
}