# FarmFeed Git Push & Pull Instructions

## 🚀 Step 1: Push to GitHub (or GitLab/Bitbucket)

### Option A: Using GitHub (Recommended)

#### 1. Create Repository on GitHub

1. Go to [GitHub](https://github.com) and login
2. Click **+** in top right → **New repository**
3. Name it: `FarmFeed`
4. Description: `Agricultural supply marketplace - Team project`
5. Choose **Private** (if you want to limit access to team only)
6. Click **Create repository**

#### 2. Connect Local Repository to GitHub

```bash
cd /home/vedant-2506/Desktop/FarmFeed

# Add GitHub as remote
git remote add origin https://github.com/YOUR_USERNAME/FarmFeed.git

# Verify connection
git remote -v
```

You should see:
```
origin  https://github.com/YOUR_USERNAME/FarmFeed.git (fetch)
origin  https://github.com/YOUR_USERNAME/FarmFeed.git (push)
```

#### 3. Set Main Branch and Push

```bash
# Set main as default branch
git branch -M main

# Push all commits
git push -u origin main
```

First time, GitHub will ask for authentication:
- Use Personal Access Token (recommended)
- Or use GitHub CLI: `gh auth login`

---

### Option B: Using GitLab

```bash
cd /home/vedant-2506/Desktop/FarmFeed

# Create project on gitlab.com first
# Then connect:
git remote add origin https://gitlab.com/YOUR_USERNAME/FarmFeed.git
git branch -M main
git push -u origin main
```

### Option C: Using Bitbucket

```bash
git remote add origin https://bitbucket.org/YOUR_USERNAME/FarmFeed.git
git branch -M main
git push -u origin main
```

---

## ✅ Verify Push Succeeded

```bash
# Check remote branches
git branch -r

# Should show:
# origin/main
# origin/HEAD -> origin/main
```

Visit your repository URL in browser to confirm code is there!

---

## 🔗 Share Repository with Team

### On GitHub:
1. Go to repository Settings
2. Click **Collaborators** (or **Members** for org)
3. Click **Add people**
4. Enter team member's GitHub username
5. Select permission level (Maintainer/Developer/Guest)

### Generate Personal Access Token for HTTPS Auth

If you want to push without SSH:

1. Go to GitHub Settings → Developer settings → **Personal access tokens**
2. Click **Generate new token**
3. Select scopes: `repo`, `workflow`
4. Click **Generate token**
5. Copy the token (you won't see it again!)
6. Use as password when git asks

---

## 📝 After Push - Share with Team

Send teammates this information:

```
🎉 Project is now on GitHub!

Repository URL: https://github.com/YOUR_USERNAME/FarmFeed
Branch: main

To get started:
1. Clone: git clone https://github.com/YOUR_USERNAME/FarmFeed.git
2. Restore DB: mysql -u root -proot < DB/farmfeed_db_backup.sql
3. Run: cd farmFeed && java -jar target/farmfeed-1.0.0.jar

See TEAM_SETUP_GUIDE.md for complete instructions
```

---

## 👥 Team Member: PULLING Code & Database

### First Time Setup

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/FarmFeed.git
cd FarmFeed

# 2. Make sure MySQL is running
sudo systemctl start mysql

# 3. Restore database
mysql -u root -proot < DB/farmfeed_db_backup.sql

# Check if data restored
mysql -u root -proot -e "use FarmFeed; show tables; select count(*) from fertilizer;"

# 4. Build project
cd farmFeed
./mvnw clean package -DskipTests

# 5. Run application
java -jar target/farmfeed-1.0.0.jar

# 6. Open browser
# http://localhost:8080
```

### Daily Workflow: Getting Latest Updates

```bash
# Go to project directory
cd FarmFeed

# Pull latest code
git pull origin main

# If database was updated:
mysql -u root -proot < DB/farmfeed_db_backup.sql

# If code changed significantly:
cd farmFeed
./mvnw clean package -DskipTests
```

---

## 🔄 Workflow: Making Changes & Pushing

### Step 1: Pull Latest

```bash
cd FarmFeed
git pull origin main
```

### Step 2: Create Feature Branch

```bash
git checkout -b feature/your-feature-name

# Examples:
# feature/vendor-dashboard
# feature/cart-improvements
# bugfix/login-error
```

### Step 3: Make Changes

Edit files, add new code, etc.

### Step 4: Commit Changes

```bash
git status                          # See what changed
git add .                           # Stage everything
git commit -m "[FEAT] Your description"
```

Good commit messages:
```
[FEAT] Add vendor inventory page
[BUG] Fix fertilizer search bug
[REFACTOR] Clean up authentication code
[DOC] Update README
```

### Step 5: Push Your Branch

```bash
git push origin feature/your-feature-name
```

### Step 6: Create Pull Request

On GitHub:
1. Go to repository
2. You'll see button "Compare & pull request"
3. Click it
4. Add description of changes
5. Click "Create pull request"
6. Team reviews and approves
7. Click "Merge pull request"

---

## 💾 Pushing Database Changes

When you modify the database (add tables, insert data, etc):

### 1. Export New Backup

```bash
mysqldump -u root -proot FarmFeed > DB/farmfeed_db_backup.sql
```

### 2. Commit & Push

```bash
git add DB/farmfeed_db_backup.sql
git commit -m "[DATABASE] Update schema - add new tables"
git push origin main
```

### 3. Team Members Pull & Restore

```bash
git pull origin main
mysql -u root -proot FarmFeed < DB/farmfeed_db_backup.sql
```

---

## 🆘 Common Issues

### ❌ "Permission denied (publickey)"

SSH authentication failed. Use HTTPS instead:

```bash
git remote set-url origin https://github.com/YOUR_USERNAME/FarmFeed.git
git push origin main
```

### ❌ "fatal: The current branch main has no upstream branch"

```bash
git push -u origin main
```

### ❌ "merge conflict"

```bash
# See conflicts
git status

# Manually edit conflicting files
# Then:
git add .
git commit -m "Resolve merge conflicts"
git push origin main
```

### ❌ "Database restore failed"

```bash
# Check MySQL is running
sudo systemctl status mysql

# Check database exists
mysql -u root -proot -e "show databases;"

# Try importing again
mysql -u root -proot FarmFeed < DB/farmfeed_db_backup.sql
```

---

## 📊 Check What's on GitHub

```bash
# View remote branches
git branch -r

# View remote changes
git fetch origin
git log origin/main --oneline

# Compare your branch with main
git diff main origin/main
```

---

## 🎯 Quick Reference

```bash
# Push to GitHub
git push origin main

# Pull from GitHub
git pull origin main

# Check status
git status

# View commits
git log --oneline -10

# Create backup
mysqldump -u root -proot FarmFeed > DB/farmfeed_db_backup.sql

# Restore backup
mysql -u root -proot < DB/farmfeed_db_backup.sql

# Force pull (overwrite local)
git fetch origin
git reset --hard origin/main
```

---

## ✅ Checklist Before Pushing

- [ ] Code compiles without errors
- [ ] Database backup updated (if needed)
- [ ] Merge conflicts resolved
- [ ] Commit message is clear
- [ ] Tested locally
- [ ] No sensitive data in code
- [ ] Followed .gitignore rules

---

**Questions?** Check your repository's README or ask your team lead! 💬
