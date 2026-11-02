package ui;

import database.DatabaseConnection;
import model.Patient;
import service.PatientService;
import service.ValidationResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * ============================================================================
 * Project Title : Hospital Management System
 * Group         : Group 11
 * Subject       : Object-Oriented Programming (OOP) Lab Mini-Project
 * ============================================================================
 * [OOP PRINCIPLE: PRESENTATION LAYER & INHERITANCE]
 * HospitalManagementSystem represents the main desktop Graphical User Interface (GUI).
 * 
 * Key OOP Concepts Demonstrated:
 *  - Inheritance: Extends javax.swing.JFrame to create the application window.
 *  - Encapsulation: UI components, models, and service instances are encapsulated private members.
 *  - Separation of Concerns: Delegates business rules and data persistence to PatientService.
 *  - Event-Driven Programming: ActionListeners handle user events in a decoupled manner.
 * 
 * Features Implemented:
 *  1. Register Patient with auto-increment database ID.
 *  2. Search Patient by numeric ID.
 *  3. Update existing Patient records.
 *  4. Delete Patient record with confirmation.
 *  5. Refresh and display all patients in a structured JTable.
 *  6. Clear form inputs and reset view state.
 *  7. Input validation with meaningful alerts.
 *  8. Safe migration/import from legacy 'patients.dat' file.
 *  9. Live database connection testing and status reporting.
 */
public class HospitalManagementSystem extends JFrame {

    // [SERVICE LAYER DEPENDENCY]
    private final PatientService patientService;

    // [GUI COMPONENTS] Input Fields
    private JTextField txtPatientId;
    private JTextField txtName;
    private JTextField txtAge;
    private JTextField txtCondition;

    // [GUI COMPONENTS] Search Bar
    private JTextField txtSearchId;
    private JButton btnSearch;
    private JButton btnResetSearch;

    // [GUI COMPONENTS] Action Buttons
    private JButton btnRegister;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClearFields;
    private JButton btnImportLegacy;

    // [GUI COMPONENTS] Table & Display
    private JTable recordTable;
    private DefaultTableModel tableModel;
    private JButton btnRefresh;
    private JButton btnTestConnection;

    // [GUI COMPONENTS] Status Bar
    private JLabel lblStatus;
    private JLabel lblDbStatus;

    // State Tracking
    private Integer selectedPatientId = null;

    /**
     * Constructs the main application frame, initializes UI components,
     * wires event listeners, and loads initial records from MySQL.
     */
    public HospitalManagementSystem() {
        // [FACULTY REQUIREMENT: Exact Window Title]
        super("Hospital Management System - Group 11");

        this.patientService = new PatientService();

        // Window configuration
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 660);
        setMinimumSize(new Dimension(850, 560));
        setLocationRelativeTo(null); // Center on screen

        // Initialize UI components and layout
        initComponents();

        // Wire Event Listeners
        initEventHandlers();

