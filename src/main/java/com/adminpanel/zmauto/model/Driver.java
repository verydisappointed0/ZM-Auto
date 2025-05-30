package com.adminpanel.zmauto.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a driver in the system.
 */
@Entity
@Table(name = "driver")
public class Driver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;
    
    @Column
    private String picture;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column
    private LocalDate birthday;
    
    @Column(nullable = false)
    private String phoneNumber;
    
    @Column
    private String address;
    
    @Column
    private String email;
    
    @Column
    private Double dailyWage;
    
    @Column
    private Double hourlyWage;
    
    @Column
    private Boolean availability;
    
    @Column
    private String status; // ACTIVE, INACTIVE, ON_LEAVE
    
    @Column
    private Integer yearsOfExperience;
    
    @Column
    private Long carId;
    
    @Column
    private Double rating;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // Default constructor required by JPA
    public Driver() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Driver(String picture, String firstName, String lastName, LocalDate birthday, 
                 String phoneNumber, String address, String email, Double dailyWage, 
                 Double hourlyWage, Boolean availability, String status, 
                 Integer yearsOfExperience, Long carId, Double rating) {
        this.picture = picture;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.dailyWage = dailyWage;
        this.hourlyWage = hourlyWage;
        this.availability = availability;
        this.status = status;
        this.yearsOfExperience = yearsOfExperience;
        this.carId = carId;
        this.rating = rating;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    
    public Long getDriverId() {
        return driverId;
    }
    
    public void setDriverId(Long driverId) {
        this.driverId = driverId;
    }
    
    public String getPicture() {
        return picture;
    }
    
    public void setPicture(String picture) {
        this.picture = picture;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDate getBirthday() {
        return birthday;
    }
    
    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Double getDailyWage() {
        return dailyWage;
    }
    
    public void setDailyWage(Double dailyWage) {
        this.dailyWage = dailyWage;
    }
    
    public Double getHourlyWage() {
        return hourlyWage;
    }
    
    public void setHourlyWage(Double hourlyWage) {
        this.hourlyWage = hourlyWage;
    }
    
    public Boolean getAvailability() {
        return availability;
    }
    
    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
    
    public Long getCarId() {
        return carId;
    }
    
    public void setCarId(Long carId) {
        this.carId = carId;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public void setRating(Double rating) {
        this.rating = rating;
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
        return "Driver{" +
                "driverId=" + driverId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", rating=" + rating +
                '}';
    }
}