/**
 * EquipmentCondition.java
 * This enum defines the possible conditions of equipment in the ECS system.
 * The employee is prompt to select a state upon returning equipment.
 */

package ecsapplication.enums;

public enum EquipmentCondition {

	Good,
	Damaged,
	Lost;
	
	// Converts a case-insensative string to the corresponding enum condition
	public static EquipmentCondition fromString(String value) {
	    if (value == null) {
	        return null;
	    }
	    for (EquipmentCondition ec : EquipmentCondition.values()) {
	        if (ec.name().equalsIgnoreCase(value)) {
	            return ec;
	        }
	    }
	    // If no match is found
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}
