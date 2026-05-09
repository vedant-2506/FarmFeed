document.addEventListener("DOMContentLoaded", () => {
  const BASE_URL = window.API_BASE_URL || window.location.origin;

  const farmerForm = document.getElementById("farmerLoginForm");
  farmerForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const phone = document.getElementById("farmerPhone").value.trim();
    const password = document.getElementById("farmerPassword").value.trim();

<<<<<<< HEAD
    if (!phone || !password) { Toast.warning("Please fill all fields"); return; }
    if (!/^\d{10}$/.test(phone)) { Toast.warning("Phone number must be exactly 10 digits"); return; }
=======
    if (!phone || !password) { alert("fill all fields"); return; }
    if (!/^\d{10}$/.test(phone)) { alert("phone needs to be 10 digits"); return; }
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263

    try {
      const response = await fetch(`${BASE_URL}/api/farmer/Login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ phone, password })
      });
      const data = await response.json();
      if (response.ok && data.success) {
        // Store farmer info in localStorage
<<<<<<< HEAD
        localStorage.setItem("farmer_id", data.farmer_id || data.id || "");
=======
        localStorage.setItem("farmer_id", data.farmer_id);
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263
        localStorage.setItem("farmer_name", data.fullName);
        localStorage.setItem("user_name", data.fullName);
        localStorage.setItem("farmer_phone", data.phone);
        localStorage.setItem("user_type", "farmer");
<<<<<<< HEAD
        Toast.success("Farmer Login Successful!");
        setTimeout(() => {
            window.location.href = "FarmerAccount.html";
        }, 1000);
=======
        alert("logged in!");
        window.location.href = "FarmerAccount.html";
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263
      } else {
        Toast.error(data.error || "Invalid Farmer Credentials");
      }
<<<<<<< HEAD
    } catch (error) { Toast.error("Server error! Please try again."); }
=======
    } catch (error) { alert("error connecting. try again"); }
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263
  });

  const vendorForm = document.getElementById("vendorLoginForm");
  vendorForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const email = document.getElementById("vendorEmail").value.trim();
    const password = document.getElementById("vendorPassword").value.trim();

<<<<<<< HEAD
    if (!email || !password) { Toast.warning("Please fill all fields"); return; }
=======
    if (!email || !password) { alert("need email and password"); return; }
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263

    try {
      const response = await fetch(`${BASE_URL}/api/shopkeeper/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
      });
      const data = await response.json();
      if (response.ok && data.success) {
        // Store vendor info in localStorage
<<<<<<< HEAD
        localStorage.setItem("shop_id", data.shop_id || data.id || "");
        localStorage.setItem("shop_name", data.shopName);
        localStorage.setItem("owner_name", data.ownerName);
        localStorage.setItem("user_name", data.ownerName); // For dropdown display
=======
        localStorage.setItem("shop_id", data.shop_id);
        localStorage.setItem("shop_name", data.shop_name);
        localStorage.setItem("owner_name", data.owner_name);
        localStorage.setItem("user_name", data.owner_name); // For dropdown display
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263
        localStorage.setItem("user_type", "vendor");
        Toast.success("Vendor Login Successful!");
        setTimeout(() => {
            window.location.href = "VendorAccount.html";
        }, 1000);
      } else {
        Toast.error(data.error || "Invalid Vendor Credentials");
      }
    } catch (error) { Toast.error("Server error! Try again later."); }
  });
});