const BASE_URL = window.API_BASE_URL || window.location.origin;

document.addEventListener("DOMContentLoaded", () => {
  const shopId = localStorage.getItem("shop_id");
  const shopName = localStorage.getItem("shop_name");
  
  if (!shopId || !shopName) {
    alert("login as vendor first");
    window.location.href = "Login.html";
    return;
  }

  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      localStorage.clear();
      window.location.href = "Login.html";
    });
  }

  loadVendorInfo();
  loadStats();

});

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

function loadStats() {
  animateCount("totalProducts", 12);
  animateCount("totalOrders", 28);
  animateSales("totalSales", 98500);
  animateCount("totalCustomers", 15);
}

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

function logout() {
  localStorage.removeItem("vendorName");
  localStorage.removeItem("vendorEmail");
  localStorage.removeItem("vendorToken");
  window.location.href = "Login.html";
}