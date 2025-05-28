package com.adminpanel.zmauto.service;

import com.adminpanel.zmauto.model.Vehicle;
import com.adminpanel.zmauto.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for vehicle-related operations.
 */
public class VehicleService {

    /**
     * Get a vehicle by ID.
     * 
     * @param id The vehicle ID
     * @return The vehicle, or null if not found
     * @throws SQLException If a database error occurs
     */
    public Vehicle getVehicleById(Long id) throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE car_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        }

        return null; // Vehicle not found
    }

    /**
     * Get all vehicles.
     * 
     * @return A list of all vehicles
     * @throws SQLException If a database error occurs
     */
    public List<Vehicle> getAllVehicles() throws SQLException {
        String sql = "SELECT * FROM vehicles";
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }

        return vehicles;
    }

    /**
     * Get all available vehicles.
     * 
     * @return A list of all available vehicles
     * @throws SQLException If a database error occurs
     */
    public List<Vehicle> getAvailableVehicles() throws SQLException {
        String sql = "SELECT * FROM vehicles WHERE rental_status = 'AVAILABLE'";
        List<Vehicle> vehicles = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                vehicles.add(mapResultSetToVehicle(rs));
            }
        }

        return vehicles;
    }

    /**
     * Create a new vehicle.
     * 
     * @param vehicle The vehicle to create
     * @return The created vehicle with ID
     * @throws SQLException If a database error occurs
     */
    public Vehicle createVehicle(Vehicle vehicle) throws SQLException {
        String sql = "INSERT INTO vehicles (license_plate, description, picture, brand, condition, model, " +
                     "mileage, type, model_year, colour, transmission, fuel, seating_capacity, " +
                     "rental_price_per_day, rental_price_per_hour, rental_status, current_location, " +
                     "last_service_date, next_service_date, insurance_expiry_date, gps_enabled, rating, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, vehicle.getLicensePlate());
            stmt.setString(2, vehicle.getDescription());
            stmt.setString(3, vehicle.getPicture());
            stmt.setString(4, vehicle.getMake());
            stmt.setString(5, vehicle.getCondition());
            stmt.setString(6, vehicle.getModel());
            setIntOrNull(stmt, 7, vehicle.getMileage());
            stmt.setString(8, vehicle.getType());
            stmt.setInt(9, vehicle.getYear());
            stmt.setString(10, vehicle.getColor());
            stmt.setString(11, vehicle.getTransmission());
            stmt.setString(12, vehicle.getFuel());
            setIntOrNull(stmt, 13, vehicle.getSeatingCapacity());
            stmt.setDouble(14, vehicle.getDailyRate());
            setDoubleOrNull(stmt, 15, vehicle.getHourlyRate());
            stmt.setString(16, vehicle.getStatus());
            stmt.setString(17, vehicle.getCurrentLocation());
            setDateOrNull(stmt, 18, vehicle.getLastServiceDate());
            setDateOrNull(stmt, 19, vehicle.getNextServiceDate());
            setDateOrNull(stmt, 20, vehicle.getInsuranceExpiryDate());
            setBooleanOrNull(stmt, 21, vehicle.getGpsEnabled());
            setDoubleOrNull(stmt, 22, vehicle.getRating());
            stmt.setTimestamp(23, new Timestamp(System.currentTimeMillis()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating vehicle failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vehicle.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating vehicle failed, no ID obtained.");
                }
            }
        }

        return vehicle;
    }

    /**
     * Update an existing vehicle.
     * 
     * @param vehicle The vehicle to update
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateVehicle(Vehicle vehicle) throws SQLException {
        String sql = "UPDATE vehicles SET license_plate = ?, description = ?, picture = ?, brand = ?, " +
                     "condition = ?, model = ?, mileage = ?, type = ?, model_year = ?, colour = ?, " +
                     "transmission = ?, fuel = ?, seating_capacity = ?, rental_price_per_day = ?, " +
                     "rental_price_per_hour = ?, rental_status = ?, current_location = ?, " +
                     "last_service_date = ?, next_service_date = ?, insurance_expiry_date = ?, " +
                     "gps_enabled = ?, rating = ?, updated_at = ? WHERE car_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, vehicle.getLicensePlate());
            stmt.setString(2, vehicle.getDescription());
            stmt.setString(3, vehicle.getPicture());
            stmt.setString(4, vehicle.getMake());
            stmt.setString(5, vehicle.getCondition());
            stmt.setString(6, vehicle.getModel());
            setIntOrNull(stmt, 7, vehicle.getMileage());
            stmt.setString(8, vehicle.getType());
            stmt.setInt(9, vehicle.getYear());
            stmt.setString(10, vehicle.getColor());
            stmt.setString(11, vehicle.getTransmission());
            stmt.setString(12, vehicle.getFuel());
            setIntOrNull(stmt, 13, vehicle.getSeatingCapacity());
            stmt.setDouble(14, vehicle.getDailyRate());
            setDoubleOrNull(stmt, 15, vehicle.getHourlyRate());
            stmt.setString(16, vehicle.getStatus());
            stmt.setString(17, vehicle.getCurrentLocation());
            setDateOrNull(stmt, 18, vehicle.getLastServiceDate());
            setDateOrNull(stmt, 19, vehicle.getNextServiceDate());
            setDateOrNull(stmt, 20, vehicle.getInsuranceExpiryDate());
            setBooleanOrNull(stmt, 21, vehicle.getGpsEnabled());
            setDoubleOrNull(stmt, 22, vehicle.getRating());
            stmt.setTimestamp(23, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(24, vehicle.getId());

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    /**
     * Update a vehicle's status.
     * 
     * @param vehicleId The vehicle ID
     * @param status The new status
     * @return true if the update was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean updateVehicleStatus(Long vehicleId, String status) throws SQLException {
        String sql = "UPDATE vehicles SET rental_status = ?, updated_at = ? WHERE car_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            stmt.setLong(3, vehicleId);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    /**
     * Delete a vehicle.
     * 
     * @param vehicleId The vehicle ID
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException If a database error occurs
     */
    public boolean deleteVehicle(Long vehicleId) throws SQLException {
        String sql = "DELETE FROM vehicles WHERE car_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, vehicleId);

            int affectedRows = stmt.executeUpdate();

            return affectedRows > 0;
        }
    }

    /**
     * Map a ResultSet to a Vehicle object.
     * 
     * @param rs The ResultSet
     * @return The Vehicle object
     * @throws SQLException If a database error occurs
     */
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(rs.getLong("car_id"));
        vehicle.setLicensePlate(rs.getString("license_plate"));
        vehicle.setDescription(rs.getString("description"));
        vehicle.setPicture(rs.getString("picture"));
        vehicle.setMake(rs.getString("brand"));
        vehicle.setCondition(rs.getString("condition"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setMileage(getIntOrNull(rs, "mileage"));
        vehicle.setType(rs.getString("type"));
        vehicle.setYear(rs.getInt("model_year"));
        vehicle.setColor(rs.getString("colour"));
        vehicle.setTransmission(rs.getString("transmission"));
        vehicle.setFuel(rs.getString("fuel"));
        vehicle.setSeatingCapacity(getIntOrNull(rs, "seating_capacity"));
        vehicle.setDailyRate(rs.getDouble("rental_price_per_day"));
        vehicle.setHourlyRate(getDoubleOrNull(rs, "rental_price_per_hour"));
        vehicle.setStatus(rs.getString("rental_status"));
        vehicle.setCurrentLocation(rs.getString("current_location"));
        vehicle.setLastServiceDate(rs.getDate("last_service_date"));
        vehicle.setNextServiceDate(rs.getDate("next_service_date"));
        vehicle.setInsuranceExpiryDate(rs.getDate("insurance_expiry_date"));
        vehicle.setGpsEnabled(getBooleanOrNull(rs, "gps_enabled"));
        vehicle.setRating(getDoubleOrNull(rs, "rating"));
        vehicle.setCreatedAt(rs.getTimestamp("created_at"));
        vehicle.setUpdatedAt(rs.getTimestamp("updated_at"));
        return vehicle;
    }

    // Helper methods for handling null values

    private Integer getIntOrNull(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        return rs.wasNull() ? null : value;
    }

    private Double getDoubleOrNull(ResultSet rs, String columnName) throws SQLException {
        double value = rs.getDouble(columnName);
        return rs.wasNull() ? null : value;
    }

    private Boolean getBooleanOrNull(ResultSet rs, String columnName) throws SQLException {
        boolean value = rs.getBoolean(columnName);
        return rs.wasNull() ? null : value;
    }

    private void setIntOrNull(PreparedStatement stmt, int parameterIndex, Integer value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, Types.INTEGER);
        } else {
            stmt.setInt(parameterIndex, value);
        }
    }

    private void setDoubleOrNull(PreparedStatement stmt, int parameterIndex, Double value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, Types.DOUBLE);
        } else {
            stmt.setDouble(parameterIndex, value);
        }
    }

    private void setBooleanOrNull(PreparedStatement stmt, int parameterIndex, Boolean value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, Types.BOOLEAN);
        } else {
            stmt.setBoolean(parameterIndex, value);
        }
    }

    private void setDateOrNull(PreparedStatement stmt, int parameterIndex, java.util.Date value) throws SQLException {
        if (value == null) {
            stmt.setNull(parameterIndex, Types.DATE);
        } else {
            stmt.setDate(parameterIndex, new java.sql.Date(value.getTime()));
        }
    }
}
