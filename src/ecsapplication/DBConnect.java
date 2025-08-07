package ecsapplication;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.OrderStatus;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

// Singleton Pattern
public class DBConnect {
	
	// Attributes
    private static DBConnect instance;
    private Connection connection;

    private static final String url = "jdbc:mysql://localhost:3306/ceis400courseproject";
    private static final String username = "root";
    private static final String password = "devry123";

    // Constructor set to private so it cannot be directly accessed outside of the class
    private DBConnect() throws SQLException {
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to the database", e);
        }
    }

    // Public method to obtain a single instance of DBConnect
    // Synchronized prevents another thread from accessing this method
    public static synchronized DBConnect getInstance() throws SQLException {
        if (instance == null || instance.getConnection().isClosed()) {
            instance = new DBConnect();
        }
        return instance;
    }

    // Getter method for Connection
    public Connection getConnection() {
        return connection;
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
	    String strSQL = "SELECT t.transactionID, t.empID, t.equipmentID, t.orderID, t.borrowDate, t.expectedReturnDate, t.returnDate, t.transactionStatus, " +
	                 "e.equipmentName, e.equipmentCondition, e.requiredSkill, e.equipStatus " +
	                 "FROM transaction t JOIN equipment e ON t.equipmentID = e.equipmentID " +
	                 "WHERE t.empID = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(strSQL)) {
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
	
	// Get all equipment
	public static List<Equipment> getAllEquipment(Connection conn) throws SQLException {
	    List<Equipment> equipmentList = new ArrayList<>();

	    String query = "SELECT equipmentID, equipmentName, equipStatus, requiredSkill FROM equipment";
	    try (PreparedStatement stmt = conn.prepareStatement(query);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            int equipmentID = rs.getInt("equipmentID");
	            String equipmentName = rs.getString("equipmentName");
	            String statusStr = rs.getString("equipStatus");
	            String skillStr = rs.getString("requiredSkill");

	            EquipmentStatus status = EquipmentStatus.fromString(statusStr);
	            SkillClassification skill = SkillClassification.fromString(skillStr);

	            Equipment equipment = new Equipment(equipmentID, equipmentName, status, skill);
	            equipmentList.add(equipment);
	        }
	    }

	    return equipmentList;
	}
	
	// Used by openCheckoutDialog method in mainapp to get available equipment by skill
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
	    String strSQL = "UPDATE transaction SET transactionStatus = ?, returnDate = ? WHERE transactionID = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
	        stmt.setString(1, txn.getTransactionStatus().name());
	        stmt.setDate(2, Date.valueOf(txn.getReturnDate()));
	        stmt.setInt(3, txn.getTransactionID());
	        stmt.executeUpdate();
	    }
	}
	
	// update equipment status and equipment condition
	public static void updateEquipment(Connection conn, Equipment eq) throws SQLException {
	    String strSQL = "UPDATE equipment SET equipStatus = ?, equipmentCondition = ? WHERE equipmentID = ?";

	    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
	        stmt.setString(1, eq.getStatus().name());        // ENUM to string
	        stmt.setString(2, eq.getEquipmentCondition().name());   // LocalDate to SQL date
	        stmt.setInt(3, eq.getEquipmentID());
	        stmt.executeUpdate();
	    }
	}
	
	// update equipment status
	public static boolean updateEquipmentStatus(int equipmentID, EquipmentStatus status) {
	    String strSQL = "UPDATE equipment SET equipStatus = ? WHERE equipmentID = ?";
	    try (Connection conn = DBConnect.getInstance().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(strSQL)) {
	        stmt.setString(1, status.name());
	        stmt.setInt(2, equipmentID);
	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	// update order status in database
	public static boolean updateOrderStatus(Connection conn, int orderID, OrderStatus status) throws SQLException {
	    String sql = "UPDATE `order` SET orderStatus = ? WHERE orderID = ?";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, status.name());
	        stmt.setInt(2, orderID);
	        return stmt.executeUpdate() > 0;
	    }
	}
	
	// inserts order into the database
	public static boolean insertOrder(Order order) {
	    String strSQL = "INSERT INTO `order` (empID, equipmentID, orderDate, orderStatus) VALUES (?, ?, ?, ?)";
	    
	    try (Connection conn = DBConnect.getInstance().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(strSQL)) {
	        
	        stmt.setInt(1, order.getEmployee().getEmpID());
	        stmt.setInt(2, order.getEquipment().getEquipmentID());
	        stmt.setDate(3, java.sql.Date.valueOf(order.getOrderDate()));
	        stmt.setString(4, order.getOrderStatus().name());
	        
	        int affectedRows = stmt.executeUpdate();
	        return affectedRows > 0;
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public static List<Order> getAllOrders(Connection conn) throws SQLException {
	    List<Order> orders = new ArrayList<>();

	    String sql = "SELECT o.orderID, o.empID, o.equipmentID, o.orderDate, o.orderStatus, o.pickUpDate, " +
	                 "e.empName, eq.equipmentName " +
	                 "FROM `order` o " +
	                 "JOIN employee e ON o.empID = e.empID " +
	                 "JOIN equipment eq ON o.equipmentID = eq.equipmentID";

	    try (PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            int orderID = rs.getInt("orderID");
	            int empID = rs.getInt("empID");
	            int equipID = rs.getInt("equipmentID");
	            Date orderDate = rs.getDate("orderDate");
	            String statusStr = rs.getString("OrderStatus");
	            Date pickupDate = rs.getDate("pickUpDate");

	            // Make Employee and Equipment object from data
	            Employee emp = new Employee(empID, rs.getString("empName"));
	            Equipment equip = new Equipment(equipID, rs.getString("equipmentName"));

	            Order order = new Order(orderID, emp, equip, orderDate.toLocalDate(),
	                    OrderStatus.valueOf(statusStr), 
	                    pickupDate != null ? pickupDate.toLocalDate() : null);

	            orders.add(order);
	        }
	    }

	    return orders;
	}

	
	// gets equipment by ID
	public static Equipment getEquipmentByID(Connection conn, int equipmentID) throws SQLException {
	    String strSQL = "SELECT * FROM equipment WHERE equipmentID = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
	        stmt.setInt(1, equipmentID);
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                return mapResultSetToEquipment(rs);
	            } else {
	                return null;
	            }
	        }
	    }
	}
	
	// functions to map a ResultSet row from the equipment table to an equipment object
		public static Equipment mapResultSetToEquipment(ResultSet rs) throws SQLException {
		    int equipmentID = rs.getInt("equipmentID");
		    String equipmentName = rs.getString("equipmentName");
		    
		    // Change string from enum in DB to enum in Java
		    EquipmentCondition equipmentCondition = EquipmentCondition.fromString(rs.getString("equipmentCondition"));
		    SkillClassification requiredSkill = SkillClassification.fromString(rs.getString("requiredSkill"));
		    EquipmentStatus equipmentStatus = EquipmentStatus.fromString(rs.getString("equipStatus"));
		    
		    
		    Equipment equipment = new Equipment(equipmentID, equipmentName, equipmentCondition, equipmentStatus, requiredSkill);
		    
		    return equipment;
		}
	
	// get all transactions
	public static List<Transaction> getAllTransactions(Connection conn) throws SQLException {
	    List<Transaction> txns = new ArrayList<>();

	    String strSQL = "SELECT t.transactionID, t.empID, t.equipmentID, t.orderID, t.returnDate, " +
	             "t.borrowDate, t.expectedReturnDate, t.transactionStatus, " +
	             "e.empName, e.skillClassification, " +
	             "eq.equipmentName, eq.equipmentCondition, eq.equipStatus, eq.requiredSkill, " +
	             "o.orderDate AS OrderDate, o.orderStatus, o.pickUpDate " +
	             "FROM transaction t " +
	             "JOIN employee e ON t.empID = e.empID " +
	             "JOIN equipment eq ON t.equipmentID = eq.equipmentID " +
	             "LEFT JOIN `order` o ON t.orderID = o.orderID " +
	             "WHERE t.transactionStatus = 'Borrowed'";

	    try (PreparedStatement stmt = conn.prepareStatement(strSQL);
	         ResultSet rs = stmt.executeQuery()) {

	        while (rs.next()) {
	            // make Employee object
	            Employee employee = new Employee(
	                rs.getInt("empID"),
	                rs.getString("empName"),
	                SkillClassification.valueOf(rs.getString("skillClassification"))
	                
	            );

	            // make Equipment object
	            Equipment eq = new Equipment(
	                rs.getInt("equipmentID"),
	                rs.getString("equipmentName"),
	                EquipmentCondition.valueOf(rs.getString("equipmentCondition")),
	                EquipmentStatus.valueOf(rs.getString("equipStatus")),
	                SkillClassification.valueOf(rs.getString("requiredSkill"))
	            
	            );

	            // make Order object, if exists
	            Order order = null;
	            int orderID = rs.getInt("orderID");
	            if (!rs.wasNull()) {
	                Equipment eqForOrder = eq;  
	                Employee empForOrder = null; 
	                
	                LocalDate orderDate = rs.getDate("orderOrderDate") != null
	                    ? rs.getDate("orderOrderDate").toLocalDate() : null;
	                
	                OrderStatus orderStatus = rs.getString("orderStatus") != null
	                    ? OrderStatus.valueOf(rs.getString("orderStatus")) : null;
	                
	                LocalDate pickUpDate = rs.getDate("pickUpDate") != null
	                    ? rs.getDate("pickUpDate").toLocalDate() : null;

	                order = new Order(orderID, eqForOrder, empForOrder, orderDate, orderStatus, pickUpDate, null);
	            }

	            // Transaction object
	            Transaction txn = new Transaction(
	                rs.getInt("transactionID"),
	                employee,
	                eq,
	                order,
	                rs.getDate("orderDate") != null ? rs.getDate("orderDate").toLocalDate() : null,
	                rs.getDate("borrowDate") != null ? rs.getDate("borrowDate").toLocalDate() : null,
	                rs.getDate("expectedReturnDate") != null ? rs.getDate("expectedReturnDate").toLocalDate() : null,
	                TransactionStatus.valueOf(rs.getString("transactionStatus"))
	            );

	            // set returnDate if exists
	            if (rs.getDate("returnDate") != null) {
	                txn.setReturnDate(rs.getDate("returnDate").toLocalDate());
	            }

	            txns.add(txn);
	        }
	    }

	    return txns;
	}
	
	
	// get orders by ID from database
	public static Order getOrderByID(Connection conn, int orderID) {
	    String sql = "SELECT * FROM `order` WHERE orderID = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setInt(1, orderID);
	        try (ResultSet rs = stmt.executeQuery()) {
	            if (rs.next()) {
	                int employeeID = rs.getInt("empID");
	                int equipmentID = rs.getInt("equipmentID");
	                LocalDate orderDate = rs.getDate("orderDate").toLocalDate();
	                
	                Date pickUpDateSQL = rs.getDate("pickUpDate");
	                LocalDate pickUpDate = (pickUpDateSQL != null) ? pickUpDateSQL.toLocalDate() : null;
	                
	                OrderStatus orderStatus = OrderStatus.fromString(rs.getString("orderStatus"));

	                Employee employee = getEmployeeByID(conn, employeeID);
	                Equipment equipment = getEquipmentByID(conn, equipmentID);

	                return new Order(orderID, equipment, employee, orderDate, orderStatus, pickUpDate, null);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
	// used to filter ordering equipment by equipment status AND required skill
	public static List<Equipment> getOrderableEquipmentBySkill(Connection conn, SkillClassification skill) throws SQLException {
	    List<Equipment> list = new ArrayList<>();
	    String strSQL = "SELECT * FROM equipment WHERE equipStatus = 'Available' AND requiredSkill = ?";
	    
	    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
	        stmt.setString(1, skill.name());
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Equipment equipment = mapResultSetToEquipment(rs);
	                list.add(equipment);
	            }
	        }
	    }
	    return list;
	}
	
}
