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

	// Get all equipment
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
		public static List<Equipment> getAvailableEquipmentBySkill(Connection conn, SkillClassification skill) throws SQLException {
		    List<Equipment> equipmentList = new ArrayList<>();
		    String strSQL = "SELECT equipmentID, equipmentName, equipmentCondition, equipStatus, requiredSkill " +
		                 "FROM equipment WHERE equipStatus = 'Available' AND requiredSkill = ?";
		    
		    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
		    	
		    	// debugging
		    	// verify skill and if sql query is executed properly through console log
		    	System.out.println("Debug - Skill: " + skill.toString());
		    	System.out.println("Debug - Running SQL: " + strSQL);

		        stmt.setString(1, skill.toString());
		        ResultSet rs = stmt.executeQuery();
		        
		        boolean isFound = false;
		        while (rs.next()) {
		        	isFound = true;
		        	System.out.println("Debug - Equipment Found: " + rs.getString("equipmentName"));
		            Equipment eq = new Equipment(
		                rs.getInt("equipmentID"),
		                rs.getString("equipmentName"),
		                EquipmentCondition.valueOf(rs.getString("equipmentCondition")),
		                EquipmentStatus.valueOf(rs.getString("equipStatus")),
		                SkillClassification.valueOf(rs.getString("requiredSkill"))
		            );
		            equipmentList.add(eq);
		        }
		        if (!isFound) {
		        	System.out.println("Debug - No equipment found for skill: " + skill.toString());
		        }
		    }
		    return equipmentList;
		}
		
		// update equipment status and equipment condition
		public static void updateEquipment(Connection conn, Equipment eq) throws SQLException {
		    String strSQL = "UPDATE equipment SET equipStatus = ?, equipmentCondition = ? WHERE equipmentID = ?";

		    try (PreparedStatement stmt = conn.prepareStatement(strSQL)) {
		        stmt.setString(1, eq.getStatus().name());        // ENUM to string
		        stmt.setString(2, eq.getEquipmentCondition().name());   // LocalDate to SQL date
		        stmt.setInt(3, eq.getEquipmentID());
		        stmt.executeUpdate();
		    }
		}
		
		// update equipment status
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
		
		// gets equipment by ID
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
		
		// functions to map a ResultSet row from the equipment table to an equipment object
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
				
				// used to filter ordering equipment by equipment status AND required skill
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
