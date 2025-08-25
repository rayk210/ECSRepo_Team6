/**
 * SkillClassification.java
 * This enum defines the possible employee skill classifications in the ECS system.
 * These classifications are used for equipment filtering during the checkout
 * and ordering processes so that an employee can only obtain equipment that is
 * suitable for their area of expertise.
 */

package ecsapplication.enums;

public enum SkillClassification {
	
	Electrician,     // Employee with electrical skills
	Plumber,         // Employee with plumbing skills
	Painter,         // Employee with painting skills
	Welder,          // Employee with welding skills
	Carpenter;       // Employee with carpentry skills
	
	// Converts a case-insensative string to the corresponding SkillClassification enum
	public static SkillClassification fromString(String value) {
		
		// Return null if input string is null
	    if (value == null) {
	        return null;
	    }
	    
	    // Iterate through all enum constants
	    for (SkillClassification sc : SkillClassification.values()) {
	    	
	    	// Compare ignoring case; return the matching enum
	        if (sc.name().equalsIgnoreCase(value)) {
	            return sc;
	        }
	    }
	    // If no match is found, throw an exception
	    throw new IllegalArgumentException("No enum constant for: " + value);
	}
}


