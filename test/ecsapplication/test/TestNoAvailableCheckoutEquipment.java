/**
 * TestNoAvailableCheckoutEquipment.java
 * This JUnit test case verifies the system
 * behavior when no equipment is available for
 * checkout by an employee with a certain skill
 * classification
 */

package ecsapplication.test;

// Import static assertion methods from JUnit
import static org.junit.jupiter.api.Assertions.*;

// Import required classes for database connection and collections
import java.sql.Connection;
import java.util.List;

// Import JUnit annotations for setup, teardown, and test methods
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// Import ECS application classes
import ecsapplication.DBConnect;
import ecsapplication.Employee;
import ecsapplication.Equipment;
import ecsapplication.EquipmentDAO;
import ecsapplication.enums.SkillClassification;

class TestNoAvailableCheckoutEquipment { // Test class definition

    private Connection conn; // Database connection used in tests

    // This method runs before each test case
    @BeforeEach
    void setup() throws Exception {
        conn = DBConnect.getInstance().getConnection(); // Open database connection
        conn.setAutoCommit(false); // Disable auto-commit to allow rollback

        // Mark all equipment with requiredSkill = 'Electrician' as 'Loaned'
        // This ensures no equipment is available for this skill when the test runs
        String sql = "UPDATE equipment SET equipStatus = 'Loaned' WHERE requiredSkill = 'Electrician'";
        conn.createStatement().executeUpdate(sql);  // Execute the update
    }

    // The method run after each test
    @AfterEach
    void teardown() throws Exception {
        // Restore database state after each test
        if (conn != null) {
            conn.rollback();  // Undo all changes made during this test
            conn.setAutoCommit(true); // Re-enable auto-commit for normal DB operations
            conn.close();     // Close the DB connection to free resources
        }
    }
    
    // Test Case: testNoAvailableEquipment
    @Test
    void testNoAvailableEquipment() throws Exception {
        // Create an employee with skill classification 'Electrician'
        Employee employee = new Employee(1, "Jorge", SkillClassification.Electrician);

        // Attempt to fetch available equipment for this employee's skill
        List<Equipment> available = EquipmentDAO.getAvailableEquipmentBySkill(
        		
        	// Pass the employee's skill classification to filter results
            conn, employee.getSkillClassification()
        );

        // Assert that no equipment is available for checkout
        assertTrue(available.isEmpty(), "There should be no available equipment for this skill");
    }
}