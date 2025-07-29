package ecsapplication;

import java.time.LocalDate;

public class Transaction {

	
	private int transactionID;
    private Employee employee;
    private Equipment equipment;
    private Order order;
    private LocalDate orderDate;
    private LocalDate borrowDate;
    private LocalDate expectedReturnDate;
    private String transactionStatus;
    
    //Constructor
	public Transaction(int transactionID, Employee employee, Equipment equipment, Order order, LocalDate orderDate,
			LocalDate borrowDate, LocalDate expectedReturnDate, String transactionStatus) {
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

	// Getters
	public Employee getEmployee() {
		return employee;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public Order getOrder() {
		return order;
	}

	public String getTransactionStatus() {
		return transactionStatus;
	}

	public int getTransactionID() {
		return transactionID;
	}

	public LocalDate getOrderDate() {
		return orderDate;
	}

	public LocalDate getBorrowDate() {
		return borrowDate;
	}

	public LocalDate getExpectedReturnDate() {
		return expectedReturnDate;
	}
	
	public String toString() {
        return "Transaction{" +
                "transactionID=" + transactionID +
                ", employee=" + (employee != null ? employee.getEmpName() : "null") +
                ", equipment=" + (equipment != null ? equipment.getEquipmentName() : "null") +
                ", orderID=" + (order != null ? order.getOrderID() : "null") +
                ", orderDate=" + orderDate +
                ", borrowDate=" + borrowDate +
                ", expectedReturnDate=" + expectedReturnDate +
                ", transactionStatus='" + transactionStatus + '\'' +
                '}';
    }
}