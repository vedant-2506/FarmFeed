#!/bin/bash

# FarmFeed Team Setup Script
# Run this to quickly set up the project after cloning

echo "🚀 FarmFeed Setup Script"
echo "======================="

# Check Java
echo "✓ Checking Java..."
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install Java 17+"
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | grep "version" | awk '{print $3}')
echo "✓ Java version: $JAVA_VERSION"

# Check Maven
echo "✓ Checking Maven..."
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven not found. Please install Maven"
    exit 1
fi
echo "✓ Maven found"

# Check MySQL
echo "✓ Checking MySQL..."
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL not found. Please install MySQL"
    exit 1
fi
echo "✓ MySQL found"

# Restore database
echo ""
echo "🗄️ Restoring database..."
mysql -u root -proot < DB/farmfeed_db_backup.sql 2>&1 | grep -i "error" && echo "⚠️ Check database restore" || echo "✓ Database restored"

# Build project
echo ""
echo "🔨 Building project..."
cd farmFeed
./mvnw clean package -DskipTests -q
if [ $? -eq 0 ]; then
    echo "✓ Build successful"
else
    echo "❌ Build failed"
    exit 1
fi

echo ""
echo "✅ Setup complete!"
echo ""
echo "📝 Next steps:"
echo "1. cd farmFeed"
echo "2. java -jar target/farmfeed-1.0.0.jar"
echo "3. Open http://localhost:8080"
echo ""
