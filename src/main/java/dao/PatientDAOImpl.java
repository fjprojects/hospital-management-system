package dao;

import database.DatabaseConnection;
import model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * Project Title : Hospital Management System
 * Group         : Group 11
 * Subject       : Object-Oriented Programming (OOP) Lab Mini-Project
 * ============================================================================
 * [OOP PRINCIPLE: CONCRETE DAO IMPLEMENTATION & JDBC BEST PRACTICES]
 * PatientDAOImpl implements PatientDAO using JDBC and MySQL 8+.
 * 
 * Best Practices Followed:
 *  - PreparedStatement: Used for all queries to prevent SQL Injection and optimize execution.
 *  - Reserved Keyword Safety: Encapsulates column `condition` in backticks for MySQL 8+ syntax safety.
 *  - Try-with-resources: Ensures Connection, PreparedStatement, and ResultSet are closed deterministically.
 *  - Auto-Increment Key Retrieval: Extracts generated patient_id via Statement.RETURN_GENERATED_KEYS.
 */
public class PatientDAOImpl implements PatientDAO {

    // SQL Statements (Parameterized with '?' placeholders)
    // Note: `condition` is enclosed in backticks because CONDITION is a reserved keyword in MySQL 8+.
    private static final String SQL_INSERT =
            "INSERT INTO patients (name, age, `condition`) VALUES (?, ?, ?)";

    private static final String SQL_SELECT_ALL =
            "SELECT patient_id, name, age, `condition`, created_at FROM patients ORDER BY patient_id ASC";

    private static final String SQL_SELECT_BY_ID =
            "SELECT patient_id, name, age, `condition`, created_at FROM patients WHERE patient_id = ?";

    private static final String SQL_UPDATE =
            "UPDATE patients SET name = ?, age = ?, `condition` = ? WHERE patient_id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM patients WHERE patient_id = ?";

    private static final String SQL_EXISTS_BY_ID =
            "SELECT 1 FROM patients WHERE patient_id = ? LIMIT 1";

    private static final String SQL_EXISTS_BY_DETAILS =
            "SELECT 1 FROM patients WHERE LOWER(TRIM(name)) = LOWER(TRIM(?)) AND age = ? AND LOWER(TRIM(`condition`)) = LOWER(TRIM(?)) LIMIT 1";

    @Override
    public int addPatient(Patient patient) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, patient.getName().trim());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getCondition().trim());

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating patient failed, no rows affected.");
            }

            // Retrieve generated primary key
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    patient.setPatientId(generatedId);
                    return generatedId;
                } else {
                    throw new SQLException("Creating patient failed, no ID obtained.");
                }
            }
        }
    }

    @Override
    public List<Patient> getAllPatients() throws SQLException {
        List<Patient> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToPatient(rs));
            }
        }
        return list;
    }

    @Override
    public Patient getPatientById(int patientId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPatient(rs);
                }
            }
        }
        return null;
    }

    @Override
    public boolean updatePatient(Patient patient) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, patient.getName().trim());
            ps.setInt(2, patient.getAge());
            ps.setString(3, patient.getCondition().trim());
            ps.setInt(4, patient.getPatientId());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean deletePatient(int patientId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, patientId);
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean patientExists(int patientId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_ID)) {

            ps.setInt(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    @Override
    public boolean patientExistsByDetails(String name, int age, String condition) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_BY_DETAILS)) {

            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, condition);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Helper method to map a ResultSet row to a Patient domain object.
     */
    private Patient mapResultSetToPatient(ResultSet rs) throws SQLException {
        int id = rs.getInt("patient_id");
        String name = rs.getString("name");
        int age = rs.getInt("age");
        String condition = rs.getString("condition");
        Timestamp createdAt = rs.getTimestamp("created_at");

        return new Patient(id, name, age, condition, createdAt);
    }
}
