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
	private int transactionID;
    private Employee employee;
    private Equipment equipment;
    private Order order;
    private LocalDate orderDate;
    private LocalDate borrowDate;
    private LocalDate expectedReturnDate;
    private LocalDate returnDate;
    private TransactionStatus transactionStatus;
    private EquipmentCondition returnCondition;
    private EquipmentCondition checkoutCondition;
    
    // Observer list
    private List<Observer> observers = new ArrayList<>();
    
    // Constructors
    public Transaction() {
    	// empty
    }
    
    public Transaction(int transactionID, Employee employee, Equipment equipment, TransactionStatus transactionStatus) {
    	this.transactionID = transactionID;
    	this.employee = employee;
    	this.equipment = equipment;
    	this.transactionStatus = transactionStatus;
    }
    
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
	
	// Subject methods
	@Override
	public void registerObserver(Observer observer) {
		if(!observers.contains(observer)) {
			observers.add(observer);
		}
	}
	
	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}
	
	@Override
	public void notifyObservers() {
		for(Observer observer : observers) {
			observer.update(this);
		}
	}
	
	// Getters and Setters
	public int getTransactionID() {
		return transactionID;
	}
	
	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}

	public Employee getEmployee() {
		return employee;
	}
	
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	
	public EquipmentCondition getReturnCondition() {
		return returnCondition;
	}
	
	public void setReturnCondition(EquipmentCondition returnCondition) {
		this.returnCondition = returnCondition;
	}
	
	public EquipmentCondition getCheckoutCondition() {
		return checkoutCondition;
	}
	
	public void setCheckoutCondition(EquipmentCondition checkoutCondition) {
		this.checkoutCondition = checkoutCondition;
	}

	public Equipment getEquipment() {
		return equipment;
	}
	
	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public Order getOrder() {
		return order;
	}
	
	public LocalDate getOrderDate() {
		return orderDate;
	}

	public LocalDate getBorrowDate() {
		return borrowDate;
	}
	
	public void setBorrowDate(LocalDate borrowDate) {
		this.borrowDate = borrowDate;
	}

	public LocalDate getExpectedReturnDate() {
		return expectedReturnDate;
	}
	
	public void setExpectedReturnDate(LocalDate expectedReturnDate) {
		this.expectedReturnDate = expectedReturnDate;
	}
	
	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}
	
	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
		notifyObservers();
	}
	
	public LocalDate getReturnDate() {
		return returnDate;
	}
	
	public void setReturnDate(LocalDate returnDate) {
		this.returnDate = returnDate;
		notifyObservers();
	}

	
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