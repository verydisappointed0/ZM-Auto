# Login Issue Fix

## Problem Description

The application was showing "Invalid username or password" error when trying to log in with the correct credentials. This was happening even though the password hash in the database was correct.

## Root Cause

The issue was caused by a double-hashing problem in the authentication process:

1. When a users is loaded from the database, the `mapResultSetToUser` method in `UserService` was using `users.setPassword(rs.getString("password"))` to set the password.
2. The `setPassword` method in the `User` class always hashes the input password.
3. This meant that the already-hashed password from the database was being hashed again, resulting in a double-hashed password stored in the User object.
4. When verifying a password during login, the `verifyPassword` method hashes the input password once and compares it to the stored password, which was now double-hashed, causing the verification to fail.

## Solution

We implemented the following changes to fix the login issue:

1. Added a new method `setHashedPassword` to the `User` class that directly sets the password field without hashing it:

    public void setHashedPassword(String hashedPassword) {
        this.password = hashedPassword;
    }

2. Updated the `mapResultSetToUser` method in `UserService` to use this new method instead of `setPassword`:

    users.setHashedPassword(rs.getString("password"));

This ensures that when a users is loaded from the database, the already-hashed password is set directly without being hashed again, allowing the `verifyPassword` method to work correctly during authentication.

## How to Login

You can now log in to the application using the credentials specified in the README.md:

- **Username**: admin
- **Password**: admin123

## Technical Details

The password hashing mechanism in the application uses SHA-256 hashing followed by Base64 encoding. The correct hash for "admin123" is:

```
JAvlGPq9JyTdtvBO6x2llnRI1+gxwIyPqCKAn3THIKk=
```

This hash is stored in the database for both the admin and user1 accounts.
