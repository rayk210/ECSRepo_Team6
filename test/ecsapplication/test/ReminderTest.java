/**
 * ReminderTest.java
 * JUnit 5 Test Case
 * 
 * Purpose:
 *   This JUnit test case verifies that reminder messages are generated
 *   correctly for borrowed equipment in the ECS. Three scenarios are tested:
 *   
 *   1. Overdue: Equipment that is past its expected return date.
 *   2. Return Soon: Equipment that should be returned one day from the current day.
 *   3. No Action Needed: Equipment that has much time until before the expected return date.
 *   
 * This test ensures that Reminder.generateReminder() produces messages that accurately
 * reflect the conditions mentioned above, and that the number of days left is calculated
 * correctly. All assertions use assertEquals to confirm the expected output.
 */

package ecsapplication.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ecsapplication.Employee;
import ecsapplication.Equipment;
import ecsapplication.Reminder;
import ecsapplication.Transaction;
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

class ReminderTest {
	
	private Employee emp;       // Employee object to be used in all tests
	private Equipment eq;       // Equipment object for each transaction
	private Transaction txn;    // Transaction object linking Employee and Equipment
	private Reminder reminder;  // Reminder object used to test logic
	
	@BeforeEach
	void setup() {
		
		// Initialize employee to be used in all three tests
		emp = new Employee(1, "Jorge", SkillClassification.Electrician);
	}
	
	// --- Scenario 1: Testing overdue items ---
	@Test
	void testReminderOverdue() {
		
		// Create Equipment object for overdue test
		eq = new Equipment(105, "Voltage Tester", SkillClassification.Electrician, EquipmentCondition.Good);
		
		// Create Transaction object marked as borrowed and set expected return date to yesterday (overdue)
		txn = new Transaction(1001, emp, eq, TransactionStatus.Borrowed);
		txn.setExpectedReturnDate(LocalDate.now().minusDays(1));  // Overdue case
		
		// Create Reminder for this transaction
		reminder = new Reminder(emp, txn);
		reminder.generateReminder();  // Generate reminder message
		
		// Asserts that the generated reminder message matches the expected overdue message
		assertEquals("Jorge has an overdue item: Voltage Tester. Due on: " + txn.getExpectedReturnDate(),
				     reminder.getReminderMSG());	
	}
	
	// --- Scenario 2: Testing items due soon ---
	@Test
	void testReminderReturnSoon() {
		
		// Create Equipment object for return soon test
		eq = new Equipment(106, "Wire Stripper", SkillClassification.Electrician, EquipmentCondition.Good);
		
		// Create a Transaction object with an expected return due tomorrow (return soon)
		txn = new Transaction(1002, emp, eq, TransactionStatus.Borrowed);
		txn.setExpectedReturnDate(LocalDate.now().plusDays(1));  // Return soon case
		
		// Create a reminder object
		reminder = new Reminder(emp, txn);
		reminder.generateReminder();  // Generate a reminder message
		
		// Asserts that the generated reminder message matches the expected "return soon" message
		assertEquals("Jorge should return: Wire Stripper by " + txn.getExpectedReturnDate(),
				     reminder.getReminderMSG());
		
	}
	
	// --- Scenario 3: Testing items requiring no immediate action ---
	@Test
	void testReminderNoAction() {
		
		// Create equipment object for no action needed test
		eq = new Equipment(107, "Conduit Bender", SkillClassification.Electrician, EquipmentCondition.Good);
		
		// Create transaction object with expected return date in the future (five days from current date)
		txn = new Transaction(1003, emp, eq, TransactionStatus.Borrowed);
		txn.setExpectedReturnDate(LocalDate.now().plusDays(5));  // No action needed case
		
		// Create a reminder object 
		reminder = new Reminder(emp, txn);
		reminder.generateReminder();  // Generate a reminder message
		
		// Calculate remaining days until return
		long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), txn.getExpectedReturnDate());
		
		// Asserts that the generated reminder message matches the expected "no action needed" message
		assertEquals("No action needed for: Conduit Bender. Time left to return: " + daysLeft + " days.",
				      reminder.getReminderMSG());
	}

}
