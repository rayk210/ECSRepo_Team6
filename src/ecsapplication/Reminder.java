/**
 * Reminder.java
 * The Reminder class acts as an Observer in the Observer design pattern.
 * It listens for updates from the Subject (Transaction) and generates the appropriate
 * reminder messages for employees, which is then stored in the database for persistent
 * storage.
 * 
 * This class receives updates from Transaction through the update() method and then
 * generates reminder messages based on due dates and employee data.
 * These reminders are then stored in the database via ReminderDAO.
 */


package ecsapplication;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


// Observer in Observer design pattern
public class Reminder implements Observer{

	// Attributes
	private int reminderID;
	private Employee employee;
	private Transaction transaction;
	private LocalDate reminderDate;
	private String reminderMSG;
	
	// Constructors
	public Reminder() {
		// Blank constructor overload
	}
	
	public Reminder(Employee employee, Transaction transaction) {
		super();
		this.employee = employee;
		this.transaction = transaction;
	}
	
	public Reminder(Employee employee, Transaction transaction, LocalDate reminderDate) {
		super();
		this.employee = employee;
		this.transaction = transaction;
		this.reminderDate = reminderDate;
		generateReminder();
	}

	// Called when this observer is notified by the Subject with a transaction update
	// Updates internal state
	// Generates a reminder message and persists it to the database
	@Override
	public void update(Transaction transaction) {
		
		if(transaction == null || transaction.getEmployee() == null) {
			System.out.println("Update not complete: transaction or employee is null");
			return;
		}
		
		this.transaction = transaction;
		this.employee = transaction.getEmployee();
		
		// Set reminder date to today when updated
		this.reminderDate = LocalDate.now();
		
		// Generate a new reminders based on up-to-date data
		generateReminder();
		
		// Save latest reminder to database
		try (Connection conn = DBConnect.getInstance().getConnection()) {
	        ReminderDAO.saveReminder(this, conn);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("Failed to save reminder to database");
	    }	
	}
	
	// Getters and Setters
	public int getReminderID() {
		return reminderID;
	}

	public void setReminderID(int reminderID) {
		this.reminderID = reminderID;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public LocalDate getReminderDate() {
		return reminderDate;
	}

	public void setReminderDate(LocalDate reminderDate) {
		this.reminderDate = reminderDate;
	}
	
	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public String getReminderMSG() {
        return reminderMSG;
    }

	public void setReminderMSG(String reminderMSG) {
		this.reminderMSG = reminderMSG;
	}

	// Generates a reminder message based on the transactions state
	public void generateReminder() {
		if (transaction == null || employee == null) {
	        reminderMSG = "Reminder Error: Transaction or Employee is missing.";
	        return;
	    }
	    
	    LocalDate dueDate = transaction.getExpectedReturnDate();
	    if (dueDate == null) {
	        reminderMSG = "Reminder Error: Expected return date is missing.";
	        return;
	    }
	    
	    Equipment equipment = transaction.getEquipment();
	    if (equipment == null) {
	        reminderMSG = "Reminder Error: Equipment data is missing.";
	        return;
	    }
	    
	    String equipmentName = equipment.getEquipmentName();
	    String empName = employee.getEmpName();
	    
	    LocalDate reminderDate = LocalDate.now();
	    long daysLeft = ChronoUnit.DAYS.between(reminderDate, dueDate);

	    // Compares todays date with the expected return date
	    // This condition indicates that equipment is overdue
	    if (reminderDate.isAfter(dueDate)) {
	        reminderMSG = empName + " has an overdue item: " + equipmentName + 
	                      ". Due on: " + dueDate;
	      
	    // This condition indicates an employee should return equipment soon
	    } else if (!reminderDate.isAfter(dueDate) &&
	               dueDate.minusDays(2).isBefore(reminderDate)) {
	        reminderMSG = empName + " should return: " + equipmentName +
	                      " by " + dueDate;
	        
	    // This indicates no action is needed at this time
	    } else {
	        reminderMSG = "No action needed for: " + equipmentName + ". Time left to return: " + daysLeft + " days.";
	    }

    }
    
    @Override
    public String toString() {
        return "Reminder{" +
                "reminderID=" + reminderID +
                ", employee=" + (employee != null ? employee.getEmpName() : "null") +
                ", transactionID=" + (transaction != null ? transaction.getTransactionID() : "null") +
                ", reminderDate=" + reminderDate +
                ", message='" + reminderMSG + '\'' +
                '}';
    }
	
}
