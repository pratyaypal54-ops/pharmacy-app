@echo off
title Opening Windows Firewall for PharmCare POS (Port 8080)...
echo ========================================================
echo   Configuring Windows Firewall to allow other devices
echo   (Phones, Tablets, Laptops on same Wi-Fi) to connect
echo ========================================================
echo.

:: Check for Administrator privileges
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo [REQUEST] Requesting Administrator Privileges...
    powershell -Command "Start-Process cmd -ArgumentList '/c \"\"%~f0\"\"' -Verb RunAs"
    exit /b
)

:: Add or update rule for Port 8080
netsh advfirewall firewall delete rule name="PharmCare POS (Port 8080)" >nul 2>&1
netsh advfirewall firewall add rule name="PharmCare POS (Port 8080)" dir=in action=allow protocol=TCP localport=8080 profile=any

echo.
echo ========================================================
echo  [SUCCESS] Port 8080 is now OPEN for your local Wi-Fi!
echo  Any device connected to your Wi-Fi can now open:
echo.
echo    Customer Catalog:  http://192.168.0.48:8080/medicines
echo    POS Billing:       http://192.168.0.48:8080/admin/sell
echo ========================================================
echo.
pause
