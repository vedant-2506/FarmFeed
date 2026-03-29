# 👥 Team Setup Instructions - FarmFeed

**For Team Members:** Follow these 5 simple steps to get FarmFeed running with all data.

---

## ✅ STEP 1: Clone the Repository

```bash
git clone https://github.com/vedant-2506/FarmFeed.git
cd FarmFeed
```

---

## ✅ STEP 2: Run the Setup Script

**If you are on Windows:**
- Double-click the file: **`setup.bat`**
- OR open Command Prompt and type: `setup.bat`

**If you are on Mac/Linux:**
```bash
chmod +x setup.sh
./setup.sh
```

✨ **This will automatically:**
- Create the MySQL database
- Import all tables and sample data
- Build the Java application

---

## ✅ STEP 3: Start the Application

**Option A (Automatic with setup script):**
- If setup succeeded, it will tell you the next command

**Option B (Manual - if setup script had issues):**
```bash
cd farmFeed
mvn spring-boot:run
```

---

## ✅ STEP 4: Open Your Browser

Navigate to: **http://localhost:8080**

🎉 **You should see:**
- Navbar at the top
- Footer at the bottom
- **5 Fertilizer Products** on the homepage (Urea, DAP, Compost, Potash, Bio)
- Add to cart functionality

---

## ✅ STEP 5: Test Login (Optional)

**Sample Admin Account:**
- Email: `admin@farmfeed.com`
- Password: `admin@123`

**Sample Farmer Account:**
- Email: `rajesh.kumar@email.com`
- Password: `password123`

---

## ⚠️ Before You Start - Prerequisites

Make sure you have:

- [ ] **MySQL Server** installed and running
  - Download: https://dev.mysql.com/downloads/mysql/
  - Or use Homebrew (Mac): `brew install mysql`
  - Or APT (Ubuntu): `sudo apt-get install mysql-server`

- [ ] **Java 11 or Higher**
  - Check: `java -version`
  - Download: https://www.oracle.com/java/technologies/javase-jdk11-downloads.html

- [ ] **Maven**
  - Check: `mvn -version`
  - Download: https://maven.apache.org/download.cgi

---

## 🐛 Troubleshooting

### "MySQL connection refused"
**Solution:**
```bash
# Start MySQL
# macOS:
brew services start mysql

# Ubuntu:
sudo systemctl start mysql

# Windows: Start from Services
```

### "Only navbar and footer, no products"
This means the database wasn't imported.

**Solution:**
```bash
cd FarmFeed/farmFeed
mysql -u root -p farmfeed_db < COMPLETE_DATABASE_SCHEMA.sql
# Enter your MySQL password when prompted
```

Then restart the app: `mvn spring-boot:run`

### "Port 8080 already in use"
**Solution:**
Edit `farmFeed/src/main/resources/application.properties`:
```properties
server.port=8081
```
Then access: http://localhost:8081

### "Build failed with Maven errors"
**Solution:**
```bash
cd farmFeed
mvn clean install -DskipTests
```

---

## 📞 Need Help?

1. Check **DATABASE_SETUP_GUIDE.md** in the repo root
2. Read **README.md** for detailed documentation
3. See troubleshooting section above
4. Contact the team lead if issues persist

---

## ✅ How to Verify Everything is Working

After following all steps, run this command in MySQL:

```bash
mysql -u root -p
USE farmfeed_db;
SELECT COUNT(*) FROM products;
SELECT * FROM products WHERE category LIKE '%Fertilizer%';
```

You should see **5 fertilizer products** listed.

---

## 🎯 Success Criteria

You're done when you can:
- ✅ See homepage with navbar, footer, and products
- ✅ See 5 fertilizer products listed
- ✅ Add products to cart
- ✅ Login with sample credentials
- ✅ No errors in console

---

**Questions?** Contact your team lead or check the full documentation in `README.md`

🌾 **Welcome to FarmFeed!** 🚀
