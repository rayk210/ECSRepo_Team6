/**
 * Transaction.java
 * Represents an entity in the ECS system that processes checking out
 * and returning equipment done by employees. This class records important
 * information related to those activities, such as the date a piece of 
 * equipment was borrowed and returned, as well as the transaction status. 
 * Transaction functions to provide a historical record of activities done
 * throughout the entire life cycle of borrowing equipment.
 *   
 * This class acts as the Subject or Observable in the Observer design pattern.
 * It manages a list of observers and utilizes a registerObserver(), removeObserver(),
 * and notifyObservers() method, which it adopts from the Subject interface it implements.
 */

package ecsapplication;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.TransactionStatus;

// Subject in Observer design pattern
public class Transaction implements Subject {

	// Attributes
	private int transactionID;                // Unique identifier for transaction
    private Employee employee;                // Employee who carries out a transaction
    private Equipment equipment;              // Equipment that is checked out/borrowed
    private Order order;                      // Order associated with this transaction
    private LocalDate orderDate;              // Date order is made
    private LocalDate borrowDate;             // Borrow date
    private LocalDate expectedReturnDate;     // Expected return date
    private LocalDate returnDate;             // Actual return date
    private TransactionStatus transactionStatus;  // Transaction status (e.g., Borrowed, Returned)
    private EquipmentCondition returnCondition;   // Equipment condition when returned
    private EquipmentCondition checkoutCondition; // Equipment condition when checked out
    
    // List of observers (e.g., Reminder) who will be given a notification
    private List<Observer> observers = new ArrayList<>();
    
    // Constructors
    public Transaction() {
    	// Empty constructor
    }
    
    // Simple constructor with transaction ID, employee, equipment, and transaction status
    public Transaction(int transactionID, Employee employee, Equipment equipment, TransactionStatus transactionStatus) {
    	this.transactionID = transactionID;
    	this.employee = employee;
    	this.equipment = equipment;
    	this.transactionStatus = transactionStatus;
    }
    
    // Constructor with order and initial dates
    public Transaction(int transactionID, Employee employee, Equipment equipment, Order order, LocalDate orderDate,
			LocalDate borrowDate, LocalDate expectedReturnDate, TransactionStatus transactionStatus) {
		super();
		this.transactionID = transactionID;
		this.employee = employee;
		this.equipment = equipment;
		this.order = order;
		this.orderDate = orderDate;
		this.borrowDate = borrowDate;
		this.expectedReturnDate = expectedReturnDate;
		this.transactionStatus = transactionStatus;
	}
    
    // Constructor with an additional return condition
    public Transaction(int transactionID, Employee employee, Equipment equipment, Order order, LocalDate orderDate,
			LocalDate borrowDate, LocalDate expectedReturnDate, TransactionStatus transactionStatus, EquipmentCondition returnCondition) {
		super();
		this.transactionID = transactionID;
		this.employee = employee;
		this.equipment = equipment;
		this.order = order;
		this.orderDate = orderDate;
		this.borrowDate = borrowDate;
		this.expectedReturnDate = expectedReturnDate;
		this.transactionStatus = transactionStatus;
		this.returnCondition = returnCondition;
		this.checkoutCondition = null;
	}
    
    // Constructor that is complete with all attributes (return condition and checkout condition included)
	public Transaction(int transactionID, Employee employee, Equipment equipment, Order order, LocalDate orderDate,
			LocalDate borrowDate, LocalDate expectedReturnDate, TransactionStatus transactionStatus, EquipmentCondition returnCondition,
			 EquipmentCondition checkoutCondition) {
		super();
		this.transactionID = transactionID;
		this.employee = employee;
		this.equipment = equipment;
		this.order = order;
		this.orderDate = orderDate;
		this.borrowDate = borrowDate;
		this.expectedReturnDate = expectedReturnDate;
		this.transactionStatus = transactionStatus;
		this.returnCondition = returnCondition;
		this.checkoutCondition = checkoutCondition;
	}
	
	// ============ SUBJECT METHODS ============= //
	@Override
	public void registerObserver(Observer observer) {
		if(!observers.contains(observer)) {
			observers.add(observer);         // Add an observer if does not already exist
		}
	}
	
	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);         // Remove an observer from the list
	}
	
	@Override
	public void notifyObservers() {
		
		// Iterate through the list of observers
		for(Observer observer : observers) {
			
			// Notify all related observers of the change in state (e.g., Reminder)
			observer.update(this);
		}
	}
	
	// Getters and Setters
	
	// Get transaction ID
	public int getTransactionID() {
		return transactionID;
	}
	
	// Set transaction ID
	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

	// Get employee
	public Employee getEmployee() {
		return employee;
	}
	
	// Set employee
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	// Get return condition
	public EquipmentCondition getReturnCondition() {
		return returnCondition;
	}
	
	// Set return condition
	public void setReturnCondition(EquipmentCondition returnCondition) {
		this.returnCondition = returnCondition;
	}
	
	// Get checkout condition
	public EquipmentCondition getCheckoutCondition() {
		return checkoutCondition;
	}
	
	// Set checkout condition
	public void setCheckoutCondition(EquipmentCondition checkoutCondition) {
		this.checkoutCondition = checkoutCondition;
	}

	// Get equipment
	public Equipment getEquipment() {
		return equipment;
	}
	
	// Set equipment
	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	// Get order
	public Order getOrder() {
		return order;
	}
	
	// Get order date
	public LocalDate getOrderDate() {
		return orderDate;
	}

	// Get borrow date
	public LocalDate getBorrowDate() {
		return borrowDate;
	}
	
	// Set borrow date
	public void setBorrowDate(LocalDate borrowDate) {
		this.borrowDate = borrowDate;
	}

	// Get expected return date
	public LocalDate getExpectedReturnDate() {
		return expectedReturnDate;
	}
	
	// Set expected return date
	public void setExpectedReturnDate(LocalDate expectedReturnDate) {
		this.expectedReturnDate = expectedReturnDate;
	}
	
	// Get transaction status
	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}
	
	// Set transaction status
	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
		
		// Inform observers if transaction status changes
		notifyObservers();
	}
	
	// Get return date
	public LocalDate getReturnDate() {
		return returnDate;
	}
	
	// Set return date
	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
		
		// Inform observers if the return date updates
		notifyObservers();
	}
	
	// String representation of Transaction object
	@Override
	public String toString() {
		return "Transaction{" +
				"transactionID=" + transactionID +
				", employee=" + (employee != null ? employee.getEmpName() : "null") +
				", equipment=" + (equipment != null ? equipment.getEquipmentName() : "null") +
				", orderID=" + (order != null ? order.getOrderID() : "null") +
				", orderDate=" + orderDate +
				", borrowDate=" + borrowDate +
				", expectedReturnDate=" + expectedReturnDate +
				", returnDate=" + returnDate +
				", transactionStatus='" + transactionStatus + '\'' +
				'}';
	}
}