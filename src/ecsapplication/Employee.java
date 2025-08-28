/**
 * Employee.java
 * Employee is an entity who can perform major operational tasks
 * such as checking out and returning equipment, placing and canceling
 * orders for equipment, and reviewing reminders. This class ensures 
 * that all interactions with equipment and orders are handled according
 * to the business rules determined.
 */

// Main package for ECS application
package ecsapplication;

// Import enumerations used by this class
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.OrderStatus;

// Import SQL libraries for connection and queries
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

// Import Java time API for date
import java.time.LocalDate;

// Import framework for lists/arrays
import java.util.ArrayList;
import java.util.List;

public class Employee {

	// Attributes
	private int empID;          // Unique Identifier for the employee
	private String empName;     // Employee name
	private SkillClassification skillClassification;  // Employee's skill classification
	private List<Transaction> empTransaction;         // List of transactions associated with this employee
	private Order order;        // Order associated with this employee

	// Constructors
	public Employee() {
		this.empTransaction = new ArrayList<>();
	}

	// Minimal constructor using empID and empName
	public Employee(int empID, String empName) {
		this.empID = empID;
		this.empName = empName;
		this.empTransaction = new ArrayList<>();
	}

	// Overloaded constructor with skill classification
	public Employee(int empID, String empName, SkillClassification skillClassification) {
		this.empID = empID;
		this.empName = empName;
		this.skillClassification = skillClassification;

		// Initializes empTransactions as an empty list to avoid NullPointerExcepetion
		this.empTransaction = new ArrayList<>();
	}

	// Overloaded constructor with employee transaction and order
	public Employee(int empID, String empName, SkillClassification skillClassification,
			List<Transaction> empTransaction, Order order) {

		this.empID = empID;
		this.empName = empName;
		this.skillClassification = skillClassification;

		// Initialize empTransaction: use provided list if not null, otherwise create a new list
		this.empTransaction = empTransaction != null ? empTransaction : new ArrayList<>();
		this.order = order;
	}

	// Getters and Setters
	// Returns the employee ID
	public int getEmpID() {
		return empID;
	}

	// Sets the employee ID
	public void setEmpID(int empID) {
		this.empID = empID;
	}

	// Returns the employee name
	public String getEmpName() {
		return empName;
	}

	// Sets the employee name
	public void setEmpName(String empName) {
		this.empName = empName;
	}

	// Returns the skill classification of the employee
	public SkillClassification getSkillClassification() {
		return skillClassification;
	}

	// Sets the skill classification of the employee
	public void setSkillClassification(SkillClassification skillClassification) {
		this.skillClassification = skillClassification;
	}

	// Returns a list of transactions associated with the employee
	public List<Transaction> getEmpTransaction() {
		return empTransaction;
	}

	// Sets a list of transactions associated with the employee
	public void setEmpTransaction(List<Transaction> transactions) {
		this.empTransaction = transactions;
	}

	// Returns the current order associated with the employee
	public Order getOrder() {
		return order;
	}

	// ======= CHECKOUT EQUIPMENT METHOD ======== //
	// Enables an employee to check out equipment
	// ========================================== //
	public Transaction checkOut(Equipment equipment) {

		// Step 1: Get current date for borrow date
		LocalDate today = LocalDate.now();

		// Step 2: Set expected return date as 7 weeks from today
		LocalDate expectedReturn = today.plusWeeks(7);

		// Step 3: Create Transaction object 
		Transaction transaction = new Transaction(
				0,               // Transaction ID will be set by the DB
				this,            // Reference to the employee performing the checkout
				equipment,       // Equipment being borrowed
				null,            // Order reference not used here
				null,            // Order date not used here
				today,           // Borrow date
				expectedReturn,  // Expected return date
				TransactionStatus.Borrowed,  // Transaction status
				null,            // returnCondition
				equipment.getEquipmentCondition()   // Filled with checkoutCondition
				);

		// Step 4: Add transaction to employees local list for tracking
		this.empTransaction.add(transaction);

		// Step 5: Return to newly created transaction object
		return transaction;
	}

