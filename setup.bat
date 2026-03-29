@echo off
REM FarmFeed Database & Application Setup Script
REM For Windows Users

echo.
echo ================================================
echo   FarmFeed Complete Setup Script (Windows)
echo ================================================
echo.

REM Check if MySQL is in PATH
where mysql >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ MySQL is not in your PATH
    echo.
    echo Please either:
    echo 1. Install MySQL from: https://dev.mysql.com/downloads/mysql/
    echo 2. OR add MySQL bin directory to PATH:
    echo    - Usually: C:\Program Files\MySQL\MySQL Server 8.0\bin
    echo.
    pause
    exit /b 1
)

echo ✅ MySQL found
echo.

REM Try to connect to MySQL (requires no password or default root:root)
echo Attempting to connect to MySQL...
mysql -u root -e "SELECT 1" >nul 2>nul

if %errorlevel% neq 0 (
    echo.
    echo ⚠️  Cannot connect with default credentials (root:root)
    echo.
    echo If MySQL needs a password, edit this script:
    echo Add -p before the database name in the mysql commands
    echo Example: mysql -p -u root farmfeed_db ^< COMPLETE_DATABASE_SCHEMA.sql
    echo.
    set /p password="Enter MySQL root password (or press Enter for no password): "
    
    if not "!password!"=="" (
        set "MYSQL_CMD=mysql -u root -p!password!"
    ) else (
        set "MYSQL_CMD=mysql -u root"
    )
) else (
    set "MYSQL_CMD=mysql -u root"
    echo ✅ Connected successfully!
)

echo.
echo 📦 Creating database and importing schema...
%MYSQL_CMD% < COMPLETE_DATABASE_SCHEMA.sql

if %errorlevel% neq 0 (
    echo.
    echo ❌ Failed to import database schema
    echo Please check MySQL connection and try again
    pause
    exit /b 1
)

echo ✅ Database created and schema imported successfully!
echo.

echo 📊 Verifying data import...
for /f %%i in ('%MYSQL_CMD% farmfeed_db -e "SELECT COUNT(*) FROM products;" -N') do set PRODUCT_COUNT=%%i
for /f %%i in ('%MYSQL_CMD% farmfeed_db -e "SELECT COUNT(*) FROM farmer;" -N') do set FARMER_COUNT=%%i  
for /f %%i in ('%MYSQL_CMD% farmfeed_db -e "SELECT COUNT(*) FROM shopkeeper;" -N') do set VENDOR_COUNT=%%i

echo    Products: %PRODUCT_COUNT%
echo    Farmers: %FARMER_COUNT%
echo    Vendors: %VENDOR_COUNT%

echo.
echo ✅ All data imported!
echo.

REM Check if Maven is available
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ⚠️  Maven not found in PATH
    echo.
    echo Please install Maven from: https://maven.apache.org/download.cgi
    echo Then add it to your PATH and run this script again
    echo.
    pause
    exit /b 0
)

echo 🔨 Building Spring Boot application...
cd farmFeed
call mvn clean install -q

if %errorlevel% neq 0 (
    echo.
    echo ❌ Build failed. Check for Java/Maven issues
    pause
    exit /b 1
)

echo ✅ Build successful!
echo.
echo ================================================
echo   ✅ Setup Complete!
echo ================================================
echo.
echo 🚀 To start the application, run:
echo    cd farmFeed
echo    mvn spring-boot:run
echo.
echo 📱 Then open: http://localhost:8080
echo.
echo ✨ You should now see:
echo    - Navbar and Footer
echo    - Fertilizer Products (Urea, DAP, Compost, Potash, Bio)
echo    - Sample Farmers and Vendors
echo.
echo 🔐 Sample Login:
echo    Admin: admin@farmfeed.com / admin@123
echo.
pause
