// signup.js

document.addEventListener("DOMContentLoaded", () => {
    const form = document.querySelector("form");

    form.addEventListener("submit", function (e) {
        e.preventDefault(); 

        // Input values
        const firstName = form.querySelector('input[placeholder="First name"]').value.trim();
        const lastName = form.querySelector('input[placeholder="Last name"]').value.trim();
        const email = form.querySelector('input[type="email"]').value.trim();
        const phone = form.querySelector('input[type="tel"]').value.trim();
        const password = form.querySelector('input[placeholder="Password"]').value.trim();
        const confirmPassword = form.querySelector('input[placeholder="Confirm password"]').value.trim();
        const aadhaarInput = form.querySelector('input[placeholder="Aadhaar Number"]'); 
        const aadhaar = aadhaarInput ? aadhaarInput.value.trim() : '';

        // Validation: either email or aadhaar is required
        if (!email && !aadhaar) {
            alert(" Please enter at least Email or Aadhaar Number.");
            return;
        }

        // Password match check
        if (password !== confirmPassword) {
            alert(" Passwords do not match!");
            return;
        }

        // Password strength check
        const passwordPattern = /^(?=.*[a-z])  (?=.*[A-Z])  (?=.*\d)  (?=.*[\W_]).{6,} $/;
       

        if (!passwordPattern.test(password)) {
            alert("⚠️ Password must be at least 6 characters and include:\n- At least 1 uppercase letter\n- At least 1 lowercase letter\n- At least 1 number\n- At least 1 special character");
            return;
        }

        // Optionally, validate phone number
        if (phone.length < 10) {
            alert("⚠️ Please enter a valid phone number.");
            return;
        }

        // Temporary success message
        alert(`Sign Up Successful!\nWelcome, ${firstName} ${lastName}!`);

      
    });
});
