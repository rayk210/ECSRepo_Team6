/**
 * OrderDAO.java
 * Provides methods to access, insert, update, and retrieve order
 * data from the database. Implements a Data Access Object (DAO)
 * pattern to separate database operations from business logic in
 * the ECS system.
 */

package ecsapplication;

// Import necessary libraries for JDBC operations, data handling, collections, and order status enum
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import ecsapplication.enums.OrderStatus;

public class OrderDAO {

	// ======================= METHOD: updateOrderStatus ============================= //
	// Updates the status of an existing order (e.g., from 'Confirmed' to 'Cancelled').
	// Returns true if the update succeeded, otherwise false
	// =============================================================================== //
	public static boolean updateOrderStatus(Connection conn, int orderID, OrderStatus status) throws SQLException {
		
		// Prepare SQL query to update the order status field for the given order ID
		String sql = "UPDATE `order` SET orderStatus = ? WHERE orderID = ?";
		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			
			// Set parameter: 1 = new status, 2 = order ID
			stmt.setString(1, status.name());
			stmt.setInt(2, orderID);
			
			// Execute statement and return whether any row was affected
			return stmt.executeUpdate() > 0;
		}
	}

	// ============== METHOD: insertOrder ============= //
	// Inserts new orders into the order table in MySQL.
	// This method is used when employees place an order
	// for equipment. Returns true if insertion succeeded,
	// otherwise, false.
	// ================================================ //
	public static boolean insertOrder(Order order) {
		
		// SQL query to insert order attributes into the order table
		String strSQL = "INSERT INTO `order` (empID, equipmentID, orderDate, orderStatus) VALUES (?, ?, ?, ?)";

		Connection conn = null;
		try {
			conn = DBConnect.getInstance().getConnection();
			try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
				
				// Set parameters for the PreparedStatement
				stmt.setInt(1, order.getEmployee().getEmpID());         // Employee ID
				stmt.setInt(2, order.getEquipment().getEquipmentID());  // Equipment ID
				stmt.setDate(3, java.sql.Date.valueOf(order.getOrderDate()));  // Order Date
				stmt.setString(4, order.getOrderStatus().name());       // Order status as string

				// Execute insertion
				int affectedRows = stmt.executeUpdate();
				
				// Return whether a row were affected
				return affectedRows > 0;
			}
		} catch (SQLException e) {
			
			// Print stack trace for debugging purposes
			e.printStackTrace();
			return false;
		}
	}

	// ================== METHOD: getAllOrders =================== //
	// Retrieves all order data from the database 
	// Provides an order history and list that can be seen on the UI
	// Ensures traceability of orders made by employees.
	// =========================================================== //
	public static List<Order> getAllOrders(Connection conn) throws SQLException {
		
		// Initialize a dynamic list to hold orders
		List<Order> orders = new ArrayList<>();

		// SQL query to join order, employee, and equipment tables
		String strSQL = "SELECT o.orderID, o.empID, o.equipmentID, o.orderDate, o.orderStatus, o.pickUpDate, " +
				"e.empName, eq.equipmentName " +
				"FROM `order` o " +
				"JOIN employee e ON o.empID = e.empID " +
				"JOIN equipment eq ON o.equipmentID = eq.equipmentID";

		// Prepare a SQL statement and execute the query, automatically closing resources after used
		try (PreparedStatement stmt = conn.prepareStatement(strSQL);
				ResultSet rs = stmt.executeQuery()) {

			// Iterate through each row in the result set
			while (rs.next()) {
				
				// Extract data from result set
				int orderID = rs.getInt("orderID");             // Get order ID
				int empID = rs.getInt("empID");                 // Get employee ID
				int equipID = rs.getInt("equipmentID");         // Get equipment ID
				Date orderDate = rs.getDate("orderDate");       // Get order date
				String statusStr = rs.getString("orderStatus"); // Get order status 
				Date pickupDate = rs.getDate("pickUpDate");     // Get pick up date

				// Create Employee and Equipment objects
				Employee emp = new Employee(empID, rs.getString("empName"));
				Equipment equip = new Equipment(equipID, rs.getString("equipmentName"));

				// Create Order object from extracted data
				Order order = new Order(orderID, emp, equip, orderDate.toLocalDate(),
						OrderStatus.valueOf(statusStr), 
						pickupDate != null ? pickupDate.toLocalDate() : null);

				// Add order to the orders list
				orders.add(order);
			}
		}

		// Return list of orders
		return orders;
	}

	// ======================= METHOD: getOrderByID ====================== //
	// Retrieves a single order from the database based on a given orderID
	// =================================================================== //
	public static Order getOrderByID(Connection conn, int orderID) {
		
		// SQL query to select all columns from the order table for a specific order ID
		String strSQL = "SELECT * FROM `order` WHERE orderID = ?";

		// Prepare the statement and set the orderID parameter
		try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
			stmt.setInt(1, orderID);
			
			// Execute the query and get the result set
			try (ResultSet rs = stmt.executeQuery()) {
				
				// Check if there is a result
				if (rs.next()) {
					
					// Retrieve the employee ID and equipment ID from the result 
					int employeeID = rs.getInt("empID");
					int equipmentID = rs.getInt("equipmentID");
					
					// Convert SQL orderDate to LocalDate
					LocalDate orderDate = rs.getDate("orderDate").toLocalDate();

					// Convert pickUpDate to LocalDate and handle null values
					Date pickUpDateSQL = rs.getDate("pickUpDate");
					LocalDate pickUpDate = (pickUpDateSQL != null) ? pickUpDateSQL.toLocalDate() : null;

					// Retrieve order status from the result set
					OrderStatus orderStatus = OrderStatus.fromString(rs.getString("orderStatus"));

					// Get employee and equipment objects from their DAOs
					Employee employee = EmployeeDAO.getEmployeeByID(conn, employeeID);
					Equipment equipment = EquipmentDAO.getEquipmentByID(conn, equipmentID);

					// Create and return a new Order object with retrieved data
					return new Order(orderID, equipment, employee, orderDate, orderStatus, pickUpDate, null);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();   // Print exception if database error occurs
		}
		
		// Return null if order is not found
		return null;
	}
}
