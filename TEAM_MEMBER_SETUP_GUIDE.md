# 🌾 FarmFeed - Team Member Setup Guide

## **PROBLEM & SOLUTION**

| Issue | Cause | Solution |
|-------|-------|----------|
| Team member sees empty homepage | They have only 5 sample products in SQL | Import YOUR full database |
| Where are the 1000+ products? | They're in YOUR local MySQL, not in git | Export & share the SQL dump |
| How do we share data? | Push updated SQL to git | Every team member runs 1 SQL file |

---

## 📋 **WHAT NEEDS TO BE DONE**

### **FOR YOU (Project Lead)** - EXPORT YOUR DATABASE
Your database has 1000+ products in your local MySQL, but only 5 sample products are in git. You need to export your full database and push it.

#### **Step 1: Export Your Database with All Products**

```bash
cd FarmFeed/farmFeed

# Replace YOUR_PASSWORD with your actual MySQL root password
mysqldump -u root -pYOUR_PASSWORD farmfeed_db > COMPLETE_DATABASE_SCHEMA_WITH_ALL_DATA.sql

# Verify the file was created and check its size
ls -lh COMPLETE_DATABASE_SCHEMA_WITH_ALL_DATA.sql
```

**Example:**
```bash
mysqldump -u root -p farmfeed_db > COMPLETE_DATABASE_SCHEMA_WITH_ALL_DATA.sql
# When it asks for password, type your MySQL password (it won't show on screen)
```

#### **Step 2: Check How Many Products Were Exported**

```bash
grep -c "INSERT INTO products" COMPLETE_DATABASE_SCHEMA_WITH_ALL_DATA.sql
```

If it shows your 1000+ products, you're good! 🎉

#### **Step 3: Replace the Old Schema File**

```bash
cd FarmFeed/farmFeed

# Backup the old file (optional)
cp COMPLETE_DATABASE_SCHEMA.sql COMPLETE_DATABASE_SCHEMA_BACKUP.sql

# Replace with your full database dump
cp COMPLETE_DATABASE_SCHEMA_WITH_ALL_DATA.sql COMPLETE_DATABASE_SCHEMA.sql
```

#### **Step 4: Commit & Push to Git**

```bash
cd FarmFeed

# Add the updated schema file
git add farmFeed/COMPLETE_DATABASE_SCHEMA.sql

# Commit
git commit -m "[DATA] Update database schema with all 1000+ products"

# Push to repository
git push origin main
```

✅ **Now all your 1000+ products are in git!**

---

## 📋 **FOR TEAM MEMBERS - HOW TO GET ALL PRODUCTS**

### **Step 1: Pull Latest Code (if not already done)**

```bash
cd FarmFeed
git pull origin main
```

### **Step 2: Ensure MySQL is Running**

**Check on Linux/Mac:**
```bash
# MySQL might be running as a service
mysql -u root -p -e "SELECT 1;"
```

**If MySQL is NOT running:**
- Linux: `sudo systemctl start mysql` or `sudo service mysql start`
- Mac: `brew services start mysql`
- Windows: Start MySQL from Services or run: `"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe"`

### **Step 3: Import the Database Schema (WITH All 1000+ Products)**

```bash
cd FarmFeed/farmFeed

# Replace YOUR_PASSWORD with your MySQL root password
mysql -u root -pYOUR_PASSWORD < COMPLETE_DATABASE_SCHEMA.sql

# Verify with empty line when MySQL asks for password
```

**Example on Linux (with password prompt):**
```bash
mysql -u root -p < COMPLETE_DATABASE_SCHEMA.sql
# Type your MySQL root password when prompted
```

### **Step 4: Verify All Products Were Imported**

```bash
# Check product count
mysql -u root -pYOUR_PASSWORD -e "SELECT COUNT(*) as total_products FROM farmfeed_db.products;"

# Should show: 1000+ products ✓
```

### **Step 5: Update Your Database Credentials (if different)**

Edit `farmFeed/src/main/resources/application.properties`:

```properties
# Check these values match YOUR MySQL setup:
spring.datasource.url=jdbc:mysql://localhost:3306/farmfeed_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD_HERE  # ← Change if needed
```

### **Step 6: Run the Application**

```bash
cd farmFeed
mvn clean install
mvn spring-boot:run
```

### **Step 7: Visit the Homepage**

🌐 Open: **http://localhost:8080**

You should now see:
- ✅ Navbar with logo/menu
- ✅ Footer 
- ✅ **1000+ Products on the homepage!**

---

## 🔧 **TROUBLESHOOTING**

### ❓ Team Member Still Sees No Products After Importing SQL?

**Check 1: Verify MySQL Has the Data**
```bash
mysql -u root -p
> USE farmfeed_db;
> SELECT COUNT(*) FROM products;
```
Should show 1000+ products.

**Check 2: MySQL Connection Error?**
- Verify credentials in `application.properties` match your MySQL setup
- Check MySQL is running: `mysql -u root -p -e "SELECT 1;"`

**Check 3: Spring Boot Can't Connect?**
- Check logs in terminal for SQL errors
- Try: `mysql -u root -p farmfeed_db -e "SELECT COUNT(*) FROM products;"`
- Ensure `createDatabaseIfNotExist=true` in properties

**Check 4: Still Only Seeing 5 Products?**
- They imported the OLD schema file before you pushed the update
- Run: `git pull origin main` to get the latest schema
- Delete old database: `mysql -u root -p -e "DROP DATABASE farmfeed_db;"`
- Re-import: `mysql -u root -pYOUR_PASSWORD < COMPLETE_DATABASE_SCHEMA.sql`

---

## ✅ **QUICK CHECKLIST FOR TEAM MEMBER**

- [ ] Git pull latest code
- [ ] MySQL is running
- [ ] MySQL import completed successfully
- [ ] Product count verified (1000+)
- [ ] Database credentials match in `application.properties`
- [ ] `mvn clean install` completed
- [ ] `mvn spring-boot:run` started
- [ ] http://localhost:8080 shows 1000+ products
- [ ] Report any issues to the project lead

---

## 🎯 **KEY TAKEAWAY**

**Your 1000+ products are stored in MySQL on YOUR computer**
- They're not automatically shared  
- You must EXPORT them to a SQL file
- PUSH that SQL file to git
- THEN team members can IMPORT it

**Once everyone imports the same SQL file, everyone sees the same 1000+ products!** 🚀

---

## 📱 **Need Help?**

This guide covers:
- ✅ How to export database
- ✅ How to share with team
- ✅ How team members import data
- ✅ Common troubleshooting

All team members should follow the "FOR TEAM MEMBERS" section after you complete "FOR YOU" section.
