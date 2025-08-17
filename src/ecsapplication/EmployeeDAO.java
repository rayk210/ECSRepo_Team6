/**
 * EmployeeDAO.java
 * A class that is responsible for accessing and manipulating employee data.
 * Follows a Data Access Object Pattern to encapsulate data from the rest of the application.
 */

package ecsapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ecsapplication.enums.SkillClassification;

public class EmployeeDAO {

	
	    // Used for ComboBox in MainApp
	    // Retrieves employee empID, empName, and skill from the employee table in the database
		public static List<Employee> getAllEmployees(Connection conn) throws SQLException {
		    List<Employee> list = new ArrayList<>();
		    String strSQL = "SELECT empID, empName, skillClassification FROM employee";
		    
		    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
		        ResultSet rs = stmt.executeQuery();
		        while (rs.next()) {
		            int empID = rs.getInt("empID");
		            String empName = rs.getString("empName");
		            SkillClassification skill = SkillClassification.fromString(rs.getString("skillClassification"));
		            
		            Employee emp = new Employee(empID, empName, skill);
		            
		            List<Transaction> transactions = TransactionDAO.getTransactionsByEmployeeID(conn, empID);
		            emp.setEmpTransaction(transactions);
		            list.add(emp);
		        }
		    }
		    return list;
		}
		
		
		// Retrieves employee data based on ID from the employee table in MySQL
		public static Employee getEmployeeByID(Connection conn, int empID) throws SQLException {
			String strSQL = "SELECT empID, empName, skillClassification FROM employee WHERE empID = ?";
			try (PreparedStatement pstmt = conn.prepareStatement(strSQL)) {
				pstmt.setInt(1, empID);
				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					String empName = rs.getString("empName");
					SkillClassification skill = SkillClassification.valueOf(rs.getString("skillClassification"));
					Employee emp = new Employee(empID, empName, skill, null, null);

		            // Retrieve and set transactions for employees
		            List<Transaction> transactions = TransactionDAO.getTransactionsByEmployeeID(conn, empID);
		            emp.setEmpTransaction(transactions);

		            return emp;
				}
			}
			return null;
		}
}
