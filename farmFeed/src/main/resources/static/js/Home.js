// ============================================================
// farmFeed - Home.js (FIXED)
// Products are in HTML — JS only handles cart + search
// ============================================================

const API_BASE_URL = "https://farmfeed-backend.onrender.com/api/fertilizers";

// ✅ INIT
document.addEventListener("DOMContentLoaded", function () {
  updateCartCount();       // show cart count from localStorage
  setupAddToCartButtons(); // wire all Add to Cart buttons
  setupSearchForm();       // wire search
});

// ===== SEARCH =====
function setupSearchForm() {
  const form = document.getElementById("searchForm");
  const box  = document.getElementById("searchBox");
  if (!form) return;

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    const query = box.value.trim();
    if (!query) {
      // Show all cards again
      document.querySelectorAll("#products-container .col").forEach(c => c.style.display = "");
      return;
    }
    // Filter visible cards by name
    document.querySelectorAll("#products-container .col").forEach(col => {
      const name = col.querySelector(".card-title")?.textContent.toLowerCase() || "";
      col.style.display = name.includes(query.toLowerCase()) ? "" : "none";
    });
  });
}

// ===== CART =====
function setupAddToCartButtons() {
  document.querySelectorAll(".add-cart-btn").forEach(btn => {
    const nb = btn.cloneNode(true);
    btn.parentNode.replaceChild(nb, btn);
    nb.addEventListener("click", function () { handleAddToCart(this); });
  });
}

function handleAddToCart(button) {
  const productId    = button.dataset.id;
  const productName  = button.dataset.name;
  const productPrice = button.dataset.price;
  const productImg   = button.dataset.img;

  let cart = JSON.parse(localStorage.getItem("cart")) || [];
  const existing = cart.find(item => item.name === productName);

  if (existing) {
    existing.qty = (existing.qty || 1) + 1;
  } else {
    cart.push({ id: productId, name: productName, price: productPrice, img: productImg, qty: 1 });
  }

  localStorage.setItem("cart", JSON.stringify(cart));
  updateCartCount();

  // Button feedback
  const original = button.textContent;
  button.textContent = "✓ Added!";
  button.disabled = true;
  button.classList.replace("btn-success", "btn-secondary");
  setTimeout(() => {
    button.textContent = original;
    button.disabled = false;
    button.classList.replace("btn-secondary", "btn-success");
  }, 1500);
}

function updateCartCount() {
  const cart  = JSON.parse(localStorage.getItem("cart")) || [];
  const total = cart.reduce((sum, item) => sum + (item.qty || item.quantity || 1), 0);
  const el    = document.getElementById("cart-count");
  if (el) el.textContent = total;
}
