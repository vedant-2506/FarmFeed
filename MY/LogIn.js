document.addEventListener("DOMContentLoaded", () => {


  // Form validation
  const loginForm = document.getElementById("loginForm");

  loginForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const userId = document.getElementById("userId").value.trim();
    const password = document.getElementById("password").value.trim();

    if (userId === "" || password === "") {
      alert(" Please fill in all fields.");
      return;
    }

    // demo data
    if (userId === "admin" && password === "12345") {
      alert("Login Successful! Redirecting...");
    //   window.location.href = "dashboard.html"; // Example redirect
    } else {
      alert("Invalid User ID or Password!");
    }
  });
});
