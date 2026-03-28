// ============================================================
// farmFeed — Cart.js
// ============================================================

document.addEventListener("DOMContentLoaded", () => {
  // ✅ Check if user is logged in
  const farmerId = localStorage.getItem("farmer_id");
  const vendorId = localStorage.getItem("shop_id");
  
  if (!farmerId && !vendorId) {
    alert("Please login first");
    window.location.href = "Login.html";
    return;
  }

  // ✅ Logout button
  const logoutBtn = document.getElementById("logoutBtn");
  if (logoutBtn) {
    logoutBtn.addEventListener("click", () => {
      localStorage.clear();
      window.location.href = "Login.html";
    });
  }

  const cartContainer = document.getElementById("cart-items");
  const clearCartBtn  = document.getElementById("clear-cart");

  // ✅ Fix: load cart fresh each render
  let cart = JSON.parse(localStorage.getItem("cart")) || [];

  renderCart();

  clearCartBtn.addEventListener("click", () => {
    if (confirm("Are you sure you want to clear the cart?")) {
      localStorage.removeItem("cart");
      cart = [];
      renderCart();
    }
  });

  function renderCart() {
    // ✅ Always reload from localStorage (fresh data)
    cart = JSON.parse(localStorage.getItem("cart")) || [];
    cartContainer.innerHTML = "";

    if (cart.length === 0) {
      cartContainer.innerHTML = `
        <div class="col-12 text-center my-5">
          <i class="bi bi-cart-x" style="font-size:4rem; color:#ccc;"></i>
          <h5 class="text-muted mt-3">Your cart is empty!</h5>
          <a href="Home.html" class="btn btn-success mt-3">Browse Products</a>
        </div>`;
      return;
    }

    let grandTotal = 0;

    cart.forEach((item, index) => {
      // ✅ Support both "qty" and "quantity" for compatibility
      const qty      = item.qty || item.quantity || 1;
      const price    = parseInt(String(item.price).replace(/[^0-9]/g, ""), 10) || 0;
      const subtotal = price * qty;
      grandTotal    += subtotal;

      const col = document.createElement("div");
      col.classList.add("col-md-4");
      col.innerHTML = `
        <div class="card shadow-sm h-100">
          <img src="${item.img}" class="card-img-top" alt="${item.name}">
          <div class="card-body text-center d-flex flex-column">
            <h5 class="card-title">${item.name}</h5>
            <p class="card-text mb-1 text-success fw-bold">₹${price} / bag</p>

            <div class="d-flex align-items-center justify-content-center gap-2 my-2">
              <button class="btn btn-outline-success btn-sm dec-btn" data-index="${index}">−</button>
              <span class="fw-bold px-2">${qty}</span>
              <button class="btn btn-outline-success btn-sm inc-btn" data-index="${index}">+</button>
            </div>

            <p class="text-muted mb-2">Subtotal: <strong>₹${subtotal}</strong></p>
            <button class="btn btn-outline-danger btn-sm remove-btn mt-auto" data-index="${index}">
              Remove
            </button>
          </div>
        </div>`;
      cartContainer.appendChild(col);
    });

    // Totals row
    const gst      = grandTotal * 0.18;
    const grandGst = grandTotal + gst;

    const totalsDiv = document.createElement("div");
    totalsDiv.classList.add("col-12", "mt-4");
    totalsDiv.innerHTML = `
      <div class="border-top pt-4">
        <div class="d-flex justify-content-end">
          <div class="text-end">
            <p class="mb-1">Subtotal: <strong>₹${grandTotal.toLocaleString("en-IN")}</strong></p>
            <p class="mb-1 text-muted">GST (18%): ₹${gst.toFixed(2)}</p>
            <p class="fs-5 fw-bold text-success">Grand Total: ₹${grandGst.toFixed(2)}</p>
            <a href="Checkout.html" class="btn btn-success px-5 mt-2">
              Proceed to Checkout →
            </a>
          </div>
        </div>
      </div>`;
    cartContainer.appendChild(totalsDiv);

    // Wire buttons
    cartContainer.querySelectorAll(".inc-btn").forEach(btn => {
      btn.addEventListener("click", () => {
        const i = parseInt(btn.dataset.index, 10);
        cart[i].qty = (cart[i].qty || cart[i].quantity || 1) + 1;
        save();
      });
    });

    cartContainer.querySelectorAll(".dec-btn").forEach(btn => {
      btn.addEventListener("click", () => {
        const i = parseInt(btn.dataset.index, 10);
        cart[i].qty = (cart[i].qty || cart[i].quantity || 1) - 1;
        if (cart[i].qty < 1) cart.splice(i, 1);
        save();
      });
    });

    cartContainer.querySelectorAll(".remove-btn").forEach(btn => {
      btn.addEventListener("click", () => {
        const i = parseInt(btn.dataset.index, 10);
        cart.splice(i, 1);
        save();
      });
    });
  }

  function save() {
    localStorage.setItem("cart", JSON.stringify(cart));
    renderCart();
  }
});