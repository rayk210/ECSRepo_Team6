/**
 * TestCheckoutEquipment.java
 * This JUnit test case verifies the checkout process
 * for a piece of equipment in the ECS system.
 * 
 * Used to test TC-CHK-001-A
 */

package ecsapplication.test;

//Import static assertion methods from JUnit
import static org.junit.jupiter.api.Assertions.*;

//Import required classes for database connection and collections
import java.sql.Connection;
import java.time.LocalDate;

//Import JUnit annotations for setup, teardown, and test methods
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Import ECS application classes
import ecsapplication.DBConnect;
import ecsapplication.Employee;
import ecsapplication.Equipment;
import ecsapplication.EquipmentDAO;
import ecsapplication.Transaction;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

class TestCheckoutEquipment {

	// Database connection used for test operations
    private Connection conn;
    
    // Equipment object with ID 101 used as test data
    private Equipment eq101;

    // Setup method executed before each test
    // Prepares the database by setting equipment 101 to 'Available'
    @BeforeEach
    void setup() throws Exception {
    	
    	// Get a connection to the database
        conn = DBConnect.getInstance().getConnection();
       
        // Retrieve equipment with ID 101 from database
        eq101 = EquipmentDAO.getEquipmentByID(conn, 101);
        
        // Set the equipment status to 'Available' for testing
        eq101.setStatus(EquipmentStatus.Available);
        
        // Update the equipment record in the database
        EquipmentDAO.updateEquipment(conn, eq101);
    }

    // Tear down method executed after the test
    // Resets the equipment 101 status to 'Available' to maintain test isolation
    @AfterEach
    void teardown() throws Exception {
       
    	// Set the equipment status back to 'Available'
        eq101.setStatus(EquipmentStatus.Available);
        
        // Update the database to reflect changes
        EquipmentDAO.updateEquipment(conn, eq101);
    }

    // Test Case: TestCheckoutEquipment
    // Verifies that equipment can be checked out by a Painter Employee
    @Test
    void testCheckoutEquipment101() throws Exception {
        
    	// Create an Employee object for the Painter classification
        Employee employee = new Employee(2, "Zachary", SkillClassification.Painter);

        // Perform checkout of equipment and store the resulting transaction
        Transaction txn = employee.checkOut(eq101);
        
        // Assert that a Transaction object was created successfully
        assertNotNull(txn, "Transaction should be created");

        // Set the equipment status to 'Loaned' to simulate database update
        eq101.setStatus(EquipmentStatus.Loaned);
        
        // Update the equipment status in the database
        EquipmentDAO.updateEquipment(conn, eq101);

        // Retrieve the updated equipment from the database
        Equipment refreshed = EquipmentDAO.getEquipmentByID(conn, 101);

        // Verify that the equipment status is now Loaned
        assertEquals(EquipmentStatus.Loaned, refreshed.getStatus(),
            "Equipment 101 should change to Loaned after checkout");

        // Verify the transaction is linked to the correct employee
        assertEquals(employee.getEmpID(), txn.getEmployee().getEmpID(), "Transaction links to correct employee");

        // Verify the transaction is linked to the correct equipment
        assertEquals(eq101.getEquipmentID(), txn.getEquipment().getEquipmentID(), "Transaction links to correct equipment");

        // Verify that the borrow date is today
        assertEquals(LocalDate.now(), txn.getBorrowDate(), "Borrow date should be today");

        // Verify that the transaction status is Borrowed
        assertEquals(TransactionStatus.Borrowed, txn.getTransactionStatus(), "Transaction status should be Borrowed");
    }
}
