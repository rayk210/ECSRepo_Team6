/**
 * JUnit 5 Test
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
		Employee emp1 = new Employee(1, "Jorge", SkillClassification.Electrician);
		Equipment eq1 = new Equipment(101, "Voltage Tester", SkillClassification.Electrician, EquipmentCondition.Good);
		
		Employee emp2 = new Employee(2, "Zachary", SkillClassification.Painter);
		Equipment eq2 = new Equipment(110, "Paint Brush", SkillClassification.Painter, EquipmentCondition.Good);
		
		Employee emp3 = new Employee(3, "Raymond", SkillClassification.Carpenter);
		Equipment eq3 = new Equipment(120, "Hammer", SkillClassification.Carpenter, EquipmentCondition.Good);
		
		Employee emp4 = new Employee(4, "Megan", SkillClassification.Welder);
		Equipment eq4 = new Equipment(130, "Welding Torch", SkillClassification.Welder, EquipmentCondition.Good);
		
		Employee emp5 = new Employee(5, "David", SkillClassification.Plumber);
		Equipment eq5 = new Equipment(140, "Hacksaw", SkillClassification.Plumber, EquipmentCondition.Good);
		
		// Creating sample transactions and adding them to a transactions list
		Transaction t1 = new Transaction(1001, emp1, eq1, TransactionStatus.Borrowed);
		t1.setBorrowDate(LocalDate.now());
		t1.setExpectedReturnDate(LocalDate.now().plusDays(7));
		transactions.add(t1);
		
		Transaction t2 = new Transaction(1002, emp2, eq2, TransactionStatus.Borrowed);
		t2.setBorrowDate(LocalDate.now());
		t2.setExpectedReturnDate(LocalDate.now().plusDays(7));
		transactions.add(t2);
		
		Transaction t3 = new Transaction(1003, emp3, eq3, TransactionStatus.Returned);
		t3.setBorrowDate(LocalDate.now().minusDays(10));
		t3.setExpectedReturnDate(LocalDate.now().minusDays(3));
		t3.setReturnCondition(EquipmentCondition.Damaged);
		transactions.add(t3);
		
		Transaction t4 = new Transaction(1004, emp4, eq4, TransactionStatus.Returned);
		t4.setBorrowDate(LocalDate.now().plusDays(10));
		t4.setExpectedReturnDate(LocalDate.now().minusDays(3));
		transactions.add(t4);
		
		Transaction t5 = new Transaction(1005, emp5, eq5, TransactionStatus.Borrowed);
		t5.setBorrowDate(LocalDate.now().minusDays(10));
		t5.setExpectedReturnDate(LocalDate.now().minusDays(3));
		t5.setReturnCondition(EquipmentCondition.Damaged);
		transactions.add(t5);
		
		// 2. Create JTable from transactions
		String[] columnNames = {
	            "Transaction ID", "Employee Name", "Employee Skill",
	            "Equipment Name", "Required Skill", "Equipment Condition",
	            "Borrow Date", "Expected Return Date","Transaction Status"
	        };
		
		// Make a two dimensional data object for JTable from list of transactions
        Object[][] data = new Object[transactions.size()][columnNames.length];
        
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            data[i][0] = t.getTransactionID();
            data[i][1] = t.getEmployee().getEmpName();
            data[i][2] = t.getEmployee().getSkillClassification().name();
            data[i][3] = t.getEquipment().getEquipmentName();
            data[i][4] = t.getEquipment().getRequiredSkill().name();
            data[i][5] = t.getReturnCondition() != null
            				? t.getReturnCondition().name()
            				: t.getEquipment().getEquipmentCondition().name();
            data[i][6] = t.getBorrowDate();
            data[i][7] = t.getExpectedReturnDate();
            data[i][8] = t.getTransactionStatus().name();
        }
        
        // JTable model
        JTable table = new JTable(new DefaultTableModel(data, columnNames));
		
        // 3. Create temporary CSV file
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
        		
        		// Assert values
        		assertEquals(String.valueOf(t.getTransactionID()), cells[0]);
        		assertEquals(t.getEmployee().getEmpName(), cells[1]);
        		assertEquals(t.getEmployee().getSkillClassification().name(), cells[2]);
        		assertEquals(t.getEquipment().getEquipmentName(), cells[3]);
        		assertEquals(t.getEquipment().getRequiredSkill().name(), cells[4]);
        		String expectedCondition = t.getReturnCondition() != null
        				? t.getReturnCondition().name()
        				: t.getEquipment().getEquipmentCondition().name();
        		assertEquals(expectedCondition, cells[5]);
        		
        		// Format LocalDate
        		assertEquals(t.getBorrowDate().format(fmt), cells[6]);
        		assertEquals(t.getExpectedReturnDate().format(fmt), cells[7]);
        		assertEquals(t.getTransactionStatus().name(), cells[8]);	
        	}
        	
        	// Ensure no additional rows are created
        	assertNull(br.readLine());
        }
	
	}
	
}
