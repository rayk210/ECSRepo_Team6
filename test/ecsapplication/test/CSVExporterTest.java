/**
 * CSVExporterTest.java
 * JUnit 5 Test Case
 * 
 * Purpose:
 *  This unit test verifies the functionality of the CSVExporter class,
 *  specifically, the exportToCSV() method. It ensures that a JTable
 *  containing transaction data is written accurately to a CSV file with
 *  the expected columns and rows. The test checks the following:
 *    - The CSV file is created at a specified location
 *    - The header matches the JTable column names
 *    - Each row matches the transaction data that corresponds to it
 *    - No additional rows are added
 *    
 * All assertions were made using the JUnit 5 assert method
 */

package ecsapplication.test;

import ecsapplication.CSVExporter;
import ecsapplication.Employee;
import ecsapplication.Equipment;
import ecsapplication.Transaction;
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.junit.jupiter.api.Test;

class CSVExporterTest {

	@Test
	void testExportTransactionsToCSV() throws IOException{
		
		// 1. Prepare sample transactions
		List<Transaction> transactions = new ArrayList<>();
		
		// Instantiating Employee and Equipment objects
		// Employee 1
		Employee emp1 = new Employee(1, "Jorge", SkillClassification.Electrician);
		// Equipment for employee 1
		Equipment eq1 = new Equipment(101, "Voltage Tester", SkillClassification.Electrician, EquipmentCondition.Good);
		
		// Employee 2
		Employee emp2 = new Employee(2, "Zachary", SkillClassification.Painter);
		// Equipment for employee 2
		Equipment eq2 = new Equipment(110, "Paint Brush", SkillClassification.Painter, EquipmentCondition.Good);
		
		// Employee 3
		Employee emp3 = new Employee(3, "Raymond", SkillClassification.Carpenter);
		// Equipment for employee 3
		Equipment eq3 = new Equipment(120, "Hammer", SkillClassification.Carpenter, EquipmentCondition.Good);
		
		// Employee 4
		Employee emp4 = new Employee(4, "Megan", SkillClassification.Welder);
		// Equipment for employee 4
		Equipment eq4 = new Equipment(130, "Welding Torch", SkillClassification.Welder, EquipmentCondition.Good);
		
		// Employee 5
		Employee emp5 = new Employee(5, "David", SkillClassification.Plumber);
		// Equipment for employee 5
		Equipment eq5 = new Equipment(140, "Hacksaw", SkillClassification.Plumber, EquipmentCondition.Good);
		
		// Creating sample transactions and adding them to a transactions list
		// Transaction for Employee 1
		Transaction t1 = new Transaction(1001, emp1, eq1, TransactionStatus.Borrowed); // Currently borrowed
		t1.setBorrowDate(LocalDate.now());  // Borrow date is today
		t1.setExpectedReturnDate(LocalDate.now().plusDays(7));  // Return expected in 7 days
		transactions.add(t1);  // Add transaction to transactions list
		
		// Transaction for Employee 2
		Transaction t2 = new Transaction(1002, emp2, eq2, TransactionStatus.Borrowed); // Currently borrowed
		t2.setBorrowDate(LocalDate.now());  // Borrow date is today
		t2.setExpectedReturnDate(LocalDate.now().plusDays(7));  // Return expected in 7 days
		transactions.add(t2);  // Add transaction to transactions list
		
		// Transaction for Employee 3
		Transaction t3 = new Transaction(1003, emp3, eq3, TransactionStatus.Returned);  // Currently returned
		t3.setBorrowDate(LocalDate.now().minusDays(10));  // Borrow date set to 10 before the current date
		t3.setExpectedReturnDate(LocalDate.now().minusDays(3));  // Expected return 3 days ago
		t3.setReturnCondition(EquipmentCondition.Damaged);  // Condition set to Damaged
		transactions.add(t3);  // Add transaction to transactions list
		
		// Transaction for Employee 4
		Transaction t4 = new Transaction(1004, emp4, eq4, TransactionStatus.Returned);  // Currently returned
		t4.setBorrowDate(LocalDate.now().plusDays(10));  // Borrow date set to 10 in the future
		t4.setExpectedReturnDate(LocalDate.now().minusDays(3)); // Expected return 3 days ago
		transactions.add(t4);  // Add transaction to transactions list
		
		// Transaction for Employee 5
		Transaction t5 = new Transaction(1005, emp5, eq5, TransactionStatus.Borrowed);  // Currently borrowed
		t5.setBorrowDate(LocalDate.now().minusDays(10));  // Borrow date set to 10 before the current date
		t5.setExpectedReturnDate(LocalDate.now().minusDays(3));  // Expected return 3 days ago
		t5.setReturnCondition(EquipmentCondition.Damaged);  // Condition set to Damaged
		transactions.add(t5);  // Add transaction to transactions list
		
		// 2. Create JTable from transactions
		String[] columnNames = {
	            "Transaction ID", "Employee Name", "Employee Skill",
	            "Equipment Name", "Required Skill", "Equipment Condition",
	            "Borrow Date", "Expected Return Date","Transaction Status"
	        };
		
		// Make a two dimensional data object for JTable from list of transactions
        Object[][] data = new Object[transactions.size()][columnNames.length];
        
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i); // Get the transaction at position 'i'from the list
            
