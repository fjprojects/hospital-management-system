package model;

import java.sql.Timestamp;

/**
 * ============================================================================
 * Project Title : Hospital Management System
 * Group         : Group 11
 * Subject       : Object-Oriented Programming (OOP) Lab Mini-Project
 * ============================================================================
 * [OOP PRINCIPLE: INHERITANCE & ENCAPSULATION]
 * The Patient class models a patient entity within the hospital system.
 * 
 * Key OOP Concepts Demonstrated:
 *  - Inheritance: Inherits common demographic fields (name, age) from Person
 *    via 'extends Person' and reuses base initialization via 'super(...)'.
 *  - Polymorphism: Overrides the abstract getRole() method defined in Person.
 *  - Encapsulation: Attributes are declared private with public getters/setters.
 *  - Constructors: Overloaded constructors for different lifecycles (creation vs. retrieval).
 *  - Object Serialization: Implements Serializable to maintain backward-compatibility
 *    with existing 'patients.dat' files.
 */
public class Patient extends Person {
    private static final long serialVersionUID = 1L;

    // Encapsulated private attributes specific to Patient
    private int patientId;
    private String condition;
    private Timestamp createdAt;

    /**
     * Default constructor.
     */
    public Patient() {
        super();
    }

    /**
     * Constructor for creating a new patient record before ID assignment.
     * 
     * @param name      Full name of the patient
     * @param age       Age in years
     * @param condition Medical condition / illness description
     */
    public Patient(String name, int age, String condition) {
        super(name, age);
        this.condition = condition;
    }

    /**
     * Constructor matching the original legacy format (ID, name, age, condition).
     * 
     * @param patientId Unique ID
     * @param name      Full name
     * @param age       Age in years
     * @param condition Medical condition / illness
     */
    public Patient(int patientId, String name, int age, String condition) {
        super(name, age);
        this.patientId = patientId;
        this.condition = condition;
    }

    /**
     * Full constructor including database audit timestamp.
     * 
     * @param patientId Unique database identifier
     * @param name      Full name
     * @param age       Age in years
     * @param condition Medical condition / illness
     * @param createdAt Creation timestamp
     */
    public Patient(int patientId, String name, int age, String condition, Timestamp createdAt) {
        super(name, age);
        this.patientId = patientId;
        this.condition = condition;
        this.createdAt = createdAt;
    }

    // --- Public Getters and Setters (Encapsulation) ---

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * [OOP PRINCIPLE: POLYMORPHISM / OVERRIDING]
     * Concrete implementation of abstract method from Person.
     */
    @Override
    public String getRole() {
        return "Patient";
    }

    @Override
    public String toString() {
        return String.format("ID: %-5d | Name: %-20s | Age: %-3d | Condition: %-20s | Registered: %s",
                patientId, getName(), getAge(), condition,
                (createdAt != null ? createdAt.toString() : "N/A"));
    }
}
