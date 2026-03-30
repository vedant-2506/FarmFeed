# 🌾 FarmFeed - Agricultural Commerce Platform

A comprehensive Java Spring Boot platform connecting farmers directly with vendors for agricultural products and supplies.

## 📋 Table of Contents
- [Quick Start](#-quick-start)
- [Project Structure](#-project-structure)
- [Database Setup](#-database-setup)
- [Running the Application](#-running-the-application)
- [Sample Data](#-sample-data)
- [Team Collaboration](#-team-collaboration-important)
- [Troubleshooting](#-troubleshooting)

---

## ⚡ Quick Start

### For Windows Users
```bash
cd FarmFeed
setup.bat
```

### For Linux/Mac Users
```bash
cd FarmFeed
chmod +x setup.sh
./setup.sh
```

Then open: **http://localhost:8080**

---

## 📁 Project Structure

```
FarmFeed/
├── DATABASE_SETUP_GUIDE.md          # Detailed database setup instructions
├── setup.sh                         # Automated setup for Linux/Mac
├── setup.bat                        # Automated setup for Windows
├── README.md                        # This file
├── farmFeed/
│   ├── COMPLETE_DATABASE_SCHEMA.sql # Database schema + sample data
│   ├── pom.xml                      # Maven dependencies
│   ├── Dockerfile                   # Docker containerization
│   └── src/
│       ├── main/
│       │   ├── java/
│       │   │   └── com/example/farmFeed/
│       │   │       ├── controller/  # REST API endpoints
│       │   │       ├── service/     # Business logic
│       │   │       ├── entity/      # JPA entities
│       │   │       ├── repository/  # Database access
│       │   │       └── config/      # Spring configurations
│       │   └── resources/
│       │       ├── application.properties  # Configuration
│       │       └── static/          # Frontend (HTML/CSS/JS)
│       │           ├── Home.html
│       │           ├── Login.html
│       │           ├── SignUp.html
│       │           ├── Cart.html
│       │           ├── Checkout.html
│       │           └── css/, js/    # Stylesheets & scripts
│       └── test/                    # Unit tests
```

---

## 🗄️ Database Setup

### Prerequisites
- **MySQL Server 8.0+** installed and running
- **Java 11+** installed
- **Maven 3.6+** installed

### Automatic Setup (Recommended)

**Linux/Mac:**
```bash
cd FarmFeed
chmod +x setup.sh
./setup.sh
```

**Windows:**
```bash
cd FarmFeed
setup.bat
```

### Manual Setup

1. **Create the database:**
   ```bash
   cd FarmFeed/farmFeed
   mysql -u root -p < COMPLETE_DATABASE_SCHEMA.sql
   ```

2. **When prompted, enter your MySQL root password**

3. **Verify setup:**
   ```bash
   mysql -u root -p
   USE farmfeed_db;
   SELECT COUNT(*) FROM products;
   SELECT * FROM products WHERE category LIKE '%Fertilizer%';
   ```

---

## 🚀 Running the Application

### Option 1: Maven Command
```bash
cd farmFeed
mvn clean install
mvn spring-boot:run
```

### Option 2: Run JAR File (After Building)
```bash
cd farmFeed
mvn clean package
java -jar target/farmfeed-1.0.0.jar
```

### Option 3: Docker
```bash
docker build -t farmfeed .
docker run -p 8080:8080 farmfeed
```

### Access the Application
Open your browser: **http://localhost:8080**

---

## 📦 Sample Data Included

### ✅ Products (5 Fertilizers)
| Product | Category | Price | Stock | Rating |
|---------|----------|-------|-------|--------|
| Urea Fertilizer | Chemical | ₹600 | 100 | 4.5/5 |
| DAP Fertilizer | Chemical | ₹1200 | 80 | 4.3/5 |
| Organic Compost | Organic | ₹400 | 150 | 4.8/5 |
| Potash Fertilizer | Chemical | ₹950 | 120 | 4.2/5 |
| Bio Fertilizer | Bio | ₹750 | 70 | 4.6/5 |

### ✅ Sample Users
**Farmers:**
- Rajesh Kumar (9876543210) / password123
- Priya Sharma (9876543211) / password123
- Amit Patel (9876543212) / password123

**Vendors:**
- Green Agro Supplies (john@greenaogro.com)
- Organic Farming Hub (sarah@organicfarm.com)

**Admin:**
- Email: `admin@farmfeed.com`
- Password: `admin@123`

---

## 🔧 Configuration

### Database Configuration
File: `farmFeed/src/main/resources/application.properties`

**Local Development (Default):**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/farmfeed_db
spring.datasource.username=root
spring.datasource.password=root
```

**Environment Variables (Production):**
```bash
export DB_URL=jdbc:mysql://your-server:3306/farmfeed_db
export DB_USER=your_user
export DB_PASS=your_password
```

### Application Port
Default: `8080` (Change in application.properties if needed)

---

## 🐛 Troubleshooting

### ❌ "Database connection refused"
**Solution:**
```bash
# Check if MySQL is running
mysql -u root -p

# If not running:
# macOS: brew services start mysql
# Ubuntu: sudo systemctl start mysql
# Windows: Start MySQL from Services
```

### ❌ "Only navbar and footer showing, no products"
**This means:** Database connection failed or data wasn't imported

**Solution:**
```bash
# Verify database exists and has data
mysql -u root -p
USE farmfeed_db;
SELECT COUNT(*) FROM products;

# If empty, import schema:
mysql -u root -p farmfeed_db < COMPLETE_DATABASE_SCHEMA.sql
```

### ❌ "Maven build fails"
**Solution:**
```bash
# Clear Maven cache and rebuild
mvn clean install -DskipTests

# If still failing, check Java version
java -version
# Should be 11 or higher
```

### ❌ "Port 8080 already in use"
**Solution:**
In `application.properties`, change:
```properties
server.port=8081
# Then access: http://localhost:8081
```

---

## 👥 Team Collaboration Guide

### For Team Members Cloning the Repo

1. **Clone the repository:**
   ```bash
   git clone https://github.com/vedant-2506/FarmFeed.git
   cd FarmFeed
   ```

2. **Run the setup script:**
   - **Windows:** Double-click `setup.bat`
   - **Linux/Mac:** Run `./setup.sh`

3. **Or manually set up:**
   ```bash
   # Import database
   mysql -u root -p < farmFeed/COMPLETE_DATABASE_SCHEMA.sql
   
   # Build and run
   cd farmFeed
   mvn spring-boot:run
   ```

4. **Open browser:** http://localhost:8080 ✅

### Important Notes
- ⚠️ **Database schema is auto-generated** from Spring Boot entities
- ✅ **Sample data is included** in the SQL file
- 📝 **First-time setup required** (MySQL import)
- 🔄 **After first setup, just run:** `mvn spring-boot:run`

---

## 📱 Features

### Farmer Portal
- User registration and profile management
- Browse and purchase agricultural products
- Add items to cart and checkout
- Order tracking and history
- Product reviews and ratings

### Vendor Portal  
- Vendor registration and shop management
- Add and manage product inventory
- Track sales and orders
- View revenue and analytics
- Manage shop details and bank information

### Admin Dashboard
- Approve/reject vendor applications
- Monitor platform activity
- View all users and transactions
- System administration

---

## 🛠️ Technology Stack

- **Backend:** Java 11+, Spring Boot 2.x, Spring Data JPA
- **Database:** MySQL 8.0+
- **Frontend:** HTML5, CSS3, JavaScript, Bootstrap
- **Build Tool:** Maven
- **Containerization:** Docker
- **ORM:** Hibernate

---

## 📄 License

This project is part of the FarmFeed initiative for agricultural technology.

---

## 📞 Support

If you encounter issues:

1. **Check DATABASE_SETUP_GUIDE.md** for detailed setup instructions
2. **See Troubleshooting section** above
3. **Check Spring Boot logs** for error messages:
   ```bash
   # Logs show in console when running mvn spring-boot:run
   ```

---

## ✅ Verification Checklist

After setup, verify:
- [ ] MySQL server is running
- [ ] Database `farmfeed_db` exists
- [ ] Tables created successfully
- [ ] Sample data imported (5 fertilizers visible)
- [ ] Spring Boot app starts without errors
- [ ] Homepage loads with navbar, footer, and products
- [ ] Can login with sample credentials

---

## 👥 Team Collaboration (IMPORTANT!)

### ⚠️ Why Your Team Member Doesn't See Products?

Your local database has 1000+ products, but they are **NOT** automatically shared with team members because:
1. **Database data lives locally** in MySQL, not in git
2. **Only the schema structure** is in the SQL file
3. **Team members need to import your data** to see the same products

### ✅ How to Share Your 1000+ Products with Team

**See [TEAM_MEMBER_SETUP_GUIDE.md](TEAM_MEMBER_SETUP_GUIDE.md) for detailed instructions:**

1. **You (Project Lead):**
   - Export your database with `mysqldump`
   - Push the updated SQL file to git
   - Share the guide with team members

2. **Team Members:**
   - Pull the latest code from git
   - Import the SQL file into their MySQL
   - Run the application

This ensures everyone has the **exact same data and products**!

📖 **Read [TEAM_MEMBER_SETUP_GUIDE.md](TEAM_MEMBER_SETUP_GUIDE.md) now!**

---

**Last Updated:** March 30, 2024  
**Version:** 1.0.0  
**Repository:** https://github.com/vedant-2506/FarmFeed
