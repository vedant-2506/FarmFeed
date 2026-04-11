
const API_BASE_URL = window.API_BASE_URL || window.location.origin;

document.addEventListener("DOMContentLoaded", async () => {
  const farmerId = localStorage.getItem("farmer_id");
  const vendorId = localStorage.getItem("shop_id");
  
  if (!farmerId && !vendorId) {
    alert("login first");
    window.location.href = "Login.html";
    return;
  }

  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      localStorage.clear();
      window.location.href = "Login.html";
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
      const price = item.subtotal ? (item.subtotal / qty) : 0;
      const subtotal = price * qty;
      grandTotal += subtotal;

      const col = document.createElement("div");
      col.classList.add("col-md-4");
      col.innerHTML = `
        <div class="card shadow-sm h-100">
          <img src="${item.product.image_url || 'https://via.placeholder.com/300'}" class="card-img-top" alt="${item.product.name}" style="height: 200px; object-fit: cover;">
          <div class="card-body text-center d-flex flex-column">
            <h5 class="card-title">${item.product.name}</h5>
            <p class="card-text mb-1 text-success fw-bold">Rs${price.toFixed(2)}</p>

            <div class="d-flex align-items-center justify-content-center gap-2 my-2">
              <button class="btn btn-outline-success btn-sm dec-btn" data-cart-id="${item.cartId}">-</button>
              <span class="fw-bold px-2">${qty}</span>
              <button class="btn btn-outline-success btn-sm inc-btn" data-cart-id="${item.cartId}">+</button>
            </div>

            <p class="text-muted mb-2">subtotal: Rs${subtotal.toFixed(2)}</p>
            <button class="btn btn-outline-danger btn-sm remove-btn" data-cart-id="${item.cartId}">
              remove
            </button>
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
            <p class="mb-1">subtotal: <strong>Rs${grandTotal.toFixed(2)}</strong></p>
            <p class="mb-1 text-muted">gst (18%): Rs${gst.toFixed(2)}</p>
            <p class="fs-5 fw-bold text-success">total: Rs${grandGst.toFixed(2)}</p>
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
      btn.addEventListener("click", async () => {
        const cartId = btn.dataset.cartId;
        await removeCartItem(cartId);
      });
    });
  }
});