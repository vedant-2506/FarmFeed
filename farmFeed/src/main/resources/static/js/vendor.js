// ============================================================
// FarmFeed - vendor.js
// ============================================================
const BASE_URL = "https://farmfeed-backend.onrender.com";

document.addEventListener("DOMContentLoaded", () => {

  loadVendorInfo();
  loadStats();

});

// ===== Load vendor name from localStorage =====
function loadVendorInfo() {
  const vendorName = localStorage.getItem("vendorName") || "Vendor";
  const vendorEmail = localStorage.getItem("vendorEmail") || "";

  const nameDisplay = document.getElementById("vendorNameDisplay");
  const nameCard = document.getElementById("vendorNameCard");
  const emailCard = document.getElementById("vendorEmailCard");
  const bannerName = document.getElementById("bannerVendorName");

  if (nameDisplay) nameDisplay.textContent = vendorName;
  if (nameCard) nameCard.textContent = vendorName;
  if (emailCard) emailCard.textContent = vendorEmail;
  if (bannerName) bannerName.textContent = vendorName;
}

// ===== Load Stats (placeholders — connect to API later) =====
function loadStats() {
  // These will come from backend API once order system is built
  // For now showing sample data
  animateCount("totalProducts", 12);
  animateCount("totalOrders", 28);
  animateSales("totalSales", 98500);
  animateCount("totalCustomers", 15);
}

// ===== Animate numbers counting up =====
function animateCount(elementId, target) {
  const el = document.getElementById(elementId);
  if (!el) return;

  let current = 0;
  const step = Math.ceil(target / 40);
  const timer = setInterval(() => {
    current += step;
    if (current >= target) {
      current = target;
      clearInterval(timer);
    }
    el.textContent = current;
  }, 30);
}

function animateSales(elementId, target) {
  const el = document.getElementById(elementId);
  if (!el) return;

  let current = 0;
  const step = Math.ceil(target / 40);
  const timer = setInterval(() => {
    current += step;
    if (current >= target) {
      current = target;
      clearInterval(timer);
    }
    el.textContent = "₹" + current.toLocaleString("en-IN");
  }, 30);
}

// ===== Logout =====
function logout() {
  localStorage.removeItem("vendorName");
  localStorage.removeItem("vendorEmail");
  localStorage.removeItem("vendorToken");
  window.location.href = "Login.html";
}