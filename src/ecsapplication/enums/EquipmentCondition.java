package ecsapplication.enums;

public enum EquipmentCondition {

	Good,
	Damaged,
	Lost;
	
	public static EquipmentCondition fromString(String value) {
	    if (value == null) {
	        return null;
	    }
	    for (EquipmentCondition ec : EquipmentCondition.values()) {
	        if (ec.name().equalsIgnoreCase(value)) {
	            return ec;
	        }
	    }
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}
