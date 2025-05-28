-- H2 database is already created and selected via the JDBC URL
-- No need for CREATE DATABASE or USE statements

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    picture VARCHAR(255),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    birthday DATE,
    phone_number VARCHAR(20),
    address VARCHAR(255),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    car_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    picture VARCHAR(255),
    brand VARCHAR(50) NOT NULL,
    condition VARCHAR(50),
    model VARCHAR(50) NOT NULL,
    mileage INT,
    type VARCHAR(50),
    model_year INT NOT NULL,
    colour VARCHAR(30) NOT NULL,
    transmission VARCHAR(30),
    fuel VARCHAR(30),
    seating_capacity INT,
    rental_price_per_day DECIMAL(10, 2) NOT NULL,
    rental_price_per_hour DECIMAL(10, 2),
    rental_status VARCHAR(20) NOT NULL,
    current_location VARCHAR(255),
    last_service_date DATE,
    next_service_date DATE,
    insurance_expiry_date DATE,
    gps_enabled BOOLEAN,
    rating DECIMAL(3, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- Create locations table
CREATE TABLE IF NOT EXISTS locations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    country VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    email VARCHAR(100),
    manager_name VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    opening_hours VARCHAR(255)
);

-- Create drivers table
CREATE TABLE IF NOT EXISTS drivers (
    driver_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    picture VARCHAR(255),
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    birthday DATE,
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(255),
    email VARCHAR(100),
    daily_wage DECIMAL(10, 2),
    hourly_wage DECIMAL(10, 2),
    availability BOOLEAN DEFAULT TRUE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    years_of_experience INT,
    car_id BIGINT,
    rating DECIMAL(3, 2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    FOREIGN KEY (car_id) REFERENCES vehicles(car_id)
);

-- Create reservations table
CREATE TABLE IF NOT EXISTS reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    driver_needed BOOLEAN DEFAULT FALSE,
    driver_id BIGINT,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    total_cost DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(car_id),
    FOREIGN KEY (driver_id) REFERENCES drivers(driver_id)
);

-- Create payments table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_id VARCHAR(100),
    notes TEXT,
    payment_date TIMESTAMP NOT NULL,
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

-- Create maintenance table
CREATE TABLE IF NOT EXISTS maintenance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    maintenance_type VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    maintenance_date DATE NOT NULL,
    next_maintenance_date DATE,
    cost DECIMAL(10, 2) NOT NULL,
    performed_by VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(car_id)
);

-- Create insurance table
CREATE TABLE IF NOT EXISTS insurance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    policy_number VARCHAR(100) NOT NULL,
    provider VARCHAR(100) NOT NULL,
    coverage_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    premium DECIMAL(10, 2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(car_id)
);

-- Create customers table
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    date_of_birth DATE,
    phone_number VARCHAR(20) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    zip_code VARCHAR(20),
    country VARCHAR(100),
    driver_license_number VARCHAR(50),
    driver_license_expiry DATE,
    emergency_contact_name VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    customer_type VARCHAR(20),
    company_name VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create reviews table
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT,
    reservation_id BIGINT,
    rating INT NOT NULL,
    comment TEXT NOT NULL,
    review_type VARCHAR(20) NOT NULL,
    approved BOOLEAN NOT NULL DEFAULT FALSE,
    admin_response TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(car_id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id)
);

-- Insert default admin user (password: admin123) and a regular user
-- First delete any existing users with the same username to avoid duplicate key errors
DELETE FROM users WHERE username IN ('admin', 'user1');

