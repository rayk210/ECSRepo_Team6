/**
 * OrderStatus.java
 * This enum defines the possible order statuses in the ECS system.
 */

package ecsapplication.enums;

public enum OrderStatus {

	Pending,
	Confirmed,  // Confirming an order
	Cancelled;  // Canceling an order
	
	// Converts a case-insensative string to the corresponding enum condition
	public static OrderStatus fromString(String value) {
	    if (value == null) {
	        return null;
	    }
	    for (OrderStatus os : OrderStatus.values()) {
	        if (os.name().equalsIgnoreCase(value)) {
	            return os;
	        }
	    }
	    // If no match is found
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}
