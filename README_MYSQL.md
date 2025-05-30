# MySQL Database Setup for ZM-Auto Admin Panel

This document provides instructions for setting up and configuring the MySQL database for the ZM-Auto Admin Panel application.

## Prerequisites

1. MySQL Server 8.0 or higher installed and running
2. MySQL client or MySQL Workbench for database management (optional)
3. Java 17 or higher installed

## Database Configuration

The application is configured to connect to a MySQL database with the following default settings:

- **Host**: localhost
- **Port**: 3306
- **Database Name**: zm_data_base
- **Username**: root
- **Password**: root

These settings can be modified in the `database.properties` file located at:
```
src/main/resources/com/adminpanel/zmauto/config/database.properties
```

## Setting Up the Database

The application will automatically create the database and required tables when it starts for the first time. However, you can also manually set up the database using the following steps:

1. Connect to your MySQL server using the MySQL client or MySQL Workbench
2. Create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS zm_data_base;
   ```
3. Use the database:
   ```sql
   USE zm_data_base;
   ```
4. Run the initialization script located at:
   ```
   src/main/resources/com/adminpanel/zmauto/db/init.sql
   ```

## Verifying the Connection

To verify that the application can connect to the MySQL database:

1. Start the application
2. Check the console output for any database-related errors
3. If the application starts without errors, the database connection is successful

## Troubleshooting

If you encounter issues connecting to the database:

1. Verify that the MySQL server is running
2. Check that the database credentials in `database.properties` are correct
3. Ensure that the MySQL JDBC driver is included in the project dependencies
4. Check firewall settings if connecting to a remote MySQL server
5. Verify that the MySQL users has the necessary permissions to create databases and tables

## Data Synchronization

The application automatically synchronizes data with the MySQL database when:

1. Creating, updating, or deleting records through the admin panel
2. Starting the application (initializes the database if needed)

No additional steps are required for data synchronization as it is handled by the application's service layer.

### Synchronization Mechanisms

The application implements several mechanisms to ensure proper synchronization between the application and the database:

1. **Sample Data Initialization**: The application initializes the database with sample data only if it doesn't already exist, preventing overwriting of existing data.

2. **Transaction Management**: The application uses transactions for operations that affect multiple tables, ensuring atomicity and consistency.

3. **Timestamp Management**: The application ensures consistent updating of timestamps across all operations.

4. **Foreign Key Constraint Handling**: The application checks for references to records in other tables before deleting them, preventing orphaned references.

These mechanisms help ensure that the data in the application and the database remain synchronized, even when multiple users are accessing the system simultaneously.
