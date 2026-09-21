@echo off
title Stopping PharmCare Server...
echo [INFO] Stopping PharmCare background engine on port 8080...

powershell -Command "Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique | ForEach-Object { Stop-Process -Id $_ -Force -ErrorAction SilentlyContinue }; Write-Host '[SUCCESS] PharmCare server stopped.'"

pause
