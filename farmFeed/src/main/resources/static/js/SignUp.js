// // ============================================================
// // farmFeed — SignUp.js
// // ============================================================

// document.addEventListener("DOMContentLoaded", () => {
//   const form = document.querySelector("form");

//   form.addEventListener("submit", (e) => {
//     e.preventDefault();

//     const firstName      = form.querySelector('input[placeholder="First name"]').value.trim();
//     const lastName       = form.querySelector('input[placeholder="Last name"]').value.trim();
//     const email          = form.querySelector('input[type="email"]').value.trim();
//     const phone          = form.querySelector('input[type="tel"]').value.trim();
//     const password       = form.querySelector('input[placeholder="Password"]').value.trim();
//     const confirmPassword= form.querySelector('input[placeholder="Confirm password"]').value.trim();

//     const aadhaarInput   = form.querySelector('input[placeholder="Aadhaar Number"]');
//     const aadhaar        = aadhaarInput ? aadhaarInput.value.trim() : "";

//     // at least email or aadhaar required
//     if (!email && !aadhaar) {
//       alert("Please enter at least Email or Aadhaar Number.");
//       return;
//     }

//     // password match
//     if (password !== confirmPassword) {
//       alert("Passwords do not match!");
//       return;
//     }

//     // password strength  (the spaces in your original regex broke it)
//     const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{6,}$/;
//     if (!passwordPattern.test(password)) {
//       alert(
//         "Password must be at least 6 characters and include:\n" +
//         "- At least 1 uppercase letter\n" +
//         "- At least 1 lowercase letter\n" +
//         "- At least 1 number\n" +
//         "- At least 1 special character"
//       );
//       return;
//     }

//     // phone length
//     if (phone.length < 10) {
//       alert("Please enter a valid phone number.");
//       return;
//     }

//     // success  (replace with real API call later)
//     alert("Sign Up Successful!\nWelcome, " + firstName + " " + lastName + "!");
//   });
// });


// ============================================================
// farmFeed — SignUp.js (FINAL WORKING VERSION)
// ============================================================

document.addEventListener("DOMContentLoaded", () => {
  const form = document.querySelector("form");

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const firstName = form.querySelector('input[placeholder="First name"]').value.trim();
    const lastName  = form.querySelector('input[placeholder="Last name"]').value.trim();
    const email     = form.querySelector('input[type="email"]').value.trim();
    const phone     = form.querySelector('input[type="tel"]').value.trim();
    const password  = form.querySelector('input[placeholder="Password"]').value.trim();
    const confirmPassword =
      form.querySelector('input[placeholder="Confirm password"]').value.trim();

    const aadhaarInput = form.querySelector('input[placeholder="Aadhaar Number"]');
    const aadhaar = aadhaarInput ? aadhaarInput.value.trim() : "";

    // at least email or aadhaar required
    if (!email && !aadhaar) {
      alert("Please enter at least Email or Aadhaar Number.");
      return;
    }

    // password match
    if (password !== confirmPassword) {
      alert("Passwords do not match!");
      return;
    }

    // password strength
    const passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{6,}$/;
    if (!passwordPattern.test(password)) {
      alert(
        "Password must be at least 6 characters and include:\n" +
        "- At least 1 uppercase letter\n" +
        "- At least 1 lowercase letter\n" +
        "- At least 1 number\n" +
        "- At least 1 special character"
      );
      return;
    }

    // phone validation
    if (phone.length < 10) {
      alert("Please enter a valid phone number.");
      return;
    }

    // ✅ SUCCESS
    alert(
      "Sign Up Successful!\nWelcome, " +
      firstName + " " + lastName +
      "\nRedirecting to Login page..."
    );

    // ✅ Save email for Login page auto-fill
    localStorage.setItem("lastSignupUser", email);

    // ✅ Redirect to Login page
    window.location.href = "Login.html"; // or Login.html (check file name)
  });
});


// document.addEventListener("DOMContentLoaded", () => {
//   const form = document.querySelector("form");

//   form.addEventListener("submit", (e) => {
//     e.preventDefault();

//     const firstName = document.getElementById("firstName").value.trim();
//     const lastName  = document.getElementById("lastName").value.trim();
//     const email     = document.getElementById("email").value.trim();
//     const phone     = document.getElementById("phone").value.trim();
//     const password  = document.getElementById("password").value.trim();

//     fetch("http://localhost:9090/api/Farmer/SignUp", {
//       method: "POST",
//       headers: { "Content-Type": "application/json" },
//       body: JSON.stringify({
//         firstName,
//         lastName,
//         email,
//         phone,
//         password
//       })
//     })
//     .then(res => res.json())
//     .then(data => {
//       alert("SignUp Successful!");
//       window.location.href = "Login.html";
//     })
//     .catch(err => {
//       console.error(err);
//       alert("SignUp failed!");
//     });
//   });
// });