-- Then insert the users
INSERT INTO users (username, password, first_name, last_name, email, role, created_at)
VALUES 
    ('admin', 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=', 'System', 'Administrator', 'admin@zmauto.com', 'ADMIN', CURRENT_TIMESTAMP),
    ('user1', 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=', 'John', 'Smith', 'john.smith@example.com', 'USER', CURRENT_TIMESTAMP);

-- Insert some sample locations
-- First delete any existing locations with the same name to avoid duplicate key errors
DELETE FROM locations WHERE name IN ('Downtown Branch', 'Airport Location');

-- Then insert the locations
INSERT INTO locations (name, address, city, state, zip_code, country, phone_number, email, manager_name, active, opening_hours)
VALUES
    ('Downtown Branch', '123 Main St', 'New York', 'NY', '10001', 'USA', '212-555-1234', 'downtown@zmauto.com', 'Jane Wilson', TRUE, 'Mon-Fri: 8AM-6PM, Sat: 9AM-4PM, Sun: Closed'),
    ('Airport Location', '789 Airport Rd', 'Los Angeles', 'CA', '90045', 'USA', '310-555-6789', 'airport@zmauto.com', 'Robert Johnson', TRUE, 'Mon-Sun: 6AM-10PM');

-- Insert some sample vehicles
-- First delete any existing vehicles with the same license plate to avoid duplicate key errors
DELETE FROM vehicles WHERE license_plate IN ('ABC123', 'DEF456', 'GHI789', 'JKL012', 'MNO345');

-- Then insert the vehicles
INSERT INTO vehicles (brand, model, model_year, license_plate, colour, rental_status, description, rental_price_per_day, created_at)
VALUES 
    ('Toyota', 'Camry', 2022, 'ABC123', 'Black', 'AVAILABLE', 'Comfortable sedan with good fuel economy', 50.00, CURRENT_TIMESTAMP),
    ('Honda', 'Civic', 2021, 'DEF456', 'White', 'AVAILABLE', 'Compact car with excellent fuel efficiency', 45.00, CURRENT_TIMESTAMP),
    ('Ford', 'Mustang', 2023, 'GHI789', 'Red', 'AVAILABLE', 'Sports car with powerful engine', 80.00, CURRENT_TIMESTAMP),
    ('Chevrolet', 'Suburban', 2022, 'JKL012', 'Silver', 'AVAILABLE', 'Large SUV with plenty of space', 75.00, CURRENT_TIMESTAMP),
    ('BMW', 'X5', 2023, 'MNO345', 'Blue', 'MAINTENANCE', 'Luxury SUV with advanced features', 90.00, CURRENT_TIMESTAMP);

-- Insert sample insurance records
-- Simplified for H2 compatibility
INSERT INTO insurance (vehicle_id, policy_number, provider, coverage_type, start_date, expiry_date, premium, notes, created_at)
SELECT 
    (SELECT car_id FROM vehicles WHERE license_plate = 'ABC123'),
    'POL-12345', 'SafeDrive Insurance', 'COMPREHENSIVE', '2023-01-01', '2023-12-31', 1200.00, 'Full coverage policy', CURRENT_TIMESTAMP;

INSERT INTO insurance (vehicle_id, policy_number, provider, coverage_type, start_date, expiry_date, premium, notes, created_at)
SELECT 
    (SELECT car_id FROM vehicles WHERE license_plate = 'DEF456'),
    'POL-67890', 'SafeDrive Insurance', 'BASIC', '2023-01-01', '2023-12-31', 800.00, 'Basic coverage policy', CURRENT_TIMESTAMP;

-- Insert sample maintenance records
-- Simplified for H2 compatibility
INSERT INTO maintenance (vehicle_id, maintenance_type, description, maintenance_date, next_maintenance_date, cost, performed_by, status, created_at)
SELECT 
    (SELECT car_id FROM vehicles WHERE license_plate = 'MNO345'),
    'REPAIR', 'Brake system replacement', '2023-05-15', '2023-11-15', 450.00, 'Mike''s Auto Shop', 'IN_PROGRESS', CURRENT_TIMESTAMP;

-- Insert sample customer records
-- Simplified for H2 compatibility
INSERT INTO customers (user_id, first_name, last_name, date_of_birth, phone_number, address, city, state, zip_code, country, driver_license_number, driver_license_expiry, emergency_contact_name, emergency_contact_phone, customer_type, company_name, created_at)
SELECT 
    (SELECT user_id FROM users WHERE username = 'user1'),
    'John', 'Smith', '1985-06-15', '555-123-4567', '456 Oak St', 'Chicago', 'IL', '60601', 'USA', 'DL12345678', '2025-06-15', 'Mary Smith', '555-987-6543', 'INDIVIDUAL', NULL, CURRENT_TIMESTAMP;

-- Insert sample drivers (moved up from below to fix foreign key constraints)
INSERT INTO drivers (picture, first_name, last_name, birthday, phone_number, address, email, daily_wage, hourly_wage, availability, status, years_of_experience, car_id, rating, created_at)
VALUES 
    ('https://randomuser.me/api/portraits/men/1.jpg', 'John', 'Doe', '1985-05-15', '555-123-4567', '123 Main St, New York, NY', 'john.doe@example.com', 120.00, 15.00, TRUE, 'ACTIVE', 5, (SELECT car_id FROM vehicles WHERE license_plate = 'ABC123'), 4.8, CURRENT_TIMESTAMP),
    ('https://randomuser.me/api/portraits/women/2.jpg', 'Jane', 'Smith', '1990-08-22', '555-987-6543', '456 Oak Ave, Los Angeles, CA', 'jane.smith@example.com', 110.00, 14.00, TRUE, 'ACTIVE', 3, (SELECT car_id FROM vehicles WHERE license_plate = 'DEF456'), 4.5, CURRENT_TIMESTAMP),
    ('https://randomuser.me/api/portraits/men/3.jpg', 'Michael', 'Johnson', '1982-11-30', '555-456-7890', '789 Pine Rd, Chicago, IL', 'michael.johnson@example.com', 130.00, 16.00, FALSE, 'ON_LEAVE', 7, (SELECT car_id FROM vehicles WHERE license_plate = 'GHI789'), 4.9, CURRENT_TIMESTAMP),
    ('https://randomuser.me/api/portraits/women/4.jpg', 'Emily', 'Williams', '1988-03-12', '555-321-6547', '321 Cedar Ln, Houston, TX', 'emily.williams@example.com', 115.00, 14.50, TRUE, 'ACTIVE', 4, (SELECT car_id FROM vehicles WHERE license_plate = 'JKL012'), 4.7, CURRENT_TIMESTAMP),
    ('https://randomuser.me/api/portraits/men/5.jpg', 'David', 'Brown', '1979-07-08', '555-654-3210', '654 Maple Dr, Phoenix, AZ', 'david.brown@example.com', 125.00, 15.50, FALSE, 'INACTIVE', 8, NULL, 4.2, CURRENT_TIMESTAMP);

-- Insert some sample reservations
-- Simplified for H2 compatibility
INSERT INTO reservations (user_id, vehicle_id, driver_needed, driver_id, start_date, end_date, status, notes, total_cost, created_at)
SELECT 
    (SELECT user_id FROM users WHERE username = 'admin'),
    (SELECT car_id FROM vehicles WHERE license_plate = 'ABC123'),
    TRUE,
    (SELECT driver_id FROM drivers WHERE first_name = 'John' AND last_name = 'Doe'),
    '2023-06-01', '2023-06-05', 'APPROVED', 'Business trip with driver', 370.00, '2023-05-20 10:00:00';

INSERT INTO reservations (user_id, vehicle_id, driver_needed, driver_id, start_date, end_date, status, notes, total_cost, created_at)
SELECT 
    (SELECT user_id FROM users WHERE username = 'user1'),
    (SELECT car_id FROM vehicles WHERE license_plate = 'DEF456'),
    FALSE,
    NULL,
    '2023-07-10', '2023-07-15', 'PENDING', 'Vacation, self-driving', 225.00, '2023-06-25 14:30:00';

-- Insert sample payments
-- Simplified for H2 compatibility
INSERT INTO payments (reservation_id, amount, payment_method, status, transaction_id, notes, payment_date)
SELECT 
    (SELECT id FROM reservations WHERE user_id = (SELECT user_id FROM users WHERE username = 'admin') AND start_date = '2023-06-01'),
    250.00, 'CREDIT_CARD', 'COMPLETED', 'TXN-12345', 'Payment for business trip', '2023-05-20 11:30:00';

-- Insert sample reviews
-- Simplified for H2 compatibility
INSERT INTO reviews (user_id, vehicle_id, reservation_id, rating, comment, review_type, approved, created_at)
SELECT 
    (SELECT user_id FROM users WHERE username = 'admin'),
    (SELECT car_id FROM vehicles WHERE license_plate = 'ABC123'),
    (SELECT id FROM reservations WHERE user_id = (SELECT user_id FROM users WHERE username = 'admin') AND start_date = '2023-06-01'),
    5, 'Great car, very comfortable and fuel-efficient!', 'VEHICLE', TRUE, '2023-06-06 09:15:00';
