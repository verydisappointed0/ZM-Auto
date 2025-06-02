-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS zm_data_base;

-- Use the zm_data_base database
USE zm_data_base;

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

-- Create car table
CREATE TABLE IF NOT EXISTS car (
    car_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL UNIQUE,
    description TEXT,
    picture VARCHAR(255),
    brand VARCHAR(50) NOT NULL,
    `condition` VARCHAR(50),
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




-- Create driver table
CREATE TABLE IF NOT EXISTS driver (
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
    FOREIGN KEY (car_id) REFERENCES car (car_id)
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
    FOREIGN KEY (vehicle_id) REFERENCES car (car_id),
    FOREIGN KEY (driver_id) REFERENCES driver (driver_id)
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


-- Insert default admin users (password: admin123) and a regular users if they don't exist
INSERT INTO users (username, password, first_name, last_name, email, role, created_at)
SELECT 'admin', 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=', 'System', 'Administrator', 'admin@zmauto.com', 'ADMIN', CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE last_name = 'Administrator');

INSERT INTO users (username, password, first_name, last_name, email, role, created_at)
SELECT 'user1', 'JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=', 'John', 'Smith', 'john.smith@example.com', 'USER', CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM users WHERE last_name = 'Smith');



-- Insert sample vehicles if they don't exist
INSERT INTO car (brand, model, model_year, license_plate, colour, rental_status, description, rental_price_per_day, created_at)
SELECT 'Toyota', 'Camry', 2022, 'ABC123', 'Black', 'AVAILABLE', 'Comfortable sedan with good fuel economy', 50.00, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM car WHERE license_plate = 'ABC123');

INSERT INTO car (brand, model, model_year, license_plate, colour, rental_status, description, rental_price_per_day, created_at)
SELECT 'Honda', 'Civic', 2021, 'DEF456', 'White', 'AVAILABLE', 'Compact car with excellent fuel efficiency', 45.00, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM car WHERE license_plate = 'DEF456');

INSERT INTO car (brand, model, model_year, license_plate, colour, rental_status, description, rental_price_per_day, created_at)
SELECT 'Ford', 'Mustang', 2023, 'GHI789', 'Red', 'AVAILABLE', 'Sports car with powerful engine', 80.00, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM car WHERE license_plate = 'GHI789');

INSERT INTO car (brand, model, model_year, license_plate, colour, rental_status, description, rental_price_per_day, created_at)
SELECT 'Chevrolet', 'Suburban', 2022, 'JKL012', 'Silver', 'AVAILABLE', 'Large SUV with plenty of space', 75.00, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM car WHERE license_plate = 'JKL012');

INSERT INTO car (brand, model, model_year, license_plate, colour, rental_status, description, rental_price_per_day, created_at)
SELECT 'BMW', 'X5', 2023, 'MNO345', 'Blue', 'MAINTENANCE', 'Luxury SUV with advanced features', 90.00, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (SELECT 1 FROM car WHERE license_plate = 'MNO345');

-- Insert sample insurance records if they don't exist






INSERT INTO driver (picture, first_name, last_name, birthday, phone_number, address, email, daily_wage, hourly_wage, availability, status, years_of_experience, car_id, rating, created_at)
SELECT 'https://randomuser.me/api/portraits/men/1.jpg', 'John', 'Doe', '1985-05-15', '555-123-4567', '123 Main St, New York, NY', 'john.doe@example.com', 120.00, 15.00, TRUE, 'ACTIVE', 5, (SELECT car_id FROM car WHERE license_plate = 'ABC123'), 4.8, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM driver
    WHERE first_name = 'John' AND last_name = 'Doe'
);

INSERT INTO driver (picture, first_name, last_name, birthday, phone_number, address, email, daily_wage, hourly_wage, availability, status, years_of_experience, car_id, rating, created_at)
SELECT 'https://randomuser.me/api/portraits/women/2.jpg', 'Jane', 'Smith', '1990-08-22', '555-987-6543', '456 Oak Ave, Los Angeles, CA', 'jane.smith@example.com', 110.00, 14.00, TRUE, 'ACTIVE', 3, (SELECT car_id FROM car WHERE license_plate = 'DEF456'), 4.5, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM driver
    WHERE first_name = 'Jane' AND last_name = 'Smith'
);