        // Check DB connection and load records
        checkDatabaseStatus();
        loadRecordsIntoTable();
    }

    /**
     * Builds and arranges all Swing UI components.
     */
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // -------------------------------------------------------------
        // 1. Header Banner Panel (North)
        // -------------------------------------------------------------
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(24, 76, 120));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("HOSPITAL MANAGEMENT SYSTEM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblGroup = new JLabel("Group 11 | OOP Lab Mini-Project | MySQL + JDBC Integrated");
        lblGroup.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblGroup.setForeground(new Color(210, 230, 250));

        JPanel headerTextPanel = new JPanel(new GridLayout(2, 1, 2, 2));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(lblTitle);
        headerTextPanel.add(lblGroup);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // -------------------------------------------------------------
        // 2. Main Content Panel (Center: Form West, Table Center)
        // -------------------------------------------------------------
        JPanel contentPanel = new JPanel(new BorderLayout(12, 12));
        contentPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // --- Registration & Management Form (West) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " Patient Information ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(24, 76, 120)
        ));
        formPanel.setPreferredSize(new Dimension(340, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Field: Patient ID (Read-only / Auto-generated)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridy = 1;
        txtPatientId = new JTextField("Auto-generated by DB");
        txtPatientId.setEditable(false);
        txtPatientId.setBackground(new Color(245, 245, 245));
        txtPatientId.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        txtPatientId.setForeground(Color.DARK_GRAY);
        formPanel.add(txtPatientId, gbc);

        // Field: Full Name
        gbc.gridy = 2;
        formPanel.add(new JLabel("Full Name: *"), gbc);
        gbc.gridy = 3;
        txtName = new JTextField();
        txtName.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtName, gbc);

        // Field: Age
        gbc.gridy = 4;
        formPanel.add(new JLabel("Age (Years): *"), gbc);
        gbc.gridy = 5;
        txtAge = new JTextField();
        txtAge.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtAge, gbc);

        // Field: Illness / Condition
        gbc.gridy = 6;
        formPanel.add(new JLabel("Illness / Condition: *"), gbc);
        gbc.gridy = 7;
        txtCondition = new JTextField();
        txtCondition.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtCondition, gbc);

        // Action Buttons Panel
        JPanel formButtonPanel = new JPanel(new GridLayout(4, 1, 6, 6));

        btnRegister = new JButton("Register Patient");
        btnRegister.setBackground(new Color(34, 139, 34)); // Forest green
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnUpdate = new JButton("Update Patient");
        btnUpdate.setBackground(new Color(230, 126, 34)); // Warm Orange
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdate.setEnabled(false); // Enabled when row selected

        btnDelete = new JButton("Delete Patient");
        btnDelete.setBackground(new Color(192, 57, 43)); // Crimson Red
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setEnabled(false); // Enabled when row selected

        btnClearFields = new JButton("Clear Form / Reset");
        btnClearFields.setBackground(new Color(108, 117, 125)); // Slate Gray
        btnClearFields.setForeground(Color.WHITE);
        btnClearFields.setFocusPainted(false);
        btnClearFields.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        formButtonPanel.add(btnRegister);
        formButtonPanel.add(btnUpdate);
        formButtonPanel.add(btnDelete);
        formButtonPanel.add(btnClearFields);

        gbc.gridy = 8;
        gbc.insets = new Insets(14, 8, 8, 8);
        formPanel.add(formButtonPanel, gbc);

        // Legacy Data Migration Button
        btnImportLegacy = new JButton("Import Legacy 'patients.dat'");
        btnImportLegacy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnImportLegacy.setBackground(new Color(235, 240, 248));
        btnImportLegacy.setForeground(new Color(24, 76, 120));
        btnImportLegacy.setFocusPainted(false);
        btnImportLegacy.setToolTipText("Import existing serialized patient records into MySQL without creating duplicates.");

        gbc.gridy = 9;
        gbc.insets = new Insets(10, 8, 8, 8);
        formPanel.add(btnImportLegacy, gbc);

        // Filler space to anchor controls at top
        gbc.gridy = 10;
        gbc.weighty = 1.0;
        formPanel.add(new JLabel(), gbc);

        contentPanel.add(formPanel, BorderLayout.WEST);

        // --- Records Display & Search Area (Center) ---
        JPanel recordsPanel = new JPanel(new BorderLayout(8, 8));
        recordsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                " Patient Records Database (MySQL) ",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(24, 76, 120)
        ));

        // Search Bar at Top of Records
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        searchPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        JLabel lblSearch = new JLabel("Search by ID:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtSearchId = new JTextField(10);
        txtSearchId.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        btnSearch = new JButton("Search");
        btnSearch.setBackground(new Color(24, 76, 120));
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnResetSearch = new JButton("Show All");
        btnResetSearch.setBackground(new Color(108, 117, 125));
        btnResetSearch.setForeground(Color.WHITE);
        btnResetSearch.setFocusPainted(false);
        btnResetSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearchId);
        searchPanel.add(btnSearch);
        searchPanel.add(btnResetSearch);

        recordsPanel.add(searchPanel, BorderLayout.NORTH);

        // JTable for structured presentation of records
        String[] columnHeaders = {"Patient ID", "Name", "Age", "Illness / Condition", "Registration Date"};
        tableModel = new DefaultTableModel(columnHeaders, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Non-editable cells
            }
        };

        recordTable = new JTable(tableModel);
        recordTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        recordTable.setRowHeight(26);
        recordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recordTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        recordTable.getTableHeader().setBackground(new Color(230, 240, 250));

        // Center align ID and Age columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        recordTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        recordTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        recordTable.getColumnModel().getColumn(1).setPreferredWidth(160);
        recordTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        recordTable.getColumnModel().getColumn(2).setPreferredWidth(60);
        recordTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        recordTable.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(recordTable);
        recordsPanel.add(scrollPane, BorderLayout.CENTER);

        // Control Panel below Table
        JPanel tableControlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));

        btnTestConnection = new JButton("Test DB Connection");
        btnTestConnection.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnTestConnection.setFocusPainted(false);

        btnRefresh = new JButton("Refresh Records");
        btnRefresh.setBackground(new Color(24, 76, 120));
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setFocusPainted(false);

        tableControlPanel.add(btnTestConnection);
        tableControlPanel.add(btnRefresh);
        recordsPanel.add(tableControlPanel, BorderLayout.SOUTH);

        contentPanel.add(recordsPanel, BorderLayout.CENTER);
        add(contentPanel, BorderLayout.CENTER);

        // -------------------------------------------------------------
        // 3. Status Bar (South)
        // -------------------------------------------------------------
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(6, 15, 6, 15)
        ));

        lblStatus = new JLabel("Ready | Loaded 0 records");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(Color.DARK_GRAY);

        lblDbStatus = new JLabel("Database: Checking...");
        lblDbStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));

        statusBar.add(lblStatus, BorderLayout.WEST);
        statusBar.add(lblDbStatus, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);
    }

    /**
     * Wires action listeners and event handlers to UI components.
     */
    private void initEventHandlers() {
        // 1. Register Patient Button
        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegisterPatient();
            }
        });

        // 2. Update Patient Button
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdatePatient();
            }
        });

        // 3. Delete Patient Button
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDeletePatient();
            }
        });

        // 4. Clear Form Button
        btnClearFields.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearInputFields();
            }
        });

        // 5. Search Button
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearchPatient();
            }
        });

        // Pressing Enter in Search field triggers Search
        txtSearchId.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearchPatient();
            }
        });

        // 6. Reset / Show All Button
        btnResetSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchId.setText("");
                loadRecordsIntoTable();
            }
        });

        // 7. Refresh Button
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRecordsIntoTable();
                JOptionPane.showMessageDialog(HospitalManagementSystem.this,
                        "Patient records refreshed successfully from MySQL database.",
                        "Records Refreshed", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // 8. Test DB Connection Button
        btnTestConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkDatabaseStatus();
                if (DatabaseConnection.testConnection()) {
                    JOptionPane.showMessageDialog(HospitalManagementSystem.this,
                            "Successfully connected to MySQL database: 'hospital_management'!",
                            "Database Online", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String error = DatabaseConnection.getLastError();
                    JOptionPane.showMessageDialog(HospitalManagementSystem.this,
                            "Database connection failed!\n" + (error != null ? error : "Unknown error")
                                    + "\n\nPlease check 'db.properties' and ensure MySQL server is running.",
                            "Database Offline", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 9. Legacy File Migration Button
        btnImportLegacy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleImportLegacyData();
            }
        });

        // 10. Table Row Selection Listener (Populates form for Update/Delete)
        recordTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    handleRowSelected();
                }
            }
        });
    }

    // ========================================================================
    // ACTION HANDLERS & BUSINESS LOGIC
    // ========================================================================

    /**
     * Handles patient registration.
     */
    private void handleRegisterPatient() {
        String name = txtName.getText();
        String ageStr = txtAge.getText();
        String condition = txtCondition.getText();

        ValidationResult val = patientService.validatePatientInput(name, ageStr, condition);
        if (!val.isValid()) {
            JOptionPane.showMessageDialog(this, val.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(ageStr.trim());
            int newId = patientService.registerPatient(name, age, condition);

            JOptionPane.showMessageDialog(this,
                    "Patient registered successfully in MySQL database!\nAssigned Patient ID: " + newId,
                    "Registration Success", JOptionPane.INFORMATION_MESSAGE);

            clearInputFields();
            loadRecordsIntoTable();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while registering patient:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles updating an existing patient record.
     */
    private void handleUpdatePatient() {
        if (selectedPatientId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient from the table to update.",
                    "No Patient Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = txtName.getText();
        String ageStr = txtAge.getText();
        String condition = txtCondition.getText();

        ValidationResult val = patientService.validatePatientInput(name, ageStr, condition);
        if (!val.isValid()) {
            JOptionPane.showMessageDialog(this, val.getMessage(), "Input Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to update records for Patient ID #" + selectedPatientId + "?",
                "Confirm Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int age = Integer.parseInt(ageStr.trim());
            boolean updated = patientService.updatePatient(selectedPatientId, name, age, condition);

            if (updated) {
                JOptionPane.showMessageDialog(this,
                        "Patient ID #" + selectedPatientId + " details updated successfully!",
                        "Update Success", JOptionPane.INFORMATION_MESSAGE);
                clearInputFields();
                loadRecordsIntoTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not find Patient ID #" + selectedPatientId + " in database.",
                        "Update Failed", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while updating patient:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Handles deleting an existing patient record with confirmation.
     */
    private void handleDeletePatient() {
        if (selectedPatientId == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select a patient from the table to delete.",
                    "No Patient Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String patientName = txtName.getText().trim();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to permanently delete Patient record:\n"
                        + "ID: " + selectedPatientId + " | Name: " + patientName + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean deleted = patientService.deletePatient(selectedPatientId);
            if (deleted) {
                JOptionPane.showMessageDialog(this,
                        "Patient record #" + selectedPatientId + " deleted successfully.",
                        "Deletion Success", JOptionPane.INFORMATION_MESSAGE);
                clearInputFields();
                loadRecordsIntoTable();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not find Patient ID #" + selectedPatientId + " to delete.",
                        "Delete Failed", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while deleting patient:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Searches for a patient by primary key ID.
     */
    private void handleSearchPatient() {
        String searchStr = txtSearchId.getText().trim();
        if (searchStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a numeric Patient ID to search.",
                    "Search Input Required", JOptionPane.WARNING_MESSAGE);
            txtSearchId.requestFocus();
            return;
        }

        int searchId;
        try {
            searchId = Integer.parseInt(searchStr);
            if (searchId <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Patient ID must be a positive whole number.",
                    "Invalid ID Format", JOptionPane.ERROR_MESSAGE);
            txtSearchId.requestFocus();
            return;
        }

        try {
            Patient p = patientService.getPatientById(searchId);
            if (p != null) {
                tableModel.setRowCount(0);
                addPatientToTable(p);
                lblStatus.setText("Search result: 1 record found for Patient ID #" + searchId);

                // Auto-select row to populate form
                if (recordTable.getRowCount() > 0) {
                    recordTable.setRowSelectionInterval(0, 0);
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "No patient record found with ID #" + searchId,
                        "Patient Not Found", JOptionPane.INFORMATION_MESSAGE);
                loadRecordsIntoTable();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error while searching:\n" + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Imports records from legacy 'patients.dat' file safely without duplicates.
     */
    private void handleImportLegacyData() {
        File datFile = new File("patients.dat");
        if (!datFile.exists()) {
            JOptionPane.showMessageDialog(this,
                    "No 'patients.dat' legacy file found in current working directory.",
                    "File Not Found", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Import existing records from 'patients.dat' into MySQL?\n"
                        + "Any records already in the database will be skipped automatically to avoid duplicates.",
                "Confirm Legacy Data Import", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            PatientService.MigrationSummary summary = patientService.importFromLegacyFile(datFile);
            JOptionPane.showMessageDialog(this,
                    "Legacy Data Import Completed!\n\n"
                            + "Total records found in file : " + summary.getTotalFound() + "\n"
                            + "Newly imported into MySQL   : " + summary.getImportedCount() + "\n"
                            + "Duplicates skipped          : " + summary.getSkippedCount(),
                    "Import Complete", JOptionPane.INFORMATION_MESSAGE);

            loadRecordsIntoTable();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Database Error during migration:\n" + ex.getMessage(),
                    "Migration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Fills the form fields when a table row is clicked, switching to Edit/Delete mode.
     */
    private void handleRowSelected() {
        int row = recordTable.getSelectedRow();
        if (row >= 0 && row < tableModel.getRowCount()) {
            try {
                Object idObj = tableModel.getValueAt(row, 0);
                selectedPatientId = Integer.parseInt(idObj.toString());

                txtPatientId.setText(String.valueOf(selectedPatientId));
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtAge.setText(tableModel.getValueAt(row, 2).toString());
                txtCondition.setText(tableModel.getValueAt(row, 3).toString());

                // Enable Update and Delete; disable Register
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnRegister.setEnabled(false);

                lblStatus.setText("Selected Patient ID #" + selectedPatientId + " | Edit fields and click Update or Delete");
            } catch (Exception ex) {
                System.err.println("Error reading row data: " + ex.getMessage());
            }
        }
    }

    /**
     * Loads all patient records from MySQL and populates the JTable.
     */
    private void loadRecordsIntoTable() {
        tableModel.setRowCount(0);

        try {
            List<Patient> list = patientService.getAllPatients();
            for (Patient p : list) {
                addPatientToTable(p);
            }
            lblStatus.setText("Total Records in MySQL: " + list.size() + " | Ready");
            checkDatabaseStatus();
        } catch (SQLException ex) {
            lblStatus.setText("Error loading records: " + ex.getMessage());
            lblDbStatus.setText("Database: OFFLINE / Error");
            lblDbStatus.setForeground(Color.RED);
        }
    }

    /**
     * Appends a Patient object as a row in the JTable model.
     */
    private void addPatientToTable(Patient p) {
        String dateStr = "N/A";
        if (p.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateStr = sdf.format(p.getCreatedAt());
        }

        Object[] rowData = {
                p.getPatientId(),
                p.getName(),
                p.getAge(),
                p.getCondition(),
                dateStr
        };
        tableModel.addRow(rowData);
    }

    /**
     * Clears all input fields and resets the form back to Registration mode.
     */
    private void clearInputFields() {
        selectedPatientId = null;
        txtPatientId.setText("Auto-generated by DB");
        txtName.setText("");
        txtAge.setText("");
        txtCondition.setText("");
        recordTable.clearSelection();

        btnRegister.setEnabled(true);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        txtName.requestFocus();
        lblStatus.setText("Form cleared | Ready for new patient registration");
    }

    /**
     * Verifies and updates the database connection status indicator in the status bar.
     */
    private void checkDatabaseStatus() {
        boolean connected = DatabaseConnection.testConnection();
        if (connected) {
            lblDbStatus.setText("Database: MySQL Connected (hospital_management)");
            lblDbStatus.setForeground(new Color(34, 139, 34)); // Green
        } else {
            lblDbStatus.setText("Database: Disconnected (Check db.properties)");
            lblDbStatus.setForeground(new Color(192, 57, 43)); // Red
        }
    }

    // ========================================================================
    // MAIN ENTRY POINT
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
