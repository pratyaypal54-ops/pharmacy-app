# ==============================================================================
# PHARMA//CORE - Automated Google Drive / Local Cloud Backup Script
# ==============================================================================
# This script creates an automatic daily timestamped snapshot of your entire
# pharmacy database (medicines, sales, invoices, accounts).
#
# INSTRUCTIONS:
# 1. Update $BackupFolder to your Google Drive path (e.g. 'G:\My Drive\PharmacyBackups')
# 2. To test manually, right click this file and select 'Run with PowerShell'.
# 3. To run automatically every day at 10 PM, add this script to Windows Task Scheduler.
# ==============================================================================

param (
    [string]$BackupFolder = "C:\CODING\Projects\pharmacy-app\backups"
)

# 1. Ensure backup directory exists
if (-not (Test-Path $BackupFolder)) {
    New-Item -ItemType Directory -Path $BackupFolder -Force | Out-Null
}

# 2. Build backup filename with timestamp
$Timestamp = (Get-Date).ToString("yyyy-MM-dd_HH-mm-ss")
$BackupFile = Join-Path $BackupFolder "pharmacy_db_backup_$Timestamp.sql"

# 3. Locate mysqldump.exe
$MysqldumpPath = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqldump.exe"

if (Test-Path $MysqldumpPath) {
    Write-Host "[*] Exporting full database backup using mysqldump..." -ForegroundColor Cyan
    & $MysqldumpPath -u root -p894509 --databases pharmacy_db --routines --triggers --result-file="$BackupFile"
    
    if (Test-Path $BackupFile) {
        $FileSize = (Get-Item $BackupFile).Length / 1KB
        Write-Host "[+] SUCCESS: Backup saved to: $BackupFile" -ForegroundColor Green
        Write-Host "[+] File size: $([Math]::Round($FileSize, 2)) KB" -ForegroundColor Green
    } else {
        Write-Host "[-] Backup creation failed." -ForegroundColor Red
    }
} else {
    Write-Host "[-] mysqldump.exe not found at $MysqldumpPath" -ForegroundColor Red
}
