#!/bin/bash

# FarmFeed Database & Application Setup Script
# For Linux/Mac Users

set -e  # Exit on error

SQL_FILE="./farmFeed/COMPLETE_DATABASE_SCHEMA.sql"
MVN_CMD="./farmFeed/mvnw"

print_usage() {
    echo "Usage: $0 [-p MYSQL_ROOT_PASSWORD]"
    echo "  -p MYSQL_ROOT_PASSWORD   optional MySQL root password (or set MYSQL_ROOT_PASSWORD env var)"
}

# parse args
while getopts 'p:h' flag; do
  case "${flag}" in
    p) MYSQL_ROOT_PASSWORD="${OPTARG}" ;;
    h) print_usage; exit 0 ;;
    *) print_usage; exit 1 ;;
  esac
done

MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-$1}"

echo "================================================"
echo "  FarmFeed Complete Setup Script"
echo "================================================"
echo ""

# Check SQL file exists
if [ ! -f "$SQL_FILE" ]; then
    echo "❌ Cannot find SQL file: $SQL_FILE"
    echo "   Make sure you run this script from the repository root."
    exit 1
fi

# Check if MySQL client is installed
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL client is not installed. Please install MySQL client/server first:" 
    echo "   macOS: brew install mysql"
    echo "   Ubuntu/Debian: sudo apt-get install mysql-server mysql-client"
    exit 1
fi

# Helper to run mysql commands with or without password
mysql_cmd() {
  if [ -n "$MYSQL_ROOT_PASSWORD" ]; then
    mysql -u root -p"$MYSQL_ROOT_PASSWORD" "$@"
  else
    mysql -u root "$@"
  fi
}

# Check if MySQL server is running (try simple query)
if ! mysql_cmd -e "SELECT 1" &> /dev/null; then
    echo "⏳ MySQL doesn't respond. Attempting to start MySQL server..."
    # Try common methods to start MySQL
    if command -v brew &> /dev/null; then
        brew services start mysql || true
    else
        sudo systemctl start mysql || sudo service mysql start || true
    fi
    sleep 3
fi

# Verify MySQL connection again
if ! mysql_cmd -e "SELECT 1" &> /dev/null; then
    echo "❌ Cannot connect to MySQL. Please:"
    echo "   1. Start MySQL: brew services start mysql (or sudo service mysql start)"
    echo "   2. Ensure root credentials are correct or pass them via -p"
    echo "   3. Or run: mysql -u root -p"
    exit 1
fi

echo "✅ MySQL is running"
echo ""

# Create database and import schema
echo "📦 Creating database and importing schema from $SQL_FILE ..."

# Use mysql client to import file
if mysql_cmd < "$SQL_FILE"; then
    echo "✅ Database created and schema imported successfully!"
else
    echo "❌ Failed to import database schema"
    exit 1
fi

# Verify import counts
echo ""
echo "📊 Verifying data import..."
PRODUCT_COUNT=$(mysql_cmd -D farmfeed_db -N -e "SELECT COUNT(*) FROM products;" 2>/dev/null || echo "0")
FARMER_COUNT=$(mysql_cmd -D farmfeed_db -N -e "SELECT COUNT(*) FROM farmer;" 2>/dev/null || echo "0")
VENDOR_COUNT=$(mysql_cmd -D farmfeed_db -N -e "SELECT COUNT(*) FROM shopkeeper;" 2>/dev/null || echo "0")

echo "   Products: $PRODUCT_COUNT"
echo "   Farmers: $FARMER_COUNT"
echo "   Vendors: $VENDOR_COUNT"

echo ""
echo "✅ Data import step finished."

echo ""
# Build using Maven wrapper
if [ ! -x "$MVN_CMD" ]; then
    echo "⚠️  Maven wrapper not found or not executable at $MVN_CMD"
    echo "   Ensure Maven is installed or make the wrapper executable: chmod +x $MVN_CMD"
    echo "   You can also run: mvn clean install inside farmFeed folder if mvn is installed."
else
    echo "🔨 Building Spring Boot application using Maven wrapper..."
    cd farmFeed
    $MVN_CMD clean install -q
    if [ $? -eq 0 ]; then
        echo "✅ Build successful!"
    else
        echo "❌ Build failed. Check for Java/Maven issues.";
        exit 1
    fi
fi

echo ""
echo "================================================"
echo "  ✅ Setup Complete!"
echo "================================================"
echo ""
echo "🚀 To start the application, run:" 
echo "   cd farmFeed"
echo "   ./mvnw spring-boot:run"
echo ""
echo "📱 Then open: http://localhost:8080 or http://localhost:8080/Home.html"
echo ""
echo "✨ You should now see:" 
echo "   - Navbar and Footer"
echo "   - Fertilizer Products (Urea, DAP, Compost, Potash, Bio)"
echo "   - Sample Farmers and Vendors"
echo ""
echo "🔐 Sample Login:"
echo "   Admin: admin@farmfeed.com / admin@123"
echo ""
exit 0
