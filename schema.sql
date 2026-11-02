-- ============================================================================
-- Hospital Management System - Database Schema
-- Group 11 - Object-Oriented Programming Lab Mini-Project
-- ============================================================================

-- Step 1: Create Database
CREATE DATABASE IF NOT EXISTS hospital_management
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Step 2: Use Database
USE hospital_management;

-- Step 3: Create Patients Table
-- IMPORTANT: In MySQL 8.0+, 'CONDITION' is an SQL reserved word.
-- Therefore, the column name must be enclosed in backticks (`condition`).
CREATE TABLE IF NOT EXISTS patients (
    patient_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL CHECK (age > 0 AND age <= 125),
    `condition` VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Step 4: Verify Schema Structure
DESCRIBE patients;
