/**
 * TransactionDAO.java
 * Provides methods to access and manipulate transaction data
 * in the database. It implements the Data Access Object (DAO)
 * pattern to isolate persistence logic from the rest of the application.
 * This class interacts with the transaction table, and may join
 * related information from the employee, equipment, and order
 * tables.
 */

package ecsapplication;

// Import statements for SQL operations, data handling, collections, and application enums
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.OrderStatus;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

public class TransactionDAO {

	// ================ METHOD: getTransactionsByID ================ // 
	// Get transaction by ID (joins Employee, Equipment, Order data)
	// ============================================================= //
	public static Transaction getTransactionByID(Connection conn, int transactionID) throws SQLException {

		// SQL statement to retrieve transaction details along with employee, equipment, and order information
		String strSQL = "SELECT t.transactionID, t.empID, t.equipmentID, t.orderID, t.borrowDate, t.expectedReturnDate, t.transactionStatus, "
				+ "e.empName, e.skillClassification, "
				+ "eq.equipmentName, eq.equipmentCondition, eq.requiredSkill, eq.equipStatus, "
				+ "o.orderDate, o.pickUpDate, o.orderStatus "
				+ "FROM transaction t "
				+ "JOIN employee e ON t.empID = e.empID "
				+ "JOIN equipment eq ON t.equipmentID = eq.equipmentID "
				+ "LEFT JOIN `order` o ON t.orderID = o.orderID "
				+ "WHERE t.transactionID = ?";

		// Use prepared statement to safely set transaction ID parameter
		try (PreparedStatement pstmt = conn.prepareStatement(strSQL)) {

			// Bind transaction ID to the first '?' placeholder in SQL
			pstmt.setInt(1, transactionID);

			// Execute the query and get the result set
			ResultSet rs = pstmt.executeQuery();

			// Check if row exists in the result set
			if (rs.next()) {

				// Create an Employee object using values from the result set
				Employee employee = new Employee(
						rs.getInt("empID"),         // Employee ID
						rs.getString("empName"),    // Employee Name
						SkillClassification.valueOf(rs.getString("skillClassification")),  // Skill Classification enum
						null,                       // Employee Transaction not loaded here
						null                        // Order is not associated 
						);

				// Create an Equipment object using values from the result set
				Equipment equipment = new Equipment(
						rs.getInt("equipmentID"),       // Equipment ID
						rs.getString("equipmentName"),  // Equipment Name
						EquipmentCondition.valueOf(rs.getString("equipmentCondition")),    // Equipment Condition enum
						EquipmentStatus.valueOf(rs.getString("equipStatus")),              // Equipment Status enum
						SkillClassification.valueOf(rs.getString("requiredSkill"))         // Required Skill enum
						);

				// Initialize Order object (nullable)
				Order order = null;
				int orderID = rs.getInt("orderID");  // Get order ID from result set

				// Check if order ID exists
				if (!rs.wasNull()) {
					order = new Order(
							orderID,     // Order ID
							equipment,   // Associated equipment
							employee,    // Associated employee
							rs.getDate("orderDate").toLocalDate(),  // Order date
							OrderStatus.valueOf(rs.getString("orderStatus")),  // Order status enum
							rs.getDate("pickUpDate").toLocalDate(), // Pick-up date
							null         // Transaction (nullable)
							);
				}

				// Create and return a new Transaction object using data gathered above
				return new Transaction(
						transactionID,       // Transaction ID
						employee,            // Associated employee
						equipment,           // Associated equipment
						order,               // Associated order (can be null)

						// Convert SQL orderDate to LocalDate if it's not null; otherwise return null
						rs.getDate("orderDate") != null ? rs.getDate("orderDate").toLocalDate() : null,

						// Convert SQL borrowDate to LocalDate if it's not null; otherwise return null
						rs.getDate("borrowDate") != null ? rs.getDate("borrowDate").toLocalDate() : null,

						// Convert SQL expectedReturnDate to LocalDate if it's not null; otherwise return null
						rs.getDate("expectedReturnDate") != null ? rs.getDate("expectedReturnDate").toLocalDate() : null,
						TransactionStatus.fromString(rs.getString("transactionStatus"))  // Transaction status enum
						);
			}
		}
		return null;
	}

