#!/bin/bash

# FarmFeed Database & Application Setup Script
# For Linux/Mac Users

set -e  # Exit on error

echo "================================================"
echo "  FarmFeed Complete Setup Script"
echo "================================================"
echo ""

# Check if MySQL is installed
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL is not installed. Please install MySQL first:"
    echo "   macOS: brew install mysql"
    echo "   Ubuntu/Debian: sudo apt-get install mysql-server"
    exit 1
fi

# Check if MySQL is running
if ! mysql -u root -e "SELECT 1" &> /dev/null; then
    echo "⏳ Starting MySQL server..."
    # Try common methods to start MySQL
    if command -v brew &> /dev/null; then
        brew services start mysql || true
    else
        sudo systemctl start mysql || sudo service mysql start || true
    fi
    sleep 2
fi

# Verify MySQL connection
if ! mysql -u root -e "SELECT 1" &> /dev/null; then
    echo "❌ Cannot connect to MySQL. Please:"
    echo "   1. Start MySQL: brew services start mysql (or sudo service mysql start)"
    echo "   2. Ensure root user has no password OR update credentials"
    echo "   3. Or pass credentials: mysql -u root -p<password>"
    exit 1
fi

echo "✅ MySQL is running"
echo ""

# Create database and import schema
echo "📦 Creating database and importing schema..."
mysql -u root < ./COMPLETE_DATABASE_SCHEMA.sql

if [ $? -eq 0 ]; then
    echo "✅ Database created and schema imported successfully!"
else
    echo "❌ Failed to import database schema"
    exit 1
fi

echo ""
echo "📊 Verifying data import..."
PRODUCT_COUNT=$(mysql -u root farmfeed_db -e "SELECT COUNT(*) FROM products;" -N)
FARMER_COUNT=$(mysql -u root farmfeed_db -e "SELECT COUNT(*) FROM farmer;" -N)
VENDOR_COUNT=$(mysql -u root farmfeed_db -e "SELECT COUNT(*) FROM shopkeeper;" -N)

echo "   Products: $PRODUCT_COUNT"
echo "   Farmers: $FARMER_COUNT"
echo "   Vendors: $VENDOR_COUNT"

echo ""
echo "✅ All data imported!"
echo ""

# Check if mvn is available
if ! command -v mvn &> /dev/null; then
    echo "⚠️  Maven not found. Please ensure Maven is installed."
    echo "   macOS: brew install maven"
    echo "   Ubuntu: sudo apt-get install maven"
    echo ""
    echo "📝 Next steps:"
    echo "   1. Install Maven"
    echo "   2. Run: cd farmFeed && mvn clean install"
    echo "   3. Run: mvn spring-boot:run"
    exit 0
fi

echo "🔨 Building Spring Boot application..."
cd farmFeed
mvn clean install -q

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
else
    echo "❌ Build failed. Check for Java/Maven issues."
    exit 1
fi

echo ""
echo "================================================"
echo "  ✅ Setup Complete!"
echo "================================================"
echo ""
echo "🚀 To start the application, run:"
echo "   cd farmFeed"
echo "   mvn spring-boot:run"
echo ""
echo "📱 Then open: http://localhost:8080"
echo ""
echo "✨ You should now see:"
echo "   - Navbar and Footer"
echo "   - Fertilizer Products (Urea, DAP, Compost, Potash, Bio)"
echo "   - Sample Farmers and Vendors"
echo ""
echo "🔐 Sample Login:"
echo "   Admin: admin@farmfeed.com / admin@123"
echo ""
