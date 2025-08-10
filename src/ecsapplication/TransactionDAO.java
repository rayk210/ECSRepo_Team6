package ecsapplication;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.OrderStatus;
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;

public class TransactionDAO {

	
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
		             "LEFT JOIN `order` o ON t.orderID = o.orderID ";

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
		                
		                LocalDate orderDate = rs.getDate("OrderDate") != null
		                    ? rs.getDate("OrderDate").toLocalDate() : null;
		                
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
		
}
