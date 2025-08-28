/**
 * TestOrderEquipment.java
 * 
 * This JUnit test case verifies the Order Equipment feature
 * in the ECS system using an H2 in-memory database to
 * isolate tests from the MySQL database.
 * 
 * Two scenarios are tested:
 *   1. Successful order
 *   2. Failed order
 * 
 * Test Case ID: TC-ORD-001-A
 */

package ecsapplication.test;

// Import JUnit assertion methods (assertEquals, assertNotNull, etc.)
import static org.junit.jupiter.api.Assertions.*;

// Import JDBC classes for database connection and statements
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

// Import JUnit lifecycle and test annotations
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Import ECS core classes and enums
import ecsapplication.Employee;
import ecsapplication.Equipment;
import ecsapplication.Order;
import ecsapplication.OrderDAO;
import ecsapplication.EquipmentDAO;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.OrderStatus;
import ecsapplication.enums.SkillClassification;

class TestOrderEquipment {

    private Connection conn;     // H2 in-memory DB connection
    private Employee employee;   // Employee performing the order
    private Equipment equipment; // Equipment being ordered

    @BeforeEach
    void setup() throws Exception {
    	
        // Load H2 JDBC driver
        Class.forName("org.h2.Driver");

        // Initialize H2 in-memory database; DB_CLOSE_DELAY=-1 keeps it alive during JVM session
        conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");
        conn.setAutoCommit(false); // disable auto-commit for transactional safety

        try (Statement stmt = conn.createStatement()) {
        	
            // Drop tables if they already exist to avoid conflicts
            stmt.execute("DROP TABLE IF EXISTS employee");
            stmt.execute("DROP TABLE IF EXISTS equipment");
            stmt.execute("DROP TABLE IF EXISTS `order`");

            // Create employee table with employee ID and employee name attributes
            stmt.execute("CREATE TABLE employee (empID INT PRIMARY KEY, empName VARCHAR(100))");

            // Create equipment table with columns for equipment ID, equipment name, equipment condition, required skill, and equipment status
            stmt.execute("CREATE TABLE equipment (" +
                    "equipmentID INT PRIMARY KEY, " +
                    "equipmentName VARCHAR(100), " +
                    "equipmentCondition VARCHAR(15), " +
                    "requiredSkill VARCHAR(20), " +
                    "equipStatus VARCHAR(20))");

            // Create orders table with order ID, employee ID, equipment ID, orderDate, pickUpDate, and orderStatus
            stmt.execute("CREATE TABLE `order` (" +
                    "orderID INT PRIMARY KEY AUTO_INCREMENT, " +
                    "empID INT, " +
                    "equipmentID INT, " +
                    "orderDate DATE, " +
                    "pickUpDate DATE, " +
                    "orderStatus VARCHAR(20))");

            // Insert sample employee data
            stmt.execute("INSERT INTO employee VALUES (5, 'David')");

            // Insert sample equipment data
            stmt.execute("INSERT INTO equipment VALUES (103, 'Hacksaw', 'Good', 'Plumber', 'Available')");
        }

        // Initialize Employee object
        employee = new Employee(5, "David", SkillClassification.Plumber);

        // Retrieve equipment from H2 DB and ensure status is Available
        equipment = EquipmentDAO.getEquipmentByID(conn, 103);
        
        // Set the equipment status to 'Available' before placing an order
        equipment.setStatus(EquipmentStatus.Available);
        
        // Update the equipment record in the H2 in-memory database to reflect the new status
        // This persists the status change so that subsequent operations (like ordering) see the correct state
        EquipmentDAO.updateEquipment(conn, equipment);
    }

    @AfterEach
    void teardown() throws Exception {
        if (conn != null) {
            try {
                // Rollback all changes to keep H2 DB clean for next test
                conn.rollback();
            } finally {
                // Close connection after test
                conn.close();
            }
        }
    }

    @Test
    @DisplayName("Successful order")
    void testOrderEquipment_Success() throws Exception {
    	
        // Place order using H2 connection
        String result = employee.orderEquipment(conn, equipment);

        // Verify that order confirmation message is correct
        assertEquals("Order confirmed.", result, "Message should confirm order");

        // Get all orders from the database
        Order latestOrder = OrderDAO.getAllOrders(conn)
        		// Convert the list of orders into a stream for processing
        		.stream()
        		// Filter the orders: only keep those made by this employee for this equipment
        		.filter(orderItem -> orderItem.getEmployee().getEmpID() == employee.getEmpID()
        		&& orderItem.getEquipment().getEquipmentID() == equipment.getEquipmentID())
        		// Reduce the stream to get the last order (most recent one)
        		.reduce((first, second) -> second)
        		// If no matching order exists, return null instead of throwing an exception
        		.orElse(null);

        // Verify the order exists
        assertNotNull(latestOrder, "Latest order should exist");

        // Verify order status is Confirmed
        assertEquals(OrderStatus.Confirmed, latestOrder.getOrderStatus(), "Order status should be Confirmed");

        // Verify the equipment in the order is named "Hacksaw"
        assertEquals("Hacksaw", latestOrder.getEquipment().getEquipmentName(), "Ordered equipment should be 'Hacksaw'");

        // Verify that equipment status is updated to Ordered
        Equipment refreshed = EquipmentDAO.getEquipmentByID(conn, equipment.getEquipmentID());
        assertEquals(EquipmentStatus.Ordered, refreshed.getStatus(), "Equipment status should be Ordered");
    }
    
    @Test
    @DisplayName("Failed order")
    // Test scenario where ordering equipment that is already ordered should fail
    void testOrderEquipment_AlreadyOrdered() throws Exception {
        // Set equipment status to Ordered
        equipment.setStatus(EquipmentStatus.Ordered);
        EquipmentDAO.updateEquipment(conn, equipment);

        // Attempt to place an order
        String result = employee.orderEquipment(conn, equipment);

        // Verify that ordering fails
        assertEquals("Failed: Equipment not available", result, "Ordering should fail for unavailable equipment");
    }
}