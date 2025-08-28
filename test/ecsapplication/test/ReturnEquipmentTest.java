/**
 * ReturnEquipmentTest.java
 * This JUnit test case verifies the Return Equipment feature
 * in the ECS system. It covers two scenarios:
 *   1. Successful return of equipment when a valid equipment
 *   condition is provided.
 *   2. Cancel return when no condition is provided (null condition)
 *   
 * This test ensures that the employee's transaction list and 
 * equipment status are updated correctly and that the return
 * condition is properly recorded.
 * 
 * Test Case ID: TC-RET-001-A
 */

package ecsapplication.test;

// Import static assertion methods from JUnit
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;      // Import the Connection interface for managing a database connection
import java.sql.DriverManager;   // Import DriverManager to obtain database connections
import java.sql.Statement;       // Import Statement to execute SQL queries and updates

import org.junit.jupiter.api.BeforeEach;   // Import JUnit annotation to run setup before each test
import org.junit.jupiter.api.DisplayName;  // Import annotation to provide a readable name for the test
import org.junit.jupiter.api.Test;         // Import annotation to mark a method as a test case

// Import ECS application and Enums
import ecsapplication.Employee;
import ecsapplication.Equipment;
import ecsapplication.Transaction;
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

// Test class for the Return Equipment feature in ECS system
// This JUnit class uses H2 in-memory database to isolate testing from real SQL database
class ReturnEquipmentTest {

	private Connection conn;         // H2 database connection used for testing
	private Employee employee;       // Employee object performing the return
	private Equipment equipment;     // Equipment object being returned
	private Transaction transaction; // Transaction representing the borrowed record

	@BeforeEach
	void setup() throws Exception {
		
		// Load H2 database driver
		Class.forName("org.h2.Driver");

		// Initialize in-memory H2 database (keeps DB alive for JVM session)
		conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

		// Create tables and insert sample data for testing
		try (Statement stmt = conn.createStatement()) {
			
			// Drop tables if exist to avoid conflicts
			stmt.execute("DROP TABLE IF EXISTS equipment");
			stmt.execute("DROP TABLE IF EXISTS transaction");

			// Create equipment table with equipmentID, name, status, and skill attributes
			stmt.execute("CREATE TABLE equipment(equipmentID INT PRIMARY KEY, name VARCHAR(100), status VARCHAR(20), skill VARCHAR(50))");
			
			// Create transaction table with transactionID, equipmentID, status, and returnCondition attributes
			stmt.execute("CREATE TABLE transaction(transactionID INT PRIMARY KEY, equipmentID INT, status VARCHAR(20), returnCondition VARCHAR(20))");

			// Insert sample equipment and transaction
			stmt.execute("INSERT INTO equipment VALUES (108, 'Voltage Tester', 'Loaned', 'Electrician')");
			stmt.execute("INSERT INTO transaction VALUES (1, 108, 'Borrowed', NULL)");
		}

		// Initialize Employee object for testing purposes
		employee = new Employee(1, "Jorge", SkillClassification.Electrician);

		// Initialize Equipment object that will be returned in the test
		equipment = new Equipment(108, "Voltage Tester", SkillClassification.Electrician);

		// Set the initial status of the equipment to 'Loaned'
		// This simulates that the equipment was borrowed before the return test
		equipment.setStatus(EquipmentStatus.Loaned);

		// Set the initial condition of the equipment to 'Good'
		// This ensures that the returnEquipment method has a valid starting condition
		equipment.setEquipmentCondition(EquipmentCondition.Good);

		// Create a Transaction object linking the employee and the equipment
		// The transaction starts with status 'Borrowed', representing a borrowed record
		transaction = new Transaction(1, employee, equipment, TransactionStatus.Borrowed);
		
		// Add transaction to employee's transaction list
		employee.getEmpTransaction().add(transaction);
	}

	@Test
	@DisplayName("Case 1: Successful return of equipment with a 'Good' condition")
	void testReturnEquipment_Success() {
		// Perform return using H2 connection and condition Good
		Transaction txn = employee.returnEquipment(conn, transaction.getTransactionID(), EquipmentCondition.Good);

		// Assertions to verify expected behavior
		assertNotNull(txn); // Transaction should not be null
		assertEquals(EquipmentStatus.Available, equipment.getStatus()); // Equipment status should update
		assertEquals(TransactionStatus.Returned, txn.getTransactionStatus()); // Transaction status updated
		assertEquals(EquipmentCondition.Good, txn.getReturnCondition()); // Return condition recorded correctly
	}

	@Test
	@DisplayName("Case 2: Return canceled when no condition is provided")
	void testReturnEquipment_Cancel() {
		// Attempt return with null condition (cancel scenario)
		Transaction txn = employee.returnEquipment(conn, transaction.getTransactionID(), null);

		// Assertions
		assertNull(txn); // Should return null
		assertEquals(EquipmentStatus.Loaned, equipment.getStatus()); // Equipment should remain Loaned
		assertEquals(TransactionStatus.Borrowed, transaction.getTransactionStatus()); // Transaction status unchanged
		assertNull(transaction.getReturnCondition()); // Return condition remains null
	}

	@Test
	@DisplayName("Case 3: Successful return of equipment with a 'Damaged' condition")
	void testReturnEquipment_Damaged() {
		// Perform return with condition Damaged
		Transaction txn = employee.returnEquipment(conn, transaction.getTransactionID(), EquipmentCondition.Damaged);

		// Assertions
		assertNotNull(txn); // Transaction should exist
		assertEquals(EquipmentStatus.Available, equipment.getStatus()); // Equipment status updated
		assertEquals(TransactionStatus.Returned, txn.getTransactionStatus()); // Transaction status updated
		assertEquals(EquipmentCondition.Damaged, txn.getReturnCondition()); // Return condition recorded
	}
}
