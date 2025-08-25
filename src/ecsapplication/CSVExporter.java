/**
 * CSVExporter.java
 * This is a utility class in the ECS system used to export data from
 * the JTable component into a .csv file format. This supportive role
 * it plays enables employees to save data related to transaction records or order history.
 */

package ecsapplication;

// Imports needed to write JTable data to a CSV file and handle I/O exceptions
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class CSVExporter {

	public static void exportToCSV(JTable table, String filePath) throws IOException {
		
		// Retrieves the table model containing data and column names
        TableModel model = table.getModel();
        
        // Open a FileWriter in try-with-resources to automatically close the stream
        try (FileWriter csv = new FileWriter(filePath)) {

        	// Writes the column headers to CSV and separates them with commas
            for (int i = 0; i < model.getColumnCount(); i++) {
                csv.write(model.getColumnName(i));    // Write column name
                if (i < model.getColumnCount() - 1) {
                    csv.write(",");  // Add comma expect for the last column
                }
            }
            csv.write("\n");  // Move to the next line after column headers

            // Iterate over each row in the table model
            for (int row = 0; row < model.getRowCount(); row++) {
            	
            	// Iterate over each column in the current row
                for (int col = 0; col < model.getColumnCount(); col++) {
                	
                	// Retrieve the cell value at the current row and column
                    Object value = model.getValueAt(row, col);
              
                    // Convert the value to a string; empty if null
                    String cell = value != null ? value.toString() : "";
                    
                    // Handle special characters (commas or quotes) in CSV
                    if (cell.contains(",") || cell.contains("\"")) {
                    	
                    	// Escape existing double quotes by doubling them
                        cell = cell.replace("\"", "\"\""); 
                        
                        // Wrap entire cell in double quotes to preserve commas or quotes
                        cell = "\"" + cell + "\"";         
                    }
                    csv.write(cell);  // Write the cell value
                    
                    // Add a comma if not the last column in the row
                    if (col < model.getColumnCount() - 1) {
                        csv.write(",");
                    }
                }
                csv.write("\n");  // Move to the next line after finishing the row
            }
        }
    }
}
