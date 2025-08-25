/**
 * DBConnect.java
 * The DBConnect class provides a centralized interface to manage a connection
 * to the MySQL database that is used by the ECS system. 
 * This class is implemented using a Singleton design pattern which ensures 
 * that only one active connection is used throughout the whole application.
 * This pattern enables the ECS system to manage its resources as well
 * as maintain consistency in transaction data.
 */

package ecsapplication;

// Import the entire class within java.sql
import java.sql.*;

// Singleton Pattern
public class DBConnect {
	
	// Attributes
    private static DBConnect instance;  // Singular instance 
    private Connection connection;      // Database connection object

    // Database connection constants
    private static final String url = "jdbc:mysql://localhost:3306/ceis400courseproject";
    private static final String username = "root";
    private static final String password = "devry123";

    // Constructor set to private so it cannot be directly accessed outside of the class
    private DBConnect() throws SQLException {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
        	
        	// If connection fails, throw an exception
            throw new SQLException("Failed to connect to the database", e);
        }
    }

    // Public method to obtain a single instance of DBConnect
    // Synchronized prevents another thread from accessing this method
    // Singleton instance getter
    public static synchronized DBConnect getInstance() throws SQLException {
    	
    	// Create a new instance if one does not yet exist or if an old connection has closed
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DBConnect();
        }
        return instance;
    }

    // Getter for connection
    public Connection getConnection() {
        return connection;
    }
}
