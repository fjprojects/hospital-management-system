package util;

import model.Patient;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * Project Title : Hospital Management System
 * Group         : Group 11
 * Subject       : Object-Oriented Programming (OOP) Lab Mini-Project
 * ============================================================================
 * [FILE HANDLING & BACKWARD COMPATIBILITY]
 * LegacyPatientFileManager manages reading and writing serialized patient records.
 * 
 * Features:
 *  - Full backward compatibility: Seamlessly deserializes both legacy default-package
 *    'Patient' objects and modern 'model.Patient' objects.
 *  - Reflection-based field extraction: Safely handles package migration without data loss.
 *  - Preserves existing 'patients.dat' file integrity.
 */
public class LegacyPatientFileManager {

    public static final String DEFAULT_FILE_NAME = "patients.dat";

    /**
     * Reads and converts patient records from a serialized file into modern model.Patient objects.
     * 
     * @param file The file to read
     * @return List of model.Patient entities; empty list if file is absent or empty
     */
    public static List<Patient> loadPatients(File file) {
        if (file == null || !file.exists()) {
            return new ArrayList<>();
        }

        List<Patient> resultList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                List<?> rawList = (List<?>) obj;
                for (Object item : rawList) {
                    if (item == null) continue;

                    if (item instanceof Patient) {
                        resultList.add((Patient) item);
                    } else {
                        // Extract attributes from legacy default-package Patient via reflection
                        try {
                            Method getId = item.getClass().getMethod("getPatientId");
                            Method getName = item.getClass().getMethod("getName");
                            Method getAge = item.getClass().getMethod("getAge");
                            Method getCond = item.getClass().getMethod("getCondition");

                            getId.setAccessible(true);
                            getName.setAccessible(true);
                            getAge.setAccessible(true);
                            getCond.setAccessible(true);

                            int id = (Integer) getId.invoke(item);
                            String name = (String) getName.invoke(item);
                            int age = (Integer) getAge.invoke(item);
                            String cond = (String) getCond.invoke(item);

                            resultList.add(new Patient(id, name, age, cond));
                        } catch (Exception ex) {
                            System.err.println("[LegacyPatientFileManager] Reflection read failed: " + ex.getMessage());
                        }
                    }
                }
            }
        } catch (EOFException e) {
            // Empty file
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("[LegacyPatientFileManager] Error reading records: " + e.getMessage());
        }

        return resultList;
    }

    /**
     * Loads patients from the default local file 'patients.dat'.
     */
    public static List<Patient> loadPatients() {
        return loadPatients(new File(DEFAULT_FILE_NAME));
    }

    /**
     * Serializes a list of modern Patient objects to the specified file.
     * 
     * @param patientList List of patients to write
     * @param file        Destination file
     * @return true if successful; false on error
     */
    public static boolean saveAllPatients(List<Patient> patientList, File file) {
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {

            oos.writeObject(patientList);
            return true;
        } catch (IOException e) {
            System.err.println("[LegacyPatientFileManager] Error writing records: " + e.getMessage());
            return false;
        }
    }
}
