@echo off
REM ===========================================================================
REM Hospital Management System - Windows Run Script
REM Group 11 - OOP Lab Mini-Project
REM ===========================================================================
setlocal

REM Determine Java runtime
set JAVA_CMD=java
where java >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\Program Files\Java\jdk-25\bin\java.exe" (
        set "JAVA_CMD=C:\Program Files\Java\jdk-25\bin\java.exe"
    ) else if defined JAVA_HOME (
        set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"
    ) else (
        echo [ERROR] java was not found in PATH or JAVA_HOME. Please install JRE/JDK.
        pause
        exit /b 1
    )
)

if not exist "db.properties" (
    echo [WARNING] 'db.properties' not found!
    echo Creating 'db.properties' from 'db.properties.example'...
    if exist "db.properties.example" (
        copy "db.properties.example" "db.properties" >nul
        echo Please edit 'db.properties' with your MySQL username and password.
        pause
    )
)

if exist "HospitalManagementSystem.jar" (
    echo Launching Hospital Management System (Executable JAR)...
    "%JAVA_CMD%" -jar HospitalManagementSystem.jar
) else if exist "bin" (
    echo Launching Hospital Management System from bin directory...
    "%JAVA_CMD%" -cp "bin;lib\mysql-connector-j-26.7.0.jar;." ui.HospitalManagementSystem
) else (
    echo [ERROR] Application is not built yet. Please run build.bat first!
    pause
    exit /b 1
)