	// ======= ORDER EQUIPMENT METHOD ====== //
	// Enables an employee to order equipment
	// ====================================== //
	public String orderEquipment(Equipment equipment) {

		// Step 1: Equipment must be available
		if (equipment.getStatus() != EquipmentStatus.Available) {
			return "Equipment is not available.";
		}

		// Step 2: Employee must have the required skill classification
		if (!this.getSkillClassification().equals(equipment.getRequiredSkill())) {
			return "You are not qualified to order this equipment.";
		}

		// Step 3: Create a new Order object
		Order order = new Order(
				0,                      // Auto-generated ID
				this,                   // Employee placing the order
				equipment,              // Equipment to order
				LocalDate.now(),        // Order date = today
				OrderStatus.Confirmed   // Order Status = Confirmed
				);

		// Step 4: Save order to the database
		boolean success = OrderDAO.insertOrder(order);

		// Step 5: Update equipment status if order is successful
		if (success) {
			EquipmentDAO.updateEquipmentStatus(equipment.getEquipmentID(), EquipmentStatus.Ordered);
			return "Order confirmed.";
		} else {
			return "Failed to place order.";
		}
	}

	// ================== ORDER EQUIPMENT METHOD (OVERLOADED) ======================= //
	// Overloaded method to place an equipment order using the provided DB connection
	// ============================================================================== //
	public String orderEquipment(Connection conn, Equipment equipment) {
		try {
			
			// Check if equipment is available before placing order
			// If not available, return failure message immediately
			if (equipment.getStatus() != EquipmentStatus.Available) {
				return "Failed: Equipment not available";
			}
			
			// Create a new Order object for this employee and equipment, with status Confirmed
			Order order = new Order(this, equipment, OrderStatus.Confirmed);

			// Insert the order into the database
			boolean success = OrderDAO.insertOrder(conn, order);

			if (success) {
				// If insertion successful, update equipment status to Ordered
				EquipmentDAO.updateEquipmentStatus(conn, equipment.getEquipmentID(), EquipmentStatus.Ordered);
				return "Order confirmed."; // Return success message
			} else {
				return "Failed to place order."; // Return failure message if DB insert fails
			}

		} catch (SQLException e) {
			e.printStackTrace(); // Print stack trace for debugging
			return "Failed due to database error."; // Return error message on SQL exception
		}
	}

