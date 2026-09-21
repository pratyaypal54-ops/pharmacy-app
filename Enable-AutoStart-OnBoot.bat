@echo off
setlocal
cd /d "%~dp0"

echo Configuring PharmCare to automatically launch when Windows boots...

powershell -Command ^
  "$ws = New-Object -ComObject WScript.Shell;" ^
  "$startup = [Environment]::GetFolderPath('Startup');" ^
  "$s = $ws.CreateShortcut(\"$startup\PharmCare POS.lnk\");" ^
  "$s.TargetPath = '%~dp0PharmCare-POS.bat';" ^
  "$s.WorkingDirectory = '%~dp0';" ^
  "$s.Description = 'PharmCare POS Auto-Start on System Boot';" ^
  "if (Test-Path '%~dp0src\main\resources\static\favicon.ico') { $s.IconLocation = '%~dp0src\main\resources\static\favicon.ico'; };" ^
  "$s.Save();"

if %ERRORLEVEL% equ 0 (
    echo.
    echo ===============================================================
    echo   [SUCCESS] PharmCare POS will now launch automatically
    echo   every morning when the computer turns on!
    echo ===============================================================
) else (
    echo [ERROR] Could not write to Startup folder.
)
pause
