// When the page is served from the same backend, using a relative URL
// avoids hard–coding the host/port. This also works if the app is deployed
// to another environment.
const BASE_URL = window.location.origin;

const farmerBtn = document.getElementById("farmerBtn");
const vendorBtn = document.getElementById("vendorBtn");

const farmerForm = document.getElementById("farmerForm");
const vendorForm = document.getElementById("vendorForm");

const title = document.getElementById("title");

farmerBtn.addEventListener("click", function(){
    farmerForm.style.display = "block";
    vendorForm.style.display = "none";

    farmerBtn.classList.add("active");
    vendorBtn.classList.remove("active");

    title.innerText = "Sign Up - Farmer";
});

vendorBtn.addEventListener("click", function(){
    farmerForm.style.display = "none";
    vendorForm.style.display = "block";

    vendorBtn.classList.add("active");
    farmerBtn.classList.remove("active");

    title.innerText = "Sign Up - Vendor";
});

// ===========================================================
// FARMER FORM SUBMISSION
// ===========================================================
farmerForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const fullName = document.getElementById("farmerFullName").value.trim();
    const address = document.getElementById("farmerAddress").value.trim();
    const phone = document.getElementById("farmerPhone").value.trim();
    const password = document.getElementById("farmerPassword").value.trim();
    const confirmPassword = document.getElementById("farmerConfirmPassword").value.trim();

    if (!fullName || !address || !phone || !password || !confirmPassword) {
        alert("Please fill all fields");
        return;
    }

    if (password !== confirmPassword) {
        alert("Passwords do not match");
        return;
    }

    // build payload; any additional fields we want to track can be added here
    const payload = { fullName, address, phone, password };

    try {
        const response = await fetch(`${BASE_URL}/api/farmer/SignUp`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const data = await response.json();
        console.log("Farmer signup response", response.status, data);

        if (response.ok && data.success) {
            alert("Farmer Registration Successful! Please login.");
            window.location.href = "Login.html";
        } else {
            // show detailed message if available
            alert(data.error || `Farmer registration failed (status ${response.status})`);
        }

    } catch (error) {
        alert("Server error during Farmer registration!");
        console.error("Farmer fetch failed", error);
    }
});

// ===========================================================
// VENDOR FORM SUBMISSION
// ===========================================================
vendorForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const ownerName = document.getElementById("vendorOwnerName").value.trim();
    const shopName = document.getElementById("vendorShopName").value.trim();
    const licenseNumber = document.getElementById("vendorLicenseNumber").value.trim();
    const shopAddress = document.getElementById("vendorShopAddress").value.trim();
    const email = document.getElementById("vendorEmail").value.trim();
    const password = document.getElementById("vendorPassword").value.trim();
    const confirmPassword = document.getElementById("vendorConfirmPassword").value.trim();

    if (!ownerName || !shopName || !licenseNumber || !shopAddress || !email || !password || !confirmPassword) {
        alert("Please fill all fields");
        return;
    }

    if (password !== confirmPassword) {
        alert("Passwords do not match");
        return;
    }

    const payload = { ownerName, shopName, licenseNumber, shopAddress, email, password };

    try {
        const response = await fetch(`${BASE_URL}/api/shopkeeper/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        const data = await response.json();
        console.log("Vendor signup response", response.status, data);

        if (response.ok && data.success) {
            alert("Vendor Registration Successful! Please login.");
            window.location.href = "Login.html";
        } else {
            alert(data.error || `Vendor registration failed (status ${response.status})`);
        }

    } catch (error) {
        alert("Server error during Vendor registration!");
        console.error("Vendor fetch failed", error);
    }
});
