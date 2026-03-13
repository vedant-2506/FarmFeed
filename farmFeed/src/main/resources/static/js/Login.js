document.addEventListener("DOMContentLoaded", () => {
  const BASE_URL = "https://farmfeed-backend.onrender.com";

  const farmerForm = document.getElementById("farmerLoginForm");
  farmerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const phone = document.getElementById("farmerPhone").value.trim();
    const password = document.getElementById("farmerPassword").value.trim();
    if (!phone || !password) { alert("Please fill all fields"); return; }
    try {
      const response = await fetch(`${BASE_URL}/api/farmer/Login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ phone, password })
      });
      const data = await response.json();
      if (response.ok && data.success) {
        alert("Farmer Login Successful!");
        window.location.href = "Home.html";
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
        alert("Vendor Login Successful!");
        window.location.href = "vendo.html";
      } else {
        alert(data.error || "Invalid Vendor Credentials");
      }
    } catch (error) { alert("Server error! Try again in 30s."); }
  });
});
