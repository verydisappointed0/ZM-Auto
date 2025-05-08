package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.Customer;
import com.adminpanel.zmauto.model.User;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for customer-related operations.
 */
public class CustomerService {
    
    private UserService userService;
    
    /**
     * Constructor.
     */
    public CustomerService() {
        this.userService = new UserService();
    }
    
    /**
     * Get a customer by ID.
     * 
     * @param id The customer ID
     * @return The customer, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Customer getCustomerById(Long id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }
        
        return null; // Customer not found
    }
    
    /**
     * Get a customer by user ID.
     * 
     * @param userId The user ID
     * @return The customer, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Customer getCustomerByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE user_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCustomer(rs);
                }
            }
        }
        
        return null; // Customer not found
    }
    
    /**
     * Get all customers.
     * 
     * @return A list of all customers
     * @throws SQLException If a database error occurs
     */
    public List<Customer> getAllCustomers() throws SQLException {
        String sql = "SELECT * FROM customers ORDER BY last_name, first_name";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                customers.add(mapResultSetToCustomer(rs));
            }
        }
        
        return customers;
    }
    
    /**
     * Search customers by name or email.
     * 
     * @param searchTerm The search term
     * @return A list of customers matching the search term
     * @throws SQLException If a database error occurs
     */
    public List<Customer> searchCustomers(String searchTerm) throws SQLException {
        String sql = "SELECT c.* FROM customers c JOIN users u ON c.user_id = u.id " +
                     "WHERE c.first_name LIKE ? OR c.last_name LIKE ? OR u.email LIKE ? " +
                     "ORDER BY c.last_name, c.first_name";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }
        
        return customers;
    }
    
    /**
     * Get customers by customer type.
     * 
     * @param customerType The customer type
     * @return A list of customers with the specified type
     * @throws SQLException If a database error occurs
     */
    public List<Customer> getCustomersByType(String customerType) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_type = ? ORDER BY last_name, first_name";
        List<Customer> customers = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customerType);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        }
        
        return customers;
    }
    
    /**
     * Create a new customer.
     * 
     * @param customer The customer to create
     * @return The created customer with ID
     * @throws SQLException If a database error occurs
     */
    public Customer createCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (user_id, first_name, last_name, date_of_birth, " +
                     "phone_number, address, city, state, zip_code, country, driver_license_number, " +
                     "driver_license_expiry, emergency_contact_name, emergency_contact_phone, " +
                     "customer_type, company_name, created_at, updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, customer.getUser().getId());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getLastName());
            
            if (customer.getDateOfBirth() != null) {
                stmt.setDate(4, Date.valueOf(customer.getDateOfBirth()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            
            stmt.setString(5, customer.getPhoneNumber());
            stmt.setString(6, customer.getAddress());
            stmt.setString(7, customer.getCity());
            stmt.setString(8, customer.getState());
            stmt.setString(9, customer.getZipCode());
            stmt.setString(10, customer.getCountry());
            stmt.setString(11, customer.getDriverLicenseNumber());
            
            if (customer.getDriverLicenseExpiry() != null) {
                stmt.setDate(12, Date.valueOf(customer.getDriverLicenseExpiry()));
            } else {
                stmt.setNull(12, Types.DATE);
            }
            
            stmt.setString(13, customer.getEmergencyContactName());
            stmt.setString(14, customer.getEmergencyContactPhone());
            stmt.setString(15, customer.getCustomerType());
            stmt.setString(16, customer.getCompanyName());
            stmt.setTimestamp(17, Timestamp.valueOf(customer.getCreatedAt()));
            
            if (customer.getUpdatedAt() != null) {
                stmt.setTimestamp(18, Timestamp.valueOf(customer.getUpdatedAt()));
            } else {
                stmt.setNull(18, Types.TIMESTAMP);
            }
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating customer failed, no ID obtained.");
                }
            }
        }
        
        return customer;
    }
    
    /**
     * Update an existing customer.
     * 
     * @param customer The customer to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, date_of_birth = ?, " +
                     "phone_number = ?, address = ?, city = ?, state = ?, zip_code = ?, " +
                     "country = ?, driver_license_number = ?, driver_license_expiry = ?, " +
                     "emergency_contact_name = ?, emergency_contact_phone = ?, customer_type = ?, " +
                     "company_name = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, customer.getFirstName());
            stmt.setString(2, customer.getLastName());
            
            if (customer.getDateOfBirth() != null) {
                stmt.setDate(3, Date.valueOf(customer.getDateOfBirth()));
            } else {
                stmt.setNull(3, Types.DATE);
            }
            
            stmt.setString(4, customer.getPhoneNumber());
            stmt.setString(5, customer.getAddress());
            stmt.setString(6, customer.getCity());
            stmt.setString(7, customer.getState());
            stmt.setString(8, customer.getZipCode());
            stmt.setString(9, customer.getCountry());
            stmt.setString(10, customer.getDriverLicenseNumber());
            
            if (customer.getDriverLicenseExpiry() != null) {
                stmt.setDate(11, Date.valueOf(customer.getDriverLicenseExpiry()));
            } else {
                stmt.setNull(11, Types.DATE);
            }
            
            stmt.setString(12, customer.getEmergencyContactName());
            stmt.setString(13, customer.getEmergencyContactPhone());
            stmt.setString(14, customer.getCustomerType());
            stmt.setString(15, customer.getCompanyName());
            
            LocalDateTime now = LocalDateTime.now();
            customer.setUpdatedAt(now);
            stmt.setTimestamp(16, Timestamp.valueOf(now));
            
            stmt.setLong(17, customer.getId());
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Delete a customer.
     * 
     * @param customerId The customer ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteCustomer(Long customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, customerId);
            
            int affectedRows = stmt.executeUpdate();
            
            return affectedRows > 0;
        }
    }
    
    /**
     * Map a ResultSet to a Customer object.
     * 
     * @param rs The ResultSet
     * @return The Customer object
     * @throws SQLException If a database error occurs
     */
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        
        // Get the user
        Long userId = rs.getLong("user_id");
        User user = userService.getUserById(userId);
        customer.setUser(user);
        
        // Set other fields
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        
        Date dateOfBirth = rs.getDate("date_of_birth");
        if (dateOfBirth != null) {
            customer.setDateOfBirth(dateOfBirth.toLocalDate());
        }
        
        customer.setPhoneNumber(rs.getString("phone_number"));
        customer.setAddress(rs.getString("address"));
        customer.setCity(rs.getString("city"));
        customer.setState(rs.getString("state"));
        customer.setZipCode(rs.getString("zip_code"));
        customer.setCountry(rs.getString("country"));
        customer.setDriverLicenseNumber(rs.getString("driver_license_number"));
        
        Date driverLicenseExpiry = rs.getDate("driver_license_expiry");
        if (driverLicenseExpiry != null) {
            customer.setDriverLicenseExpiry(driverLicenseExpiry.toLocalDate());
        }
        
        customer.setEmergencyContactName(rs.getString("emergency_contact_name"));
        customer.setEmergencyContactPhone(rs.getString("emergency_contact_phone"));
        customer.setCustomerType(rs.getString("customer_type"));
        customer.setCompanyName(rs.getString("company_name"));
        customer.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            customer.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return customer;
    }
}