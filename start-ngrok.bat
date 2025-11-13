@echo off
REM =============================================
REM Script de chay ngrok cho VNPay Callback
REM =============================================
REM 
REM Script nay se chay PowerShell script ma khong can cai dat gi them
REM
REM =============================================

echo =============================================
echo Starting Ngrok for VNPay Callback
echo =============================================
echo.

REM Chay PowerShell script voi ExecutionPolicy Bypass
REM Khong can cai dat gi them, chi can PowerShell co san trong Windows
powershell.exe -ExecutionPolicy Bypass -NoProfile -File "%~dp0start-ngrok.ps1"

REM Neu PowerShell script exit, dong cua so
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Script da dung voi loi. Nhan phim bat ky de dong...
    pause >nul
)



