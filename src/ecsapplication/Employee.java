package ecsapplication;

import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.EquipmentCondition;
import java.util.ArrayList;
import java.util.List;

public class Employee {

	// Attributes
	private int empID;
    private String empName;
    private SkillClassification skillClassification;
    private List<Transaction> empTransaction;
    private Order order;
    
    // Constructor
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

	public Order getOrder() {
		return order;
	}
	
	public Transaction checkOut(int equipmentID) {
        return null;
    }

    public Order orderEquipment(int equipmentID) {
        return null;
    }

    public Transaction returnEquipment(int equipmentID, EquipmentCondition condition) {
        return null;
    }

    public List<Transaction> viewRecord() {
        return empTransaction;
    }
    
    @Override
    public String toString() {
		return "Employee{" +
		           "empID=" + empID +
		           ", empName=" + empName +
		           ", skillClassification=" + skillClassification +
		           ", totalTransactions=" + (empTransaction != null ? empTransaction.size() : 0) +
		           '}';
	}
}