package model;

import java.io.Serializable;

/**
 * ============================================================================
 * Project Title : Hospital Management System
 * Group         : Group 11
 * Subject       : Object-Oriented Programming (OOP) Lab Mini-Project
 * ============================================================================
 * [OOP PRINCIPLE: ABSTRACTION & BASE CLASS]
 * The abstract Person class encapsulates core attributes common to any person
 * within a hospital domain (patients, doctors, staff).
 * 
 * Key OOP Concepts Demonstrated:
 *  - Abstraction: Abstract class cannot be instantiated directly and defines
 *    the abstract contract getRole() to be implemented by concrete subclasses.
 *  - Encapsulation: Attributes are private with controlled public accessors.
 *  - Constructors: Overloaded constructors to support various initialization modes.
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulated private attributes
    private String name;
    private int age;

    /**
     * Default constructor.
     */
    public Person() {
    }

    /**
     * Parameterized constructor.
     * 
     * @param name Full name of the person
     * @param age  Age in years
     */
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // --- Public Getters and Setters (Controlled Access / Encapsulation) ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    /**
     * [OOP PRINCIPLE: ABSTRACT METHOD]
     * Returns the role/classification of the person within the hospital system.
     * Must be implemented by concrete subclasses.
     * 
     * @return String representation of the role (e.g., "Patient", "Doctor")
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("Name: %-20s | Age: %-3d | Role: %s", name, age, getRole());
    }
}
