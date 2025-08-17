/**
 * TransactionStatus.java
 * This enum defines the possible transaction statuses in the ECS system.
 */

package ecsapplication.enums;

public enum TransactionStatus {

	Borrowed,  // when an employee checks out equipment
    Returned,  // when an employee returns equipment
    Late,      // when the due date for returning borrowed equipment has passed
    Cancelled; 
    
	// Converts a case-insensative string to the corresponding enum condition
    public static TransactionStatus fromString(String value) {
    	
    	if (value == null) {
	        return null;
	    }
	    for (TransactionStatus ts : TransactionStatus.values()) {
	        if (ts.name().equalsIgnoreCase(value)) {
	            return ts;
	        }
	    }
	 // If no match is found
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}