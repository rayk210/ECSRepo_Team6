/**
 * OrderStatus.java
 * This enum defines the possible order statuses in the ECS system.
 */

package ecsapplication.enums;

public enum OrderStatus {

	Pending,    // Order has been placed but not yet confirmed
	Confirmed,  // Order has been confirmed
	Cancelled;  // Order has been cancelled by employee
	
	// Converts a case-insensative string to the corresponding OrderStatus enum
	public static OrderStatus fromString(String value) {
		
		// Return null if input string is null
	    if (value == null) {
	        return null;
	    }
	    
	    // Iterate through all enum constants
	    for (OrderStatus os : OrderStatus.values()) {
	    	
	    	// Compare ignoring case; return the matching enum
	        if (os.name().equalsIgnoreCase(value)) {
	            return os;
	        }
	    }
	    // If no match is found, throw an exception
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}