	// ====================== METHOD: getAllTransactions ==================== //
	// Retrieves all transactions along with related employees, equipment,
	// and order details. Helps provide a complete view of system activity.
	// ====================================================================== //
	public static List<Transaction> getAllTransactions(Connection conn) throws SQLException {

		// Initialize a dynamic list to hold transactions from the query
		List<Transaction> txns = new ArrayList<>();

		// SQL query to retrieve all transaction data
		String strSQL = "SELECT t.transactionID, t.empID, t.equipmentID, t.orderID, t.returnDate, " +
				"t.borrowDate, t.expectedReturnDate, t.transactionStatus, t.returnCondition, t.checkoutCondition, " +
				"e.empName, e.skillClassification, " +
				"eq.equipmentName, eq.equipmentCondition, eq.equipStatus, eq.requiredSkill, " +
				"o.orderDate AS orderDate, o.orderStatus, o.pickUpDate " +
				"FROM transaction t " +
				"JOIN employee e ON t.empID = e.empID " +
				"JOIN equipment eq ON t.equipmentID = eq.equipmentID " +
				"LEFT JOIN `order` o ON t.orderID = o.orderID " +
				"ORDER BY t.transactionID ASC";

		// Try-with-resources ensures that rs and stmt are auto-closed
		try (PreparedStatement stmt = conn.prepareStatement(strSQL);
				ResultSet rs = stmt.executeQuery()) {

			// Iterate through every row of the result set (1 row = 1 transaction record)
			while (rs.next()) {

				// Create a new Employee object from the query results
				Employee employee = new Employee(
						rs.getInt("empID"),
						rs.getString("empName"),
						SkillClassification.valueOf(rs.getString("skillClassification"))
						);

				// Create a new Equipment object
				Equipment eq = new Equipment(
						rs.getInt("equipmentID"),
						rs.getString("equipmentName"),
						null,       // Equipment condition set to null so it does not overwrite equipment conditions
						EquipmentStatus.valueOf(rs.getString("equipStatus")),
						SkillClassification.valueOf(rs.getString("requiredSkill"))
						);

				// Order object is optional; default null until proven otherwise
				Order order = null;
				int orderID = rs.getInt("orderID");

				// Only construct Order if order ID is not null in DB
				if (!rs.wasNull()) {

					// Convert orderDate safely: if DB column is not null, then convert to LocalDate; otherwise stay null
					LocalDate orderDate = rs.getDate("orderDate") != null
							? rs.getDate("orderDate").toLocalDate() : null;

					// Convert orderStatus safely: if DB column is not null, then map to Enum; otherwise stay null
					OrderStatus orderStatus = rs.getString("orderStatus") != null
							? OrderStatus.valueOf(rs.getString("orderStatus")) : null;

					// Convert pickUpDate safely: if DB column is not null, then convert to LocalDate; otherwise stay null
					LocalDate pickUpDate = rs.getDate("pickUpDate") != null
							? rs.getDate("pickUpDate").toLocalDate() : null;

					// Create the Order object with relevant fields
					order = new Order(orderID, eq, null, orderDate, orderStatus, pickUpDate, null);
				}

				// Create a new Transaction object composed of employee, equipment, and optional order
				// Defensive checks ensure safe conversion of DB fields to Java types
				Transaction txn = new Transaction(
						rs.getInt("transactionID"),     // Transaction ID from DB
						employee,                       // Associated Employee object
						eq,                             // Associated Equipment object
						order,                          // Associated Order object (may be null)
						rs.getDate("orderDate") != null ? rs.getDate("orderDate").toLocalDate() : null,   // Convert to LocalDate if not null
						rs.getDate("borrowDate") != null ? rs.getDate("borrowDate").toLocalDate() : null, // Borrow date (nullable)
						rs.getDate("expectedReturnDate") != null ? rs.getDate("expectedReturnDate").toLocalDate() : null,  // Expected return date (nullable)
						TransactionStatus.valueOf(rs.getString("transactionStatus")),                     // Transaction status
						rs.getString("returnCondition") != null ? EquipmentCondition.valueOf(rs.getString("returnCondition")) : null,    // Return condition (nullable)
						rs.getString("checkoutCondition") != null ? EquipmentCondition.valueOf(rs.getString("checkoutCondition")) : null // Checkout condition (nullable)
						);

				// Set returnDate if exists
				if (rs.getDate("returnDate") != null) {
					txn.setReturnDate(rs.getDate("returnDate").toLocalDate());
				}

				// Add transaction to list of transactions
				txns.add(txn);
			}
		}

		// Return the transactions list
		return txns;
	}

