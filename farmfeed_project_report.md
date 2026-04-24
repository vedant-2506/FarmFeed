# FarmFeed – Complete Project Report

---

## 1. Introduction

FarmFeed is a web-based agricultural marketplace built to connect **Farmers**, **Vendors (Shop Owners)**, and **Admins** on a single digital platform. The system allows farmers to browse and purchase fertilizers with a simple, visual interface, while vendors can manage their inventory. The admin module provides full control and oversight.

---

## 2. Project Overview

| Property | Details |
|---|---|
| Project Name | FarmFeed |
| Type | Web Application (Full Stack) |
| Primary Users | Farmers, Vendors, Admins |
| Database | MySQL (`FarmFeed` schema) |
| Server Port | 8080 (default) |
| Environment | Local Dev → Cloud Deployment |

---

## 3. Technologies Used

### Frontend
| Technology | Version | Purpose |
|---|---|---|
| HTML5 | — | Page Structure |
| CSS3 (Vanilla) | — | Custom Styling |
| JavaScript (ES6) | — | Dynamic Behavior |
| React (CDN) | 18 | Product Catalog UI (`HomeReact.js`) |
| Bootstrap | 5.3.8 | Responsive Layout & Components |
| Bootstrap Icons | 1.11.3 | Visual Icons |
| Google Fonts (Outfit) | — | Typography |

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Core Backend Language |
| Spring Boot | 3.3.3 | Application Framework |
| Spring Data JPA | — | Database ORM |
| Spring Security | — | Auth & Role-based Access |
| Hibernate | — | SQL Generation |
| HikariCP | — | Connection Pooling |

### Database
| Technology | Details |
|---|---|
| MySQL | Version 8+, DB: `FarmFeed` |
| JPA DDL | `update` (auto-migrates schema) |

---

## 4. System Architecture

```
┌─────────────────────────────────────────────┐
│                  Browser (Client)           │
│  HTML + CSS + Bootstrap + React (CDN)       │
│  Pages: Home, Cart, Vendor, Admin, etc.     │
└────────────────────┬────────────────────────┘
                     │  HTTP / REST API
                     ▼
┌─────────────────────────────────────────────┐
│         Spring Boot Backend (Port 8080)     │
│  Controllers → Services → Repositories     │
│  SecurityConfig | WebConfig                 │
└────────────────────┬────────────────────────┘
                     │  JDBC / JPA
                     ▼
┌─────────────────────────────────────────────┐
│            MySQL Database (FarmFeed)        │
│  Tables: farmer, vendor, admin, product,   │
│          cart, orders, rating, inventory   │
└─────────────────────────────────────────────┘
```

---

## 5. Module Descriptions

### 🌾 Farmer Module
**Pages:** `Home.html`, `Login.html`, `SignUp.html`, `Cart.html`, `Checkout.html`, `FarmerAccount.html`

| Feature | Description |
|---|---|
| Product Browsing | Browse all fertilizers with category & price filters |
| Search | Real-time product search by name/description |
| Filtering | Filter by Organic/Chemical, Price Range, Rating |
| Cart System | Add/remove products; cart persists in database |
| Checkout | Place orders from cart |
| Account | View/manage farmer profile |

**UI Design Focus:** Large icons, minimal text, simple navigation for low-literacy users.

---

### 🏪 Vendor Module
**Pages:** `VendorAccount.html`, `AddProduct.html`

| Feature | Description |
|---|---|
| Login | Vendor-specific login with `user_type: vendor` |
| Inventory Dashboard | Table of all products added by the vendor |
| Add Product | Form to add new fertilizer with image, price, stock |
| Edit / Delete | Manage existing products |
| Stats | View product counts and stock status |

---

### 🔐 Admin Module
**Pages:** `AdminDashboard.html`, `AdminLogin.html`, `AdminSignup.html`

| Feature | Description |
|---|---|
| Secure Login | Admin-only authentication |
| Manage Farmers | View, search, disable farmer accounts |
| Manage Vendors | View, search, manage vendor accounts |
| View Inventory | See all products across all vendors |
| Admin Creation | Only existing admin can create new admin accounts |
| Single Role | System enforces one admin role |

---

### 📄 Information Pages
| Page | Purpose |
|---|---|
| `AboutUs.html` | Platform story, marketing ("Fresh from Farm", "Direct to Buyer") |
| `Contact.html` | Phone, email, contact form |
| `Help.html` | FAQ accordion, step-by-step guide |
| `PrivacyPolicy.html` | Simple privacy points with icons |

---

## 6. Key Backend Files

