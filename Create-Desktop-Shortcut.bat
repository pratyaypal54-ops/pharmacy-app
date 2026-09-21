@echo off
setlocal
cd /d "%~dp0"

echo Creating PharmCare POS desktop shortcut...

powershell -Command ^
  "$ws = New-Object -ComObject WScript.Shell;" ^
  "$desktop = [Environment]::GetFolderPath('Desktop');" ^
  "$s = $ws.CreateShortcut(\"$desktop\PharmCare POS.lnk\");" ^
  "$s.TargetPath = '%~dp0PharmCare-POS.bat';" ^
  "$s.WorkingDirectory = '%~dp0';" ^
  "$s.Description = 'Launch PharmCare Retail POS Terminal';" ^
  "if (Test-Path '%~dp0src\main\resources\static\favicon.ico') { $s.IconLocation = '%~dp0src\main\resources\static\favicon.ico'; };" ^
  "$s.Save();"

if %ERRORLEVEL% equ 0 (
    echo.
    echo ===============================================================
    echo   [SUCCESS] Shortcut 'PharmCare POS' placed on your Desktop!
    echo   Simply double-click it anytime to open your POS billing window.
    echo ===============================================================
) else (
    echo [ERROR] Failed to create shortcut.
)
pause
