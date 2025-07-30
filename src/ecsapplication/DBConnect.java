package ecsapplication;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.OrderStatus;
import ecsapplication.enums.SkillClassification;


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
				return new Employee(empID, empName, skill, null, null);
			}
		}
		return null;
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
					rs.getString("transactionStatus")
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
	            Employee emp = new Employee();
	            emp.setEmpID(rs.getInt("empID"));
	            emp.setEmpName(rs.getString("empName"));
	            emp.setSkillClassification(SkillClassification.fromString(rs.getString("skillClassification")));
	            list.add(emp);
	        }
	    }
	    return list;
	}
}
