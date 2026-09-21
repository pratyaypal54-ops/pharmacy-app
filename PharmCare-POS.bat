@echo off
title Launching PharmCare POS Terminal...
cd /d "%~dp0"

:: Check if port 8080 is already running
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
if %ERRORLEVEL% equ 0 (
    goto LaunchBrowser
)

:: Start background server silently using javaw (no black console window)
if exist "%~dp0pharmacy-app.jar" (
    start "" javaw -jar "%~dp0pharmacy-app.jar"
    goto WaitLoop
)

if exist "%~dp0target\pharmacy-app-0.0.1-SNAPSHOT.jar" (
    start "" javaw -jar "%~dp0target\pharmacy-app-0.0.1-SNAPSHOT.jar"
    goto WaitLoop
)

:WaitLoop
timeout /t 2 /nobreak >nul
netstat -ano | findstr ":8080" | findstr "LISTENING" >nul
if %ERRORLEVEL% neq 0 (
    goto WaitLoop
)

:LaunchBrowser
:: Launch Microsoft Edge in native standalone App Mode (no browser search bar or tabs)
if exist "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" (
    start "" "C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe" --app=http://localhost:8080/admin/sell
    exit /b 0
)

if exist "C:\Program Files\Microsoft\Edge\Application\msedge.exe" (
    start "" "C:\Program Files\Microsoft\Edge\Application\msedge.exe" --app=http://localhost:8080/admin/sell
    exit /b 0
)

:: Check Google Chrome in App Mode
if exist "C:\Program Files\Google\Chrome\Application\chrome.exe" (
    start "" "C:\Program Files\Google\Chrome\Application\chrome.exe" --app=http://localhost:8080/admin/sell
    exit /b 0
)

:: Fallback to default browser
start http://localhost:8080/admin/sell
exit /b 0
