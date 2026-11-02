package service;

import dao.PatientDAO;
import dao.PatientDAOImpl;
import model.Patient;
import util.LegacyPatientFileManager;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

/**
 * ============================================================================
 * Project Title : Hospital Management System
 * Group         : Group 11
 * Subject       : Object-Oriented Programming (OOP) Lab Mini-Project
 * ============================================================================
 * [OOP PRINCIPLE: SERVICE LAYER & SEPARATION OF CONCERNS]
 * PatientService acts as the intermediary between the UI presentation layer
 * and the persistence DAO layer, executing business rules, validations, and
 * transactional migrations.
 */
public class PatientService {

    private final PatientDAO patientDAO;

    /**
     * Default constructor wiring the standard MySQL PatientDAOImpl.
     */
    public PatientService() {
        this.patientDAO = new PatientDAOImpl();
    }

    /**
     * Constructor allowing Dependency Injection (useful for testing or alternative DAOs).
     * 
     * @param patientDAO Concrete DAO implementation
     */
    public PatientService(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    /**
     * Validates raw patient input data before processing.
     * 
     * @param name      Patient name string
     * @param ageStr    Patient age string
     * @param condition Illness / condition string
     * @return ValidationResult indicating success or descriptive error message
     */
    public ValidationResult validatePatientInput(String name, String ageStr, String condition) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error("Patient Name cannot be blank.");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() < 2) {
            return ValidationResult.error("Patient Name must contain at least 2 characters.");
        }

        if (trimmedName.length() > 100) {
            return ValidationResult.error("Patient Name cannot exceed 100 characters.");
        }

        // Allow letters, spaces, hyphens, periods, and apostrophes in patient names
        if (!trimmedName.matches("^[a-zA-Z\\s\\-'.]+$")) {
            return ValidationResult.error("Patient Name contains invalid characters. Please use alphabets only.");
        }

        if (ageStr == null || ageStr.trim().isEmpty()) {
            return ValidationResult.error("Patient Age cannot be blank.");
        }

        int age;
        try {
            age = Integer.parseInt(ageStr.trim());
        } catch (NumberFormatException e) {
            return ValidationResult.error("Patient Age must be a valid integer number.");
        }

        if (age < 1 || age > 125) {
            return ValidationResult.error("Please enter a realistic age between 1 and 125 years.");
        }

        if (condition == null || condition.trim().isEmpty()) {
            return ValidationResult.error("Medical Condition / Illness cannot be blank.");
        }

        String trimmedCondition = condition.trim();
        if (trimmedCondition.length() < 2) {
            return ValidationResult.error("Condition description must contain at least 2 characters.");
        }

        if (trimmedCondition.length() > 255) {
            return ValidationResult.error("Condition description cannot exceed 255 characters.");
        }

        return ValidationResult.success();
    }

    /**
     * Registers a new patient in the database.
     * 
     * @param name      Full name
     * @param age       Age in years
     * @param condition Medical condition
     * @return Generated patient ID
     * @throws SQLException on database error
     */
    public int registerPatient(String name, int age, String condition) throws SQLException {
        Patient patient = new Patient(name.trim(), age, condition.trim());
        return patientDAO.addPatient(patient);
    }

    /**
     * Retrieves all registered patients.
     */
    public List<Patient> getAllPatients() throws SQLException {
        return patientDAO.getAllPatients();
    }

    /**
     * Finds a patient by primary key ID.
     */
    public Patient getPatientById(int id) throws SQLException {
        if (id <= 0) {
            return null;
        }
        return patientDAO.getPatientById(id);
    }

    /**
     * Updates an existing patient record.
     */
    public boolean updatePatient(int id, String name, int age, String condition) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid patient ID: " + id);
        }
        Patient patient = new Patient(id, name.trim(), age, condition.trim());
        return patientDAO.updatePatient(patient);
    }

    /**
     * Deletes a patient record by ID.
     */
    public boolean deletePatient(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid patient ID: " + id);
        }
        return patientDAO.deletePatient(id);
    }

    /**
     * [DATA MIGRATION UTILITY]
     * Safely imports records from a legacy 'patients.dat' file into the MySQL database.
     * Prevents duplication by checking if an equivalent record already exists.
     * 
     * @param file The legacy .dat file (or null for default 'patients.dat')
     * @return MigrationSummary detailing total records, imported count, and skipped duplicates
     */
    public MigrationSummary importFromLegacyFile(File file) throws SQLException {
        File targetFile = (file != null) ? file : new File(LegacyPatientFileManager.DEFAULT_FILE_NAME);
        if (!targetFile.exists()) {
            return new MigrationSummary(0, 0, 0, "File '" + targetFile.getName() + "' not found.");
        }

        List<Patient> legacyPatients = LegacyPatientFileManager.loadPatients(targetFile);
        if (legacyPatients.isEmpty()) {
            return new MigrationSummary(0, 0, 0, "No records found in '" + targetFile.getName() + "'.");
        }

        int imported = 0;
        int skipped = 0;

        for (Patient p : legacyPatients) {
            if (p.getName() == null || p.getName().trim().isEmpty()) {
                continue;
            }

            // Check if patient already exists by name, age, and condition
            boolean exists = patientDAO.patientExistsByDetails(p.getName(), p.getAge(), p.getCondition());
            if (exists) {
                skipped++;
            } else {
                Patient newPatient = new Patient(p.getName(), p.getAge(), p.getCondition());
                patientDAO.addPatient(newPatient);
                imported++;
            }
        }

        return new MigrationSummary(legacyPatients.size(), imported, skipped, "Migration completed successfully.");
    }

    /**
     * Value object encapsulating migration outcome metrics.
     */
    public static class MigrationSummary {
        private final int totalFound;
        private final int importedCount;
        private final int skippedCount;
        private final String message;

        public MigrationSummary(int totalFound, int importedCount, int skippedCount, String message) {
            this.totalFound = totalFound;
            this.importedCount = importedCount;
            this.skippedCount = skippedCount;
            this.message = message;
        }

        public int getTotalFound() {
            return totalFound;
        }

        public int getImportedCount() {
            return importedCount;
        }

        public int getSkippedCount() {
            return skippedCount;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return String.format("Found: %d | Imported: %d | Skipped (duplicates): %d",
                    totalFound, importedCount, skippedCount);
        }
    }
}
