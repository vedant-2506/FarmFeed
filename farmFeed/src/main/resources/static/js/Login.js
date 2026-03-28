document.addEventListener("DOMContentLoaded", () => {
  const BASE_URL = window.API_BASE_URL || window.location.origin;

  const farmerForm = document.getElementById("farmerLoginForm");
  farmerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const phone = document.getElementById("farmerPhone").value.trim();
    const password = document.getElementById("farmerPassword").value.trim();

    if (!phone || !password) { alert("Please fill all fields"); return; }
    if (!/^\d{10}$/.test(phone)) { alert("Phone number must be exactly 10 digits"); return; }

    try {
      const response = await fetch(`${BASE_URL}/api/farmer/Login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ phone, password })
      });
      const data = await response.json();
      if (response.ok && data.success) {
        // Store farmer info in localStorage
        localStorage.setItem("farmer_id", data.id);
        localStorage.setItem("farmer_name", data.fullName);
        localStorage.setItem("user_name", data.fullName); // For dropdown display
        localStorage.setItem("farmer_phone", data.phone);
        localStorage.setItem("user_type", "farmer");
        alert("Farmer Login Successful!");
        window.location.href = "FarmerAccount.html";
      } else {
        alert(data.error || "Invalid Farmer Credentials");
      }
    } catch (error) { alert("Server error! Backend may be waking up, try again in 30s."); }
  });

  const vendorForm = document.getElementById("vendorLoginForm");
  vendorForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("vendorEmail").value.trim();
    const password = document.getElementById("vendorPassword").value.trim();

    if (!email || !password) { alert("Please fill all fields"); return; }

    try {
      const response = await fetch(`${BASE_URL}/api/shopkeeper/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });
      const data = await response.json();
      if (response.ok && data.success) {
        // Store vendor info in localStorage
        localStorage.setItem("shop_id", data.id);
        localStorage.setItem("shop_name", data.shopName);
        localStorage.setItem("owner_name", data.ownerName);
        localStorage.setItem("user_name", data.ownerName); // For dropdown display
        localStorage.setItem("user_type", "vendor");
        alert("Vendor Login Successful!");
        window.location.href = "VendorAccount.html";
      } else {
        alert(data.error || "Invalid Vendor Credentials");
      }
    } catch (error) { alert("Server error! Try again in 30s."); }
  });
});