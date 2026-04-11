document.addEventListener("DOMContentLoaded", () => {
  const BASE_URL = window.API_BASE_URL || window.location.origin;

  const farmerForm = document.getElementById("farmerLoginForm");
  farmerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const phone = document.getElementById("farmerPhone").value.trim();
    const password = document.getElementById("farmerPassword").value.trim();

    if (!phone || !password) { alert("fill all fields"); return; }
    if (!/^\d{10}$/.test(phone)) { alert("phone needs to be 10 digits"); return; }

    try {
      const response = await fetch(`${BASE_URL}/api/farmer/Login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ phone, password })
      });
      const data = await response.json();
      if (response.ok && data.success) {
        // Store farmer info in localStorage
        localStorage.setItem("farmer_id", data.farmer_id);
        localStorage.setItem("farmer_name", data.fullName);
        localStorage.setItem("user_name", data.fullName);
        localStorage.setItem("farmer_phone", data.phone);
        localStorage.setItem("user_type", "farmer");
        alert("logged in!");
        window.location.href = "FarmerAccount.html";
      } else {
        alert(data.error || "Invalid Farmer Credentials");
      }
    } catch (error) { alert("error connecting. try again"); }
  });

  const vendorForm = document.getElementById("vendorLoginForm");
  vendorForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("vendorEmail").value.trim();
    const password = document.getElementById("vendorPassword").value.trim();

    if (!email || !password) { alert("need email and password"); return; }

    try {
      const response = await fetch(`${BASE_URL}/api/shopkeeper/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });
      const data = await response.json();
      if (response.ok && data.success) {
        // Store vendor info in localStorage
        localStorage.setItem("shop_id", data.shop_id);
        localStorage.setItem("shop_name", data.shop_name);
        localStorage.setItem("owner_name", data.owner_name);
        localStorage.setItem("user_name", data.owner_name); // For dropdown display
        localStorage.setItem("user_type", "vendor");
        alert("Vendor Login Successful!");
        window.location.href = "VendorAccount.html";
      } else {
        alert(data.error || "Invalid Vendor Credentials");
      }
    } catch (error) { alert("Server error! Try again in 30s."); }
  });
});