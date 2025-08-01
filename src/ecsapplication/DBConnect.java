package ecsapplication;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.OrderStatus;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;


public class DBConnect {
	
	private static final String url = "jdbc:mysql://localhost:3306/ceis400courseproject";
	private static final String username = "root";
	private static final String password = "devry123";

	public static Connection getConnection()throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
	
	public static Employee getEmployeeByID(Connection conn, int empID) throws SQLException {
		String strSQL = "SELECT empID, empName, skillClassification FROM employee WHERE empID = ?";
		try (PreparedStatement pstmt = conn.prepareStatement(strSQL)) {
			pstmt.setInt(1, empID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				String empName = rs.getString("empName");
				SkillClassification skill = SkillClassification.valueOf(rs.getString("skillClassification"));
				Employee emp = new Employee(empID, empName, skill, null, null);

	            // take and set transactions for employee ID
	            List<Transaction> transactions = getTransactionsByEmployeeID(conn, empID);
	            emp.setEmpTransaction(transactions);

	            return emp;
			}
		}
		return null;
	}
	
	public static List<Transaction> getTransactionsByEmployeeID(Connection conn, int empID) throws SQLException {
	    List<Transaction> txns = new ArrayList<>();
	    String sql = "SELECT t.transactionID, t.empID, t.equipmentID, t.orderID, t.borrowDate, t.expectedReturnDate, t.returnDate, t.transactionStatus, " +
	                 "e.equipmentName, e.equipmentCondition, e.requiredSkill, e.equipStatus " +
	                 "FROM transaction t JOIN equipment e ON t.equipmentID = e.equipmentID " +
	                 "WHERE t.empID = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setInt(1, empID);
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            Equipment eq = new Equipment(
	                rs.getInt("equipmentID"),
	                rs.getString("equipmentName"),
	                EquipmentCondition.valueOf(rs.getString("equipmentCondition")),
	                EquipmentStatus.valueOf(rs.getString("equipStatus")),
	                SkillClassification.valueOf(rs.getString("requiredSkill"))
	            );

	            Transaction txn = new Transaction(
	                rs.getInt("transactionID"),
	                null,
	                eq,
	                null,
	                null,
	                rs.getDate("borrowDate").toLocalDate(),
	                rs.getDate("expectedReturnDate") != null ? rs.getDate("expectedReturnDate").toLocalDate() : null,
	                TransactionStatus.valueOf(rs.getString("transactionStatus"))
	            );

	            if (rs.getDate("returnDate") != null) {
	                txn.setReturnDate(rs.getDate("returnDate").toLocalDate());
	            }

	            txns.add(txn);
	        }
	    }
	    return txns;
	}

	public static Transaction getTransactionByID(Connection conn, int transactionID) throws SQLException {
		String strSQL = "SELECT t.transactionID, t.empID, t.equipmentID, t.orderID, t.borrowDate, t.expectedReturnDate, t.transactionStatus, "
				   + "e.empName, e.skillClassification, "
				   + "eq.equipmentName, eq.equipmentCondition, eq.requiredSkill, eq.equipStatus, "
				   + "o.orderDate, o.pickUpDate, o.orderStatus "
				   + "FROM transaction t "
				   + "JOIN employee e ON t.empID = e.empID "
				   + "JOIN equipment eq ON t.equipmentID = eq.equipmentID "
				   + "LEFT JOIN `order` o ON t.orderID = o.orderID "
				   + "WHERE t.transactionID = ?";

		try (PreparedStatement pstmt = conn.prepareStatement(strSQL)) {
			pstmt.setInt(1, transactionID);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				// Employee
				Employee employee = new Employee(
					rs.getInt("empID"),
					rs.getString("empName"),
					SkillClassification.valueOf(rs.getString("skillClassification")),
					null,
					null
				);

				// Equipment
				Equipment equipment = new Equipment(
					rs.getInt("equipmentID"),
					rs.getString("equipmentName"),
					EquipmentCondition.valueOf(rs.getString("equipmentCondition")),
					EquipmentStatus.valueOf(rs.getString("equipStatus")),
					SkillClassification.valueOf(rs.getString("requiredSkill"))
				);

				// Order (nullable)
				Order order = null;
				int orderID = rs.getInt("orderID");
				if (!rs.wasNull()) {
					order = new Order(
						orderID,
						equipment,
						employee,
						rs.getDate("orderDate").toLocalDate(),
						OrderStatus.valueOf(rs.getString("orderStatus")),
						rs.getDate("pickUpDate").toLocalDate(),
						null
					);
				}

				// Transaction
				return new Transaction(
					transactionID,
					employee,
					equipment,
					order,
					// If orderDate is Not null, then get orderDate from result set as LocalDate object; if null return null
					rs.getDate("orderDate") != null ? rs.getDate("orderDate").toLocalDate() : null,
					rs.getDate("borrowDate") != null ? rs.getDate("borrowDate").toLocalDate() : null,
					rs.getDate("expectedReturnDate") != null ? rs.getDate("expectedReturnDate").toLocalDate() : null,
					TransactionStatus.fromString(rs.getString("transactionStatus"))
				);
			}
		}
		return null;
	}
	
	
	// Reminders
	public static Reminder getLatestReminderMessage(Connection conn, int empID) throws SQLException {
	    String strSQL = "SELECT * FROM reminder WHERE empID = ? ORDER BY reminderDate DESC LIMIT 1";
	    try (PreparedStatement pstmt = conn.prepareStatement(strSQL)) {
	        pstmt.setInt(1, empID);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            Reminder reminder = new Reminder();
	            reminder.setReminderID(rs.getInt("reminderID"));
	            reminder.setReminderMSG(rs.getString("reminderMSG"));
	            reminder.setReminderDate(rs.getDate("reminderDate").toLocalDate());

	            int transactionID = rs.getInt("transactionID");

	         
	            Employee emp = getEmployeeByID(conn, empID);
	            Transaction trans = getTransactionByID(conn, transactionID);

	            reminder.setEmployee(emp);
	            reminder.setTransaction(trans);

	            return reminder;
	        } else {
	            return null;
	        }
	    } 
	}
	
	// Used for ComboBox in MainApp
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
	            
	            List<Transaction> transactions = getTransactionsByEmployeeID(conn, empID);
	            emp.setEmpTransaction(transactions);
	            list.add(emp);
	        }
	    }
	    return list;
	}
	
	// Used by openCheckoutDialog method in mainapp to get available equipment 
	public static List<Equipment> getAvailableEquipmentBySkill(Connection conn, SkillClassification skill) throws SQLException {
	    List<Equipment> equipmentList = new ArrayList<>();
	    String strSQL = "SELECT equipmentID, equipmentName, equipmentCondition, equipStatus, requiredSkill " +
	                 "FROM equipment WHERE equipStatus = 'Available' AND requiredSkill = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
	    	
	    	// debugging
	    	// verify skill and if sql query is executed properly through console log
	    	System.out.println("Debug - Skill: " + skill.toString());
	    	System.out.println("Debug - Running SQL: " + strSQL);

	        stmt.setString(1, skill.toString());
	        ResultSet rs = stmt.executeQuery();
	        
	        boolean isFound = false;
	        while (rs.next()) {
	        	isFound = true;
	        	System.out.println("Debug - Equipment Found: " + rs.getString("equipmentName"));
	            Equipment eq = new Equipment(
	                rs.getInt("equipmentID"),
	                rs.getString("equipmentName"),
	                EquipmentCondition.valueOf(rs.getString("equipmentCondition")),
	                EquipmentStatus.valueOf(rs.getString("equipStatus")),
	                SkillClassification.valueOf(rs.getString("requiredSkill"))
	            );
	            equipmentList.add(eq);
	        }
	        if (!isFound) {
	        	System.out.println("Debug - No equipment found for skill: " + skill.toString());
	        }
	    }
	    return equipmentList;
	}
	
	// update transaction status to returned and set return date
	public static void updateTransactionReturn(Connection conn, Transaction txn) throws SQLException {
	    String sql = "UPDATE transaction SET returnDate = ?, transactionStatus = ? WHERE transactionID = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setDate(1, Date.valueOf(txn.getReturnDate()));
	        stmt.setString(2, txn.getTransactionStatus().name());
	        stmt.setInt(3, txn.getTransactionID());
	        stmt.executeUpdate();
	    }
	}
	
	// update equipment status and equipment condition
	public static void updateEquipment(Connection conn, Equipment eq) throws SQLException {
	    String sql = "UPDATE equipment SET equipStatus = ?, equipmentCondition = ? WHERE equipmentID = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, eq.getStatus().name());        // ENUM to string
	        stmt.setString(2, eq.getEquipmentCondition().name());   // LocalDate to SQL date
	        stmt.setInt(3, eq.getEquipmentID());
	        stmt.executeUpdate();
	    }
	}

}
