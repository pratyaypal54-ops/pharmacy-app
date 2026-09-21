@echo off
title Starting PharmCare POS...
cd /d "%~dp0"

echo =========================================================
echo    ✚ PharmCare - Retail Pharmacy Management & POS
echo =========================================================
echo.

:: Check if port 8080 is already listening
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
if %ERRORLEVEL% equ 0 (
    echo [INFO] PharmCare server is already active!
    goto LaunchBrowser
)

echo [INFO] Starting PharmCare server background engine...
start "PharmCare Server Engine" /min mvnw.cmd spring-boot:run

echo [INFO] Waiting for server to initialize...
:WaitLoop
timeout /t 2 /nobreak >nul
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
if %ERRORLEVEL% neq 0 (
    echo [WAIT] Initializing database and security layer...
    goto WaitLoop
)

:LaunchBrowser
echo.
echo [SUCCESS] PharmCare POS ready! Launching terminal...

:: Check for Microsoft Edge and run in standalone App Mode (no URL bar, like a desktop app)
if exist "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" (
    start "" "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" --app=http://localhost:8080/admin/sell
    exit /b 0
)

if exist "C:\Program Files\Microsoft\Edge\Application\msedge.exe" (
    start "" "C:\Program Files\Microsoft\Edge\Application\msedge.exe" --app=http://localhost:8080/admin/sell
    exit /b 0
)

:: Check for Google Chrome in App Mode
if exist "C:\Program Files\Google\Chrome\Application\chrome.exe" (
    start "" "C:\Program Files\Google\Chrome\Application\chrome.exe" --app=http://localhost:8080/admin/sell
    exit /b 0
)

:: Fallback to default browser
start http://localhost:8080/admin/sell
exit /b 0
