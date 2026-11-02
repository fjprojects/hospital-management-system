import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * Project Title : Hospital Management System
 * Group         : Group 11
 * Subject       : Object-Oriented Programming (OOP) Lab Mini-Project
 * Concepts Used : OOP Principles (Encapsulation, Classes & Objects),
 *                 Java Swing GUI, File Handling (Object Serialization)
 * ============================================================================
 */

// ============================================================================
// 1. [OOP PRINCIPLE: MODEL CLASS & ENCAPSULATION]
// ============================================================================
/**
 * The Patient class encapsulates all data related to a single patient.
 * Encapsulation is achieved by:
 *  - Declaring member variables as 'private'.
 *  - Providing public 'getters' and 'setters' to access and mutate the state.
 *  - Implementing Serializable to allow object state to be saved to a binary file.
 */
class Patient implements Serializable {
    // serialVersionUID ensures compatibility during deserialization
    private static final long serialVersionUID = 1L;

    // Private fields (Data Hiding / Encapsulation)
    private int patientId;
    private String name;
    private int age;
    private String condition;

    // Parameterized Constructor
    public Patient(int patientId, String name, int age, String condition) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.condition = condition;
    }

    // --- Public Getters and Setters (Controlled Access) ---
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    // Helper method to format object data for text display
    @Override
    public String toString() {
        return String.format("ID: %-5d | Name: %-20s | Age: %-3d | Condition: %s",
                patientId, name, age, condition);
    }
}


// ============================================================================
// 2. [OOP PRINCIPLE: SEPARATION OF CONCERNS / FILE HANDLING CONTROLLER]
// ============================================================================
/**
 * PatientFileManager manages persistent storage operations using Java Object Serialization.
 * This class abstracts file I/O operations away from the GUI layer.
 */
class PatientFileManager {
    private static final String FILE_NAME = "patients.dat";

