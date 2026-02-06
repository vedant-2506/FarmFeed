// ============================================================
// farmFeed — Cart.js
// ============================================================

document.addEventListener("DOMContentLoaded", () => {
  const cartContainer = document.getElementById("cart-items");
  const clearCartBtn  = document.getElementById("clear-cart");

  let cart = JSON.parse(localStorage.getItem("cart")) || [];

  renderCart();

  // ---- clear all ----
  clearCartBtn.addEventListener("click", () => {
    if (confirm("Are you sure you want to clear the cart?")) {
      localStorage.removeItem("cart");
      cart = [];
      renderCart();
    }
  });

  // ==============================================================
  function renderCart() {
    cartContainer.innerHTML = "";

    if (cart.length === 0) {
      cartContainer.innerHTML =
        '<div class="col-12"><h5 class="text-center text-muted my-4">Your cart is empty!</h5></div>';
      return;
    }

    let grandTotal = 0;

    cart.forEach((item, index) => {
      const qty   = item.qty || 1;
      // price may contain commas from the HTML text; strip them
      const price = parseInt(String(item.price).replace(/[^0-9]/g, ""), 10) || 0;
      const subtotal = price * qty;
      grandTotal += subtotal;

      const col = document.createElement("div");
      col.classList.add("col-md-4");
      col.innerHTML = `
        <div class="card shadow-sm h-100">
          <img src="${item.img}" class="card-img-top" alt="${item.name}">
          <div class="card-body text-center d-flex flex-column">
            <h5 class="card-title">${item.name}</h5>
            <p class="card-text mb-1"><strong>₹${price} each</strong></p>

            <!-- quantity controls -->
            <div class="d-flex align-items-center justify-content-center gap-2 my-2">
              <button class="btn btn-outline-success btn-sm dec-btn" data-index="${index}">−</button>
              <span class="fw-bold qty-label" data-index="${index}">${qty}</span>
              <button class="btn btn-outline-success btn-sm inc-btn" data-index="${index}">+</button>
            </div>

            <p class="text-muted mb-2">Subtotal: <strong>₹${subtotal}</strong></p>
            <button class="btn btn-danger btn-sm remove-btn mt-auto" data-index="${index}">Remove</button>
          </div>
        </div>`;
      cartContainer.appendChild(col);
    });

    // ---- totals row ----
    const gst      = grandTotal * 0.18;
    const grandGst = grandTotal + gst;

    const totalsDiv = document.createElement("div");
    totalsDiv.classList.add("col-12", "mt-3");
    totalsDiv.innerHTML = `
      <div class="border-top pt-3 text-end">
        <p class="mb-1">Subtotal: <strong>₹${grandTotal}</strong></p>
        <p class="mb-1 text-muted">GST (18%): ₹${gst.toFixed(2)}</p>
        <p class="fs-5">Grand Total: <strong>₹${grandGst.toFixed(2)}</strong></p>
      </div>`;
    cartContainer.appendChild(totalsDiv);

    // ---- wire inc / dec / remove ----
    cartContainer.querySelectorAll(".inc-btn").forEach((btn) => {
      btn.addEventListener("click", () => {
        const i = parseInt(btn.dataset.index, 10);
        cart[i].qty = (cart[i].qty || 1) + 1;
        save();
      });
    });

    cartContainer.querySelectorAll(".dec-btn").forEach((btn) => {
      btn.addEventListener("click", () => {
        const i = parseInt(btn.dataset.index, 10);
        cart[i].qty = (cart[i].qty || 1) - 1;
        if (cart[i].qty < 1) {
          cart.splice(i, 1);
        }
        save();
      });
    });

    cartContainer.querySelectorAll(".remove-btn").forEach((btn) => {
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