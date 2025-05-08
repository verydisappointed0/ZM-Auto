package com.adminpanel.zmauto.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a customer in the system.
 */
@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column
    private LocalDate dateOfBirth;
    
    @Column(nullable = false)
    private String phoneNumber;
    
    @Column
    private String address;
    
    @Column
    private String city;
    
    @Column
    private String state;
    
    @Column
    private String zipCode;
    
    @Column
    private String country;
    
    @Column
    private String driverLicenseNumber;
    
    @Column
    private LocalDate driverLicenseExpiry;
    
    @Column
    private String emergencyContactName;
    
    @Column
    private String emergencyContactPhone;
    
    @Column
    private String customerType; // INDIVIDUAL, CORPORATE
    
    @Column
    private String companyName;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime updatedAt;
    
    // Default constructor required by JPA
    public Customer() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Customer(User user, String firstName, String lastName, LocalDate dateOfBirth, 
                   String phoneNumber, String address, String city, String state, 
                   String zipCode, String country, String driverLicenseNumber, 
                   LocalDate driverLicenseExpiry, String emergencyContactName, 
                   String emergencyContactPhone, String customerType, String companyName) {
        this.user = user;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.driverLicenseNumber = driverLicenseNumber;
        this.driverLicenseExpiry = driverLicenseExpiry;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.customerType = customerType;
        this.companyName = companyName;
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
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }
    
    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }
    
    public LocalDate getDriverLicenseExpiry() {
        return driverLicenseExpiry;
    }
    
    public void setDriverLicenseExpiry(LocalDate driverLicenseExpiry) {
        this.driverLicenseExpiry = driverLicenseExpiry;
    }
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public String getCustomerType() {
        return customerType;
    }
    
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
     * Get the full name of the customer.
     * 
     * @return The full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Get the full address as a formatted string.
     * 
     * @return The full address
     */
    public String getFullAddress() {
        if (address == null || city == null) {
            return "No address provided";
        }
        return address + ", " + city + (state != null ? ", " + state : "") + 
               (zipCode != null ? " " + zipCode : "") + 
               (country != null ? ", " + country : "");
    }
    
    /**
     * Check if the driver's license is currently valid.
     * 
     * @return true if the license is valid, false otherwise
     */
    public boolean isDriverLicenseValid() {
        if (driverLicenseExpiry == null) {
            return false;
        }
        return !LocalDate.now().isAfter(driverLicenseExpiry);
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", driverLicenseNumber='" + driverLicenseNumber + '\'' +
                ", customerType='" + customerType + '\'' +
                '}';
    }
}