    /**
     * [FILE HANDLING: DESERIALIZATION]
     * Reads and returns the list of Patient objects saved in 'patients.dat'.
     * If the file does not exist yet, returns an empty ArrayList.
     */
    @SuppressWarnings("unchecked")
    public static List<Patient> loadPatients() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            
            // Read serialized list of Patient objects from file
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                return (List<Patient>) obj;
            }
        } catch (EOFException e) {
            // File is empty; return empty list
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error reading records from file: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * [FILE HANDLING: SERIALIZATION]
     * Appends a new Patient object to the persistent store and writes back to disk.
     */
    public static boolean savePatient(Patient newPatient) {
        List<Patient> patientList = loadPatients();
        patientList.add(newPatient);

        try (FileOutputStream fos = new FileOutputStream(FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            // Write entire updated list of Patient objects to binary file
            oos.writeObject(patientList);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving record to file: " + e.getMessage());
            return false;
        }
    }

    /**
     * Helper to generate a continuous auto-incremented Patient ID
     */
    public static int getNextPatientId() {
        List<Patient> patientList = loadPatients();
        if (patientList.isEmpty()) {
            return 101; // Starting ID
        }
        return patientList.get(patientList.size() - 1).getPatientId() + 1;
    }
}


// ============================================================================
// 3. [GUI IMPLEMENTATION: JAVA SWING FRAME]
// ============================================================================
/**
 * Main application window for Hospital Management System.
 * Inherits from JFrame to create the graphical interface.
 */
public class HospitalManagementSystem extends JFrame {

    // [GUI COMPONENTS] Input Fields
    private JTextField txtName;
    private JTextField txtAge;
    private JTextField txtCondition;

    // [GUI COMPONENTS] Action Buttons
    private JButton btnRegister;
    private JButton btnViewRecords;
    private JButton btnClearFields;

    // [GUI COMPONENTS] Record Display (Table & Scroll Pane)
    private JTable recordTable;
    private DefaultTableModel tableModel;

    // Constructor to configure and build the Swing GUI
    public HospitalManagementSystem() {
        // [FACULTY REQUIREMENT: Exact Window Title]
        super("Hospital Management System - Group 11");

        // Window configuration
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null); // Center on screen

        // Initialize UI components and layout
        initComponents();

        // Load existing records on startup
        loadRecordsIntoTable();
    }

    private void initComponents() {
        // Set layout manager
        setLayout(new BorderLayout(10, 10));

        // -------------------------------------------------------------
        // Header Banner Panel
        // -------------------------------------------------------------
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(24, 76, 120));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 60));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));

        JLabel lblTitle = new JLabel("HOSPITAL MANAGEMENT SYSTEM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblGroup = new JLabel("(Group 11 - OOP Lab Mini-Project)");
        lblGroup.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblGroup.setForeground(new Color(210, 230, 250));

        headerPanel.add(lblTitle);
        headerPanel.add(lblGroup);
        add(headerPanel, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // Main Content Panel (Split into Form and Display)
        // -------------------------------------------------------------
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // --- Registration Form Panel (West) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " Patient Registration ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(24, 76, 120)
        ));
        formPanel.setPreferredSize(new Dimension(320, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Label & Field: Patient Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridy = 1;
        txtName = new JTextField();
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtName, gbc);

        // Label & Field: Age
        gbc.gridy = 2;
        formPanel.add(new JLabel("Age (Years):"), gbc);
        gbc.gridy = 3;
        txtAge = new JTextField();
        txtAge.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtAge, gbc);

        // Label & Field: Illness / Condition
        gbc.gridy = 4;
        formPanel.add(new JLabel("Illness / Condition:"), gbc);
        gbc.gridy = 5;
        txtCondition = new JTextField();
        txtCondition.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtCondition, gbc);

        // Button Panel inside Form
        JPanel formButtonPanel = new JPanel(new GridLayout(2, 1, 8, 8));
        btnRegister = new JButton("Register Patient");
        btnRegister.setBackground(new Color(34, 139, 34)); // Forest green
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnClearFields = new JButton("Clear Form");
        btnClearFields.setBackground(new Color(108, 117, 125)); // Gray
        btnClearFields.setForeground(Color.WHITE);
        btnClearFields.setFocusPainted(false);
        btnClearFields.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        formButtonPanel.add(btnRegister);
        formButtonPanel.add(btnClearFields);

        gbc.gridy = 6;
        gbc.insets = new Insets(18, 8, 8, 8);
        formPanel.add(formButtonPanel, gbc);

        // Filler to keep controls at top
        gbc.gridy = 7;
        gbc.weighty = 1.0;
        formPanel.add(new JLabel(), gbc);

        contentPanel.add(formPanel, BorderLayout.WEST);

        // --- Records Display Area (Center) ---
        JPanel recordsPanel = new JPanel(new BorderLayout(5, 5));
        recordsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " Saved Patient Records (File Storage) ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(24, 76, 120)
        ));

        // JTable for structured presentation of records
        String[] columnHeaders = {"Patient ID", "Name", "Age", "Illness / Condition"};
        tableModel = new DefaultTableModel(columnHeaders, 0) {
            // Make table cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recordTable = new JTable(tableModel);
        recordTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recordTable.setRowHeight(24);
        recordTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        recordTable.getTableHeader().setBackground(new Color(230, 240, 250));

        JScrollPane scrollPane = new JScrollPane(recordTable);
        recordsPanel.add(scrollPane, BorderLayout.CENTER);

        // Control panel below table
        JPanel tableControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnViewRecords = new JButton("Refresh / View Records");
        btnViewRecords.setBackground(new Color(24, 76, 120)); // Primary blue
        btnViewRecords.setForeground(Color.WHITE);
        btnViewRecords.setFocusPainted(false);
        btnViewRecords.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tableControlPanel.add(btnViewRecords);
        recordsPanel.add(tableControlPanel, BorderLayout.SOUTH);

        contentPanel.add(recordsPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // Status Bar (South)
        // -------------------------------------------------------------
        JLabel statusBar = new JLabel(" Local Storage File: patients.dat | Ready");
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(5, 10, 5, 10)
        ));
        statusBar.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusBar.setForeground(Color.DARK_GRAY);
        add(statusBar, BorderLayout.SOUTH);

        // -------------------------------------------------------------
        // Event Listeners (Action Handling)
        // -------------------------------------------------------------
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegisterPatient();
            }
        });

        btnViewRecords.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRecordsIntoTable();
                JOptionPane.showMessageDialog(HospitalManagementSystem.this,
                        "Patient records refreshed successfully from 'patients.dat'!",
                        "Records Loaded", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnClearFields.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearInputFields();
            }
        });
    }

    // ========================================================================
    // 4. [INPUT VALIDATION & BUSINESS LOGIC]
    // ========================================================================
    /**
     * Validates input values, constructs a Patient object, and persists it.
     */
    private void handleRegisterPatient() {
        String name = txtName.getText().trim();
        String ageStr = txtAge.getText().trim();
        String condition = txtCondition.getText().trim();

        // 1. Validation: Ensure fields are not empty
        if (name.isEmpty() || ageStr.isEmpty() || condition.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all fields (Name, Age, Condition) before registering.",
                    "Input Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validation: Ensure name doesn't contain just numbers/symbols
        if (name.length() < 2) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid patient name (at least 2 characters).",
                    "Invalid Name",
                    JOptionPane.WARNING_MESSAGE);
            txtName.requestFocus();
            return;
        }

        // 3. Validation: Ensure Age is a valid positive integer
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0 || age > 125) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a realistic age between 1 and 125.",
                        "Invalid Age",
                        JOptionPane.ERROR_MESSAGE);
                txtAge.requestFocus();
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Age must be a valid numeric whole number.",
                    "Number Format Error",
                    JOptionPane.ERROR_MESSAGE);
            txtAge.requestFocus();
            return;
        }

        // [OOP: CREATING OBJECT OF CLASS PATIENT]
        int newId = PatientFileManager.getNextPatientId();
        Patient patient = new Patient(newId, name, age, condition);

        // [FILE HANDLING: SAVING VIA SERIALIZATION]
        boolean isSaved = PatientFileManager.savePatient(patient);

        if (isSaved) {
            JOptionPane.showMessageDialog(this,
                    "Patient registered successfully!\nAssigned ID: " + newId,
                    "Registration Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Refresh table with newly added patient
            loadRecordsIntoTable();

            // Clear input fields for next entry
            clearInputFields();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save patient record to file.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * [FILE HANDLING & GUI: POPULATING SWING COMPONENT]
     * Loads patients from the serialized file and populates the JTable.
     */
    private void loadRecordsIntoTable() {
        // Clear previous rows from table model
        tableModel.setRowCount(0);

        List<Patient> patients = PatientFileManager.loadPatients();
        for (Patient p : patients) {
            Object[] row = {
                    p.getPatientId(),
                    p.getName(),
                    p.getAge(),
                    p.getCondition()
            };
            tableModel.addRow(row);
        }
    }

    /**
     * Helper to clear form inputs.
     */
    private void clearInputFields() {
        txtName.setText("");
        txtAge.setText("");
        txtCondition.setText("");
        txtName.requestFocus();
    }

    // ========================================================================
    // 5. [MAIN ENTRY POINT]
    // ========================================================================
    public static void main(String[] args) {
        // Set cross-platform look and feel for a clean appearance
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // Fall back to default Swing look and feel
        }

        // Launch GUI safely on the Event Dispatch Thread (Swing best practice)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HospitalManagementSystem app = new HospitalManagementSystem();
                app.setVisible(true);
            }
        });
    }
}