	// ===================== CANCEL ORDER METHOD ===================== //
	// Enables an employee to cancel an order made.
	// Updates the order status to ‘Cancelled’ and set the associated 
	// equipment status to ‘Available’ so that it can be ordered or 
	// checked out again.
	// =============================================================== //
	public String cancelOrder(int orderID) {
		try (Connection conn = DBConnect.getInstance().getConnection()) {

			// Retrieve order from the database using the given orderID
			Order order = OrderDAO.getOrderByID(conn, orderID);
			if (order == null) {
				return "Order not found";  // Return if order does not exist
			}
			if (order.getOrderStatus() == OrderStatus.Cancelled) {
				return "Order is already cancelled";  // Return if the order is already cancelled
			}

			// SQL statement to update the order status to "Cancelled"
			String sqlUpdateOrder = "UPDATE `order` SET orderStatus = 'Cancelled' WHERE orderID = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateOrder)) {
				stmt.setInt(1, orderID);
				stmt.executeUpdate();  // Execute the update
			}

			// SQL statement to update the equipment status to "Available"
			String sqlUpdateEquip = "UPDATE equipment SET equipStatus = 'Available' WHERE equipmentID = ?";
			try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateEquip)) {
				stmt.setInt(1, order.getEquipment().getEquipmentID());

				int affected = stmt.executeUpdate();  // Apply the update

				if (affected > 0) {
					return "Order successfully cancelled";  // Success message
				}
				else {
					return "Failed to cancel order";  // Failure message if equipment update fails
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();      // Print exception if database operation fails
			return "An error occured while cancelling the order.";
		}
	}

	// =========================== RETURN EQUIPMENT METHOD ========================= //
	// Allows an employee to return equipment previously borrowed equipment.
	// Updates the transaction status to ‘Returned’, equipment status to ‘Available’,
	// and equipment condition to the one chosen by the employee
	// ============================================================================= //
	public Transaction returnEquipment(int transactionID, EquipmentCondition condition) {

		// Iterates over the employee's list of transactions
		for(Transaction txn : empTransaction) {

			// Check that the transaction matches the given ID and is currently borrowed 
			if (txn.getTransactionID() == transactionID && txn.getTransactionStatus() == TransactionStatus.Borrowed) {

				// Validate the input: if no condition is provided, 
				// stop processing and return null
				if (condition == null) {
					return null;
				}

				// Capture today's date to be used as the return date 
				// for the equipment in this transaction
				LocalDate today = LocalDate.now();

				// Set return date to today and update the transaction status
				txn.setReturnDate(today);
				txn.setTransactionStatus(TransactionStatus.Returned);

				// Record the condition of the equipment in the transaction
				txn.setReturnCondition(condition);

				// Retrieve the Equipment object associated with this transaction
				// so that we can update its status 
				Equipment eq = txn.getEquipment();

				// Update equipment status to available
				eq.setStatus(EquipmentStatus.Available);

				// Persist the changes to the database
				Connection conn = null;
				try {
					// Obtain a database connection through DBConnect
					conn = DBConnect.getInstance().getConnection();
					conn.setAutoCommit(false);  // start transaction

					// Update transaction in the transaction table (e.g., update status, return date, return condition)
					TransactionDAO.updateTransactionReturn(conn, txn);

					// Update equipment status in the equipment table (e.g., Loaned)
					EquipmentDAO.updateEquipment(conn, eq);

					// If all updates succeeded, save changes to the database
					conn.commit();
				} catch (SQLException e) {

					// Print error to stack trace
					e.printStackTrace();
					if (conn != null) {

						// Cancel all changes if an error occurs
						try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
					}
				} finally {
					// Close connection in the 'Finally' block
					if (conn != null) {
						try { conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
					}
				}

				return txn;  // Return the transaction object
			}
		}
		return null;
	}
	
	// ================= RETURN EQUIPMENT OVERLOAD METHOD ================= //
	// Handles returning a borrow equipment for testing purposes. Uses an
	// H2 in-memory database connection to simulate actual DB operations.
	// ==================================================================== //
	public Transaction returnEquipment(Connection conn, int transactionID, EquipmentCondition condition) {
		
		// If no condition is provided, cancel the return and return null
		if (condition == null) return null; 

		try {
			// Call the DAO method to update the transaction in the database and get the updated transaction
			Transaction txnFromDB = TransactionDAO.returnEquipment(conn, transactionID, condition);

			// Iterate through the employee's local transaction list to update the in-memory objects
			for (Transaction t : empTransaction) {
				
				// Find the matching transaction by ID
				if (t.getTransactionID() == transactionID) {
					
					// Update the transaction status to 'Returned'
					t.setTransactionStatus(TransactionStatus.Returned);

					// Record the return condition in the transaction object
					t.setReturnCondition(condition);

					// Update the equipment status to 'Available'
					t.getEquipment().setStatus(EquipmentStatus.Available);

					// Return the updated in-memory transaction
					return t;
				}
			}

			// If the transaction was not found in the employee's local list, return the one retrieved from the database
			return txnFromDB;

		} catch (SQLException e) {
			// Print stack trace if any database exception occurs and return null
			e.printStackTrace();
			return null;
		}
	}

	// ============================== VIEW RECORD METHOD ================================ //
	// Retrieves a list of transactions associated with this employeeID from the database
	// Used for displaying individual employee's records in the "View Record" panel
	// ==================================================================================
	public List<Transaction> viewRecord() {

		// Declare a dynamic list to hold employee transactions
		List<Transaction> transactions = new ArrayList<>();

		try(Connection conn = DBConnect.getInstance().getConnection()){

			// Query database for transactions by employee ID
			transactions = TransactionDAO.getTransactionsByEmployeeID(conn, this.empID);

		}catch (SQLException e){
			e.printStackTrace();  // Print error if database access fails
		}
		return transactions;  // Return the list of transactions
	}

	// Returns a string representation of the employee (ID - Name)
	@Override
	public String toString() {
		return empID + " - " + empName;
	}
}