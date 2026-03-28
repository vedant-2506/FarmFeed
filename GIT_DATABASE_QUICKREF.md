# FarmFeed Git & Database Quick Reference

## 🚀 First Time Setup

```bash
# Clone repository
git clone <your-repo-url>
cd FarmFeed

# Restore database
mysql -u root -proot < DB/farmfeed_db_backup.sql

# Build and run
cd farmFeed
./mvnw clean package -DskipTests
java -jar target/farmfeed-1.0.0.jar
```

Visit: **http://localhost:8080**

---

## 📝 Daily Workflow

### Start your day
```bash
git pull origin main                    # Get latest code
mysql -u root -proot FarmFeed < DB/farmfeed_db_backup.sql  # Sync database
```

### After making code changes
```bash
git status                              # See what changed
git add .                               # Stage changes
git commit -m "Brief description"       # Commit
git push origin main                    # Push to server
```

### After database changes
```bash
mysqldump -u root -proot FarmFeed > DB/farmfeed_db_backup.sql
git add DB/farmfeed_db_backup.sql
git commit -m "Update database"
git push origin main
```

---

## 🔄 Team Collaboration

### Get latest updates
```bash
git pull origin main
```

### See who changed what
```bash
git log --oneline
git diff HEAD~1
```

### Share your branch with team
```bash
git checkout -b my-feature
# ... make changes ...
git push origin my-feature
# Create Pull Request on GitHub
```

---

## 📊 Database Operations

### Export database
```bash
mysqldump -u root -proot FarmFeed > DB/farmfeed_db_backup.sql
```

### Restore database
```bash
mysql -u root -proot < DB/farmfeed_db_backup.sql
```

### Backup to specific file (dated)
```bash
mysqldump -u root -proot FarmFeed > DB/backup_$(date +%Y%m%d_%H%M%S).sql
```

### View what's in database
```bash
mysql -u root -proot
> use FarmFeed;
> show tables;
> select count(*) from fertilizer;
```

---

## 🛠️ Troubleshooting

### SSH key not configured
```bash
ssh-keygen -t ed25519 -C "your-email@example.com"
# Add public key to GitHub Settings > SSH Keys
```

### Can't push - need to pull first
```bash
git pull origin main
git push origin main
```

### Accidentally changed database
```bash
# Restore from Git backup
mysql -u root -proot FarmFeed < DB/farmfeed_db_backup.sql
```

### Port 8080 in use
```bash
# Find and kill process
lsof -i :8080
kill -9 <PID>

# Or use different port
java -jar target/farmfeed-1.0.0.jar --server.port=9090
```

---

## 📋 Commit Message Examples

```
✅ Good:
[FEAT] Add vendor inventory dashboard
[BUG] Fix fertilizer ID parsing error
[REFACTOR] Simplify AddProduct.js
[DOC] Update team setup guide

❌ Avoid:
Fix things
update
asdf
change

```

---

## 🔐 Never Push to Git

- Passwords or API keys
- Large binary files (> 10MB)
- Log files
- Generated build files (target/, dist/)
- IDE settings (.vscode, .idea)

These are auto-ignored by `.gitignore`

---

## 📞 Need Help?

- Check Git status: `git status`
- View changes: `git diff`
- Undo last commit: `git reset --soft HEAD~1`
- View commit history: `git log -5`
- Clear local changes: `git checkout .`

---

## 🎯 Project Structure

```
FarmFeed/
├── farmFeed/                 ← Main project (Java/Spring Boot)
│   ├── src/main/java/       ← Backend code
│   ├── src/main/resources/  ← HTML, CSS, JS, properties
│   ├── pom.xml              ← Dependencies
│   └── target/              ← Build output (NOT in Git)
├── DB/                       ← Database files
│   └── farmfeed_db_backup.sql ← Complete database backup
├── .gitignore               ← What to ignore
├── TEAM_SETUP_GUIDE.md      ← Setup instructions
└── setup.sh                 ← Automated setup script
```

---

**Last Updated:** March 28, 2026
