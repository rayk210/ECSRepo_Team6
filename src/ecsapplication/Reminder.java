package ecsapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Reminder {

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
	
	public Reminder(Employee employee, Transaction transaction, LocalDate reminderDate) {
		super();
		this.employee = employee;
		this.transaction = transaction;
		this.reminderDate = reminderDate;
		generateReminder();
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

	// Generate reminder
	public void generateReminder() {
		
		if (transaction == null || employee == null) {
			reminderMSG = "Reminder Error: Transaction or Employee is missing.";
			return;
		}
		
        LocalDate dueDate = transaction.getExpectedReturnDate();
        String equipmentName = transaction.getEquipment().getEquipmentName();
        String empName = employee.getEmpName();

        if (reminderDate.isAfter(dueDate)) {
            reminderMSG = "Reminder: " + empName + " has an overdue item: " + equipmentName + 
                              ". Due on: " + dueDate;
        } else if (reminderDate.plusDays(2).isAfter(dueDate)) {
            reminderMSG = "Reminder: " + empName + " should return: " + equipmentName +
                              " by " + dueDate;
        } else {
            reminderMSG = "No action needed.";
        }
    }

    // Simulate sending reminder
    public void sendTo(Employee employee) {
        System.out.println("Sending reminder to: " + employee.getEmpName());
        System.out.println("Message: " + reminderMSG);
    }

    
    public void saveToDatabase(Connection conn) {
        String strSQL = "INSERT INTO reminder (empID, transactionID, reminderDate, reminderMSG) VALUES (?, ?, ?, ?)";
        
        // Prepared Statement to protect against SQL attacks
        try (PreparedStatement pstmt = conn.prepareStatement(strSQL)) {
            pstmt.setInt(1, employee.getEmpID());
            pstmt.setInt(2, transaction.getTransactionID());
            pstmt.setDate(3, java.sql.Date.valueOf(reminderDate));
            pstmt.setString(4, reminderMSG);
            pstmt.executeUpdate();
            System.out.println("Reminder saved to database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String toString() {
        return "Reminder{" +
                "reminderID=" + reminderID +
                ", employee=" + employee.getEmpName() +
                ", transactionID=" + transaction.getTransactionID() +
                ", reminderDate=" + reminderDate +
                ", message='" + reminderMSG + '\'' +
                '}';
    }
	
}
