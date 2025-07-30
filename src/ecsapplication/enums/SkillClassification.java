package ecsapplication.enums;

public enum SkillClassification {
	
	Electrician,
	Plumber,
	Painter,
	Welder,
	Carpenter;
	
	public static SkillClassification fromString(String value) {
	    if (value == null) {
	        return null;
	    }
	    for (SkillClassification sc : SkillClassification.values()) {
	        if (sc.name().equalsIgnoreCase(value)) {
	            return sc;
	        }
	    }
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}


