package ecsapplication.enums;

public enum TransactionStatus {

	Borrowed,
    Returned,
    Late,
    Cancelled;
    
    public static TransactionStatus fromString(String value) {
    	
    	if (value == null) {
	        return null;
	    }
	    for (TransactionStatus ts : TransactionStatus.values()) {
	        if (ts.name().equalsIgnoreCase(value)) {
	            return ts;
	        }
	    }
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}