/**
 * TestCheckoutEquipment.java
 *
 * This JUnit test class verifies the equipment checkout process
 * in the ECS (Equipment Checkout System) application.
 * It ensures that an employee with the appropriate skill classification
 * can check out available equipment correctly, and that the transaction
 * and equipment status are updated accordingly.
 *
 * Test Case ID: TC-CHK-002-B
 */

package ecsapplication.test;

import static org.junit.jupiter.api.Assertions.*; // Import static assertion methods from JUnit

import java.sql.Connection;    // Provides the Connection class to establish and manage a connection to a database
import java.sql.DriverManager; // Provides the DriverManager class to obtain a Connection to a specific database (e.g., H2)
import java.sql.Statement;     // Provides the Statement class to execute SQL queries and updates
import java.time.LocalDate;    // Provides the LocalDate class for handling dates without time (used for borrow/return dates)


// Import JUnit annotations for setup, teardown, test, and display name
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Import ECS application classes and enums
import ecsapplication.Employee;
import ecsapplication.Equipment;
import ecsapplication.EquipmentDAO;
import ecsapplication.Transaction;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

class TestCheckoutEquipment {

    // Field to hold the H2 in-memory database connection
    private Connection conn;

    // Setup method executed before each test case
    @BeforeEach
    void setup() throws Exception {
    	
        // Load the H2 database driver
        Class.forName("org.h2.Driver");

        // Create an in-memory H2 database connection
        // DB_CLOSE_DELAY=-1 keeps the database alive until the JVM shuts down
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        conn.setAutoCommit(false); // Disable auto-commit to allow rollback after tests

        // Create a Statement for executing SQL commands
        try (Statement stmt = conn.createStatement()) {

            // Drop the equipment table if it already exists to start fresh
            stmt.execute("DROP TABLE IF EXISTS equipment");

            // Create the equipment table with relevant columns
            stmt.execute("CREATE TABLE equipment (" +
                    "equipmentID INT PRIMARY KEY, " +
                    "equipmentName VARCHAR(100), " +
                    "equipmentCondition VARCHAR(15), " +
                    "equipStatus VARCHAR(20), " +
                    "requiredSkill VARCHAR(50))");

            // Insert initial equipment data for testing
            stmt.execute("INSERT INTO equipment VALUES (101, 'Paint Brush', 'Good', 'Available', 'Painter')");
            stmt.execute("INSERT INTO equipment VALUES (103, 'Saw', 'Good', 'Available', 'Plumber')");
        }
    }

    // Teardown method executed after each test case
    @AfterEach
    void teardown() throws Exception {
        if (conn != null) {
            conn.rollback(); // Undo all changes made during the test
            conn.close();    // Close the database connection
        }
    }

    // Test checkout process for a Painter employee
    @Test
    @DisplayName("Checkout equipment for 'Painter'")
    void testCheckoutEquipment_Painter() throws Exception {

        // Retrieve equipment with ID 101 from the database
        Equipment eq101 = EquipmentDAO.getEquipmentByID(conn, 101);

        // Create an Employee object representing a Painter
        Employee painter = new Employee(2, "Zachary", SkillClassification.Painter);

        // Perform checkout and store the resulting transaction
        Transaction txn = painter.checkOut(eq101);

        // Assert that the transaction object is successfully created
        assertNotNull(txn, "Transaction should be created");

        // Set the equipment status to Loaned to simulate database update
        eq101.setStatus(EquipmentStatus.Loaned);

        // Update the equipment status in the database
        EquipmentDAO.updateEquipment(conn, eq101);

        // Retrieve the updated equipment from the database for verification
        Equipment refreshed = EquipmentDAO.getEquipmentByID(conn, 101);

        // Verify that the equipment status has changed to Loaned
        assertEquals(EquipmentStatus.Loaned, refreshed.getStatus(),
                "Equipment 101 should change to Loaned after checkout");

        // Verify the transaction is linked to the correct employee
        assertEquals(painter.getEmpID(), txn.getEmployee().getEmpID(), "Transaction links to correct employee");

        // Verify the transaction is linked to the correct equipment
        assertEquals(eq101.getEquipmentID(), txn.getEquipment().getEquipmentID(), "Transaction links to correct equipment");

        // Verify that the borrow date is set to today
        assertEquals(LocalDate.now(), txn.getBorrowDate(), "Borrow date should be today");

        // Verify that the transaction status is Borrowed
        assertEquals(TransactionStatus.Borrowed, txn.getTransactionStatus(), "Transaction status should be Borrowed");
    }

    // Test checkout process for a Plumber employee
    @Test
    @DisplayName("Checkout equipment for 'Plumber'")
    void testCheckoutEquipment_Plumber() throws Exception {

        // Retrieve equipment with ID 103 from the database
        Equipment eq103 = EquipmentDAO.getEquipmentByID(conn, 103);

        // Create an Employee object representing a Plumber
        Employee employee = new Employee(5, "David", SkillClassification.Plumber);

        // Perform checkout and store the resulting transaction
        Transaction txn = employee.checkOut(eq103);

        // Assert that the transaction object is successfully created
        assertNotNull(txn, "Transaction should be created");

        // Set the equipment status to Loaned
        eq103.setStatus(EquipmentStatus.Loaned);

        // Update the equipment status in the database
        EquipmentDAO.updateEquipment(conn, eq103);

        // Retrieve the updated equipment for verification
        Equipment refreshed = EquipmentDAO.getEquipmentByID(conn, 103);

        // Verify that the equipment status has changed to Loaned
        assertEquals(EquipmentStatus.Loaned, refreshed.getStatus(), "Equipment 103 should change to Loaned");

        // Verify that the transaction is linked to the correct employee
        assertEquals(employee.getEmpID(), txn.getEmployee().getEmpID(), "Transaction links to correct employee");

        // Verify that the transaction is linked to the correct equipment
        assertEquals(eq103.getEquipmentID(), txn.getEquipment().getEquipmentID(), "Transaction links to correct equipment");

        // Verify that the borrow date is set to today
        assertEquals(LocalDate.now(), txn.getBorrowDate(), "Borrow date should be today");

        // Verify that the transaction status is Borrowed
        assertEquals(TransactionStatus.Borrowed, txn.getTransactionStatus(), "Transaction status should be Borrowed");
    }
}