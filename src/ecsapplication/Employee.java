/**
 * Employee.java
 * Employee is an entity who can perform major operational tasks
 * such as checking out and returning equipment, placing and canceling
 * orders for equipment, and reviewing reminders. This class ensures 
 * that all interactions with equipment and orders are handled according
 * to the business rules determined.
 */


package ecsapplication;

import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.OrderStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Employee {

	// Attributes
	private int empID;
    private String empName;
    private SkillClassification skillClassification;
    private List<Transaction> empTransaction;
    private Order order;
    
    // Constructors
    
    public Employee() {
    	this.empTransaction = new ArrayList<>();
    }
    
    public Employee(int empID, String empName) {
        this.empID = empID;
        this.empName = empName;
        this.empTransaction = new ArrayList<>();
    }
    
    public Employee(int empID, String empName, SkillClassification skillClassification) {
        this.empID = empID;
        this.empName = empName;
        this.skillClassification = skillClassification;
        this.empTransaction = new ArrayList<>();
    }
    
	public Employee(int empID, String empName, SkillClassification skillClassification,
			List<Transaction> empTransaction, Order order) {
		
		this.empID = empID;
		this.empName = empName;
		this.skillClassification = skillClassification;
		this.empTransaction = empTransaction != null ? empTransaction : new ArrayList<>();
		this.order = order;
	}
	
	// Getters and Setters

	public int getEmpID() {
		return empID;
	}
	
	public void setEmpID(int empID) {
		this.empID = empID;
	}

	public String getEmpName() {
		return empName;
	}

	public void setEmpName(String empName) {
		this.empName = empName;
	}

	public SkillClassification getSkillClassification() {
		return skillClassification;
	}

	public void setSkillClassification(SkillClassification skillClassification) {
		this.skillClassification = skillClassification;
	}

	public List<Transaction> getEmpTransaction() {
		return empTransaction;
	}
	
	public void setEmpTransaction(List<Transaction> transactions) {
	    this.empTransaction = transactions;
	}

	public Order getOrder() {
		return order;
	}
	
	// Enables an employee to check out equipment
	// Sets the transaction status to ‘Borrowed’
	public Transaction checkOut(Equipment equipment) {
	    LocalDate today = LocalDate.now();
	    LocalDate expectedReturn = today.plusWeeks(7);
	    
	    Equipment equipmentCopy = equipment.clone();

	    Transaction transaction = new Transaction(
	        0,
	        this,        // this employee
	        equipmentCopy,
	        null,
	        null,      // orderDate
	        today,      // borrowDate
	        expectedReturn,
	        TransactionStatus.Borrowed
	    );

	    this.empTransaction.add(transaction);
	    return transaction;
    }

	// Enables an employee to order equipment
	// Sets the order status to ‘Confirmed’ and equipment status to ‘Ordered’
    public String orderEquipment(Equipment equipment) {
    	// Validate status
        if (equipment.getStatus() != EquipmentStatus.Available) {
            return "Equipment is not available.";
        }

        // Validate skill
        if (!this.getSkillClassification().equals(equipment.getRequiredSkill())) {
            return "You are not qualified to order this equipment.";
        }

        // Make a new order
        Order order = new Order(
            0,                 
            this,               
            equipment,
            LocalDate.now(),   
            OrderStatus.Confirmed
        );
        
        // Save to the database
        boolean success = OrderDAO.insertOrder(order);

        if (success) {
            // Update equipment status in database
            EquipmentDAO.updateEquipmentStatus(equipment.getEquipmentID(), EquipmentStatus.Ordered);
            return "Order confirmed.";
        } else {
            return "Failed to place order.";
        }

    }
    
    // Enables an employee to cancel an order made
    // Sets the order status to ‘Cancelled’ and equipment status to ‘Available’ so that it can be ordered or checked out again
    public String cancelOrder(int orderID) {
        try (Connection conn = DBConnect.getInstance().getConnection()) {
            // Retrieve order 
            Order order = OrderDAO.getOrderByID(conn, orderID);
            if (order == null) {
            	return "Order not found";
            }
            if (order.getOrderStatus() == OrderStatus.Cancelled) {
            	return "Order is already cancelled";
            }
            

            // Update order status to Cancelled
            String sqlUpdateOrder = "UPDATE `order` SET orderStatus = 'Cancelled' WHERE orderID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateOrder)) {
                stmt.setInt(1, orderID);
                stmt.executeUpdate();
            }

            // Update equipment status to Available
            String sqlUpdateEquip = "UPDATE equipment SET equipStatus = 'Available' WHERE equipmentID = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdateEquip)) {
                stmt.setInt(1, order.getEquipment().getEquipmentID());
                
                int affected = stmt.executeUpdate();
                
                if (affected > 0) {
                	return "Order successfully cancelled";
                }
                else {
                	return "Failed to cancel order";
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return "An error occured while cancelling the order.";
        }
    }


    // Enables an employee to return equipment that they have previously checked out
    // Sets the transaction status to ‘Returned’, equipment status to ‘Available’, and equipment condition to the one chosen by the employee
    public Transaction returnEquipment(int transactionID, EquipmentCondition condition) {
    	
    	for(Transaction txn : empTransaction) {

    		// Ensures that the equipment being returned is indeed borrowed 
    		if (txn.getTransactionID() == transactionID && txn.getTransactionStatus() == TransactionStatus.Borrowed) {
    			LocalDate today = LocalDate.now();

    			// Set return date and change transaction status to returned
    			txn.setReturnDate(today);
    			txn.setTransactionStatus(TransactionStatus.Returned);

    			// Set return condition in Transaction
    			txn.setReturnCondition(condition);

    			Equipment eq = txn.getEquipment();
    			eq.setEquipmentCondition(condition);
    			// Update equipment status to available
    			eq.setStatus(EquipmentStatus.Available);

    			// Update to database
    			try {
    				EquipmentDAO.updateEquipment(DBConnect.getInstance().getConnection(), eq);
    				TransactionDAO.updateTransactionReturn(DBConnect.getInstance().getConnection(), txn);
    			} catch (SQLException e) {
    				e.printStackTrace();
    			 }
    			System.out.println("Transaction " + txn.getTransactionID() + " was successfully returned by: " + this.getEmpName());
    			return txn;
    		}
    	}
    	System.out.println("No active transactions found for this equipment.");
        return null;
    }

    // Retrieves a list of transactions based on employeeID
    // Used to display individual employee records for the View Record use case
    public List<Transaction> viewRecord() {
        
    	List<Transaction> transactions = new ArrayList<>();
    	
    	try(Connection conn = DBConnect.getInstance().getConnection()){
    		transactions = TransactionDAO.getTransactionsByEmployeeID(conn, this.empID);
    		
    	}catch (SQLException e){
    		e.printStackTrace();
    	}
    	return transactions;
    }
    
    @Override
    public String toString() {
		return empID + " - " + empName;
    }
}