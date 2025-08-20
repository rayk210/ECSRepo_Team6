/**
 * EquipmentDAO.java
 * A class that is responsible for accessing and manipulating equipment data.
 * Follows a Data Access Object pattern to encapsulate data from the rest of the application.
 */

package ecsapplication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;
import ecsapplication.enums.SkillClassification;

public class EquipmentDAO {

	    // Retrieves a list of all equipment name, ID, status, and the required skill needed from the equipment table in MySQL
		public static List<Equipment> getAllEquipment(Connection conn) throws SQLException {
		    List<Equipment> equipmentList = new ArrayList<>();

		    String query = "SELECT equipmentID, equipmentName, equipStatus, requiredSkill FROM equipment";
		    try (PreparedStatement stmt = conn.prepareStatement(query);
		         ResultSet rs = stmt.executeQuery()) {

		        while (rs.next()) {
		            int equipmentID = rs.getInt("equipmentID");
		            String equipmentName = rs.getString("equipmentName");
		            String statusStr = rs.getString("equipStatus");
		            String skillStr = rs.getString("requiredSkill");

		            EquipmentStatus status = EquipmentStatus.fromString(statusStr);
		            SkillClassification skill = SkillClassification.fromString(skillStr);

		            Equipment equipment = new Equipment(equipmentID, equipmentName, status, skill);
		            equipmentList.add(equipment);
		        }
		    }

		    return equipmentList;
		}
		
		// Used by openCheckoutDialog method in mainapp to get available equipment by skill
		// Retrieves a list of all equipment that currently has an ‘Available’ status and corresponds to with a certain skill classification for an employee
		// Supports filtering equipment based on employee skills
		public static List<Equipment> getAvailableEquipmentBySkill(Connection conn, SkillClassification skill) throws SQLException {
		    List<Equipment> equipmentList = new ArrayList<>();
		    // Query: join equipment with its last returned transaction (if any)
		    // A sub-query is used to get the latest returned transaction per equipment
		    String strSQL = """
		        SELECT eq.equipmentID, eq.equipmentName, eq.equipStatus, eq.requiredSkill,
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

		    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
		        // Set the skill parameter
		        stmt.setString(1, skill.name());

		        try (ResultSet rs = stmt.executeQuery()) {
		            while (rs.next()) {
		                // Determine the equipment condition:
		                // Use the last returnCondition if available, otherwise fallback to default (Good)
		                EquipmentCondition cond = rs.getString("returnCondition") != null
		                        ? EquipmentCondition.valueOf(rs.getString("returnCondition"))
		                        : EquipmentCondition.Good; // default fallback

		                // Create Equipment object with the correct condition
		                Equipment eq = new Equipment(
		                    rs.getInt("equipmentID"),
		                    rs.getString("equipmentName"),
		                    cond,
		                    EquipmentStatus.valueOf(rs.getString("equipStatus")),
		                    SkillClassification.valueOf(rs.getString("requiredSkill"))
		                );

		                // Add to the result list
		                equipmentList.add(eq);
		            }
		        }
		    }

		    return equipmentList;
		}

		// Used to update the availability status of equipment in the database based on equipment ID
		// This method is invoked when employees return or checkout equipment to reflect a change in state
		// Manages the state of equipment
		public static void updateEquipment(Connection conn, Equipment eq) throws SQLException {
		    String strSQL = "UPDATE equipment SET equipStatus = ?, equipmentCondition = ? WHERE equipmentID = ?";

		    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
		        stmt.setString(1, eq.getStatus().name());        // ENUM to string
		        stmt.setString(2, eq.getEquipmentCondition().name());   // LocalDate to SQL date
		        stmt.setInt(3, eq.getEquipmentID());
		        stmt.executeUpdate();
		    }
		}
		
		// Update equipment status
		public static boolean updateEquipmentStatus(int equipmentID, EquipmentStatus status) {
		    String strSQL = "UPDATE equipment SET equipStatus = ? WHERE equipmentID = ?";
		    try (Connection conn = DBConnect.getInstance().getConnection();
		         PreparedStatement stmt = conn.prepareStatement(strSQL)) {
		        stmt.setString(1, status.name());
		        stmt.setInt(2, equipmentID);
		        int affectedRows = stmt.executeUpdate();
		        return affectedRows > 0;
		    } catch (SQLException e) {
		        e.printStackTrace();
		        return false;
		    }
		}
		
		// Retrieves equipment based on equipmentID from the equipment table in MySQL
		public static Equipment getEquipmentByID(Connection conn, int equipmentID) throws SQLException {
		    String strSQL = "SELECT * FROM equipment WHERE equipmentID = ?";
		    
		    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
		        stmt.setInt(1, equipmentID);
		        
		        try (ResultSet rs = stmt.executeQuery()) {
		            if (rs.next()) {
		                return mapResultSetToEquipment(rs);
		            } else {
		                return null;
		            }
		        }
		    }
		}
		
		// Functions to map a ResultSet row from the equipment table to an equipment object
				public static Equipment mapResultSetToEquipment(ResultSet rs) throws SQLException {
				    int equipmentID = rs.getInt("equipmentID");
				    String equipmentName = rs.getString("equipmentName");
				    
				    // Change string from enum in DB to enum in Java
				    EquipmentCondition equipmentCondition = EquipmentCondition.fromString(rs.getString("equipmentCondition"));
				    SkillClassification requiredSkill = SkillClassification.fromString(rs.getString("requiredSkill"));
				    EquipmentStatus equipmentStatus = EquipmentStatus.fromString(rs.getString("equipStatus"));
				    
				    
				    Equipment equipment = new Equipment(equipmentID, equipmentName, equipmentCondition, equipmentStatus, requiredSkill);
				    
				    return equipment;
				}
				
				// Used to filter ordering equipment by equipment status AND required skill
				// Business rules are implemented where not all employees can order the same equipment
				// This method functions as a safety feature
				public static List<Equipment> getOrderableEquipmentBySkill(Connection conn, SkillClassification skill) throws SQLException {
				    List<Equipment> list = new ArrayList<>();
				    String strSQL = "SELECT * FROM equipment WHERE equipStatus = 'Available' AND requiredSkill = ?";
				    
				    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
				        stmt.setString(1, skill.name());
				        try (ResultSet rs = stmt.executeQuery()) {
				            while (rs.next()) {
				                Equipment equipment = mapResultSetToEquipment(rs);
				                list.add(equipment);
				            }
				        }
				    }
				    return list;
				}
}
