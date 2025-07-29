package ecsapplication;

import ecsapplication.enums.SkillClassification;
import ecsapplication.enums.EquipmentCondition;
import ecsapplication.enums.EquipmentStatus;

public class Equipment {

	
	// Attributes
	private int equipmentID;
	private String equipmentName;
	private EquipmentCondition equipmentCondition;
	private EquipmentStatus status;
	private SkillClassification requiredSkill;
	
	// Constructor
	public Equipment(int equipmentID, String equipmentName, EquipmentCondition equipmentCondition,
			EquipmentStatus status, SkillClassification requiredSkill) {
		super();
		this.equipmentID = equipmentID;
		this.equipmentName = equipmentName;
		this.equipmentCondition = equipmentCondition;
		this.status = status;
		this.requiredSkill = requiredSkill;
	}

	// Getters and Setters
	public int getEquipmentID() {
		return equipmentID;
	}

	public void setEquipmentID(int equipmentID) {
		this.equipmentID = equipmentID;
	}

	public String getEquipmentName() {
		return equipmentName;
	}

	public void setEquipmentName(String equipmentName) {
		this.equipmentName = equipmentName;
	}

	public EquipmentCondition getEquipmentCondition() {
		return equipmentCondition;
	}

	public void setEquipmentCondition(EquipmentCondition equipmentCondition) {
		this.equipmentCondition = equipmentCondition;
	}

	public EquipmentStatus getStatus() {
		return status;
	}

	public void setStatus(EquipmentStatus status) {
		this.status = status;
	}

	public SkillClassification getRequiredSkill() {
		return requiredSkill;
	}

	public void setRequiredSkill(SkillClassification requiredSkill) {
		this.requiredSkill = requiredSkill;
	}
	
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