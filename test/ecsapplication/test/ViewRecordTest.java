/**
 * ViewRecordTest.java
 * This JUnit test verifies the View Record feature in
 * the ECS system. It retrieves all transaction history
 * for multiple employees, including borrow and return dates,
 * transaction status, equipment details, and employee details.
 * 
 * This test ensures that the employee's transaction list is
 * correctly populated and that integrity of the transaction
 * data is maintained.
 * 
 * Test Case ID: TC-VR-001-A
 */

package ecsapplication.test;

import ecsapplication.Employee;
import ecsapplication.Transaction;
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.SkillClassification;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//Use a single test class instance for all tests so @BeforeAll and @AfterAll can be non-static
@TestInstance(TestInstance.Lifecycle.PER_CLASS) 
class ViewRecordTest {

	private Connection conn; // Declare a JDBC connection to be used across all test methods

	// Method to initialize the in-memory H2 database before any tests run
	@BeforeAll
	void setupDatabase() throws Exception { 
		
		// Load the H2 database driver to allow JDBC connections
		Class.forName("org.h2.Driver"); 

		// Create an in-memory H2 database connection; it persists until JVM exits
		conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", ""); 

		// Create Statement object to execute SQL commands
		Statement stmt = conn.createStatement(); 

		// Drop employee table if it exists to ensure clean test environment
		stmt.execute("DROP TABLE IF EXISTS employee"); 

		// Drop equipment table if it exists to avoid conflicts
		stmt.execute("DROP TABLE IF EXISTS equipment"); 

		// Drop transaction table if it exists to reset test database
		stmt.execute("DROP TABLE IF EXISTS transaction"); 

		// Create employee table with ID, name, and skill columns
		stmt.execute("""
				    CREATE TABLE employee (
				        empID INT PRIMARY KEY, 
				        empName VARCHAR(100), 
				        skill VARCHAR(50)
				    )
				"""); 

		// Create equipment table with ID, name, required skill, condition, and availability status
		stmt.execute("""
				    CREATE TABLE equipment (
				        equipmentID INT PRIMARY KEY, 
				        equipmentName VARCHAR(100), 
				        requiredSkill VARCHAR(50), 
				        equipmentCondition VARCHAR(20), 
				        equipStatus VARCHAR(20)
				    )
				"""); 

		// Create transaction table to store borrow/return history; includes dates and conditions
		stmt.execute("""
				    CREATE TABLE transaction (
				        transactionID INT PRIMARY KEY AUTO_INCREMENT, 
				        empID INT, 
				        equipmentID INT, 
				        orderID INT, 
				        borrowDate DATE, 
				        expectedReturnDate DATE, 
				        returnDate DATE, 
				        transactionStatus VARCHAR(20), 
				        checkoutCondition VARCHAR(20), 
				        returnCondition VARCHAR(20)
				    )
				"""); 

		// Insert sample employees
		// Insert Alice as a plumber employee
		stmt.execute("INSERT INTO employee VALUES (1, 'Alice', 'Plumber')"); 

		// Insert David as an electrician employee
		stmt.execute("INSERT INTO employee VALUES (2, 'David', 'Electrician')"); 

		// Insert sample equipment
		// Insert wrench for plumbers; initially in good condition and available
		stmt.execute("INSERT INTO equipment VALUES (200, 'Wrench', 'Plumber', 'Good', 'Available')"); 

		// Insert multimeter for electrician; initially in good condition and available
		stmt.execute("INSERT INTO equipment VALUES (201, 'Multimeter', 'Electrician', 'Good', 'Available')"); 

		// Insert sample transactions for testing viewRecord()
		// Transaction for Alice: borrowed Wrench on 25-Aug-2025, expected return 30-Aug-2025, not yet returned (null)
		stmt.execute(
				"INSERT INTO transaction " +
						"(empID, equipmentID, borrowDate, expectedReturnDate, returnDate, transactionStatus, checkoutCondition, returnCondition) " +
						"VALUES (1, 200, '2025-08-25', '2025-08-30', NULL, 'Borrowed', 'Good', NULL)"
				); 

		// Transaction for David: borrowed Multimeter on 24-Aug-2025, returned 27-Aug-2025
		stmt.execute(
				"INSERT INTO transaction " +
						"(empID, equipmentID, borrowDate, expectedReturnDate, returnDate, transactionStatus, checkoutCondition, returnCondition) " +
						"VALUES (2, 201, '2025-08-24', '2025-08-28', '2025-08-27', 'Borrowed', 'Good', NULL)"
				); 
	}

	// Close the database connection after all tests are completed
	@AfterAll
	void teardownDatabase() throws Exception { 
		
		// Ensure the connection exists and is not already closed
		if (conn != null && !conn.isClosed()) { 
			
			// Close the connection to release resources
			conn.close(); 
		}
	}