INSERT INTO driver (picture, first_name, last_name, birthday, phone_number, address, email, daily_wage, hourly_wage, availability, status, years_of_experience, car_id, rating, created_at)
SELECT 'https://randomuser.me/api/portraits/men/3.jpg', 'Michael', 'Johnson', '1982-11-30', '555-456-7890', '789 Pine Rd, Chicago, IL', 'michael.johnson@example.com', 130.00, 16.00, FALSE, 'ON_LEAVE', 7, (SELECT car_id FROM car WHERE license_plate = 'GHI789'), 4.9, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM driver
    WHERE first_name = 'Michael' AND last_name = 'Johnson'
);

INSERT INTO driver (picture, first_name, last_name, birthday, phone_number, address, email, daily_wage, hourly_wage, availability, status, years_of_experience, car_id, rating, created_at)
SELECT 'https://randomuser.me/api/portraits/women/4.jpg', 'Emily', 'Williams', '1988-03-12', '555-321-6547', '321 Cedar Ln, Houston, TX', 'emily.williams@example.com', 115.00, 14.50, TRUE, 'ACTIVE', 4, (SELECT car_id FROM car WHERE license_plate = 'JKL012'), 4.7, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM driver
    WHERE first_name = 'Emily' AND last_name = 'Williams'
);

INSERT INTO driver (picture, first_name, last_name, birthday, phone_number, address, email, daily_wage, hourly_wage, availability, status, years_of_experience, car_id, rating, created_at)
SELECT 'https://randomuser.me/api/portraits/men/5.jpg', 'David', 'Brown', '1979-07-08', '555-654-3210', '654 Maple Dr, Phoenix, AZ', 'david.brown@example.com', 125.00, 15.50, FALSE, 'INACTIVE', 8, NULL, 4.2, CURRENT_TIMESTAMP
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM driver
    WHERE first_name = 'David' AND last_name = 'Brown'
);

-- Insert sample reservations if they don't exist
INSERT INTO reservations (user_id, vehicle_id, driver_needed, driver_id, start_date, end_date, status, notes, total_cost, created_at)
SELECT 
    (SELECT user_id FROM users WHERE last_name = 'Administrator'),
    (SELECT car_id FROM car WHERE license_plate = 'ABC123'),
    TRUE,
    (SELECT driver_id FROM driver WHERE first_name = 'John' AND last_name = 'Doe'),
    '2023-06-01', '2023-06-05', 'APPROVED', 'Business trip with driver', 370.00, '2023-05-20 10:00:00'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM reservations 
    WHERE user_id = (SELECT user_id FROM users WHERE last_name = 'Administrator')
    AND vehicle_id = (SELECT car_id FROM car WHERE license_plate = 'ABC123')
    AND start_date = '2023-06-01'
);

INSERT INTO reservations (user_id, vehicle_id, driver_needed, driver_id, start_date, end_date, status, notes, total_cost, created_at)
SELECT 
    (SELECT user_id FROM users WHERE last_name = 'Smith'),
    (SELECT car_id FROM car WHERE license_plate = 'DEF456'),
    FALSE,
    NULL,
    '2023-07-10', '2023-07-15', 'PENDING', 'Vacation, self-driving', 225.00, '2023-06-25 14:30:00'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM reservations 
    WHERE user_id = (SELECT user_id FROM users WHERE last_name = 'Smith')
    AND vehicle_id = (SELECT car_id FROM car WHERE license_plate = 'DEF456')
    AND start_date = '2023-07-10'
);

-- Insert sample payments if they don't exist
INSERT INTO payments (reservation_id, amount, payment_method, status, transaction_id, notes, payment_date)
SELECT 
    (SELECT id FROM reservations WHERE user_id = (SELECT user_id FROM users WHERE last_name = 'Administrator') AND start_date = '2023-06-01'),
    250.00, 'CREDIT_CARD', 'COMPLETED', 'TXN-12345', 'Payment for business trip', '2023-05-20 11:30:00'
FROM dual
WHERE NOT EXISTS (
    SELECT 1 FROM payments 
    WHERE reservation_id = (SELECT id FROM reservations WHERE user_id = (SELECT user_id FROM users WHERE last_name = 'Administrator') AND start_date = '2023-06-01')
    AND transaction_id = 'TXN-12345'
);
