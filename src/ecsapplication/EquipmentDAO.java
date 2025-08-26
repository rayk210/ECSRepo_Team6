/**
 * EquipmentDAO.java
 * Data Access Object (DAO) for the Equipment entity.
 * Handles all database operations related to Equipment,
 * including data retrievals, status updates, and filtering
 * by skill or availability.
 */

package ecsapplication;

// Imports needed for database operations
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// Import need for handling SQL exceptions
import java.sql.SQLException;

// Import collection framework to work with lists
import java.util.ArrayList;
import java.util.List;

// Import enumerations for equipment and employee skill
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.SkillClassification;

public class EquipmentDAO {

	// ================== METHOD: getAllEquipment ================= //
	// Retrieves a list of all equipment name, ID, status, and the 
	// required skill needed from the equipment table in MySQL
	// ============================================================ //
	public static List<Equipment> getAllEquipment(Connection conn) throws SQLException {

		// Initialize a dynamic equipment list to hold results
		List<Equipment> equipmentList = new ArrayList<>();

		// SQL query to retrieve equipment attributes from the equipment table
		String query = "SELECT equipmentID, equipmentName, equipStatus, requiredSkill FROM equipment";
		try (PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				int equipmentID = rs.getInt("equipmentID");            // Retrieve equipment ID
				String equipmentName = rs.getString("equipmentName");  // Retrieve equipment name
				String statusStr = rs.getString("equipStatus");        // Retrieve equipment status string
				String skillStr = rs.getString("requiredSkill");       // Retrieve required skill string

				// Convert string to enums
				EquipmentStatus status = EquipmentStatus.fromString(statusStr);
				SkillClassification skill = SkillClassification.fromString(skillStr);

				// Create new equipment object
				Equipment equipment = new Equipment(equipmentID, equipmentName, status, skill);

				// Add equipment to the list
				equipmentList.add(equipment);
			}
		}

