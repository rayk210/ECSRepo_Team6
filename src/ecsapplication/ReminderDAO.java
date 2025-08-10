package ecsapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReminderDAO {

	
	public static void saveReminder(Reminder reminder, Connection conn) throws SQLException {
        String checkSql = "SELECT reminderID FROM reminder WHERE transactionID = ?";
        
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, reminder.getTransaction().getTransactionID());
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                int reminderID = rs.getInt("reminderID");
                String updateSql = "UPDATE reminder SET empID = ?, reminderDate = ?, reminderMSG = ? WHERE reminderID = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, reminder.getEmployee().getEmpID());
                    updateStmt.setDate(2, java.sql.Date.valueOf(reminder.getReminderDate()));
                    updateStmt.setString(3, reminder.getReminderMSG());
                    updateStmt.setInt(4, reminderID);
                    updateStmt.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO reminder (transactionID, empID, reminderDate, reminderMSG) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setInt(1, reminder.getTransaction().getTransactionID());
                    insertStmt.setInt(2, reminder.getEmployee().getEmpID());
                    insertStmt.setDate(3, java.sql.Date.valueOf(reminder.getReminderDate()));
                    insertStmt.setString(4, reminder.getReminderMSG());
                    insertStmt.executeUpdate();
                }
            }
        }
    }
}