### Controllers
| File | REST Endpoints |
|---|---|
| `FarmerController.java` | `/api/farmer/register`, `/api/farmer/login`, `/api/farmer/{id}` |
| `VendorController.java` | `/api/vendor/register`, `/api/vendor/login` |
| `AdminController.java` | `/api/admin/login`, `/api/admin/register`, etc. |
| `FertilizerController.java` | `/api/fertilizers` (GET all products) |
| `CartController.java` | `/api/cart/add`, `/api/cart/{farmerId}`, `/api/cart/remove` |
| `OrderController.java` | `/api/orders/place`, `/api/orders/{farmerId}` |
| `ProductController.java` | `/api/products` (vendor product management) |
| `VendorInventoryController.java` | `/api/vendor/inventory` |

### Entities / DB Tables
| Entity | Table | Key Fields |
|---|---|---|
| `Farmer` | farmer | id, name, email, phone, password |
| `Vendor` | vendor | id, shopName, email, phone, password |
| `Admin` | admin | id, name, email, password, role |
| `Product` | product | id, name, price, stock, category, vendorId |
| `Cart` | cart | id, farmerId, productId, quantity |
| `Order` | orders | id, farmerId, productId, total, status |
| `Rating` | rating | id, productId, farmerId, stars |
| `VendorInventory` | vendor_inventory | id, vendorId, productId, quantity |

---

## 7. Testing Plan

### 7.1 Functional Test Cases

| ID | Test Case | Steps | Expected Result | Status |
|---|---|---|---|---|
| TC-01 | Farmer Registration | Fill form, click Sign Up | Account created, redirect to login | ⬜ |
| TC-02 | Farmer Login | Enter email & password | Logged in, redirected to Home | ⬜ |
| TC-03 | Vendor Login | Enter vendor credentials | Logged in, redirected to Vendor Dashboard | ⬜ |
| TC-04 | Admin Login | Enter admin credentials | Admin dashboard loads | ⬜ |
| TC-05 | Browse Products | Open Home.html as farmer | All products displayed correctly | ⬜ |
| TC-06 | Category Filter | Click "Seed" filter | Only seed products shown | ⬜ |
| TC-07 | Organic Filter | Click "Organic" filter | Only organic products shown | ⬜ |
| TC-08 | Price Filter | Select "Under ₹500" | Only products below ₹500 shown | ⬜ |
| TC-09 | Dropdown Auto-Close | Select price option | Dropdown closes immediately | ⬜ |
| TC-10 | Add to Cart | Click "Add to Cart" button | Product added to cart, count updates | ⬜ |
| TC-11 | View Cart | Click cart icon | Cart items shown with correct prices | ⬜ |
| TC-12 | Remove from Cart | Click remove in cart | Item removed, total updates | ⬜ |
| TC-13 | Checkout | Click "Checkout" in cart | Order placed, confirmation shown | ⬜ |
| TC-14 | Add Product (Vendor) | Fill add product form | Product appears in inventory | ⬜ |
| TC-15 | Edit Product | Update price in inventory | Updated price shows immediately | ⬜ |
| TC-16 | Delete Product | Remove product from list | Product removed from catalog | ⬜ |
| TC-17 | Admin: View Farmers | Open admin dashboard | All farmer accounts listed | ⬜ |
| TC-18 | Admin: Manage Vendors | Open vendor tab | All vendors shown with options | ⬜ |
| TC-19 | Admin Registration | Register new admin | Only works with existing admin secret | ⬜ |
| TC-20 | Logout | Click logout | Session cleared, redirected to home | ⬜ |
| TC-21 | Session Timeout | Leave idle 5 mins | Auto-logout triggers | ⬜ |
| TC-22 | Clear All Filters | Click "Clear All" | All filters reset, all products shown | ⬜ |
| TC-23 | In-Stock Filter | Toggle "In Stock" | Only available products shown | ⬜ |

**Status Key:** ✅ Pass | ❌ Fail | ⬜ Not Tested

---

### 7.2 UI Testing Checklist

| Check | Description | Result |
|---|---|---|
| ☐ | Navbar visible on all pages | — |
| ☐ | Logo loads on all pages | — |
| ☐ | Filter buttons aligned and visible | — |
| ☐ | Product cards load with images | — |
| ☐ | Price dropdown closes after selection | — |
| ☐ | Mobile view (< 768px) works | — |
| ☐ | Cart count updates after add | — |
| ☐ | Active filter button turns green | — |
| ☐ | About Us, Contact, Help pages load | — |
| ☐ | HD images load on info pages | — |

---

### 7.3 API Testing (Postman)

Test the following endpoints using **Postman**:

