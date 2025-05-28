package com.adminpanel.zmauto.model;

import jakarta.persistence.*;
import java.util.Date;

/**
 * Entity class representing a vehicle in the system.
 */
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long id;

    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    @Column
    private String description;

    @Column
    private String picture;

    @Column(name = "brand", nullable = false)
    private String make;

    @Column(name = "condition")
    private String condition;

    @Column(nullable = false)
    private String model;

    @Column
    private Integer mileage;

    @Column
    private String type;

    @Column(nullable = false)
    private Integer year;

    @Column(name = "colour", nullable = false)
    private String color;

    @Column
    private String transmission;

    @Column
    private String fuel;

    @Column(name = "seating_capacity")
    private Integer seatingCapacity;

    @Column(name = "rental_price_per_day", nullable = false)
    private Double dailyRate;

    @Column(name = "rental_price_per_hour")
    private Double hourlyRate;

    @Column(name = "rental_status", nullable = false)
    private String status; // AVAILABLE, RESERVED, MAINTENANCE

    @Column(name = "current_location")
    private String currentLocation;

    @Column(name = "last_service_date")
    @Temporal(TemporalType.DATE)
    private Date lastServiceDate;

    @Column(name = "next_service_date")
    @Temporal(TemporalType.DATE)
    private Date nextServiceDate;

    @Column(name = "insurance_expiry_date")
    @Temporal(TemporalType.DATE)
    private Date insuranceExpiryDate;

    @Column(name = "gps_enabled")
    private Boolean gpsEnabled;

    @Column
    private Double rating;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Default constructor required by JPA
    public Vehicle() {
        this.createdAt = new Date();
    }

    public Vehicle(String make, String model, Integer year, String licensePlate, 
                  String color, String status, String description, Double dailyRate) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.licensePlate = licensePlate;
        this.color = color;
        this.status = status;
        this.description = description;
        this.dailyRate = dailyRate;
        this.createdAt = new Date();
    }

    // Full constructor with all fields
    public Vehicle(String licensePlate, String description, String picture, String make, 
                  String condition, String model, Integer mileage, String type, Integer year, 
                  String color, String transmission, String fuel, Integer seatingCapacity, 
                  Double dailyRate, Double hourlyRate, String status, String currentLocation, 
                  Date lastServiceDate, Date nextServiceDate, Date insuranceExpiryDate, 
                  Boolean gpsEnabled, Double rating) {
        this.licensePlate = licensePlate;
        this.description = description;
        this.picture = picture;
        this.make = make;
        this.condition = condition;
        this.model = model;
        this.mileage = mileage;
        this.type = type;
        this.year = year;
        this.color = color;
        this.transmission = transmission;
        this.fuel = fuel;
        this.seatingCapacity = seatingCapacity;
        this.dailyRate = dailyRate;
        this.hourlyRate = hourlyRate;
        this.status = status;
        this.currentLocation = currentLocation;
        this.lastServiceDate = lastServiceDate;
        this.nextServiceDate = nextServiceDate;
        this.insuranceExpiryDate = insuranceExpiryDate;
        this.gpsEnabled = gpsEnabled;
        this.rating = rating;
        this.createdAt = new Date();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getFuel() {
        return fuel;
    }

    public void setFuel(String fuel) {
        this.fuel = fuel;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public Double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(Double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(Double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Date getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(Date lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

    public Date getNextServiceDate() {
        return nextServiceDate;
    }

    public void setNextServiceDate(Date nextServiceDate) {
        this.nextServiceDate = nextServiceDate;
    }

    public Date getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(Date insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public Boolean getGpsEnabled() {
        return gpsEnabled;
    }

    public void setGpsEnabled(Boolean gpsEnabled) {
        this.gpsEnabled = gpsEnabled;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
        if (this.createdAt == null) {
            this.createdAt = updatedAt;
        }
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", licensePlate='" + licensePlate + '\'' +
                ", color='" + color + '\'' +
                ", status='" + status + '\'' +
                ", dailyRate=" + dailyRate +
                '}';
    }
}