            // Fill data for JTable: every column at row i is contains a value from the transaction object
            data[i][0] = t.getTransactionID();         // Column 0: Transaction ID
            data[i][1] = t.getEmployee().getEmpName(); // Column 1: Employee Name
            data[i][2] = t.getEmployee().getSkillClassification().name();  // Column 2: Employee Skill Classification
            data[i][3] = t.getEquipment().getEquipmentName();  // Column 3: Equipment Name
            data[i][4] = t.getEquipment().getRequiredSkill().name();  // Column 4: Equipment Required Skill
            
            // Column 5: Equipment Return Condition, if return condition exists, use it; if null, use default equipment condition
            data[i][5] = t.getReturnCondition() != null
            				? t.getReturnCondition().name()
            				: t.getEquipment().getEquipmentCondition().name();
            data[i][6] = t.getBorrowDate();  // Column 6: Borrow Date
            data[i][7] = t.getExpectedReturnDate();  // Column 7: Expected Return Date
            data[i][8] = t.getTransactionStatus().name();  // Column 8: Transaction Status
        }
        
        // JTable model
        JTable table = new JTable(new DefaultTableModel(data, columnNames));
		
        // 3. Create temporary CSV file
        // ***Change temporary file path for proper use***
        File tempFile = new File("C:\\Users\\rayk2\\OneDrive\\Desktop\\CEIS400\\Lab6\\transactions_export_test.csv");
        
        // 4. Export to CSV file
        CSVExporter.exportToCSV(table, tempFile.getAbsolutePath());
        
        // 5. Read CSV and assert
        try(BufferedReader br = new BufferedReader(new FileReader(tempFile))) {
        	
        	// Check header
        	String header = br.readLine();
        	assertEquals(String.join(",", columnNames), header, "Header of the CSV is not as expected");
        	
        	// Date formatter
        	DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        	
        	// Check each row
        	for(int i = 0; i < transactions.size(); i++) {
        		String line = br.readLine();
        		String[] cells = line.split(",", -1); // empty columns are read too with -1
        		assertNotNull(line, "Row " + " is missing in the CSV");
        		Transaction t = transactions.get(i);
        		
        		// Assert values: compare each value in the CSV row (actual) with the corresponding value in the Transaction object (expected)
        		assertEquals(String.valueOf(t.getTransactionID()), cells[0]); // Compares TransactionID from object with CSV cell[0]
        		assertEquals(t.getEmployee().getEmpName(), cells[1]);         // Compare Employee Name from object with CSV cell[1]
        		assertEquals(t.getEmployee().getSkillClassification().name(), cells[2]);  // Compare Employee Skill Classification with CSV cell[2]
        		assertEquals(t.getEquipment().getEquipmentName(), cells[3]);  // Compare Equipment Name with CSV cell[3]
        		assertEquals(t.getEquipment().getRequiredSkill().name(), cells[4]);  // Compare Equipment Required Skill with CSV cell[4]
        		
        		// Determine expected condition: if returnCondition exists, use it; otherwise, use the equipment's default condition
        		String expectedCondition = t.getReturnCondition() != null
        				? t.getReturnCondition().name()
        				: t.getEquipment().getEquipmentCondition().name();
        		assertEquals(expectedCondition, cells[5]);  // Compare expected equipment condition with CSV cell[5]
        		
        		// Format LocalDate to match CVS date format and assert
        		assertEquals(t.getBorrowDate().format(fmt), cells[6]);          // Compare Borrow Date with CSV cell[6]
        		assertEquals(t.getExpectedReturnDate().format(fmt), cells[7]);  // Compare Expected Return Date with CSV cell[7]
        		assertEquals(t.getTransactionStatus().name(), cells[8]);	    // Compare Transaction Status with CSV cell[8]
        	}
        	
        	// Ensure no additional rows are created
        	assertNull(br.readLine());
        }
	
	}
	
}
