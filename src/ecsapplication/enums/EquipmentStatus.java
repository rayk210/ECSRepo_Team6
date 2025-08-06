package ecsapplication.enums;

public enum EquipmentStatus {

	Loaned,
	Lost,
	Available,
	Ordered;
	
	public static EquipmentStatus fromString(String value) {
	    if (value == null) {
	        return null;
	    }
	    for (EquipmentStatus es : EquipmentStatus.values()) {
	        if (es.name().equalsIgnoreCase(value)) {
	            return es;
	        }
	    }
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}
