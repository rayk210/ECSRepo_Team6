/**
 * Order.java
 * This class represents an entity in the ECS system where an order
 * can be made for a piece of equipment. This class stores important
 * information, such as who has ordered what equipment and when the order was made.
 */

package ecsapplication;

import ecsapplication.enums.OrderStatus;
import java.time.LocalDate;

public class Order {

	// Attributes
	private int orderID;
    private Equipment equipment;
    private Employee employee;
    private LocalDate orderDate;
    private OrderStatus orderStatus;
    private LocalDate pickUpDate;
    private Transaction transaction;
    
    // Constructors
    public Order(int orderID, Employee employee, Equipment equipment, LocalDate orderDate, OrderStatus orderStatus) {
        this.orderID = orderID;
        this.employee = employee;
        this.equipment = equipment;
        this.orderDate = orderDate;
        this.orderStatus = orderStatus;
        this.pickUpDate = null;
        this.transaction = null;
    }
    
    public Order(int orderID, Employee employee, Equipment equipment, LocalDate orderDate,
            OrderStatus orderStatus, LocalDate pickUpDate) {
    	this.orderID = orderID;
    	this.employee = employee;
    	this.equipment = equipment;
    	this.orderDate = orderDate;
    	this.orderStatus = orderStatus;
    	this.pickUpDate = pickUpDate;
    	this.transaction = null;
    }
    
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
	public int getOrderID() {
		return orderID;
	}

	public Equipment getEquipment() {
		return equipment;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	
	public LocalDate getOrderDate() {
		return orderDate;
	}
	
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	
	public LocalDate getPickUpDate() {
		return pickUpDate;
	}
	
	public Transaction getTransaction() {
		return transaction;
	}
	
	public void confirmOrder() {
		if (this.orderStatus == OrderStatus.Cancelled) {
	        System.out.println("Order " + orderID + " has been cancelled");
	        return;
	    }
		else if (this.orderStatus == OrderStatus.Confirmed) {
	        System.out.println("Order " + orderID + " is already confirmed.");
	        return;
		}

		else {
			this.orderStatus = OrderStatus.Confirmed;
			System.out.println("Order " + orderID + " confirmed.");
		}
    }

	@Override
	public String toString() {
		return "Order{" +
		           "orderID=" + orderID +
		           ", equipment=" + (equipment != null ? equipment.getEquipmentName() : "null") +
		           ", employee=" + (employee != null ? employee.getEmpName() : "null") +
		           ", orderDate=" + orderDate +
		           ", orderStatus=" + orderStatus +
		           ", pickUpDate=" + pickUpDate +
		           '}';
	}
}
