// ============================================================
// farmFeed — Login.js (WORKING VERSION)
// ============================================================

document.addEventListener("DOMContentLoaded", () => {
  const loginForm = document.getElementById("loginForm");
  const userIdInput = document.getElementById("userId");
  const passwordInput = document.getElementById("password");

  if (!loginForm) {
    console.error("❌ Login form not found!");
    return;
  }

  // ✅ Auto-fill email from SignUp (if exists)
  const savedUser = localStorage.getItem("lastSignupUser");
  if (savedUser && userIdInput) {
    userIdInput.value = savedUser;
  }

  // ✅ Handle form submission
  loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const email = userIdInput.value.trim();
    const password = passwordInput.value.trim();

    // Validation
    if (!email || !password) {
      alert("Please fill in all fields");
      return;
    }

    try {
      console.log("📤 Sending login request for email:", email);

      const response = await fetch("http://localhost:9090/api/Farmer/Login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email,
          password
        })
      });

      const data = await response.json();

      if (response.ok && data.success) {
        alert("✅ Login Successful!\nWelcome, " + (data.name || "Farmer") + "!");
        
        // Store login session
        localStorage.setItem("loggedInUser", JSON.stringify({
          farmer_id: data.farmer_id,
          email: data.email,
          name: data.name
        }));

        // Redirect to Home page
        window.location.href = "Home.html";
      } else {
        alert("❌ Login Failed\n" + (data.error || "Invalid email or password"));
      }
    } catch (error) {
      console.error("❌ Error:", error);
      alert("❌ Connection error. Is the backend running?");
    }
  });
});
