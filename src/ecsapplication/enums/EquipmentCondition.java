/**
 * EquipmentCondition.java
 * This enum defines the possible conditions of equipment in the ECS system.
 * The employee is prompt to select a state upon returning equipment.
 */

package ecsapplication.enums;

public enum EquipmentCondition {

	Good,      // Equipment is in good condition
	Damaged,   // Equipment is damaged
	Lost;      // Equipment is lost
	
	// Converts a case-insensative string to the corresponding EquipmentCondition enum
	public static EquipmentCondition fromString(String value) {
		
		// If input string is null, return null
	    if (value == null) {
	        return null;
	    }
	    
	    // Iterate through all enum constants
	    for (EquipmentCondition ec : EquipmentCondition.values()) {
	    	
	    	// Compare ignoring case; return the matching enum
	        if (ec.name().equalsIgnoreCase(value)) {
	            return ec;
	        }
	    }
	    // If no match is found, throw an exception
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}
