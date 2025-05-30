# ZM-Auto Admin Panel

A JavaFX-based admin panel for managing a vehicle rental system. This application allows administrators to manage users, vehicles, and reservation requests.

## Features

- **User Authentication**: Secure login system with password hashing
- **User Management**: Add, edit, and delete users with different roles (ADMIN, USER)
- **Vehicle Management**: Add, edit, and delete vehicles with details like make, model, year, etc.
- **Reservation Management**: View, approve, and reject reservation requests
- **Database Integration**: H2 embedded database for persistent storage of all data
- **Responsive UI**: Modern and responsive users interface built with JavaFX and CSS

## Technologies Used

- Java 23
- JavaFX 21
- H2 Database (embedded)
- Hibernate/JPA
- HikariCP (Connection Pooling)
- CSS for styling

## Prerequisites

- Java 23 or higher
- Maven (or use the included Maven wrapper)

## Setup and Installation

1. Clone the repository
2. Set the JAVA_HOME environment variable to your Java installation directory:
   ```
   # Windows (PowerShell)
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-23"

   # Windows (Command Prompt)
   set JAVA_HOME=C:\Program Files\Java\jdk-23

   # Linux/macOS
   export JAVA_HOME=/path/to/your/java/home
   ```
3. Build and run the application using the Maven wrapper:
   ```
   # Windows
   .\mvnw.cmd clean javafx:run

   # Linux/macOS
   ./mvnw clean javafx:run
   ```

## Default Login Credentials

- **Username**: admin
- **Password**: admin123

## Project Structure

- `src/main/java/com/adminpanel/zmauto/`
  - `model/` - Entity classes (User, Vehicle, Reservation)
  - `controller/` - JavaFX controllers
  - `service/` - Service classes for business logic
  - `util/` - Utility classes for database connection, etc.
- `src/main/resources/com/adminpanel/zmauto/`
  - `css/` - CSS stylesheets
  - `db/` - Database initialization scripts
  - FXML files for UI layouts

## Screenshots

(Screenshots will be added here)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgements

- JavaFX community for the excellent UI framework
- Hibernate team for the ORM framework
- HikariCP for the high-performance connection pool
