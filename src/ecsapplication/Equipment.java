/**
 * Equipment.java
 * This class represents an equipment entity in the ECS system.
 * It is responsible for storing important information about equipment
 * such as ID, name, condition, status, and the skill required to use it.
 * The Equipment class also facilitates the process of checking out, 
 * returning, ordering, and tracking of the equipment.
 */

package ecsapplication;

// Import enumerations for equipment condition, status, and required skill
import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;

public class Equipment {

	// Attributes
	private int equipmentID;        // Unique ID of the equipment
	private String equipmentName;   // Name of the equipment
	private EquipmentCondition equipmentCondition;  // Equipment condition (e.g., Good, Damaged)
	private EquipmentStatus status; // Status for equipment (e.g., Loaned, Available)
	private SkillClassification requiredSkill;      // Skill required to use equipment
	
	// Constructors
	// Minimal constructor 
	public Equipment(int equipmentID, String equipmentName) {
	    this.equipmentID = equipmentID;
	    this.equipmentName = equipmentName;
	}
	
	// Constructor with required skill and equipment condition
	public Equipment(int equipmentID, String equipmentName, SkillClassification requiredSkill,EquipmentCondition equipmentCondition) {
		this.equipmentID = equipmentID;
		this.equipmentName = equipmentName;
		this.requiredSkill = requiredSkill;
		this.equipmentCondition = equipmentCondition;
	}
	
	// Constructor with status and required skill
	public Equipment(int equipmentID, String equipmentName, EquipmentStatus status, SkillClassification requiredSkill) {
	    this.equipmentID = equipmentID;
	    this.equipmentName = equipmentName;
	    this.status = status;
	    this.requiredSkill = requiredSkill;
	}

	// Full constructor with all attributes
	public Equipment(int equipmentID, String equipmentName, EquipmentCondition equipmentCondition,
			EquipmentStatus status, SkillClassification requiredSkill) {
		super();
		this.equipmentID = equipmentID;
		this.equipmentName = equipmentName;
		this.equipmentCondition = equipmentCondition;
		this.status = status;
		this.requiredSkill = requiredSkill;
	}
	
	// Constructor with condition and status
	public Equipment(int equipmentID, String equipmentName, EquipmentCondition equipmentCondition, EquipmentStatus status) {
		this.equipmentID = equipmentID;
		this.equipmentName = equipmentName;
		this.equipmentCondition = equipmentCondition;
		this.status = status;
	}

	// Getters and Setters
	// Get equipment ID
	public int getEquipmentID() {
		return equipmentID;
	}
	
	// Set equipment ID
	public void setEquipmentID(int equipmentID) {
		this.equipmentID = equipmentID;
	}

	// Get equipment name
	public String getEquipmentName() {
		return equipmentName;
	}

	// Set equipment name
	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	// Get equipment condition
	public EquipmentCondition getEquipmentCondition() {
		return equipmentCondition;
	}

	// Set equipment condition
	public void setEquipmentCondition(EquipmentCondition equipmentCondition) {
		this.equipmentCondition = equipmentCondition;
	}

	// Get status
	public EquipmentStatus getStatus() {
		return status;
	}

	// Set status
	public void setStatus(EquipmentStatus status) {
		this.status = status;
	}

	// Get required skill
	public SkillClassification getRequiredSkill() {
		return requiredSkill;
	}

	// Set required skill
	public void setRequiredSkill(SkillClassification requiredSkill) {
		this.requiredSkill = requiredSkill;
	}
	
	// Provides a readable string representation of equipment objects
	@Override
	public String toString() {
		return "Equipment{" +
                "equipmentID=" + equipmentID +
                ", equipmentName='" + equipmentName + '\'' +
                ", equipmentCondition=" + equipmentCondition +
                ", status=" + status +
                ", skillClassification=" + requiredSkill +
                '}';
	}
}	