// ============================================================
// farmFeed — LogIn.js (FINAL WORKING VERSION)
// ============================================================

document.addEventListener("DOMContentLoaded", () => {

  // Get elements safely
  const loginForm = document.getElementById("LoginForm");
  const userIdInput = document.getElementById("userId");
  const passwordInput = document.getElementById("password");

  // ✅ Auto-fill userId from SignUp (if exists)
  const savedUser = localStorage.getItem("lastSignupUser");
  if (savedUser && userIdInput) {
    userIdInput.value = savedUser;
  }

  // ✅ If login form exists, attach submit listener
  if (loginForm) {
    loginForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const userId = userIdInput.value.trim();
      const password = passwordInput.value.trim();

      // Validation
      if (!userId || !password) {
        alert("Please fill in all fields.");
        return;
      }

      //  Demo login (replace with backend API later)
      if (userId === "admin" && password === "12345") {
        alert("Login Successful! Redirecting to Home...");

        // // Optional: store login session
        // localStorage.setItem("loggedInUser", userId);

        // // Redirect to Home page
        // window.location.href = "Home.html";
      } else {
        alert("Invalid User ID or Password!");
      }
    });
  }

});
