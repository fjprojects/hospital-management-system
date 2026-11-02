package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * ============================================================================
 * Project Title : Hospital Management System
 * Group         : Group 11
 * Subject       : Object-Oriented Programming (OOP) Lab Mini-Project
 * ============================================================================
 * [OOP PRINCIPLE: SEPARATION OF CONCERNS & ENCAPSULATION]
 * DatabaseConnection abstracts low-level JDBC driver loading, configuration
 * management, and physical connection acquisition away from data access objects.
 * 
 * Features:
 *  - Externalized configuration via 'db.properties' (no hardcoded passwords).
 *  - Fallback loader searching both local file system and classpath resources.
 *  - Safe resource closing helpers for try-with-resources and explicit teardown.
 *  - Diagnostic connection testing utility for GUI status indicators.
 */
public class DatabaseConnection {

    private static final String CONFIG_FILE_NAME = "db.properties";
    private static Properties properties = null;
    private static String lastError = null;

    // Static initializer to load configuration once
    static {
        loadConfiguration();
    }

    /**
     * Loads database configuration properties from file or classpath.
     */
    public static synchronized void loadConfiguration() {
        properties = new Properties();
        InputStream input = null;

        try {
            // 1. Try loading from local working directory (highest priority)
            File localFile = new File(CONFIG_FILE_NAME);
            if (localFile.exists() && localFile.isFile()) {
                input = new FileInputStream(localFile);
            } else {
                // 2. Fall back to classpath resource
                input = DatabaseConnection.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
            }

            if (input != null) {
                properties.load(input);
                lastError = null;
            } else {
                lastError = "Configuration file '" + CONFIG_FILE_NAME + "' not found. "
                        + "Please copy 'db.properties.example' to 'db.properties' and set your MySQL credentials.";
                System.err.println("[DatabaseConnection] " + lastError);
            }
        } catch (Exception ex) {
            lastError = "Failed to load '" + CONFIG_FILE_NAME + "': " + ex.getMessage();
            System.err.println("[DatabaseConnection] " + lastError);
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Obtains a new JDBC Connection to the MySQL database.
     * 
     * @return an open java.sql.Connection
     * @throws SQLException if connection fails or configuration is missing
     */
    public static Connection getConnection() throws SQLException {
        if (properties == null || properties.isEmpty()) {
            loadConfiguration();
        }

        if (properties == null || properties.isEmpty()) {
            throw new SQLException(lastError != null ? lastError : "Database configuration is missing.");
        }

        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");
        String driver = properties.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        if (url == null || user == null) {
            throw new SQLException("Invalid db.properties: 'db.url' and 'db.user' must be specified.");
        }

        try {
            // Explicitly load JDBC driver class
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver class not found: " + driver
                    + ". Ensure mysql-connector-j jar is present in classpath or lib/ folder.", e);
        }

        return DriverManager.getConnection(url, user, (password != null ? password : ""));
    }

    /**
     * Diagnostic method to test whether MySQL connection is currently alive.
     * 
     * @return true if connection succeeds; false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            lastError = e.getMessage();
            return false;
        }
    }

    /**
     * Returns the last recorded error message during configuration or connection.
     */
    public static String getLastError() {
        return lastError;
    }

    /**
     * Safely closes one or more AutoCloseable resources without throwing checked exceptions.
     * 
     * @param resources Connection, Statement, ResultSet, etc.
     */
    public static void close(AutoCloseable... resources) {
        if (resources == null) return;
        for (AutoCloseable res : resources) {
            if (res != null) {
                try {
                    res.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
