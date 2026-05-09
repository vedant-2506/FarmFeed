# FarmFeed – Testing Plan

---

## 🔹 1. Functional Test Cases
Use this table to verify every core feature of the platform.

| ID | Test Case | Steps | Expected Result | Status |
| ------------ | ----------- | ----------- | ----------- | ------ |
| **TC-01** | Farmer Registration | Fill SignUp.html, click Register | Account created, redirect to Login | ⬜ |
| **TC-02** | Farmer Login | Enter valid farmer credentials | Redirected to Home.html | ⬜ |
| **TC-03** | Vendor Login | Enter valid vendor credentials | Redirected to Vendor Dashboard | ⬜ |
| **TC-04** | Admin Login | Enter valid admin credentials | Redirected to Admin Dashboard | ⬜ |
| **TC-05** | Category Filter | Click "Seed" on Home.html | Only seed products are displayed | ⬜ |
| **TC-06** | Organic Filter | Click "Organic" button | Only organic products are displayed | ⬜ |
| **TC-07** | Price Range | Select "Under ₹500" in dropdown | Products <= ₹500 are shown | ⬜ |
| **TC-08** | Dropdown Close | Select any price filter | **Dropdown closes automatically** | ⬜ |
| **TC-09** | Search Function | Type product name in search bar | Results update in real-time | ⬜ |
| **TC-10** | Add to Cart | Click "Add to Cart" on a card | Cart count increases instantly | ⬜ |
| **TC-11** | View Cart | Open Cart.html | Correct items and prices shown | ⬜ |
| **TC-12** | Remove Item | Click remove in Cart | Item disappears, total updates | ⬜ |
| **TC-13** | Checkout | Place order from Cart | Success message, cart clears | ⬜ |
| **TC-14** | Add Product (Vendor) | Vendor adds new fertilizer | Product appears in public Home.html | ⬜ |
| **TC-15** | Update Inventory | Vendor changes stock quantity | New stock level reflects on site | ⬜ |
| **TC-16** | Admin: Farmers | Admin views Farmer list | All registered farmers are visible | ⬜ |
| **TC-17** | Admin: Vendors | Admin views Vendor list | All registered vendors are visible | ⬜ |
| **TC-18** | Logout | Click logout in any module | Session cleared, redirected to login | ⬜ |
| **TC-19** | Session Security | Try to access Admin page without login | Redirected back to login | ⬜ |
| **TC-20** | In-Stock Toggle | Toggle "In Stock Only" | Out-of-stock items are hidden | ⬜ |

---

## 🔹 2. UI/UX Testing Checklist
Ensure the visual experience meets the project standards.

- [ ] **Navbar Consistency**: Navigation bar is visible and links work on all pages.
- [ ] **Image Loading**: All HD images on About Us, Help, and Home pages load correctly.
- [ ] **Filter State**: The active filter button (Category/Price) turns solid green when active.
- [ ] **Mobile Responsive**: Open site on mobile; buttons are easy to tap and cards stack properly.
- [ ] **Clean Interaction**: No dropdowns "get stuck" after clicking an option.
- [ ] **Marketing Text**: "Fresh from Farm", "Direct to Buyer", etc., are clearly visible on Info pages.

---

## 🔹 3. API Testing (Postman)
Verify backend reliability by testing these endpoints:

- **GET** `/api/fertilizers` - Should return JSON list of all fertilizers.
- **POST** `/api/farmer/login` - Test with correct and incorrect passwords.
- **POST** `/api/cart/add` - Ensure `farmerId` and `productId` are correctly received.
- **GET** `/api/admin/farmers` - Verify only authorized admins receive the list.

---

## 🔹 4. Testing Tools
- **Chrome DevTools (F12)**: Check the "Console" for errors and "Network" for API response times.
- **Postman**: For automated API testing.
- **Lighthouse**: Run a report from Chrome to check mobile performance and accessibility.
- **MySQL Workbench**: Directly check tables to ensure data isn't duplicated.

---
*Generated for FarmFeed Project v1.0*
