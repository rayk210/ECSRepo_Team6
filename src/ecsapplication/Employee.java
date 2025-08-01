package ecsapplication;

import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.TransactionStatus;
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;

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

	    Transaction transaction = new Transaction(
	        0,
	        this,        // this employee
	        equipment,
	        null,
	        null,      // orderDate
	        today,      // borrowDate
	        expectedReturn,
	        TransactionStatus.Borrowed
	    );

	    this.empTransaction.add(transaction);
	    return transaction;
    }

    public Order orderEquipment(int equipmentID) {
        return null;
    }

    public Transaction returnEquipment(int transactionID, EquipmentCondition condition) {
    	
    	for(Transaction txn : empTransaction) {
    		Equipment eq = txn.getEquipment();
    		
    		// ensures that the equipment being returned is indeed borrowed 
    		if (eq.getEquipmentID() == transactionID && txn.getTransactionStatus() == TransactionStatus.Borrowed) {
    			LocalDate today = LocalDate.now();
    			
    			// set return date and change transaction status to returned
    			txn.setReturnDate(today);
    			txn.setTransactionStatus(TransactionStatus.Returned);
    			
    			// change equipment status to available
    			eq.setStatus(EquipmentStatus.Available);
    			// change equipments condition
    			eq.setEquipmentCondition(condition);
    			
    			System.out.println("Transaction " + txn.getTransactionID() + "was successfully returned by: " + this.getEmpName());
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