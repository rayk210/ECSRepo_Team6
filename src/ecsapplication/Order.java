/**
 * Order.java
 * This class represents an entity in the ECS system where an order
 * can be made for a piece of equipment. This class stores important
 * information, such as who has ordered what equipment and when the order was made.
 */

package ecsapplication;

// Import order status enumeration
import ecsapplication.enums.OrderStatus;

// Import time API to work with current date
import java.time.LocalDate;

public class Order {

	// Attributes
	private int orderID;               // Unique identifier for the order
    private Equipment equipment;       // Equipment being ordered
    private Employee employee;         // Employee who placed the order
    private LocalDate orderDate;       // Date the order was created
    private OrderStatus orderStatus;   // Status of the order (e.g., Confirmed, Cancelled)
    private LocalDate pickUpDate;      // Date the order is picked up (can be null)
    private Transaction transaction;   // Associated transaction when order is fulfilled
    
    // Constructors
    
    // Overload constructor for testing placing orders
    public Order(Employee employee, Equipment equipment, OrderStatus orderStatus) {
    	this.employee = employee;
    	this.equipment = equipment;
    	this.orderStatus = orderStatus;
    }
    
    // Basic constructor without pick up date or transaction
    public Order(int orderID, Employee employee, Equipment equipment, LocalDate orderDate, OrderStatus orderStatus) {
        this.orderID = orderID;
        this.employee = employee;
        this.equipment = equipment;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.pickUpDate = null;        // No pick up date
        this.transaction = null;       // No transaction
    }
    
    // Constructor with pick up date
    public Order(int orderID, Employee employee, Equipment equipment, LocalDate orderDate,
            OrderStatus orderStatus, LocalDate pickUpDate) {
    	this.orderID = orderID;
    	this.employee = employee;
    	this.equipment = equipment;
    	this.orderDate = orderDate;
    	this.orderStatus = orderStatus;
    	this.pickUpDate = pickUpDate;     // Set pickup date
    	this.transaction = null;    
    }
    
    // Full constructor including transaction
	public Order(int orderID, Equipment equipment, Employee employee, LocalDate orderDate, OrderStatus orderStatus,
			LocalDate pickUpDate, Transaction transaction) {
		
		this.orderID = orderID;
		this.equipment = equipment;
		this.employee = employee;
		this.orderDate = orderDate;
		this.orderStatus = orderStatus;
		this.pickUpDate = pickUpDate;
		this.transaction = transaction;
	}
	
	// Getters
	// Get order ID
	public int getOrderID() {
		return orderID;
	}

	// Get equipment
	public Equipment getEquipment() {
		return equipment;
	}
	
	// Get employee
	public Employee getEmployee() {
		return employee;
	}
	
	// Get order date
	public LocalDate getOrderDate() {
		return orderDate;
	}
	
	// Get order status
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	
	// Get pick up date
	public LocalDate getPickUpDate() {
		return pickUpDate;
	}
	
	// Get transaction
	public Transaction getTransaction() {
		return transaction;
	}

	// A string representation of the Order object
	@Override
	public String toString() {
		return "Order{" +
				"orderID=" + orderID +

				// Safely print equipment and employee names; use null if either do not exist
				", equipment=" + (equipment != null ? equipment.getEquipmentName() : "null") +
				", employee=" + (employee != null ? employee.getEmpName() : "null") +

				", orderDate=" + orderDate +
				", orderStatus=" + orderStatus +
				", pickUpDate=" + pickUpDate +
				'}';
	}
}
