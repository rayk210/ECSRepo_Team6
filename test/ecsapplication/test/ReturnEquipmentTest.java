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
 * Used for Test Case: TC-RET-001-A
 */

package ecsapplication.test;

// Import static assertion methods from JUnit
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

// Import ECS application and Enums
import ecsapplication.Employee;
import ecsapplication.Equipment;
import ecsapplication.Transaction;
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

//Test class for the Return Equipment feature in ECS
class ReturnEquipmentTest {

 
 private Connection conn;         // Database connection (H2 in-memory) used for testing
 private Employee employee;       // Employee performing the return
 private Equipment equipment;     // Equipment being returned
 private Transaction transaction; // Transaction representing the borrowing

 @BeforeEach
 void setup() throws Exception {
     // Explicitly load the H2 driver
     Class.forName("org.h2.Driver");

     // Create an in-memory H2 database; DB_CLOSE_DELAY=-1 keeps DB alive for JVM session
     conn = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1", "sa", "");

     // Create a Statement for executing SQL commands
     try (Statement stmt = conn.createStatement()) {
    	 
         // Drop tables if they exist to avoid duplication errors
         stmt.execute("DROP TABLE IF EXISTS equipment");
         stmt.execute("DROP TABLE IF EXISTS transaction");

         // Create the equipment table with columns: ID, name, status, skill
         stmt.execute("CREATE TABLE equipment("
                 + "equipmentID INT PRIMARY KEY,"
                 + "name VARCHAR(100),"
                 + "status VARCHAR(20),"
                 + "skill VARCHAR(50))");

         // Create the transaction table with columns: ID, equipmentID, status, returnCondition
         stmt.execute("CREATE TABLE transaction("
                 + "transactionID INT PRIMARY KEY,"
                 + "equipmentID INT,"
                 + "status VARCHAR(20),"
                 + "returnCondition VARCHAR(20))");

         // Insert sample data for testing
         stmt.execute("INSERT INTO equipment VALUES (108, 'Voltage Tester', 'Loaned', 'Electrician')");
         stmt.execute("INSERT INTO transaction VALUES (1, 108, 'Borrowed', NULL)");
     }

     // Initialize test Employee object
     employee = new Employee(1, "Jorge", SkillClassification.Electrician);

     // Initialize test Equipment object
     equipment = new Equipment(108, "Voltage Tester", SkillClassification.Electrician);
     equipment.setStatus(EquipmentStatus.Loaned);           // Set initial status as Loaned
     equipment.setEquipmentCondition(EquipmentCondition.Good); // Set initial condition

     // Initialize a Transaction representing a borrowed record
     transaction = new Transaction(
             1,          // Transaction ID
             employee,   // Associated Employee
             equipment,  // Associated Equipment
             TransactionStatus.Borrowed // Initial status Borrowed
     );

     // Add transaction to the employee's transaction list
     employee.getEmpTransaction().add(transaction);
 }

 @Test
 @DisplayName("Successful return of equipment with a condition 'Good'")
 void testReturnEquipment_Success() {
     // Perform return with condition Good
     Transaction txn = employee.returnEquipment(transaction.getTransactionID(), EquipmentCondition.Good);

     // Ensure a transaction is created
     assertNotNull(txn, "Return transaction should be created");

     // Equipment status should change to Available
     assertEquals(EquipmentStatus.Available, equipment.getStatus(), "Equipment should be 'Available'");

     // Transaction status should change to Returned
     assertEquals(TransactionStatus.Returned, txn.getTransactionStatus(), "Transaction status should be 'Returned'");

     // Return condition should be recorded as Good
     assertEquals(EquipmentCondition.Good, txn.getReturnCondition(), "Return condition should be 'Good'");
 }

 @Test
 @DisplayName("Return canceled when no equipment condition is selected")
 void testReturnEquipment_Cancel() {
	 // Perform return with null condition (cancel)
	 Transaction txn = employee.returnEquipment(transaction.getTransactionID(), null);

	 // Return should fail (null)
	 assertNull(txn, "Return should fail when condition is null");

	 // Equipment status should remain Loaned
	 assertEquals(EquipmentStatus.Loaned, equipment.getStatus(), "Equipment should remain 'Loaned'");

	 // Transaction status should remain Borrowed
	 assertEquals(TransactionStatus.Borrowed, transaction.getTransactionStatus(), "Transaction status should remain 'Borrowed'");

	 // Return condition should remain null
	 assertNull(transaction.getReturnCondition(), "Return condition should remain null");
 }
 
 @Test
 @DisplayName("Successful return of equipment with a condition 'Damaged'")
 void testReturnEquipment_Damaged() {
     // Execute the returnEquipment method with the 'Damaged' condition
     Transaction txn = employee.returnEquipment(transaction.getTransactionID(), EquipmentCondition.Damaged);

     // Verify that a Transaction object is returned (not null)
     assertNotNull(txn, "Return transaction should be created");

     // Verify that the equipment status has been updated to 'Available' after return
     assertEquals(EquipmentStatus.Available, equipment.getStatus(), "Equipment should be 'Available'");

     // Verify that the transaction status is updated to 'Returned'
     assertEquals(TransactionStatus.Returned, txn.getTransactionStatus(), "Transaction status should be 'Returned'");

     // Verify that the return condition is correctly recorded as 'Damaged'
     assertEquals(EquipmentCondition.Damaged, txn.getReturnCondition(), "Return condition should be 'Damaged'");
 }

}