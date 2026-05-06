# FarmFeed - Fertilizer E-Commerce Management System

## 🌾 Project Overview

FarmFeed is a comprehensive e-commerce platform connecting farmers with fertilizer vendors. The system handles order management, inventory tracking, and revenue management with a focus on security and user experience.

### Key Users
- **Farmers**: Purchase fertilizers and track orders
- **Vendors**: Manage inventory and fulfill orders
- **Admin**: Oversee entire system and track operations

---

## 🎯 Recent Features & Enhancements (Latest Update)

### 1. Vendor Side Improvements

✅ **Product Display**
- Images visible on vendor product listings
- Browse Fertilizer section shows: Product Name, Price, Add to Cart button
- Clean, uncluttered vendor interface

✅ **Vendor Stock Management**
- Vendors can ONLY increase/decrease stock quantity
- **Vendors CANNOT change product prices** (Admin control only)
- VendorInventory table manages vendor-specific stock

✅ **Password Security**
- Eye icon toggle on all login pages (Farmer, Vendor, Admin)
- Click to show/hide password
- Enhanced UX for password entry

### 2. Vendor Order Management System

✅ **Order Viewing**
- View all available orders in "Orders" menu
- See orders assigned to them and unassigned orders
- Display: Product Name, Farmer Name, Address, Order ID, Mobile Number

✅ **Order Actions**
- **Accept Order**: Claims the order, status changes to "Shifting"
- **Reject Order**: Declines the order (available for other vendors)
- Orders automatically hidden from other vendors once accepted

✅ **Shifting Orders Menu** (New)
- New dedicated menu for "Shifting Orders" (Out for Delivery)
- Shows all orders in transit
- Action: Mark as Delivered
- Table format with: Product Name, Farmer Name, Address, Mobile Number

### 3. Farmer Purchase History (Updated)

✅ **New Structure**
- **Kept Fields**: Order ID, Date, Status, Total Amount, Payment Method
- **Removed**: Delivery Address (not in list view)
- **Added**: Product Details (name and quantity)
- **Added**: Visual status timeline

✅ **Real-Time Status Tracking**
```
Order Placed (Pending) 
    ↓
Out for Delivery (Shifting)
    ↓
Delivered
```
- Visual timeline shows progress
- Auto-updates every 30 seconds
- Status badges with color coding

### 4. Admin Order Dashboard (New)

✅ **Comprehensive Order View**
- New "Orders/Shifted Products" menu section
- View all system orders with complete details
- Filter by status: Pending, Shifting, Delivered

✅ **Statistics Panel**
- Total Orders count
- Pending Orders (with count)
- Shipping Orders (with count)
- Delivered Orders (with count)
- Total Revenue calculation

✅ **Order Details**
- Order ID, Farmer name, Vendor (assigned/unassigned)
- Product info, Quantity, Amount
- Status badges (color-coded)
- Payment status (Paid/Unpaid)
- Order date

---

## 🔐 Security Features

### Password Encryption
- ✅ **BCrypt Algorithm** (Strength: 12 rounds)
- ✅ No plain text passwords in database
- ✅ Applied to all users: Farmers, Vendors, Admins
- ✅ Passwords encrypted before saving
- ✅ Login uses secure password matching

### SQL Injection Protection
- ✅ All queries use parameterized statements
- ✅ Spring Data JPA prevents SQL injection
- ✅ No string concatenation in queries
- ✅ Tested with common injection attempts

### Data Protection
- ✅ HTTPS support (configurable)
- ✅ CORS configured for security
- ✅ Secure session management
- ✅ Database encryption ready

---

## 🗄️ Database Schema

### Updated Tables

#### `orders` table
```
- order_id (PK)
- farmer_id (FK)
- vendor_id (FK) - nullable
- product_id
- quantity
- total_price
- status (ENUM: pending, shifting, delivered, cancelled)
- delivery_address
- farmer_name ✨ NEW
- farmer_phone ✨ NEW
- farmer_address ✨ NEW
- product_name ✨ NEW
- product_quantity ✨ NEW
- payment_method
- is_paid
- order_date
- delivery_date
- tracking_number
- created_at, updated_at
```

#### `vendor_inventory` table
```
- inventory_id (PK)
- vendor_id (FK)
- fertilizer_id
- vendor_price (NOT editable by vendor)
- quantity_in_stock ✨ (editable by vendor)
- is_active
- added_at, updated_at
```

