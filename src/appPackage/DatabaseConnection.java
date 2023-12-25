package appPackage;
import java.sql.*;

// This class is responsible for managing database connections
public class DatabaseConnection {

    // Database URL, username, and password are defined as constants.
    // Update these values based on your database configuration.
    private static final String url = "jdbc:mysql://localhost:3306/footballleague";
    private static final String user = "root";
    private static final String password = "pass1234!";

    /**
     * Gets a connection to the database.
     * @return A Connection object or null if a connection could not be established.
     */
    public static Connection getConnection() {
        try {
            // Load the JDBC driver class. This step is necessary to register the driver.
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish and return a connection to the database using the defined URL, user, and password.
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            // Print the stack trace if an exception occurs.
            // Note: For production applications, it's better to use a logging framework instead of printing the stack trace.
            e.printStackTrace();
            return null;
        }
    }
}

