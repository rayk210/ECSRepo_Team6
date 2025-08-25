/**
 * TransactionStatus.java
 * This enum defines the possible transaction statuses in the ECS system.
 */

package ecsapplication.enums;

public enum TransactionStatus {

	Borrowed,   // Equipment has been checked out by an employee
    Returned,   // Equipment has been returned by an employee
    Late,       // Borrowed equipment has passed its expected return date
    Cancelled;  // Transaction was cancelled
    
	// Converts a case-insensative string to the corresponding TransactionStatus enum
    public static TransactionStatus fromString(String value) {
    	
    	// Return null if input string is null
    	if (value == null) {
	        return null;
	    }
    	
    	// Iterate through all enum constants
	    for (TransactionStatus ts : TransactionStatus.values()) {
	    	
	    	// Compare ignoring case; return the matching enum
	        if (ts.name().equalsIgnoreCase(value)) {
	            return ts;
	        }
	    }
	 // If no match is found, throw an exception
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}