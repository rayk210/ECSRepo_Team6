package ecsapplication;

import java.sql.*;


public class DBConnect {
	
	private static final String url = "jdbc:mysql://localhost:3306/ceis400courseproject";
	private static final String username = "root";
	private static final String password = "devry123";

	public static Connection getConnection()throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
}
