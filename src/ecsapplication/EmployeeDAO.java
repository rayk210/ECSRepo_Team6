/**
 * EmployeeDAO.java
 * A class that is responsible for accessing and manipulating employee data.
 * Follows a Data Access Object Pattern to encapsulate data from the rest of the application.
 */

// Main package for ECS application
package ecsapplication;

// Import SQL utilities for database operations
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Import collection framework for lists
import java.util.ArrayList;
import java.util.List;

// Import skill classification enumeration for employee skills
import ecsapplication.enums.SkillClassification;

public class EmployeeDAO {

		// ============== METHOD: getAllEmployees =============== //
	    // Used for ComboBox in MainApp
	    // Retrieves employee empID, empName, and skill from the 
	    // employee table in the database.
		// ===================================================== //
		public static List<Employee> getAllEmployees(Connection conn) throws SQLException {
			
			// Initialize a dynamic list to store employee objects
		    List<Employee> list = new ArrayList<>();
		    
		    // SQL query to selection employee attributes from the employee table
		    String strSQL = "SELECT empID, empName, skillClassification FROM employee";
		    
		    // Use try-with-resources to automatically close PrepareStatement
		    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
		        ResultSet rs = stmt.executeQuery();  // Execute the query
		        
		        // Iterate over the result set
		        while (rs.next()) {
		            int empID = rs.getInt("empID");            // Get employee ID
		            String empName = rs.getString("empName");  // Get employee name
		            SkillClassification skill = SkillClassification.fromString(rs.getString("skillClassification"));  // Convert string to enum
		            
		            // Create employee object
		            Employee emp = new Employee(empID, empName, skill);
		            
		            // Retrieve transactions for this employee
		            List<Transaction> transactions = TransactionDAO.getTransactionsByEmployeeID(conn, empID);
		            emp.setEmpTransaction(transactions);
		            
		            // Add to employee list
		            list.add(emp);
		        }
		    }
		    
		    // Return full employee list
		    return list;
		}
		
		// ====================== METHOD: getEmployeeByID ===================== //
		// Retrieves employee data based on ID from the employee table in MySQL
		// ==================================================================== //
		public static Employee getEmployeeByID(Connection conn, int empID) throws SQLException {
			
			// SQL query to retrieve employee attributes based on empID
			String strSQL = "SELECT empID, empName, skillClassification FROM employee WHERE empID = ?";
			
			// Use try-with-resource to ensure PrepareStatement to closed automatically
			try (PreparedStatement pstmt = conn.prepareStatement(strSQL)) {
				
				// Set first parameter (?) with the provided empID
				pstmt.setInt(1, empID);
				ResultSet rs = pstmt.executeQuery();
				
				// If a matching record is found
				if (rs.next()) {
					
					// Retrieve employee name from the result set
					String empName = rs.getString("empName");
					
					// Convert SkillClassification string to enum
					SkillClassification skill = SkillClassification.valueOf(rs.getString("skillClassification"));
					
					// Create a new employee object with retrieve data
					// Last two parameters (null, null) are placeholder for unused attributes
					Employee emp = new Employee(empID, empName, skill, null, null);

		            // Retrieve all transactions associated with this employee
		            List<Transaction> transactions = TransactionDAO.getTransactionsByEmployeeID(conn, empID);
		            
		            // Attach transactions to the employee object
		            emp.setEmpTransaction(transactions);

		            // Return the fully populated employee object
		            return emp;
				}
			}
			
			// Return null if no employee with the given empID exists
			return null;
		}
}
