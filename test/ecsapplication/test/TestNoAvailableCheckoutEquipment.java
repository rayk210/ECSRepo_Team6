/**
 * TestNoAvailableCheckoutEquipment.java
 * This JUnit test case verifies the system
 * behavior when no equipment is available for
 * checkout by an employee with a certain skill
 * classification.
 *
 * Use for Test Case TC-CHK-001-A
 */

package ecsapplication.test;

// Import static assertion methods from JUnit
import static org.junit.jupiter.api.Assertions.*;

// Import required classes for database connection and collections
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

// Import JUnit annotations for setup and test methods
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Import ECS application classes
import ecsapplication.Employee;
import ecsapplication.Equipment;
import ecsapplication.EquipmentDAO;
import ecsapplication.enums.SkillClassification;

class TestNoAvailableCheckoutEquipment {

	// Field to hold the H2 in-memory database connection
	private Connection conn;

	/**
	 * Setup method executed before each test case.
	 * Creates a fresh in-memory database and populates
	 * it with sample data where no equipment is available.
	 */
	@BeforeEach
	void setup() throws Exception {

		// Load the H2 JDBC driver class into memory
		Class.forName("org.h2.Driver");

		// Create a new H2 in-memory database connection.
		// DB_CLOSE_DELAY=-1 keeps the DB alive until JVM shuts down.
		conn = DriverManager.getConnection(
				"jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

		conn.setAutoCommit(true); // Enable auto-commit mode for SQL statements

		// Use try-with-resources so Statement closes automatically
		try (Statement stmt = conn.createStatement()) {

			// Drop the transaction table if it already exists (fresh start)
			stmt.execute("DROP TABLE IF EXISTS transaction");

			// Drop the equipment table if it already exists
			stmt.execute("DROP TABLE IF EXISTS equipment");

			// Create new equipment table with 4 columns
			stmt.execute("CREATE TABLE equipment ("
					+ "equipmentID INT PRIMARY KEY, "       // Unique ID for each equipment
					+ "equipmentName VARCHAR(100), "        // Name of equipment
					+ "equipStatus VARCHAR(20), "           // Current status (Loaned / Available)
					+ "requiredSkill VARCHAR(50))");        // Skill needed to use equipment

			// Create new transaction table with 4 columns
			stmt.execute("CREATE TABLE transaction ("
					+ "transactionID INT PRIMARY KEY, "     // Unique ID for each transaction
					+ "equipmentID INT, "                   // Which equipment is involved
					+ "transactionStatus VARCHAR(20), "     // Status (Borrowed / Returned)
					+ "returnCondition VARCHAR(20))");      // Condition after return

			// Insert equipment records - all equipment set to "Loaned"
			stmt.execute("INSERT INTO equipment VALUES (108, 'Voltage Tester', 'Loaned', 'Electrician')");
			stmt.execute("INSERT INTO equipment VALUES (106, 'Chisel', 'Loaned', 'Carpenter')");
			stmt.execute("INSERT INTO equipment VALUES (104, 'Hacksaw', 'Loaned', 'Plumber')");
			stmt.execute("INSERT INTO equipment VALUES (100, 'Hammer', 'Loaned', 'Carpenter')");

			// Insert related transactions
			stmt.execute("INSERT INTO transaction VALUES (1, 108, 'Borrowed', 'Good')");
			stmt.execute("INSERT INTO transaction VALUES (2, 106, 'Borrowed', 'Good')");
			stmt.execute("INSERT INTO transaction VALUES (3, 104, 'Returned', 'Damaged')");
			stmt.execute("INSERT INTO transaction VALUES (4, 100, 'Borrowed', 'Damaged')");
		}
	}

	/**
	 * Test case verifying that no equipment is available
	 * for an employee with "Electrician" skill classification.
	 */
	@Test
	@DisplayName("No available equipment for 'Electrician'")
	void testNoAvailableEquipment_Electrician() throws Exception {

		// Create an Employee with Electrician skill
		Employee employee = new Employee(1, "Jorge", SkillClassification.Electrician);

		// Query available equipment for Electrician skill
		List<Equipment> available =
				EquipmentDAO.getAvailableEquipmentBySkill(conn, employee.getSkillClassification());

		// Assert that the list is empty (no equipment available)
		assertTrue(available.isEmpty(),
				"There should be no available equipment for Electrician");
	}

	/**
	 * Test case verifying that no equipment is available
	 * for an employee with 'Carpenter' skill classification.
	 */
	@Test
	@DisplayName("No available equipment for 'Carpenter'")
	void testNoAvailableEquipment_Carpenter() throws Exception {
		// Create an Employee with Carpenter skill
		Employee employee = new Employee(3, "Raymond", SkillClassification.Carpenter);

		// Query available equipment for Carpenter skill
		List<Equipment> available =
				EquipmentDAO.getAvailableEquipmentBySkill(conn, employee.getSkillClassification());

		// Assert that the list is empty (no equipment available)
		assertTrue(available.isEmpty(),
				"There should be no available equipment for Carpenter");
	}
}