// ============================================================
// farmFeed — SignUp.js (WORKING VERSION)
// ============================================================

document.addEventListener("DOMContentLoaded", () => {
  const form = document.querySelector("form");

  if (!form) {
    console.error("❌ Sign up form not found!");
    return;
  }

  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    // Get form values
    const name = document.getElementById("name")?.value.trim();
    const email = document.getElementById("email")?.value.trim();
    const phone = document.getElementById("phone")?.value.trim();
    const password = document.getElementById("password")?.value.trim();
    const confirmPassword = document.getElementById("confirmPassword")?.value.trim();
    const gender = document.querySelector('input[name="gender"]:checked')?.value;
    const address = document.getElementById("address")?.value.trim();

    // Validation
    if (!name || !email || !phone || !password) {
      alert("Please fill in all required fields");
      return;
    }

    if (password !== confirmPassword) {
      alert("Passwords do not match!");
      return;
    }

    if (password.length < 6) {
      alert("Password must be at least 6 characters");
      return;
    }

    if (phone.length < 10) {
      alert("Please enter a valid phone number");
      return;
    }

    // Prepare data for backend
    const farmerData = {
      name,
      email,
      phone,
      password,
      gender: gender || null,
      address: address || null
    };

    try {
      console.log("📤 Sending sign up request:", farmerData);

      const response = await fetch("http://localhost:9090/api/Farmer/SignUp", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(farmerData)
      });

      const data = await response.json();

      if (response.ok && data.success) {
        alert("✅ Sign Up Successful!\nRedirecting to Login...");
        // Save email for login page auto-fill
        localStorage.setItem("lastSignupUser", email);
        // Redirect to Login page
        window.location.href = "Login.html";
      } else {
        alert("❌ Sign Up Failed\n" + (data.error || "Unknown error"));
      }
    } catch (error) {
      console.error("❌ Error:", error);
      alert("❌ Connection error. Is the backend running?");
    }
  });
});