	// ==================== METHOD: updateTransactionReturn ================== //
	// Updates a transaction marked as 'Returned' and records the return date
	// and condition in the database. Invoked when an employee returns equipment.
	// ======================================================================= //
	public static void updateTransactionReturn(Connection conn, Transaction txn) throws SQLException {

		// SQL statement to update transaction status, return date, and return condition
		String strSQL = "UPDATE transaction SET transactionStatus = ?, returnDate = ?, returnCondition = ? WHERE transactionID = ?";

		// Use try-with-resources to automatically close the PreparedStatement
		try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {

			// Set the transaction status parameter (first '?')
			stmt.setString(1, txn.getTransactionStatus().name());

			// Set the return date parameter (second '?')
			// Date conversion for storing in DB
			stmt.setDate(2, Date.valueOf(txn.getReturnDate()));

			// Set the return condition parameter (third '?')
			// If returnCondition is null, the database column will be set to null; otherwise, convert Enum to string
			stmt.setString(3, txn.getReturnCondition() != null ? txn.getReturnCondition().name() : null);

			// Set the transaction ID parameter (fourth '?') for WHERE clause
			stmt.setInt(4, txn.getTransactionID());

			// Execute the SQL statement
			stmt.executeUpdate();
		}
	}

	// =================== METHOD: getTransactionsByEmployeeID =================== //
	// Queries the database and retrieves all transactions for a given employee ID.
	// Returns a list of Transaction objects, each linked to its equipment.
	// =========================================================================== //
	public static List<Transaction> getTransactionsByEmployeeID(Connection conn, int empID) throws SQLException {

		// Initialize a list to store transactions
		List<Transaction> txns = new ArrayList<>();

		// SQL query to join transaction and equipment tables, filtered by employee ID
		String strSQL = "SELECT t.transactionID, t.empID, t.equipmentID, t.orderID, t.borrowDate, t.expectedReturnDate, t.returnDate, t.transactionStatus, " +
				"e.equipmentName, e.equipmentCondition, e.requiredSkill, e.equipStatus " +
				"FROM transaction t JOIN equipment e ON t.equipmentID = e.equipmentID " +
				"WHERE t.empID = ?";

		// Use try-with-resources to ensure PreparedStatement closes automatically
		try (PreparedStatement pstmt = conn.prepareStatement(strSQL)) {

			// Set the employee ID parameter for the WHERE clause
			pstmt.setInt(1, empID);

			// Execute the query 
			ResultSet rs = pstmt.executeQuery();

			// Iterate through each row in the result set
			while (rs.next()) {

				// Create an Equipment object from result set
				Equipment eq = new Equipment(
						rs.getInt("equipmentID"),
						rs.getString("equipmentName"),
						EquipmentCondition.valueOf(rs.getString("equipmentCondition")),  // Enum conversion
						EquipmentStatus.valueOf(rs.getString("equipStatus")),            // Enum conversion
						SkillClassification.valueOf(rs.getString("requiredSkill"))       // Enum conversion
						);

				// Create a Transaction object
				// Employee and Order are null because this method only retrieves transactions by empID
				Transaction txn = new Transaction(
						rs.getInt("transactionID"),
						null,      // Employee object not included here
						eq,        // Equipment object
						null,      // Order object not included here
						null,      // Order data not retrieved 
						rs.getDate("borrowDate").toLocalDate(),  // Borrow date
						rs.getDate("expectedReturnDate") != null ? rs.getDate("expectedReturnDate").toLocalDate() : null,  // Expected return date (nullable)
						TransactionStatus.valueOf(rs.getString("transactionStatus"))  // Enum conversion of transaction status
						);

				// Set return date if exists
				if (rs.getDate("returnDate") != null) {
					txn.setReturnDate(rs.getDate("returnDate").toLocalDate());
				}

				// Add transaction to list of transactions
				txns.add(txn);
			}
		}

		// Return list of transactions
		return txns;
	}

