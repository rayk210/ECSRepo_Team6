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
    
    // Constructor
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

    public void cancelOrder() {
        this.orderStatus = OrderStatus.Cancelled;
        System.out.println("Order " + orderID + " cancelled.");
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
