package service;

/**
 * ============================================================================
 * Project Title : Hospital Management System
 * Group         : Group 11
 * Subject       : Object-Oriented Programming (OOP) Lab Mini-Project
 * ============================================================================
 * [OOP PRINCIPLE: VALUE OBJECT & ENCAPSULATION]
 * ValidationResult encapsulates the result of an input validation operation,
 * carrying validation status and an explanatory error message if invalid.
 */
public class ValidationResult {
    private final boolean valid;
    private final String message;

    private ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult error(String message) {
        return new ValidationResult(false, message);
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }
}