		// Return the equipment list
		return equipmentList;
	}

	// ===================== METHOD: getAvailableEquipmentBySkill =================== //
	// Returns available equipment for a specific skill (used in checkout dialog for
	// skill-based filtering.
	// ============================================================================== //
	public static List<Equipment> getAvailableEquipmentBySkill(Connection conn, SkillClassification skill) throws SQLException {

		// Initialize a dynamic equipment list to hold results
		List<Equipment> equipmentList = new ArrayList<>();

		// SQL query to get equipment with status 'Available' filtered by required skill,
		// including the last returned condition if available
		String strSQL = """
				    SELECT eq.equipmentID, 
				           eq.equipmentName, 
				           eq.equipStatus, 
				           eq.requiredSkill,
				           t_last.returnCondition
				    FROM equipment eq
				    LEFT JOIN (
				        SELECT t1.equipmentID, t1.returnCondition
				        FROM transaction t1
				        WHERE t1.transactionStatus = 'Returned'
				          AND t1.returnCondition IS NOT NULL
				          AND t1.transactionID = (
				              SELECT MAX(t2.transactionID)
				              FROM transaction t2
				              WHERE t2.equipmentID = t1.equipmentID
				                AND t2.transactionStatus = 'Returned'
				          )
				    ) AS t_last
				      ON eq.equipmentID = t_last.equipmentID
				    WHERE eq.equipStatus = 'Available'
				      AND eq.requiredSkill = ?
				    ORDER BY eq.equipmentID ASC
				""";

		// Prepare the SQL statement using try-with-resources to ensure automatic closing
		try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {

			// Bind the skill parameter to the Prepared Statement
			stmt.setString(1, skill.name());

			// Execute the query and obtain the result set
			try (ResultSet rs = stmt.executeQuery()) {

				// Iterate over each row in the result set
				while (rs.next()) {
					// Determine the equipment condition:
					// Use the last returnCondition if available, otherwise fallback to default (Good)
					EquipmentCondition cond = rs.getString("returnCondition") != null
							? EquipmentCondition.valueOf(rs.getString("returnCondition"))
									: EquipmentCondition.Good; // default fallback

					// Create Equipment object with the retrieved data and correct condition
					Equipment eq = new Equipment(
							rs.getInt("equipmentID"),
							rs.getString("equipmentName"),
							cond,
							EquipmentStatus.valueOf(rs.getString("equipStatus")),
							SkillClassification.valueOf(rs.getString("requiredSkill"))
							);

					// Add the Equipment object to the list to be returned
					equipmentList.add(eq);
				}
			}
		}

		// Return the list of available equipment filtered by skill
		return equipmentList;
	}

	// ==================== METHOD: updateEquipment ====================== //
	// Used to update the availability status of equipment in the database
	// based on equipment ID. This method is invoked when employees return
	// or checkout equipment to reflect a change in state.
	// Manages the state of equipment.
	// =================================================================== //
	public static void updateEquipment(Connection conn, Equipment eq) throws SQLException {

		// SQL statement to update equipment status and connection for a specific equipment ID
		String strSQL = "UPDATE equipment SET equipStatus = ?, equipmentCondition = ? WHERE equipmentID = ?";

		try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
			stmt.setString(1, eq.getStatus().name());        // Convert enum to string
			stmt.setString(2, eq.getEquipmentCondition().name()); // Convert enum to string
			stmt.setInt(3, eq.getEquipmentID());             // Set equipment ID in the 'where' clause
			stmt.executeUpdate();                            // Execute the update statement
		}
	}

	// ================== METHOD: updateEquipmentStatus =============== //
	// Updates only the status of a specific equipment in the database.
	// Returns true if at least one row was affected, false if otherwise.
	// ================================================================ //
	public static boolean updateEquipmentStatus(int equipmentID, EquipmentStatus status) {

		// SQL statement to update the equipment status column for a specific equipment ID
		String strSQL = "UPDATE equipment SET equipStatus = ? WHERE equipmentID = ?";

		// Try-with-resources to automatically close connection and statement
		try (Connection conn = DBConnect.getInstance().getConnection();
				PreparedStatement stmt = conn.prepareStatement(strSQL)) {

			// Bind the new status value to the first parameter of the SQL statement
			stmt.setString(1, status.name());

			// Bind the equipment ID to the second parameter of the SQL statement
			stmt.setInt(2, equipmentID);

			// Execute the update and return true if at least one row was affected
			int affectedRows = stmt.executeUpdate();
			return affectedRows > 0;

		} catch (SQLException e) {

			// Print stack trace if a SQL error occurs and return false
			e.printStackTrace();
			return false;
		}
	}

	// ==================== METHOD: getEquipmentByID ================== //
	// Retrieves a single equipment record by its ID from the database
	// ================================================================ //
	public static Equipment getEquipmentByID(Connection conn, int equipmentID) throws SQLException {

		// SQL query to select all columns from the equipment table for a specific equipment ID
		String strSQL = "SELECT * FROM equipment WHERE equipmentID = ?";

		// Try-with-resources ensures PreparedStatement is close automatically
		try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {

			// Bind the equipment ID parameter to the query
			stmt.setInt(1, equipmentID);

			// Execute query and get result set
			try (ResultSet rs = stmt.executeQuery()) {

				// If a record exists, map it to an Equipment object
				if (rs.next()) {
					return mapResultSetToEquipment(rs);
				} else {

					// Return null if no equipment with the given ID exists
					return null;
				}
			}
		}
	}

	// ==================== METHOD: mapResultSetToEquipmennt ===================== //
	// Maps a single row from the Result Set into an Equipment object
	// =========================================================================== //
	public static Equipment mapResultSetToEquipment(ResultSet rs) throws SQLException {
		
		// Get basic fields from the result set
		int equipmentID = rs.getInt("equipmentID");
		String equipmentName = rs.getString("equipmentName");

		// Convert string values from DB into corresponding enums in Java
		EquipmentCondition equipmentCondition = EquipmentCondition.fromString(rs.getString("equipmentCondition"));
		SkillClassification requiredSkill = SkillClassification.fromString(rs.getString("requiredSkill"));
		EquipmentStatus equipmentStatus = EquipmentStatus.fromString(rs.getString("equipStatus"));

		// Create an Equipment object with the mapped data
		Equipment equipment = new Equipment(equipmentID, equipmentName, equipmentCondition, equipmentStatus, requiredSkill);

		// Return equipment object
		return equipment;
	}
	
	// ====================== METHOD: getOrderableEquipmentBySkill ======================= //
	// Returns a list of equipment that can be ordered by employees based on their skill
	// =================================================================================== //
	public static List<Equipment> getOrderableEquipmentBySkill(Connection conn, SkillClassification skill) throws SQLException {
		
		// Initialize a dynamic list to hold eligible equipment
		List<Equipment> list = new ArrayList<>();
		
		// SQL statement to get equipment that is available and matches the employee skill
		String strSQL = "SELECT * FROM equipment WHERE equipStatus = 'Available' AND requiredSkill = ?";

		try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
			
			// Bind the skill parameter to SQL statement
			stmt.setString(1, skill.name());
			try (ResultSet rs = stmt.executeQuery()) {      // Execute SQL statement
				
				// Map each row to an Equipment object
				while (rs.next()) {
					Equipment equipment = mapResultSetToEquipment(rs);
					
					// Add mapped equipment objects to list
					list.add(equipment);
				}
			}
		}
		
		// Return filtered list of equipment
		return list;
	}
	
	// ==================== METHOD: markAllLoaned ===================== //
	// This method sets the equipment status of all equipment of a 
	// certain skill to 'Loaned'. Useful for preparing test conditions
	// where no equipment is available for checkout under a given 
	// skill classification.
	// ================================================================ //
	public static void markAllLoaned(Connection conn, SkillClassification skill) throws SQLException {
		
		// Define SQL update statement that sets equipment status to 'Loaned' for all
		// equipment matching the required skill.
	    String sql = "UPDATE equipment SET equipStatus = 'Loaned' WHERE requiredSkill = ?";
	    
	    // Create a PreparedStatement using the SQL query and the active connection
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	    	
	    	// Bind the skill classification to the query parameter
	        stmt.setString(1, skill.name());
	        
	        // Execute the update statement to apply the changes in the database
	        stmt.executeUpdate();
	    }
	}
}
