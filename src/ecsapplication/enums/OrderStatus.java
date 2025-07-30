package ecsapplication.enums;

public enum OrderStatus {

	Pending,
	Confirmed,
	Cancelled;
	
	public static OrderStatus fromString(String value) {
	    if (value == null) {
	        return null;
	    }
	    for (OrderStatus os : OrderStatus.values()) {
	        if (os.name().equalsIgnoreCase(value)) {
	            return os;
	        }
	    }
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}
