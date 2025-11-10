@echo off
REM =============================================
REM Build WAR File for WebEcommerce
REM =============================================
REM 
REM Script này sẽ build WAR file từ NetBeans project
REM Sử dụng Apache Ant để build project
REM
REM =============================================

echo =============================================
echo Building WebEcommerce WAR File
echo =============================================
echo.

REM Kiểm tra Ant có được cài đặt không
where ant >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Apache Ant chua duoc cai dat!
    echo Vui long cai dat Apache Ant hoac su dung NetBeans de build.
    echo.
    echo Download Ant: https://ant.apache.org/
    pause
    exit /b 1
)

echo [INFO] Dang build project...
echo.

REM Build project
ant dist

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Build that bai!
    pause
    exit /b 1
)

echo.
echo =============================================
echo Build thanh cong!
echo =============================================
echo.
echo WAR file da duoc tao tai: dist\WebEcommerce.war
echo.
echo Ban co the upload file nay len VPS cua inet.vn
echo.
pause


