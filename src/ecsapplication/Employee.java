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
        boolean success = DBConnect.insertOrder(order);

        if (success) {
            // Update equipment status in database
            DBConnect.updateEquipmentStatus(equipment.getEquipmentID(), EquipmentStatus.Ordered);
            return "Order confirmed.";
        } else {
            return "Failed to place order.";
        }

    }
    
    public String cancelOrder(int orderID) {
        try (Connection conn = DBConnect.getInstance().getConnection()) {
            // Retrieve order 
            Order order = DBConnect.getOrderByID(conn, orderID);
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


    public Transaction returnEquipment(int transactionID, EquipmentCondition condition) {
    	
    	for(Transaction txn : empTransaction) {
    		
    		// ensures that the equipment being returned is indeed borrowed 
    		if (txn.getTransactionID() == transactionID && txn.getTransactionStatus() == TransactionStatus.Borrowed) {
    			LocalDate today = LocalDate.now();
    			
    			// set return date and change transaction status to returned
    			txn.setReturnDate(today);
    			txn.setTransactionStatus(TransactionStatus.Returned);
    			
    			// change equipment status to available
    			Equipment eq = txn.getEquipment();
    			eq.setStatus(EquipmentStatus.Available);
    			// change equipments condition
    			eq.setEquipmentCondition(condition);
    			
    			System.out.println("Transaction " + txn.getTransactionID() + " was successfully returned by: " + this.getEmpName());
    			return txn;
    		}
    	}
    	System.out.println("No active transactions found for this equipment.");
        return null;
    }

    public List<Transaction> viewRecord() {
        return empTransaction;
    }
    
    @Override
    public String toString() {
		return empID + " - " + empName;
    }
}