@echo off
title Starting PharmCare Server...
cd /d "%~dp0"

echo ========================================================
echo    ✚ PharmCare - Retail Pharmacy Management & POS
echo ========================================================
echo.

:: Check if port 8080 is already active
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
if %ERRORLEVEL% equ 0 (
    echo [INFO] PharmCare server engine is already active and running!
    goto LaunchApp
)

echo [INFO] Starting PharmCare standalone server in the background...

:: Try to launch the packaged standalone JAR using javaw (no command prompt window)
if exist "%~dp0pharmacy-app.jar" (
    start "" javaw -jar "%~dp0pharmacy-app.jar"
    goto WaitLoop
)

if exist "%~dp0target\pharmacy-app-0.0.1-SNAPSHOT.jar" (
    start "" javaw -jar "%~dp0target\pharmacy-app-0.0.1-SNAPSHOT.jar"
    goto WaitLoop
)

:: Fallback if JAR not found
echo [INFO] Running via Java runtime...
start "PharmCare Server" /min java -jar "%~dp0pharmacy-app.jar"

:WaitLoop
echo [WAIT] Initializing database and security layer...
timeout /t 3 /nobreak >nul
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
if %ERRORLEVEL% neq 0 (
    goto WaitLoop
)

:LaunchApp
echo.
echo ========================================================
echo  [SUCCESS] PharmCare POS Engine is Online!
echo  Opening POS Terminal in browser...
echo ========================================================
start http://localhost:8080/admin/sell
exit /b 0
