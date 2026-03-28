document.addEventListener("DOMContentLoaded", () => {
  const BASE_URL = window.API_BASE_URL || window.location.origin;

  document.getElementById("farmerForgotForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const phone = document.getElementById("farmerMobile").value.trim();
    const newPassword = document.getElementById("farmerNewPassword").value.trim();
    const confirmPassword = document.getElementById("farmerConfirmPassword").value.trim();

    if (!phone || !newPassword || !confirmPassword) {
      alert("Please fill all fields"); return;
    }
    if (!/^\d{10}$/.test(phone)) {
      alert("Phone number must be exactly 10 digits"); return;
    }
    if (newPassword !== confirmPassword) {
      alert("Passwords do not match"); return;
    }

    try {
      const response = await fetch(`${BASE_URL}/api/farmer/reset-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ phone, password: newPassword })
      });
      const data = await response.json();
      if (response.ok && data.success) {
        alert("Password Reset Successful!");
        window.location.href = "Login.html";
      } else {
        alert(data.error || "Reset Failed");
      }
    } catch (error) { alert("Server Error! Try again in 30s."); }
  });

  document.getElementById("vendorForgotForm").addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("vendorEmail").value.trim();
    const newPassword = document.getElementById("vendorNewPassword").value.trim();
    const confirmPassword = document.getElementById("vendorConfirmPassword").value.trim();

    if (!email || !newPassword || !confirmPassword) {
      alert("Please fill all fields"); return;
    }
    if (newPassword !== confirmPassword) {
      alert("Passwords do not match"); return;
    }

    try {
      const response = await fetch(`${BASE_URL}/api/shopkeeper/reset-password`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password: newPassword })
      });
      const data = await response.json();
      if (response.ok && data.success) {
        alert("Password Reset Successful!");
        window.location.href = "Login.html";
      } else {
        alert(data.error || "Reset Failed");
      }
    } catch (error) { alert("Server Error! Try again in 30s."); }
  });
});