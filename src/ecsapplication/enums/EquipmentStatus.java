/**
 * EquipmentStatus.java
 * This enum defines the possible equipment statuses in the ECS system.
 */

package ecsapplication.enums;

public enum EquipmentStatus {

	Loaned,    // Equipment is currently checked out
	Lost,      // Equipment is lost
	Available, // Equipment is returned and ready for use
	Ordered;   // Equipment has been ordered
	
	// Converts a case-insensative string to the corresponding EquipmentStatus enum
	public static EquipmentStatus fromString(String value) {
		
		// If input string is null, return null
	    if (value == null) {
	        return null;
	    }
	    
	    // Iterate through all enum constants
	    for (EquipmentStatus es : EquipmentStatus.values()) {
	    	
	    	// Compare ignoring case; return the matching enum
	        if (es.name().equalsIgnoreCase(value)) {
	            return es;
	        }
	    }
	    // If no match is found, throw an exception
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}