	// ============== METHOD: getBorrowedTransactionsByEmployee ============= //
	// Retrieves all transactions for a given employee ID that are currently
	// 'Borrowed'. Returns a list of Transaction objects with Employee and
	// Equipment objects populated. 
	// ====================================================================== //
	public static List<Transaction> getBorrowedTransactionsByEmployee(int empID, Connection conn) throws SQLException {

		// Initialize a list to store borrowed transactions
		List<Transaction> transactions = new ArrayList<>();

		// SQL query to select all transaction for employee ID with status 'Borrowed'
		String strSQL = "SELECT * FROM Transaction WHERE empID = ? AND transactionStatus = 'Borrowed'";

		// Use try-with-resources for PreparedStatement to ensure automatic closing
		try(PreparedStatement ps = conn.prepareStatement(strSQL)){

			// Set the employee ID parameter for the WHERE clause
			ps.setInt(1, empID);

			// Execute the query and obtain the results
			try(ResultSet rs = ps.executeQuery()){

				// Iterate through each row in the result set
				while(rs.next()) {

					// Create a new Transaction object
					Transaction t = new Transaction();

					// Populate fields of the Transaction object
					t.setTransactionID(rs.getInt("transactionID"));                        // Set transaction ID
					t.setEmployee(EmployeeDAO.getEmployeeByID(conn, rs.getInt("empID")));  // Retrieve Employee object from DAO
					t.setEquipment(EquipmentDAO.getEquipmentByID(conn, rs.getInt("equipmentID")));  // Retrieve Equipment object from DAO

					// Set expected return date
					t.setExpectedReturnDate(rs.getDate("expectedReturnDate").toLocalDate());

					// Set transaction status
					t.setTransactionStatus(TransactionStatus.valueOf(rs.getString("transactionStatus")));

					// Add transaction to list of transactions
					transactions.add(t);
				}
			}
		}

		// Return list of borrowed transactions for the employee
		return transactions;
	}

	// ===================== METHOD: returnEquipment ===================== //
	// Handles the return of a borrowed equipment in the H2 in-memory
	// database for unit testing. Updates transaction status and return
	// condition, then retrieves the updated transaction.
	// This is a static method intended for DAO usage in unit tests only.
	// ================================================================== //
	public static Transaction returnEquipment(Connection conn, int transactionID, EquipmentCondition condition) throws SQLException {

		// 1. SQL statement to update the transaction with return condition and status
		String updateSql = "UPDATE transaction SET returnCondition = ?, status = ? WHERE transactionID = ?";

		// Prepare the SQL statement for execution
		try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
			pstmt.setString(1, condition.name()); // Set the return condition as string (enum name)
			pstmt.setString(2, "Returned");       // Update transaction status to 'Returned'
			pstmt.setInt(3, transactionID);       // Specify which transaction to update by ID
			pstmt.executeUpdate();                // Execute the update query
		}

		// 2. SQL statement to fetch the updated transaction with its equipment details
		String selectSql = "SELECT t.transactionID, t.equipmentID, t.status, t.returnCondition, "
				+ "e.name, e.status AS eqStatus, e.skill "
				+ "FROM transaction t JOIN equipment e ON t.equipmentID = e.equipmentID "
				+ "WHERE t.transactionID = ?";

		// Prepare the select statement
		try (PreparedStatement pstmt = conn.prepareStatement(selectSql)) {
			
			// Set the transaction ID parameter
			pstmt.setInt(1, transactionID);
			
			// Execute query and get results
			try (ResultSet rs = pstmt.executeQuery()) {
				
				// If a record is found in the result set
				if (rs.next()) {
					
					// Construct Equipment object from result set
					Equipment eq = new Equipment(rs.getInt("equipmentID"), rs.getString("name")); // ID and name
					
					// Convert string to enum
					eq.setStatus(EquipmentStatus.valueOf(rs.getString("eqStatus")));

					// Construct and return the Transaction object
					return new Transaction(
							rs.getInt("transactionID"),   // Transaction ID from DB
							null,                         // Employee is null (testing only)
							eq,                           // Equipment object
							null,                         // Order object is null
							null,                         // Order date is null
							LocalDate.now().minusDays(1), // Simulated borrow date for testing
							LocalDate.now(),              // Return date set to current date
							TransactionStatus.Returned    // Transaction status set to Returned
							);
				}
			}
		}

		// Return null if no transaction is found with the given ID
		return null;
	}
}
