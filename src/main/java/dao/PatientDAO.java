package dao;

import model.Patient;
import java.sql.SQLException;
import java.util.List;

/**
 * ============================================================================
 * Project Title : Hospital Management System
 * Group         : Group 11
 * Subject       : Object-Oriented Programming (OOP) Lab Mini-Project
 * ============================================================================
 * [OOP PRINCIPLE: ABSTRACTION & INTERFACE DESIGN]
 * PatientDAO defines the abstract contract for patient persistence operations.
 * 
 * Benefits:
 *  - Abstraction: Defines 'what' persistence operations exist without specifying
 *    'how' they are executed (decoupling from MySQL, file storage, or mock test doubles).
 *  - Loose Coupling: Higher architectural layers (Service and UI) depend solely
 *    on this interface rather than concrete SQL implementation details.
 */
public interface PatientDAO {

    /**
     * Inserts a new patient record into persistent storage.
     * 
     * @param patient Patient object containing name, age, and condition
     * @return the auto-generated primary key ID assigned to the patient
     * @throws SQLException if a database access error occurs
     */
    int addPatient(Patient patient) throws SQLException;

    /**
     * Retrieves all patient records ordered by patient_id.
     * 
     * @return List of Patient entities
     * @throws SQLException if a database access error occurs
     */
    List<Patient> getAllPatients() throws SQLException;

    /**
     * Finds a single patient record by their unique ID.
     * 
     * @param patientId The unique patient ID
     * @return Patient object if found; null otherwise
     * @throws SQLException if a database access error occurs
     */
    Patient getPatientById(int patientId) throws SQLException;

    /**
     * Updates an existing patient record's name, age, and condition.
     * 
     * @param patient Patient object with updated data and existing patientId
     * @return true if one record was updated; false if not found
     * @throws SQLException if a database access error occurs
     */
    boolean updatePatient(Patient patient) throws SQLException;

    /**
     * Deletes a patient record by ID.
     * 
     * @param patientId The unique patient ID to remove
     * @return true if the record was deleted; false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean deletePatient(int patientId) throws SQLException;

    /**
     * Checks if a patient exists with the given ID.
     * 
     * @param patientId The patient ID
     * @return true if present; false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean patientExists(int patientId) throws SQLException;

    /**
     * Checks if a patient record with identical name, age, and condition already exists.
     * Used to prevent duplicate entries during migration from legacy files.
     * 
     * @param name      Full name
     * @param age       Age
     * @param condition Medical condition
     * @return true if an identical record is present; false otherwise
     * @throws SQLException if a database access error occurs
     */
    boolean patientExistsByDetails(String name, int age, String condition) throws SQLException;
}