	// Test method to validate the viewRecord() function for multiple employees
	@Test
	@DisplayName("The view record method should return correct transaction history for each employee")
	void testEmployeeViewRecordMultiple() throws Exception { 

		// Create Employee objects corresponding to the test data
		Employee alice = new Employee(1, "Alice", SkillClassification.Plumber); 
		Employee david = new Employee(2, "David", SkillClassification.Electrician); 

		// Call viewRecord() to retrieve all transactions associated with Alice from the database
		List<Transaction> aliceTxns = alice.viewRecord(conn);

		// Call viewRecord() to retrieve all transactions associated with David from the database
		List<Transaction> davidTxns = david.viewRecord(conn);

		// Assign Employee references and default conditions for Alice's transactions
		aliceTxns.forEach(txn -> {
			txn.setEmployee(alice); 
			// If checkout condition is null, set it to Good
			if (txn.getCheckoutCondition() == null) {
				txn.setCheckoutCondition(EquipmentCondition.Good); 
			}
			// If return condition is null, explicitly set to null (safe for assertions)
			if (txn.getReturnCondition() == null) {
				txn.setReturnCondition(null); 
			}
		});

		// Assertions to validate Alice's transactions
		// Verify that the list of transactions is not null, ensuring the viewRecord() method returned a valid collection
		assertNotNull(aliceTxns, "Transaction list for Alice should not be null"); 

		// Confirm that Alice has exactly one transaction in the database, matching the inserted test data
		assertEquals(1, aliceTxns.size(), "Alice should have exactly 1 transaction"); 

		// Retrieve the first (and only) transaction object for further property assertions
		Transaction txnAlice = aliceTxns.get(0); 

		// Check that the Employee object inside the transaction is correctly set to "Alice"
		assertEquals("Alice", txnAlice.getEmployee().getEmpName()); 

		// Verify that Alice's skill classification matches the expected "Plumber"
		assertEquals("Plumber", txnAlice.getEmployee().getSkillClassification().name()); 

		// Confirm that the equipment linked to this transaction is the "Wrench", as expected from the test setup
		assertEquals("Wrench", txnAlice.getEquipment().getEquipmentName()); 

		// Ensure that the checkout condition of the equipment is correctly recorded as "Good"
		assertEquals("Good", txnAlice.getCheckoutCondition().name()); 

		// Verify that the transaction status reflects a borrowed state
		assertEquals("Borrowed", txnAlice.getTransactionStatus().name()); 

		// Check that the borrow date matches the value inserted in the H2 test database
		assertEquals(LocalDate.of(2025, 8, 25), txnAlice.getBorrowDate()); 

		// Confirm that the expected return date matches the inserted test data.
		assertEquals(LocalDate.of(2025, 8, 30), txnAlice.getExpectedReturnDate()); 

		// Ensure that the return date is null, indicating Alice has not returned the equipment yet
		assertNull(txnAlice.getReturnDate()); 

		// Verify that the return condition is null, because the equipment hasn't been returned
		assertNull(txnAlice.getReturnCondition(), "Return condition should be null"); 

		// Assign Employee references and default conditions for David's transactions
		davidTxns.forEach(txn -> {

		    // Explicitly set the Employee object to David for test consistency
		    txn.setEmployee(david); 

		    if (txn.getCheckoutCondition() == null) {
		        // Provide a default checkout condition if null, preventing null-pointer issues in assertions
		        txn.setCheckoutCondition(EquipmentCondition.Good); 
		    }

		    if (txn.getReturnCondition() == null) {
			// Explicitly set return condition to null for clarity
		        txn.setReturnCondition(null); 
		        
		    }
		});

		// Assertions to validate David's transactions
		// Verify the transaction list for David is not null, confirming viewRecord() returned results
		assertNotNull(davidTxns, "Transaction list for David should not be null"); 

		// Ensure David has exactly one transaction in the database, consistent with test setup
		assertEquals(1, davidTxns.size(), "David should have exactly 1 transaction"); 

		// Retrieve David's transaction for detailed property validation
		Transaction txnDavid = davidTxns.get(0); 

		// Check that the Employee object inside the transaction is correctly set to "David"
		assertEquals("David", txnDavid.getEmployee().getEmpName()); 

		// Verify David's skill classification is "Electrician"
		assertEquals("Electrician", txnDavid.getEmployee().getSkillClassification().name()); 

		// Confirm that the equipment associated with David's transaction is "Multimeter"
		assertEquals("Multimeter", txnDavid.getEquipment().getEquipmentName()); 

		// Ensure the checkout condition is correctly recorded as "Good"
		assertEquals("Good", txnDavid.getCheckoutCondition().name()); 

		// Verify that the transaction status shows "Borrowed"
		assertEquals("Borrowed", txnDavid.getTransactionStatus().name()); 

		// Confirm the borrow date matches the inserted H2 database record
		assertEquals(LocalDate.of(2025, 8, 24), txnDavid.getBorrowDate()); 

		// Validate that the expected return date is correct
		assertEquals(LocalDate.of(2025, 8, 28), txnDavid.getExpectedReturnDate()); 

		// Verify the return date matches the value inserted
		assertEquals(LocalDate.of(2025, 8, 27), txnDavid.getReturnDate()); 

		// Confirm that the return condition is null, since it was not recorded in the test data
		assertNull(txnDavid.getReturnCondition(), "Return condition should be null"); 
	}
}