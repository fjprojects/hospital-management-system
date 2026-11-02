@echo off
REM ===========================================================================
REM Hospital Management System - Windows Build Script
REM Group 11 - OOP Lab Mini-Project
REM ===========================================================================
setlocal EnableDelayedExpansion

echo ===========================================================================
echo  Compiling Hospital Management System (Group 11)...
echo ===========================================================================

REM Determine Java Compiler
set JAVAC_CMD=javac
where javac >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\Program Files\Java\jdk-25\bin\javac.exe" (
        set "JAVAC_CMD=C:\Program Files\Java\jdk-25\bin\javac.exe"
    ) else if defined JAVA_HOME (
        set "JAVAC_CMD=%JAVA_HOME%\bin\javac.exe"
    ) else (
        echo [ERROR] javac was not found in PATH or JAVA_HOME. Please install JDK.
        pause
        exit /b 1
    )
)

REM Determine JAR Tool
set JAR_CMD=jar
where jar >nul 2>&1
if %ERRORLEVEL% neq 0 (
    if exist "C:\Program Files\Java\jdk-25\bin\jar.exe" (
        set "JAR_CMD=C:\Program Files\Java\jdk-25\bin\jar.exe"
    ) else if defined JAVA_HOME (
        set "JAR_CMD=%JAVA_HOME%\bin\jar.exe"
    )
)

set "LIB_JAR=lib\mysql-connector-j-26.7.0.jar"
if not exist "%LIB_JAR%" (
    echo [WARNING] %LIB_JAR% not found. JDBC operations will fail at runtime.
)

REM Prepare Output Directories
if exist "bin" rd /s /q "bin"
mkdir "bin"

echo [1/4] Compiling Java source files...
"%JAVAC_CMD%" -encoding UTF-8 -cp ".;%LIB_JAR%" -d "bin" src\main\java\model\*.java src\main\java\database\*.java src\main\java\dao\*.java src\main\java\service\*.java src\main\java\util\*.java src\main\java\ui\*.java
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [2/4] Copying resources and legacy compatibility classes...
if exist "src\main\resources" (
    xcopy /y /s "src\main\resources\*" "bin\" >nul 2>&1
)
if exist "backup_original\Patient.class" (
    copy /y "backup_original\Patient.class" "bin\" >nul 2>&1
)

echo [3/4] Packaging Executable JAR...
if exist "manifest.mf" del "manifest.mf"
(
    echo Manifest-Version: 1.0
    echo Main-Class: ui.HospitalManagementSystem
    echo Class-Path: lib/mysql-connector-j-26.7.0.jar .
    echo.
) > manifest.mf

"%JAR_CMD%" cfm HospitalManagementSystem.jar manifest.mf -C bin .
if exist "manifest.mf" del "manifest.mf"

echo.
echo ===========================================================================
echo  BUILD SUCCESSFUL
echo  Compiled classes located in: bin/
echo  Executable JAR generated:     HospitalManagementSystem.jar
echo ===========================================================================
echo  To run the application, double-click run.bat or execute:
echo    java -jar HospitalManagementSystem.jar
echo ===========================================================================
