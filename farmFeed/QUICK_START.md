# FarmFeed - Quick Setup & Testing Checklist

## ✅ All Issues Fixed

### Problems Resolved:
- ✅ Search bar not working (Script was commented)
- ✅ Login page not functional (Form ID mismatch)  
- ✅ Sign Up page not functional (Missing integration & field IDs)
- ✅ Database connection issue (Wrong database name)
- ✅ Entity-database mapping mismatch
- ✅ Missing password field in Sign Up form
- ✅ Missing Login API endpoint

---

## 🚀 Quick Start Guide

### Step 1: Setup MySQL Database
```bash
# Open MySQL and execute:
source C:\Users\swami\OneDrive\Desktop\FarmFeed\farmFeed\database.sql
```

Verify:
```sql
USE farmfeed;
SHOW TABLES;  -- Should show: farmer, fertilizers, cart, orders, etc.
SELECT * FROM fertilizers;  -- Should show sample data
```

### Step 2: Ensure MySQL Connection
- **Host**: localhost
- **Port**: 3306
- **Username**: root
- **Password**: root
- **Database**: farmfeed

If you need to change credentials, update [application.properties](src/main/resources/application.properties)

### Step 3: Start the Application
```bash
cd c:\Users\swami\OneDrive\Desktop\FarmFeed\farmFeed
mvn clean spring-boot:run
```

**Wait for**: `Started FarmFeedApplication in X seconds`

### Step 4: Access Application
Open browser and go to:
- http://localhost:9090/Home.html

---

## 🧪 Test These Features

### 1️⃣ Sign Up Page
**URL**: http://localhost:9090/SignUp.html

**Test Data**:
```
Name: John Farmer
Email: john@farm.com
Phone: 9876543210
Password: Test@123
Confirm: Test@123
Gender: Male
Address: 123 Farm Lane
```

**Expected**: ✅ Success message → Redirects to Login

---

### 2️⃣ Login Page
**URL**: http://localhost:9090/Login.html

**Test with Sign Up Data**:
```
Email: john@farm.com
Password: Test@123
```

**Expected**: ✅ Login successful → Redirects to Home

---

### 3️⃣ Search Feature
**URL**: http://localhost:9090/Home.html

**Test Cases**:
- Search: "urea" → Should show Urea Fertilizer
- Search: "dap" → Should show DAP Fertilizer
- Search: "xyz" → Should show "No products found"
- Empty search → Should show all fertilizers

---

## 📋 What Changed

### Java Files Modified:
1. [Farmer.java](src/main/java/com/example/farmFeed/entity/Farmer.java)
   - Fixed table name: `farmer` (was `farmers`)
   - Fixed field mapping to database columns
   - Correct ID generator

2. [FarmerRepository.java](src/main/java/com/example/farmFeed/repository/FarmerRepository.java)
   - Added `findByEmail()` method
   - Added `findByEmailAndPassword()` method for login

3. [FarmerService.java](src/main/java/com/example/farmFeed/service/FarmerService.java)
   - Added `login()` method
   - Added `findByEmail()` method

4. [FarmerController.java](src/main/java/com/example/farmFeed/controller/FarmerController.java)
   - Enhanced SignUp endpoint with validation
   - Added Login endpoint
   - Added error handling

### HTML Files Modified:
1. [Home.html](src/main/resources/static/Home.html)
   - Uncommented Home.js script

2. [Login.html](src/main/resources/static/Login.html)
   - Fixed script loading

3. [SignUp.html](src/main/resources/static/SignUp.html)
   - Simplified form
   - Added proper IDs to all inputs
   - Fixed script paths

### JavaScript Files Modified:
1. [Login.js](src/main/resources/static/js/Login.js)
   - Now calls `/api/Farmer/Login` endpoint
   - Proper error handling

2. [SignUp.js](src/main/resources/static/js/SignUp.js)
   - Now calls `/api/Farmer/SignUp` endpoint
   - Validates all fields
   - Proper error handling

### Config Files:
1. [application.properties](src/main/resources/application.properties)
   - Changed database URL from `farmfeed_db` to `farmfeed`

---

## 🔍 Verify Everything Works

### Check Browser Console (F12)
Look for successful logs:
- `✅ Search results:` when searching
- `✅ Login successful` when logging in
- No `❌` errors (except maybe CORS if backend down)

### Check Terminal Output
Look for:
```
Started FarmFeedApplication in X seconds
```

### Test the Endpoints Directly (using curl or Postman)

**Sign Up**:
```bash
curl -X POST http://localhost:9090/api/Farmer/SignUp \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test123@farm.com","phone":"9876543210","password":"Test@123"}'
```

**Login**:
```bash
curl -X POST http://localhost:9090/api/Farmer/Login \
  -H "Content-Type: application/json" \
  -d '{"email":"test123@farm.com","password":"Test@123"}'
```

**Search**:
```bash
curl http://localhost:9090/api/fertilizers/search?name=urea
```

---

## ⚠️ If Something Goes Wrong

### Error: "Connection refused"
```
Solution: Make sure MySQL is running
Windows: Start MySQL Service from Services
Command: mysql -u root -p (should connect)
```

### Error: "Unknown database 'farmfeed'"
```
Solution: Run the database.sql script
Check: use farmfeed; show tables;
```

### Error: Port 9090 already in use
```
Solution: Change port in application.properties
Add: server.port=8080
```

### Error: CORS error in browser
```
Console: Already configured with @CrossOrigin(origins = "*")
This should work if backend is running on 9090
```

---

## 📝 Database Schema Quick Reference

### Farmer Table
```
farmer_id (PK)
name
email (UNIQUE)
password
contact_number
gender
address
age
```

### Fertilizers Table
```
fertilizer_id (PK)
name
price
stock
description
feedback
rating_review
```

---

## ✨ Working Features

✅ **Search Bar**: Real-time product search
✅ **Sign Up**: User registration with validation
✅ **Login**: Email & password authentication  
✅ **Shopping Cart**: Add/remove items
✅ **Product Display**: Show all fertilizers

---

## 🎯 Next Steps

1. **Run**: `mvn clean spring-boot:run`
2. **Wait**: For "Started FarmFeedApplication" message
3. **Open**: http://localhost:9090/Home.html
4. **Test**: Sign Up → Login → Search
5. **Debug**: Check browser console (F12) for issues

**Everything should work now!** 🚀
