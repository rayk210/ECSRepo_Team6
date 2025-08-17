/**
 * EquipmentStatus.java
 * This enum defines the possible equipment statuses in the ECS system.
 */

package ecsapplication.enums;

public enum EquipmentStatus {

	Loaned,  // Checkout
	Lost,
	Available, // Returned
	Ordered;   // Ordered
	
	// Converts a case-insensative string to the corresponding enum condition
	public static EquipmentStatus fromString(String value) {
	    if (value == null) {
	        return null;
	    }
	    for (EquipmentStatus es : EquipmentStatus.values()) {
	        if (es.name().equalsIgnoreCase(value)) {
	            return es;
	        }
	    }
	    // If no match is found
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}
