package ecsapplication;

import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class CSVExporter {

	public static void exportToCSV(JTable table, String filePath) throws IOException {
		
		// Creates table model containing data and column names
        TableModel model = table.getModel();
        
        // Open a file output stream wrapped in a try block
        try (FileWriter csv = new FileWriter(filePath)) {

        	// Writes the column name to CSV and separates values with a comma
            for (int i = 0; i < model.getColumnCount(); i++) {
                csv.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) {
                    csv.write(",");
                }
            }
            csv.write("\n");

            // Iterates through every row the JTable model
            for (int row = 0; row < model.getRowCount(); row++) {
            	
            	// Iterates though every column 
                for (int col = 0; col < model.getColumnCount(); col++) {
                	
                	// Takes the value at each row and column
                    Object value = model.getValueAt(row, col);
              
                    // Change values to a string
                    String cell = value != null ? value.toString() : "";
                    
                    // Checks if cells contain a quote and doubles them
                    if (cell.contains(",") || cell.contains("\"")) {
                        cell = cell.replace("\"", "\"\"");
                        cell = "\"" + cell + "\"";
                    }
                    csv.write(cell);
                    if (col < model.getColumnCount() - 1) {
                        csv.write(",");
                    }
                }
                csv.write("\n");
            }
        }
    }

}
