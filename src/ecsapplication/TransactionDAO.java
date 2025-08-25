/**
 * TransactionDAO.java
 * Provides methods to access and manipulate transaction data
 * in the database. It implements the Data Access Object (DAO)
 * to isolate persistence logic from the rest of the application.
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
						rs.getInt("equipmentID"),   // Equipment ID
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

	// Is used to provide a throughout view of all transactions by joining the employee, equipment, and order tables
	public static List<Transaction> getAllTransactions(Connection conn) throws SQLException {
		List<Transaction> txns = new ArrayList<>();

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

		try (PreparedStatement stmt = conn.prepareStatement(strSQL);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				// Make Employee object
				Employee employee = new Employee(
						rs.getInt("empID"),
						rs.getString("empName"),
						SkillClassification.valueOf(rs.getString("skillClassification"))
						);

				// Make Equipment object
				Equipment eq = new Equipment(
						rs.getInt("equipmentID"),
						rs.getString("equipmentName"),
						null, // current inventory condition
						EquipmentStatus.valueOf(rs.getString("equipStatus")),
						SkillClassification.valueOf(rs.getString("requiredSkill"))
						);

				// Make Order object, if exists
				Order order = null;
				int orderID = rs.getInt("orderID");
				if (!rs.wasNull()) {
					LocalDate orderDate = rs.getDate("orderDate") != null
							? rs.getDate("orderDate").toLocalDate() : null;
					OrderStatus orderStatus = rs.getString("orderStatus") != null
							? OrderStatus.valueOf(rs.getString("orderStatus")) : null;
					LocalDate pickUpDate = rs.getDate("pickUpDate") != null
							? rs.getDate("pickUpDate").toLocalDate() : null;

					order = new Order(orderID, eq, null, orderDate, orderStatus, pickUpDate, null);
				}

				// Transaction object
				Transaction txn = new Transaction(
						rs.getInt("transactionID"),
						employee,
						eq,
						order,
						rs.getDate("orderDate") != null ? rs.getDate("orderDate").toLocalDate() : null,
						rs.getDate("borrowDate") != null ? rs.getDate("borrowDate").toLocalDate() : null,
						rs.getDate("expectedReturnDate") != null ? rs.getDate("expectedReturnDate").toLocalDate() : null,
						TransactionStatus.valueOf(rs.getString("transactionStatus")),
						rs.getString("returnCondition") != null ? EquipmentCondition.valueOf(rs.getString("returnCondition")) : null,
						rs.getString("checkoutCondition") != null ? EquipmentCondition.valueOf(rs.getString("checkoutCondition")) : null
						);

				// Set returnDate if exists
				if (rs.getDate("returnDate") != null) {
					txn.setReturnDate(rs.getDate("returnDate").toLocalDate());
				}

				txns.add(txn);
			}
		}

		return txns;
	}

	// Is used to update transaction status to ‘Returned’ and record the return date in the database
	// This method is invoked when employees return equipment
	public static void updateTransactionReturn(Connection conn, Transaction txn) throws SQLException {
		String strSQL = "UPDATE transaction SET transactionStatus = ?, returnDate = ?, returnCondition = ? WHERE transactionID = ?";

		try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
			stmt.setString(1, txn.getTransactionStatus().name());
			stmt.setDate(2, Date.valueOf(txn.getReturnDate()));
			System.out.println("Updating transaction ID: " + txn.getTransactionID());
			System.out.println("Return condition: " + txn.getReturnCondition());
			stmt.setString(3, txn.getReturnCondition() != null ? txn.getReturnCondition().name() : null);
			stmt.setInt(4, txn.getTransactionID());
			stmt.executeUpdate();
		}
	}

	// Queries the database and retrieves all transactions according to the empID parameter
	public static List<Transaction> getTransactionsByEmployeeID(Connection conn, int empID) throws SQLException {
		List<Transaction> txns = new ArrayList<>();
		String strSQL = "SELECT t.transactionID, t.empID, t.equipmentID, t.orderID, t.borrowDate, t.expectedReturnDate, t.returnDate, t.transactionStatus, " +
				"e.equipmentName, e.equipmentCondition, e.requiredSkill, e.equipStatus " +
				"FROM transaction t JOIN equipment e ON t.equipmentID = e.equipmentID " +
				"WHERE t.empID = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(strSQL)) {
			pstmt.setInt(1, empID);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				Equipment eq = new Equipment(
						rs.getInt("equipmentID"),
						rs.getString("equipmentName"),
						EquipmentCondition.valueOf(rs.getString("equipmentCondition")),
						EquipmentStatus.valueOf(rs.getString("equipStatus")),
						SkillClassification.valueOf(rs.getString("requiredSkill"))
						);

				Transaction txn = new Transaction(
						rs.getInt("transactionID"),
						null,
						eq,
						null,
						null,
						rs.getDate("borrowDate").toLocalDate(),
						rs.getDate("expectedReturnDate") != null ? rs.getDate("expectedReturnDate").toLocalDate() : null,
						TransactionStatus.valueOf(rs.getString("transactionStatus"))
						);

				if (rs.getDate("returnDate") != null) {
					txn.setReturnDate(rs.getDate("returnDate").toLocalDate());
				}

				txns.add(txn);
			}
		}
		return txns;
	}

	// Retrieves a list of all employees with a Borrowed transaction status
	public static List<Transaction> getBorrowedTransactionsByEmployee(int empID, Connection conn) throws SQLException {
		List<Transaction> transactions = new ArrayList<>();

		String strSQL = "SELECT * FROM Transaction WHERE empID = ? AND transactionStatus = 'Borrowed'";

		try(PreparedStatement ps = conn.prepareStatement(strSQL)){
			ps.setInt(1, empID);
			try(ResultSet rs = ps.executeQuery()){
				while(rs.next()) {
					Transaction t = new Transaction();

					t.setTransactionID(rs.getInt("transactionID"));
					t.setEmployee(EmployeeDAO.getEmployeeByID(conn, rs.getInt("empID")));
					t.setEquipment(EquipmentDAO.getEquipmentByID(conn, rs.getInt("equipmentID")));
					t.setExpectedReturnDate(rs.getDate("expectedReturnDate").toLocalDate());
					t.setTransactionStatus(TransactionStatus.valueOf(rs.getString("transactionStatus")));

					transactions.add(t);
				}
			}
		}
		return transactions;
	}
}
