# FarmFeed - Complete Working Solution

## Issues Fixed

### 1. **Database Connection Error**
- **Problem**: Application was looking for `farmfeed_db` database but SQL script creates `farmfeed`
- **Solution**: Updated `application.properties` to use `jdbc:mysql://localhost:3306/farmfeed`

### 2. **Search Bar Not Working**
- **Problem**: `Home.js` script was commented out in `Home.html`
- **Solution**: Uncommented the script tag to enable search functionality

### 3. **Login Form Not Working**
- **Problem**: Form ID mismatch - JavaScript looked for `"LoginForm"` but HTML had `"loginForm"` (case-sensitive)
- **Solution**: Fixed the ID reference in `Login.js`

### 4. **Sign Up Not Working**
- **Problems**: 
  - Duplicate script paths in HTML
  - Missing proper form IDs
  - No backend integration
- **Solution**: Cleaned up HTML, added proper IDs, and integrated with backend API

### 5. **Entity-Database Mapping Mismatch**
- **Problem**: Farmer entity used `farmers` table but database uses `farmer` table with different column names
- **Solution**: Updated entity to map correctly to database schema

---

## Backend Implementation

### Updated Farmer Entity
- Maps to `farmer` table
- Fields: `id`, `name`, `email`, `password`, `phone` (contact_number), `gender`, `address`, `age`

### API Endpoints

#### 1. **Sign Up**
```
POST /api/Farmer/SignUp
Content-Type: application/json

{
  "name": "Farmer Name",
  "email": "farmer@example.com",
  "phone": "9876543210",
  "password": "password123",
  "gender": "Male",
  "address": "Farm Address"
}

Response (Success):
{
  "success": true,
  "message": "Sign up successful",
  "farmer_id": 1,
  "email": "farmer@example.com"
}
```

#### 2. **Login**
```
POST /api/Farmer/Login
Content-Type: application/json

{
  "email": "farmer@example.com",
  "password": "password123"
}

Response (Success):
{
  "success": true,
  "message": "Login successful",
  "farmer_id": 1,
  "email": "farmer@example.com",
  "name": "Farmer Name"
}
```

#### 3. **Search Fertilizers**
```
GET /api/fertilizers/search?name=urea
```

#### 4. **Get All Fertilizers**
```
GET /api/fertilizers
```

---

## Database Setup

### 1. Create Database
Run the `database.sql` script in your MySQL:

```sql
-- This creates the 'farmfeed' database with all tables
source database.sql;
```

### 2. Verify Tables
```sql
USE farmfeed;
SHOW TABLES;
```

---

## Running the Application

### Prerequisites
- **MySQL**: Running on `localhost:3306`
- **Database**: `farmfeed` created with all tables
- **Java**: JDK 17 or higher
- **Maven**: For building and running

### Start Application
```bash
cd c:\Users\swami\OneDrive\Desktop\FarmFeed\farmFeed
mvn clean spring-boot:run
```

The application will start on `http://localhost:9090`

### Access the Web Pages
- **Home**: http://localhost:9090/Home.html
- **Sign Up**: http://localhost:9090/SignUp.html
- **Login**: http://localhost:9090/Login.html
- **Cart**: http://localhost:9090/Cart.html

---

## Frontend Features

### 1. **Search Bar** (Home.html)
- Real-time product search via backend API
- Shows all products if search is empty
- Displays "No products found" if search has no results

### 2. **Sign Up** (SignUp.html)
- Form fields: Name, Email, Phone, Password, Gender, Address
- Validates:
  - All required fields are filled
  - Passwords match
  - Phone number is valid (10+ digits)
- On success: Redirects to Login page and saves email for auto-fill

### 3. **Login** (Login.html)
- Email and password authentication via backend
- Auto-fills email if coming from Sign Up
- Stores session in localStorage
- On success: Redirects to Home page

---

## JavaScript Updates

### Login.js
- Connects to `/api/Farmer/Login` endpoint
- Validates form inputs
- Stores login session in localStorage
- Redirects to Home on success

### SignUp.js
- Connects to `/api/Farmer/SignUp` endpoint
- Validates all fields and password strength
- Saves email to localStorage for Login auto-fill
- Redirects to Login on success

### Home.js
- Connects to `/api/fertilizers` and `/api/fertilizers/search` endpoints
- Handles product search functionality
- Manages shopping cart with localStorage

---

## Testing the Application

### Test Sign Up
1. Go to http://localhost:9090/SignUp.html
2. Fill in form:
   - Name: "John Farmer"
   - Email: "john@example.com"
   - Phone: "9876543210"
   - Password: "Test@123"
   - Confirm: "Test@123"
3. Click "Sign Up"

### Test Login
1. Go to http://localhost:9090/Login.html
2. Email: "john@example.com"
3. Password: "Test@123"
4. Click "Log In"

### Test Search
1. Go to http://localhost:9090/Home.html
2. Type fertilizer name in search box (e.g., "urea", "dap")
3. Click "Search"

---

## Common Issues & Solutions

### Issue: "Connection refused" on Login/Sign Up
**Solution**: Make sure MySQL is running and database is set up with the `farmfeed` database

### Issue: Search returns empty
**Solution**: Make sure sample data is inserted in the fertilizers table (run database.sql)

### Issue: CORS errors in browser console
**Solution**: Already configured with `@CrossOrigin(origins = "*")` in controllers

### Issue: Password validation too strict
**Solution**: You can modify password validation in SignUp.js line ~50

---

## File Structure
```
src/main/java/com/example/farmFeed/
├── entity/
│   └── Farmer.java              (Updated with correct mapping)
├── repository/
│   ├── FarmerRepository.java     (Added login methods)
│   └── FertilizerRepository.java  
├── service/
│   ├── FarmerService.java        (Added login service)
│   └── FertilizerService.java
├── controller/
│   ├── FarmerController.java     (Added Login endpoint)
│   └── FertilizerController.java

src/main/resources/static/
├── Home.html                     (Script uncommented)
├── Login.html                     (Fixed form ID)
├── SignUp.html                   (Proper form IDs)
├── js/
│   ├── Home.js                   (Search functionality)
│   ├── Login.js                  (Login with backend API)
│   └── SignUp.js                 (Sign up with backend API)
│   
application.properties            (Database URL corrected)
```

---

## Next Steps

1. **Ensure MySQL is running** with `farmfeed` database
2. **Run the application** using the command above
3. **Test Sign Up and Login** flows
4. **Test Search functionality** on Home page
5. **Monitor browser console** for any errors

The application should now be fully functional! ✅
