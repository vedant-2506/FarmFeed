# FarmFeed Team Setup Guide 🚀

This guide helps team members set up the FarmFeed project on their local machine and stay in sync with Git.

---

## 📋 Initial Setup (First Time)

### 1️⃣ Clone the Repository

```bash
git clone <repository-url>
cd FarmFeed
```

Replace `<repository-url>` with your actual GitHub/GitLab repository URL.

### 2️⃣ Set Up Java & Maven

Make sure you have:
- **Java 17+** (Check: `java -version`)
- **Maven 3.8+** (Check: `mvn -version`)

### 3️⃣ Set Up MySQL Database

First, ensure MySQL is running:

```bash
# On Linux/Mac
sudo systemctl start mysql

# On Windows
net start MySQL80
```

Then restore the database:

```bash
# Option A: Using the backup file from Git
mysql -u root -proot < DB/farmfeed_db_backup.sql

# Option B: Using mysqldump directly
cd FarmFeed
chmod +x DB/farmfeed_db_backup.sql
mysql -u root -proot FarmFeed < DB/farmfeed_db_backup.sql
```

### 4️⃣ Build & Run the Project

```bash
cd farmFeed
./mvnw clean package -DskipTests

# Start the server
java -jar target/farmfeed-1.0.0.jar

# OR use Maven to run directly
./mvnw spring-boot:run
```

The application will be available at: **http://localhost:8080**

---

## 🔄 Daily Workflow

### Before Starting Work

```bash
# Pull latest changes
git pull origin main
```

### After Making Changes

```bash
# See what changed
git status

# Stage your changes
git add .

# Commit with meaningful message
git commit -m "Add vendor inventory feature"

# Push to remote
git push origin main
```

### If Someone Updated the Database

```bash
# Pull the updated backup
git pull origin main

# Restore the database
mysql -u root -proot FarmFeed < DB/farmfeed_db_backup.sql
```

---

## 💾 Pushing Database Changes to Git

When you make changes to the database (add tables, insert important data, etc):

### 1. Export the Database

```bash
mysqldump -u root -proot FarmFeed > DB/farmfeed_db_backup.sql
```

### 2. Commit & Push

```bash
git add DB/farmfeed_db_backup.sql
git commit -m "Update database schema"
git push origin main
```

---

## 👥 Team Member Pulling Code

### First Time Setup

```bash
# 1. Clone repo
git clone <repository-url>
cd FarmFeed

# 2. Restore database
mysql -u root -proot < DB/farmfeed_db_backup.sql

# 3. Build & run
cd farmFeed
./mvnw clean package -DskipTests
java -jar target/farmfeed-1.0.0.jar
```

### Pulling Latest Updates

```bash
# Get latest code
git pull origin main

# If database was updated, restore it
mysql -u root -proot FarmFeed < DB/farmfeed_db_backup.sql

# Rebuild if needed
./mvnw clean package -DskipTests
```

---

## 📝 Important Files in Git

| File/Folder | Purpose |
|-----------|---------|
| `farmFeed/src/` | All source code (Java, HTML, CSS, JS) |
| `farmFeed/pom.xml` | Maven dependencies & build config |
| `farmFeed/application.properties` | Application configuration |
| `DB/farmfeed_db_backup.sql` | Complete database schema + data |
| `.gitignore` | Excludes `target/`, logs, etc. |
| `COMPLETE_DATABASE_SCHEMA.sql` | Database schema reference |

---

## ⚠️ What's NOT Pushed to Git

These are automatically ignored (see `.gitignore`):

- `target/` - Compiled build files
- `*.jar` - JAR files (except keeping backup)
- `*.log` - Log files
- `.vscode/`, `.idea/` - IDE settings
- `node_modules/` - Dependencies
- `.env` - Environment secrets

---

## 🔐 Managing Database Credentials

**For local development:**
- Username: `root`
- Password: `root`
- Database: `FarmFeed`

These are set in: `farmFeed/src/main/resources/application.properties`

**For production:** Use environment variables:
```bash
export DB_URL=jdbc:mysql://prod-server:3306/FarmFeed
export DB_USER=prod_user
export DB_PASS=secure_password
```

---

## 🆘 Troubleshooting

### ❌ "Git not initialized"
```bash
cd FarmFeed && git init
```

### ❌ "MySQL connection failed"
```bash
# Check if MySQL is running
sudo systemctl status mysql

# Start MySQL
sudo systemctl start mysql
```

### ❌ "Port 8080 already in use"
```bash
# Kill the process using port 8080
lsof -i :8080
kill -9 <PID>

# Or use a different port
java -jar target/farmfeed-1.0.0.jar --server.port=8081
```

### ❌ "Build failed"
```bash
# Clean everything and rebuild
./mvnw clean
./mvnw package -DskipTests
```

---

## 📱 Recommended Git Workflow

### Branch Strategy
```bash
# Create feature branch
git checkout -b feature/vendor-dashboard

# Do your work...
git add .
git commit -m "Add vendor dashboard"

# Merge to main
git checkout main
git merge feature/vendor-dashboard
git push origin main
```

### Commit Message Format
```
[TYPE] Brief description

Optional detailed explanation here

Example:
[FEAT] Add vendor inventory API endpoint
[BUG] Fix fertilizer ID parsing issue
[REFACTOR] Clean up AddProduct.html
[DOC] Update setup instructions
```

---

## ✅ Checklist Before Pushing

- [ ] Code compiles without errors
- [ ] No breaking changes
- [ ] Updated database? → Exported backup
- [ ] Commit message is descriptive
- [ ] Pulled latest changes before pushing
- [ ] Tested locally

---

## 🚀 Quick Commands Reference

```bash
# Clone
git clone <url>

# Check status
git status

# Pull latest
git pull origin main

# Add and commit
git add .
git commit -m "message"

# Push
git push origin main

# View history
git log --oneline

# Restore database
mysql -u root -proot FarmFeed < DB/farmfeed_db_backup.sql

# Export database
mysqldump -u root -proot FarmFeed > DB/farmfeed_db_backup.sql

# Build project
./mvnw clean package -DskipTests

# Run project
java -jar target/farmfeed-1.0.0.jar
```

---

**Questions? Ask your team lead or check the repository documentation!** 💬
