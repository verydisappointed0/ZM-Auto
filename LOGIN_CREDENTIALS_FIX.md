# Login Credentials Fix

## Problem Description

The login credentials specified in the README.md file were not working because the password hash in the database initialization script did not match the expected hash for the password "admin123".

## Solution

We implemented the following changes to fix the login credentials issue:

1. **Updated the Password Hash**: We updated the password hash in the database initialization script (`init.sql`) to match the correct hash for "admin123".

2. **Fixed Database Initialization for H2**: We also made several changes to make the database initialization script compatible with the H2 embedded database:
   - Removed the `CREATE DATABASE` and `USE` statements, which are not supported by H2
   - Escaped the `year` column in the vehicles table, as it's a reserved keyword in H2
   - Simplified the `INSERT ... SELECT ... WHERE NOT EXISTS` statements to make them compatible with H2

## How to Login

You can now log in to the application using the credentials specified in the README.md:

- **Username**: admin
- **Password**: admin123

## Technical Details

The password hashing mechanism in the application uses SHA-256 hashing followed by Base64 encoding. The correct hash for "admin123" is:

```
JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=
```

This hash is now correctly stored in the database for both the admin and user1 accounts.

## Additional Notes

- The application now uses an H2 embedded database instead of MySQL, which means you don't need to have a MySQL server running.
- The database is created in memory and will be lost when the application is closed.
- If you need persistent storage, you can modify the JDBC URL in `DatabaseUtil.java` to use a file-based H2 database.