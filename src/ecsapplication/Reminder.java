/**
 * Reminder.java
 * The Reminder class represents notifications for Employees about their
 * equipment transactions.
 * 
 * Design Pattern:
 * The Reminder class acts as an Observer in the Observer design pattern.
 * It listens for updates from the Subject (Transaction) and generates the appropriate
 * reminder messages for employees, which is then stored in the database for persistent
 * storage.
 * 
 * Responsibilities:
 *   -Maintain data about reminders.
 *   -Generate reminder messages that indicate whether
 *   an item is overdue, due soon, or still on time.
 *   -Persists reminder messages to the database using 
 *   ReminderDAO.
 */

package ecsapplication;

// Import libraries for database interactions
import java.sql.Connection;
import java.sql.SQLException;

// Import library for handling dates of reminders
import java.time.LocalDate;

// Import for calculating time differences (e.g., days until reminder)
import java.time.temporal.ChronoUnit;

// Observer in Observer design pattern
public class Reminder implements Observer{

	// Attributes
	private int reminderID;            // Unique identifier for the reminder
	private Employee employee;         // The employee to whom to reminder is addressed
	private Transaction transaction;   // The transaction associated with this reminder
	private LocalDate reminderDate;    // The date when the reminder should be sent
	private String reminderMSG;        // The message content of the reminder
	
	// Constructors
	public Reminder() {
		// Default constructor used for empty initialization
	}
	
	// Constructor for employee and transaction
	public Reminder(Employee employee, Transaction transaction) {
		super();
		this.employee = employee;
		this.transaction = transaction;
	}
	
	// Constructor includes reminderDate
	public Reminder(Employee employee, Transaction transaction, LocalDate reminderDate) {
		super();
		this.employee = employee;
		this.transaction = transaction;
		this.reminderDate = reminderDate;
		generateReminder();  // Generate reminder message immediately
	}

	// ============================= UPDATE METHOD ============================ //
	/**
	 * Called when this observer is notified by the Subject (Transaction).
	 * Updates internal state with the latest transaction and employee information,
	 * generates a new reminder message, and persists it to the database.
	 */
	// ========================================================================== //
	@Override
	public void update(Transaction transaction) {
		
		// Check if transaction or employee parameter is null
		if(transaction == null || transaction.getEmployee() == null) {
			System.out.println("Update not complete: transaction or employee is null");
			return;
		}
		
		// Update state with latest transaction data
		this.transaction = transaction;
		this.employee = transaction.getEmployee();
		this.reminderDate = LocalDate.now();       // Set the reminder date for today
		
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
	// Get reminder ID
	public int getReminderID() {
		return reminderID;
	}

	// Set reminder ID
	public void setReminderID(int reminderID) {
		this.reminderID = reminderID;
	}

	// Get employee
	public Employee getEmployee() {
		return employee;
	}

	// Set employee
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	// Get reminder date
	public LocalDate getReminderDate() {
		return reminderDate;
	}

	// Set reminder date
	public void setReminderDate(LocalDate reminderDate) {
		this.reminderDate = reminderDate;
	}
	
	// Get transaction
	public Transaction getTransaction() {
		return transaction;
	}

	// Set transaction
	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	// Get reminder message
	public String getReminderMSG() {
        return reminderMSG;
    }

	// Set reminder message
	public void setReminderMSG(String reminderMSG) {
		this.reminderMSG = reminderMSG;
	}

	// ============== GENERATE REMINDER METHOD ============== //
	/**
	 * Generates a reminder based on the current transaction
	 * state of either overdue, due soon, or no action needed
	 */
	// ====================================================== //
	public void generateReminder() {
		
		// Validate to ensure transaction or employee is not null
		if (transaction == null || employee == null) {
			reminderMSG = "Reminder Error: Transaction or Employee is missing.";
			return;
		}

		// Retrieve the expected return date
		LocalDate dueDate = transaction.getExpectedReturnDate();
		if (dueDate == null) {
			reminderMSG = "Reminder Error: Expected return date is missing.";
			return;
		}

		// Retrieve equipment data
		Equipment equipment = transaction.getEquipment();
		if (equipment == null) {
			reminderMSG = "Reminder Error: Equipment data is missing.";
			return;
		}

		// Get the employee and equipment name for the reminder message
		String equipmentName = equipment.getEquipmentName();
		String empName = employee.getEmpName();

		// Today's date
		LocalDate reminderDate = LocalDate.now();
		
		// Calculate the number of days remaining until the due date
		long daysLeft = ChronoUnit.DAYS.between(reminderDate, dueDate);

		// Case 1 (Overdue): equipment has already passed the return date
		if (reminderDate.isAfter(dueDate)) {
			reminderMSG = empName + " has an overdue item: " + equipmentName + 
					". Due on: " + dueDate;

		// Case 2 (Return Soon): equipment is almost due (2 days before due date)
		} else if (!reminderDate.isAfter(dueDate) &&
				dueDate.minusDays(2).isBefore(reminderDate)) {
			reminderMSG = empName + " should return: " + equipmentName +
					" by " + dueDate;

		// Case 3 (No Action Needed): time still remains until return date; no immediate action is required
		} else {
			reminderMSG = "No action needed for: " + equipmentName + ". Time left to return: " + daysLeft + " days.";
		}
	}
    
	// Returns a string representation of the Reminder object
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
