/**
 * SkillClassification.java
 * This enum defines the possible employee skill classifications in the ECS system.
 * These classifications are used for equipment filtering during the checkout
 * and ordering processes so that an employee can only obtain equipment that is
 * suitable for their area of expertise.
 */

package ecsapplication.enums;

public enum SkillClassification {
	
	Electrician,
	Plumber,
	Painter,
	Welder,
	Carpenter;
	
	// Converts a case-insensative string to the corresponding enum condition
	public static SkillClassification fromString(String value) {
	    if (value == null) {
	        return null;
	    }
	    for (SkillClassification sc : SkillClassification.values()) {
	        if (sc.name().equalsIgnoreCase(value)) {
	            return sc;
	        }
	    }
	    // If no match is found
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}


