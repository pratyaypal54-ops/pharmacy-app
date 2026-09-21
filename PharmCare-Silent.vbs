Set WshShell = CreateObject("WScript.Shell")
WshShell.Run chr(34) & Replace(WScript.ScriptFullName, WScript.ScriptName, "PharmCare-POS.bat") & chr(34), 0
Set WshShell = Nothing
