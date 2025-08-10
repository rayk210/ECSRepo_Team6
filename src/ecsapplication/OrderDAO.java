package ecsapplication;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ecsapplication.enums.OrderStatus;

public class OrderDAO {

	
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
		    
		    Connection conn = null;
		    try {
		        conn = DBConnect.getInstance().getConnection();
		        try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
		            stmt.setInt(1, order.getEmployee().getEmpID());
		            stmt.setInt(2, order.getEquipment().getEquipmentID());
		            stmt.setDate(3, java.sql.Date.valueOf(order.getOrderDate()));
		            stmt.setString(4, order.getOrderStatus().name());

		            int affectedRows = stmt.executeUpdate();
		            return affectedRows > 0;
		        }
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
		            String statusStr = rs.getString("orderStatus");
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

		                Employee employee = EmployeeDAO.getEmployeeByID(conn, employeeID);
		                Equipment equipment = EquipmentDAO.getEquipmentByID(conn, equipmentID);

		                return new Order(orderID, equipment, employee, orderDate, orderStatus, pickUpDate, null);
		            }
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		    return null;
		}
		
}
