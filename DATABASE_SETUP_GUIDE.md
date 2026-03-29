# FarmFeed Database Setup Guide for Team

## Problem Summary
The fertilizer data **IS** in the repository (`COMPLETE_DATABASE_SCHEMA.sql`), but your team member cannot see it because:
1. ❌ The MySQL database wasn't created
2. ❌ The SQL schema file wasn't imported into MySQL
3. ❌ Database name mismatch between schema and Spring Boot config

## Quick Setup (5 minutes)

### Step 1: Create MySQL Database
```sql
-- Open MySQL and run:
CREATE DATABASE IF NOT EXISTS farmfeed_db;
USE farmfeed_db;
```

### Step 2: Import the Schema and Data
```bash
# Linux/Mac
mysql -u root -p farmfeed_db < farmFeed/COMPLETE_DATABASE_SCHEMA.sql

# Windows (Command Prompt)
mysql -u root -p farmfeed_db < farmFeed\COMPLETE_DATABASE_SCHEMA.sql
```

### Step 3: Verify Data was imported
```sql
-- Check if fertilizer products exist:
SELECT * FROM products WHERE category LIKE '%Fertilizer%';
```

### Step 4: Update MySQL Credentials (if needed)
Edit `farmFeed/src/main/resources/application.properties`:
```properties
# Example if your MySQL password is different:
spring.datasource.url=jdbc:mysql://localhost:3306/farmfeed_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### Step 5: Run the Spring Boot Application
```bash
cd farmFeed
mvn clean install
mvn spring-boot:run
```

### Step 6: Access the Application
- Open browser: `http://localhost:8080`
- You should see the **Navbar, Footer, AND fertilizer products on the homepage**

---

## What Data Gets Imported

### ✅ Fertilizer Products (5 products)
1. **Urea Fertilizer** - ₹600 (100 units in stock)
2. **DAP Fertilizer** - ₹1200 (80 units)
3. **Organic Compost** - ₹400 (150 units)
4. **Potash Fertilizer** - ₹950 (120 units)
5. **Bio Fertilizer** - ₹750 (70 units)

### ✅ Sample Users
- **Farmers**: Rajesh Kumar, Priya Sharma, Amit Patel
- **Vendors**: Green Agro Supplies, Organic Farming Hub
- **Admin**: admin@farmfeed.com / admin@123

---

## If Using Environment Variables (Production)

Set these environment variables instead of editing properties:
```bash
export DB_URL=jdbc:mysql://localhost:3306/farmfeed_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
export DB_USER=root
export DB_PASS=root
export DB_DRIVER=com.mysql.cj.jdbc.Driver
export JPA_DIALECT=org.hibernate.dialect.MySQLDialect
```

---

## Troubleshooting

### ❓ "No data showing on homepage"
- [ ] Check if MySQL is running: `mysql -u root -p`
- [ ] Verify database was created: `SHOW DATABASES;`
- [ ] Check if schema was imported: `USE farmfeed_db; SELECT COUNT(*) FROM products;`

### ❓ "Connection refused error"
- [ ] Ensure MySQL server is running
- [ ] Check database credentials in application.properties
- [ ] Verify firewall isn't blocking port 3306

### ❓ "Only navbar and footer show"
- [ ] This means Java backend is running but database connection failed
- [ ] Check Spring Boot logs for SQL errors
- [ ] Verify fertilizer data exists: `SELECT * FROM products;` in MySQL

---

## For Your Team

**Share this with your team member:**
1. Clone repo: `git clone https://github.com/vedant-2506/FarmFeed.git`
2. Navigate to folder: `cd FarmFeed/farmFeed`
3. **Import database**: `mysql -u root -p farmfeed_db < COMPLETE_DATABASE_SCHEMA.sql`
4. Update credentials if needed in `application.properties`
5. Run: `mvn spring-boot:run`
6. Open: `http://localhost:8080`

✅ **Data will appear!**
