package ecsapplication.test;

import ecsapplication.CSVExporter;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.junit.jupiter.api.Test;

class CSVExporterTest {

	@Test
	void testExportToCSV() throws IOException{
		
		// Create a simple JTable
		String[] columns = {"Employee ID", "Employee Name", "Skill Classification"};
		Object[][] data = {
				{1, "Jorge", "Electrician"},
				{2, "Zachary", "Painter"},
				{3, "Raymond", "Carpenter"},
				{4, "Megan", "Welder"},
				{5, "David", "Plumber"}
		};
		JTable table = new JTable(new DefaultTableModel(data, columns));
		
		// Create temporary file
		File tempFile = File.createTempFile("test_export", ".csv");
		
		// Automatically delete file upon exit
		tempFile.deleteOnExit();
		
		// Call CSVExporter
		CSVExporter.exportToCSV(table, tempFile.getAbsolutePath());
		
		// Read the CSV file
		try(BufferedReader br = new BufferedReader(new FileReader(tempFile))){
			// Check header
			String header = br.readLine();
			assertEquals("Employee ID,Employee Name,Skill Classification", header);
			
			// Check the first row
			String line1 = br.readLine();
			assertEquals("1,Jorge,Electrician", line1);
			
			// Check the second row
			String line2 = br.readLine();
			assertEquals("2,Zachary,Painter", line2);
			
			// Check the third row
			String line3 = br.readLine();
			assertEquals("3,Raymond,Carpenter", line3);
			
			// Check the fourth row
			String line4 = br.readLine();
			assertEquals("4,Megan,Welder", line4);
			
			// Check the fifth row
			String line5 = br.readLine();
			assertEquals("5,David,Plumber", line5);
			
			// Ensure no additional rows
			assertNull(br.readLine());
		}
	}

}
