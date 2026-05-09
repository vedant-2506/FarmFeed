
const API_BASE_URL = window.API_BASE_URL || window.location.origin;

document.addEventListener("DOMContentLoaded", async () => {
  const farmerId = localStorage.getItem("farmer_id");
  const vendorId = localStorage.getItem("shop_id");
  
  if (!farmerId && !vendorId) {
<<<<<<< HEAD
    if (window.Toast) Toast.error("Please login first");
    setTimeout(() => {
      window.location.href = "Login.html";
    }, 2000);
=======
    alert("login first");
    window.location.href = "Login.html";
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263
    return;
  }

  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", async () => {
      // Cart persists across sessions - do NOT clear it on logout
      // Just clear localStorage and redirect
      localStorage.clear();
<<<<<<< HEAD
      if (window.Toast) Toast.success("Logged out successfully");
      setTimeout(() => {
        window.location.href = "Login.html";
      }, 1000);
=======
      sessionStorage.clear();
      window.location.href = "Login.html";
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263
    });
  }

  const cartContainer = document.getElementById("cart-items");
  const clearCartBtn  = document.getElementById("clear-cart");

  let cartItems = [];
  
  await loadCart();

  clearCartBtn.addEventListener("click", async () => {
    if (confirm("clear cart?")) {
      await clearCartFromDB();
    }
  });

  async function loadCart() {
    try {
      const response = await fetch(`${API_BASE_URL}/api/cart/farmer/${farmerId}/details`);
      const data = await response.json();
      
      if (data.success && data.data) {
        cartItems = data.data;
      }
    } catch (e) {
      console.error("error loading cart:", e);
    }
    
    renderCart();
  }

  async function clearCartFromDB() {
    try {
      const response = await fetch(`${API_BASE_URL}/api/cart/farmer/${farmerId}/clear`, {
        method: "DELETE"
      });
      const data = await response.json();
      
      if (data.success) {
        alert("cart cleared");
        await loadCart();
      }
    } catch (e) {
      alert("error clearing cart");
      console.error(e);
    }
  }

  async function removeCartItem(cartId) {
    try {
      const response = await fetch(`${API_BASE_URL}/api/cart/${cartId}`, {
        method: "DELETE"
      });
      const data = await response.json();
      
      if (data.success) {
        await loadCart();
      }
    } catch (e) {
      alert("error removing item");
      console.error(e);
    }
  }

  async function updateQuantityDB(cartId, newQty) {
    try {
      const response = await fetch(`${API_BASE_URL}/api/cart/${cartId}/quantity`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ quantity: newQty })
      });
      const data = await response.json();
      
      if (data.success) {
        await loadCart();
      }
    } catch (e) {
      alert("error updating quantity");
      console.error(e);
    }
  }

  function renderCart() {
    cartContainer.innerHTML = "";

    if (!cartItems || cartItems.length === 0) {
      cartContainer.innerHTML = `
        <div class="col-12 text-center my-5">
          <i class="bi bi-cart-x" style="font-size:4rem; color:#ccc;"></i>
          <h5 class="text-muted mt-3">cart is empty</h5>
          <a href="Home.html" class="btn btn-success mt-3">browse</a>
        </div>`;
      return;
    }

    let grandTotal = 0;

    cartItems.forEach((item, index) => {
      const qty = item.quantity || 1;
      const price = item.subtotal ? (item.subtotal / qty) : (item.product?.price || 0);
      const subtotal = price * qty;
      grandTotal += subtotal;

      const col = document.createElement("div");
      col.classList.add("col-md-4");
      const productName = item.product?.name || "Product";
      
      // Create image HTML
      const fallbackImage = "https://images.unsplash.com/photo-1592982537447-6f2a6a0b2d8f?w=800&q=80";
      const productImage = item.product?.image || fallbackImage;
      
      imageHtml = `<img src="${productImage}" alt="${productName}" style="width: 100%; height: 100%; object-fit: cover;">`;
      
      col.innerHTML = `
        <div class="product-card h-100">
          <div class="product-card-image">
            <img src="${productImage}" alt="${productName}" loading="lazy">
          </div>
          <div class="product-card-body">
            <h6 class="product-card-title">${productName}</h6>
            <div class="product-card-price">₹${price.toLocaleString("en-IN")}</div>
            
            <div class="d-flex align-items-center gap-2 my-2">
              <button class="btn btn-outline-success btn-sm dec-btn" data-cart-id="${item.cartId}">-</button>
              <span class="fw-bold px-2">${qty}</span>
              <button class="btn btn-outline-success btn-sm inc-btn" data-cart-id="${item.cartId}">+</button>
            </div>

            <div class="d-flex justify-content-between align-items-center mt-auto pt-2 border-top">
              <span class="text-muted small">Subtotal: ₹${subtotal.toLocaleString("en-IN")}</span>
              <button class="btn btn-sm btn-danger remove-btn" data-cart-id="${item.cartId}">
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </div>
        </div>`;
      cartContainer.appendChild(col);
    });

    const gst = grandTotal * 0.18;
    const grandGst = grandTotal + gst;

    const totalsDiv = document.createElement("div");
    totalsDiv.classList.add("col-12", "mt-4");
    totalsDiv.innerHTML = `
      <div class="border-top pt-4">
        <div class="d-flex justify-content-end">
          <div class="text-end">
            <p class="mb-1">subtotal: <strong>₹${grandTotal.toLocaleString("en-IN", {minimumFractionDigits: 2, maximumFractionDigits: 2})}</strong></p>
            <p class="mb-1 text-muted">gst (18%): ₹${gst.toLocaleString("en-IN", {minimumFractionDigits: 2, maximumFractionDigits: 2})}</p>
            <p class="fs-5 fw-bold text-success">total: ₹${grandGst.toLocaleString("en-IN", {minimumFractionDigits: 2, maximumFractionDigits: 2})}</p>
            <a href="Checkout.html" class="btn btn-success px-5 mt-2">
              checkout
            </a>
          </div>
        </div>
      </div>`;
    cartContainer.appendChild(totalsDiv);

    cartContainer.querySelectorAll(".inc-btn").forEach(btn => {
      btn.addEventListener("click", async () => {
        const cartId = btn.dataset.cartId;
        const item = cartItems.find(c => c.cartId == cartId);
        if (item) {
          await updateQuantityDB(cartId, item.quantity + 1);
        }
      });
    });

    cartContainer.querySelectorAll(".dec-btn").forEach(btn => {
      btn.addEventListener("click", async () => {
        const cartId = btn.dataset.cartId;
        const item = cartItems.find(c => c.cartId == cartId);
        if (item && item.quantity > 1) {
          await updateQuantityDB(cartId, item.quantity - 1);
        }
      });
    });

    cartContainer.querySelectorAll(".remove-btn").forEach(btn => {
<<<<<<< HEAD
      btn.addEventListener("click", () => {
        const i = parseInt(btn.dataset.index, 10);
        const name = cart[i].name;
        cart.splice(i, 1);
        save();
        if (window.Toast) Toast.info(`${name} removed from cart`);
=======
      btn.addEventListener("click", async () => {
        const cartId = btn.dataset.cartId;
        await removeCartItem(cartId);
>>>>>>> 80581568b497d44057ac1e76cfd3dc0e15879263
      });
    });
  }
});