#### `farmer` table
```
- farmer_id (PK)
- full_name
- phone (unique)
- email (unique)
- password (encrypted)
- address ✨ NEW
- city, state
- is_active
- created_at, updated_at
```

---

## 🚀 API Endpoints

### Vendor Endpoints

```bash
# Get pending orders (for this vendor)
GET /api/orders/vendor/{vendorId}/pending
Response: List of pending orders visible to vendor

# Accept order and claim it
POST /api/orders/{id}/accept-vendor
Body: {"vendorId": 123}
Response: Order with status="shifting" and vendorId set

# Reject order
POST /api/orders/{id}/reject
Response: Order status remains "pending" for other vendors

# Get shifting orders (out for delivery)
GET /api/orders/vendor/{vendorId}/shifting
Response: List of orders being delivered

# Mark order as delivered
POST /api/orders/{id}/deliver
Response: Order with status="delivered"
```

### Farmer Endpoints

```bash
# Get all orders
GET /api/orders/farmer/{farmerId}
Response: Complete order history with status

# Get pending orders only
GET /api/orders/farmer/{farmerId}/pending
Response: Orders waiting for vendor acceptance
```

### Admin Endpoints

```bash
# Get all orders (with optional filter)
GET /api/orders/admin/all?status=pending|shifting|delivered
Response: All system orders

# Get order statistics
GET /api/orders/admin/stats
Response: {
  "totalOrders": 150,
  "pendingOrders": 25,
  "shiftingOrders": 12,
  "deliveredOrders": 110,
  "cancelledOrders": 3,
  "totalRevenue": 125000.00
}
```

---

## 📁 Project Structure

```
FarmFeed/
├── src/main/java/com/example/farmFeed/
│   ├── config/
│   │   └── SecurityConfig.java ✨ BCrypt encoder
│   ├── entity/
│   │   ├── Order.java ✨ Updated with new fields
│   │   ├── Farmer.java
│   │   ├── Vendor.java
│   │   └── Product.java
│   ├── service/
│   │   ├── FarmerService.java ✨ Password encryption
│   │   ├── VendorService.java ✨ Password encryption
│   │   ├── AdminService.java ✨ Password encryption
│   │   ├── OrderService.java ✨ New order methods
│   │   └── ...
│   ├── controller/
│   │   ├── OrderController.java ✨ New endpoints
│   │   ├── VendorController.java
│   │   ├── FarmerController.java
│   │   └── AdminController.java
│   └── repository/
│       ├── OrderRepository.java ✨ Updated queries
│       ├── FarmerRepository.java
│       ├── VendorRepository.java
│       └── ...
│
├── src/main/resources/
│   ├── static/
│   │   ├── Login.html ✨ Password toggle
│   │   ├── AdminLogin.html ✨ Password toggle
│   │   ├── FarmerAccount.html ✨ Order timeline
│   │   ├── VendorAccount.html ✨ Order management
│   │   ├── AdminDashboard.html ✨ Order dashboard
│   │   ├── css/
│   │   │   └── ... stylesheets
│   │   └── js/
│   │       ├── password-toggle.js ✨ NEW
│   │       ├── vendor-orders.js ✨ NEW
│   │       ├── farmer-orders.js ✨ NEW
│   │       ├── admin-orders.js ✨ NEW
│   │       └── ... other scripts
│   └── application.properties
│
├── pom.xml
├── DATABASE_MIGRATION.sql ✨ NEW
├── IMPLEMENTATION_SUMMARY.md ✨ NEW
├── TESTING_DEPLOYMENT_GUIDE.md ✨ NEW
└── README.md (this file)
```

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.3.3
- **Language**: Java 17
- **Database**: MySQL 5.7+
- **Build Tool**: Maven
- **Security**: Spring Security + BCrypt

### Frontend
- **HTML5**, **CSS3**, **JavaScript**
- **Bootstrap 5.3.8** (UI Framework)
- **Bootstrap Icons** (Icon Library)
- **Fetch API** (HTTP Requests)
- **LocalStorage** (Session Management)

### Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- MySQL Connector
- Lombok
- JWT (JSON Web Tokens)
- Validation (Jakarta)

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- MySQL 5.7+
- Git

### Installation

```bash
# 1. Clone repository
git clone <repository-url>
cd FarmFeed

# 2. Create database
mysql -u root -p < DATABASE_MIGRATION.sql

# 3. Build project
mvn clean compile

# 4. Run application
mvn spring-boot:run

# 5. Access application
# Frontend: http://localhost:8080
# API: http://localhost:8080/api
```

### Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/FarmFeed
spring.datasource.username=root
spring.datasource.password=root

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

---

## 📊 Order Workflow

### Complete Order Lifecycle

```
┌─────────────────────────────────────────────────────┐
│                 FARMER PLACES ORDER                 │
│        (Status: PENDING, VendorId: NULL)            │
└──────────────────┬──────────────────────────────────┘
                   │
                   ├─────────────────────────┐
                   │  Visible to ALL Vendors │
                   └─────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────┐
│      VENDOR ACCEPTS ORDER (Claims Ownership)        │
│   (Status: SHIFTING, VendorId: Set, Farmer sees)   │
│        (Status updates to "Out for Delivery")       │
└──────────────────┬──────────────────────────────────┘
                   │
                   ├─────────────────────────────┐
                   │ Hidden from Other Vendors   │
                   └─────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────┐
│       VENDOR MARKS AS DELIVERED                     │
│        (Status: DELIVERED)                          │
│    (Farmer sees "Delivered" with checkmark)         │
└─────────────────────────────────────────────────────┘

Alternative Paths:
- REJECT: Vendor rejects → Order goes back to PENDING
- CANCEL: Admin/Farmer cancels → Status: CANCELLED
```

---

## 🧪 Testing

### Quick Test Commands

```bash
# Test farmer login
curl -X POST http://localhost:8080/api/farmer/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"9999999999","password":"password"}'

# Test vendor orders
curl http://localhost:8080/api/orders/vendor/1/pending

# Test admin stats
curl http://localhost:8080/api/orders/admin/stats
```

### Full Testing Guide
See `TESTING_DEPLOYMENT_GUIDE.md` for complete testing procedures.

---

## 📝 API Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { /* Response data */ }
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error description"
}
```

---

## 🔒 Security Best Practices

1. **Passwords**
   - Never stored in plain text
   - BCrypt encrypted with 12 rounds
   - Always sent over HTTPS

2. **Database**
   - Use parameterized queries only
   - Run migration script to update schema
   - Regular backups recommended

3. **Authentication**
   - Secure session tokens
   - Token expiry implemented
   - CORS configured

4. **API Security**
   - All endpoints validate input
   - SQL injection protected
   - CSRF protection enabled

---

## 📦 Deployment

### Development
```bash
mvn spring-boot:run
```

### Production
```bash
mvn clean package -DskipTests
java -Dspring.profiles.active=production -jar farmfeed-1.0.0.jar
```

### Docker (Optional)
```bash
docker build -t farmfeed:latest .
docker run -p 8080:8080 farmfeed:latest
```

See `TESTING_DEPLOYMENT_GUIDE.md` for detailed deployment instructions.

---

## 🐛 Troubleshooting

### Common Issues

**Port 8080 in use?**
```bash
lsof -i :8080
kill -9 <PID>
```

**Database connection failed?**
```bash
mysql -h localhost -u root -p -e "SELECT 1;"
```

**Password not hashing?**
Check `SecurityConfig.java` for PasswordEncoder bean.

---

## 📋 Checklist for Going Live

- [ ] Database created and migrations run
- [ ] All tests passing
- [ ] Environment variables configured
- [ ] Passwords encrypted in database
- [ ] HTTPS/SSL configured
- [ ] Backups scheduled
- [ ] Monitoring set up
- [ ] Error logging enabled
- [ ] API documentation reviewed
- [ ] Performance tested

---

## 📞 Support & Contact

For issues or questions:
1. Check `TESTING_DEPLOYMENT_GUIDE.md`
2. Review `IMPLEMENTATION_SUMMARY.md`
3. Check application logs
4. Verify database connection

---

## 📄 License

FarmFeed © 2026. All rights reserved.

---

## ✨ Latest Updates Summary

### Version 2.0 - Order Management & Security
- ✅ Vendor order management (accept/reject)
- ✅ Shifting orders tracking
- ✅ Farmer order tracking with timeline
- ✅ Admin order dashboard
- ✅ Password encryption (BCrypt)
- ✅ Eye icon password toggle
- ✅ SQL injection protection
- ✅ Database schema updates
- ✅ New API endpoints
- ✅ Comprehensive documentation

### Next Planned Features
- [ ] Email notifications
- [ ] Order tracking number generation
- [ ] Vendor rating system
- [ ] Advanced analytics
- [ ] Mobile app
- [ ] Payment gateway integration

---

**Happy Farming! 🌾**
