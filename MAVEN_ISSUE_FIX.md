# Maven Issue Fix

## Problem Description

The application was failing to run with the error message `'mvn' is not recognized as a valid command`, indicating that Maven was not installed or not in the system's PATH.

## Solution

We implemented the following changes to fix the Maven issue:

1. **Used the Maven Wrapper**: The project already included Maven Wrapper files (`mvnw.cmd` for Windows and `mvnw` for Linux/macOS), which allow running Maven commands without having Maven installed globally.

2. **Set JAVA_HOME Environment Variable**: The Maven Wrapper requires the JAVA_HOME environment variable to be set to the Java installation directory. We added instructions in the README.md file for setting this variable.

3. **Updated JavaFX Dependencies**: We updated the JavaFX dependencies from version 17.0.6 to version 21.0.2 to better match the Java 23 version being used.

4. **Fixed Database Connection**: We replaced the MySQL database connection with an H2 embedded database, which doesn't require a separate database server to be running. This makes the application more self-contained and easier to run.

## How to Run the Application

1. Set the JAVA_HOME environment variable to your Java installation directory:
   ```
   # Windows (PowerShell)
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
   
   # Windows (Command Prompt)
   set JAVA_HOME=C:\Program Files\Java\jdk-23
   
   # Linux/macOS
   export JAVA_HOME=/path/to/your/java/home
   ```

2. Build and run the application using the Maven wrapper:
   ```
   # Windows
   .\mvnw.cmd clean javafx:run
   
   # Linux/macOS
   ./mvnw clean javafx:run
   ```

## Additional Notes

- The application now uses an H2 embedded database instead of MySQL, which means you don't need to have a MySQL server running.
- The database is created in memory and will be lost when the application is closed. If you need persistent storage, you can modify the JDBC URL in `DatabaseUtil.java` to use a file-based H2 database.
- If you prefer to use MySQL, you can revert the changes in `DatabaseUtil.java` and update the database connection settings to match your MySQL configuration.