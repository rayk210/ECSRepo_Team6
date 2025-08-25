/**
 * ReminderDAO.java
 * A Data Access Object (DAO) class responsible for accessing
 * and manipulating reminder data in the ECS system. This class
 * provides methods to insert or update reminder records in the
 * database, ensuring separation between persistence logic and
 * business logic.
 */

package ecsapplication;

// Import required JDBC libraries for connection and data manipulation in the database
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReminderDAO {

	// =================== METHOD: saveReminder ==================== //
	// Inserts a new reminder or updates an existing one for a given
	// transaction. Checks first to see if a reminder already exists
	// for that transaction: if exists, update the reminder details;
	// otherwise, insert a new reminder record.
	// ============================================================= //
	public static void saveReminder(Reminder reminder, Connection conn) throws SQLException {
		
		// SQL statement to check if a reminder already exists for this transaction
		String checkSql = "SELECT reminderID FROM reminder WHERE transactionID = ?";

		try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
			
			// Set the transaction ID parameter
			checkStmt.setInt(1, reminder.getTransaction().getTransactionID());
			ResultSet rs = checkStmt.executeQuery();

			// If reminder exists, update it
			if (rs.next()) {
				int reminderID = rs.getInt("reminderID");  // Retrieve the reminder ID
				
				// SQL statement to update existing reminder for a given reminder ID
				// Updated columns: employee ID, reminder date, and reminder message
				String updateSql = "UPDATE reminder SET empID = ?, reminderDate = ?, reminderMSG = ? WHERE reminderID = ?";
				try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {  
					updateStmt.setInt(1, reminder.getEmployee().getEmpID());     // Set employee ID
					updateStmt.setDate(2, java.sql.Date.valueOf(reminder.getReminderDate()));  // Set reminder date
					updateStmt.setString(3, reminder.getReminderMSG());          // Set message content
					updateStmt.setInt(4, reminderID);  // Target the correct reminder ID
					updateStmt.executeUpdate();        // Execute the update
				}
			} else {
				// If a reminder for a transaction does not exist, create one
				// SQL statement to insert new record into the reminder table with transaction ID, employee ID, reminder date, and reminder message
				String insertSql = "INSERT INTO reminder (transactionID, empID, reminderDate, reminderMSG) VALUES (?, ?, ?, ?)";
				try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
					
					// Set parameters for the PreparedStatement from the Reminder object
					insertStmt.setInt(1, reminder.getTransaction().getTransactionID());
					insertStmt.setInt(2, reminder.getEmployee().getEmpID());
					insertStmt.setDate(3, java.sql.Date.valueOf(reminder.getReminderDate()));
					insertStmt.setString(4, reminder.getReminderMSG());
					
					// Execute the insert statement
					insertStmt.executeUpdate();
				}
			}
		}
	}
}