| Method | Endpoint | Body | Expected |
|---|---|---|---|
| POST | `/api/farmer/register` | `{name, email, phone, password}` | 201 Created |
| POST | `/api/farmer/login` | `{email, password}` | `{farmer_id, user_name, user_type}` |
| GET | `/api/fertilizers` | — | Array of all products |
| POST | `/api/cart/add` | `{farmerId, productId, quantity}` | Cart item added |
| GET | `/api/cart/{farmerId}` | — | Cart items for farmer |
| POST | `/api/admin/login` | `{email, password}` | Admin session |

---

### 7.4 Testing Tools

| Tool | Purpose | Where to Get |
|---|---|---|
| **Chrome DevTools** | Inspect UI, Network tab, Console errors | Built-in (F12) |
| **Postman** | Test all REST APIs | postman.com (free) |
| **MySQL Workbench** | View DB tables directly | mysql.com (free) |
| **JMeter** | Load/performance testing | jmeter.apache.org (free) |
| **Lighthouse** | Page speed & accessibility audit | Chrome DevTools > Lighthouse |

---

## 8. Deployment Plan

### 8.1 Pre-Deployment Checklist

- [ ] All features tested locally
- [ ] `application.properties` uses environment variables (`${DB_URL}`, `${DB_USER}`, etc.)
- [ ] Code pushed to GitHub repository
- [ ] MySQL schema exported as `.sql` file

### 8.2 Step-by-Step Deployment

#### Step 1: Prepare GitHub Repository
```bash
# In project directory
git init
git add .
git commit -m "Initial commit - FarmFeed complete project"
git remote add origin https://github.com/YOUR_USERNAME/farmfeed.git
git push -u origin main
```

#### Step 2: Database (Railway MySQL – Free)
1. Go to **railway.app** → New Project → MySQL
2. Copy the `DATABASE_URL`, username, and password shown
3. Open MySQL Workbench → Connect to Railway DB
4. Run your local FarmFeed schema SQL to create tables

#### Step 3: Backend (Render.com – Free)
1. Go to **render.com** → New Web Service → Connect GitHub repo
2. Set Build Command: `./mvnw package -DskipTests`
3. Set Start Command: `java -jar target/farmfeed-1.0.0.jar`
4. Add Environment Variables:
   ```
   DB_URL     = jdbc:mysql://<railway-host>:3306/FarmFeed
   DB_USER    = <railway-user>
   DB_PASS    = <railway-password>
   PORT       = 8080
   ```
5. Deploy → Get your backend URL: `https://farmfeed.onrender.com`

#### Step 4: Update Frontend Config
Update `/js/config.js` to point to your live backend:
```javascript
const API_BASE_URL = "https://farmfeed.onrender.com";
```

#### Step 5: Frontend (Netlify – Free)
1. Go to **netlify.com** → New Site → Import from GitHub
2. Set Publish Directory: `src/main/resources/static`
3. Deploy → Get your frontend URL: `https://farmfeed.netlify.app`

---

### 8.3 Two Separate Links

| Link | Purpose | URL |
|---|---|---|
| **Link 1 – Main Site** | Farmers + Vendors | `https://farmfeed.netlify.app/Home.html` |
| **Link 2 – Admin Panel** | Admin Only | `https://farmfeed.netlify.app/AdminLogin.html` |

Both links use the **same backend** on Render and the **same MySQL** database on Railway.

---

## 9. Challenges & Solutions

| # | Challenge | Solution |
|---|---|---|
| 1 | Price dropdown not closing after selection | Used Bootstrap JS API `Dropdown.getInstance().hide()` |
| 2 | Cart not persisting after logout | Cart stored in DB linked to farmer ID, not session |
| 3 | Admin security (only admin can create admin) | AdminSignup page validates secret key or existing session |
| 4 | React component conflicts with Bootstrap | Used React CDN (no build step), initialized after Bootstrap |
| 5 | Images not loading in vendor inventory | Fixed image URL resolution path in product API |
| 6 | Session timeout for user safety | Implemented 5-min inactivity auto-logout in `Home.html` |
| 7 | Farmer UI needs to be simple | Minimal text, large buttons, icons-first design approach |

---

## 10. Conclusion

FarmFeed successfully delivers a **role-based agricultural marketplace** that is:

- ✅ **Functional** – Full CRUD for farmers, vendors, and admin
- ✅ **User-Friendly** – Simple visual UI for low-literacy farmers
- ✅ **Secure** – Role-based login, session management, admin-only registration
- ✅ **Deployable** – Environment variable-ready backend, static frontend
- ✅ **Professional** – Marketing-quality info pages, HD imagery, smooth UX

The platform is ready for real-world use after completing the testing checklist and deployment steps above.

---

*Report Generated: April 2026 | FarmFeed v1.0*
