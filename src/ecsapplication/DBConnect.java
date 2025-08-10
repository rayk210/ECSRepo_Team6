package ecsapplication;

import java.sql.*;

// Singleton Pattern
public class DBConnect {
	
	// Attributes
    private static DBConnect instance;
    private Connection connection;

    private static final String url = "jdbc:mysql://localhost:3306/ceis400courseproject";
    private static final String username = "root";
    private static final String password = "devry123";

    // Constructor set to private so it cannot be directly accessed outside of the class
    private DBConnect() throws SQLException {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to the database", e);
        }
    }

    // Public method to obtain a single instance of DBConnect
    // Synchronized prevents another thread from accessing this method
    public static synchronized DBConnect getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DBConnect();
        }else if (instance.connection == null || instance.connection.isClosed()) {
        	instance.connection = DriverManager.getConnection(url, username, password);
        }
        return instance;
    }

    // Getter method for Connection
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
