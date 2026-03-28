const API_BASE_URL = window.API_BASE_URL || window.location.origin;
const FERTILIZER_API = `${API_BASE_URL}/api/fertilizers`;
const FALLBACK_IMAGE = "https://images.unsplash.com/photo-1592982537447-6f2a6a0b2d8f?w=800&q=80";

let allProducts = [];
let activeCategory = "all";
let searchQuery = "";

document.addEventListener("DOMContentLoaded", async function () {
  updateCartCount();
  setupSearchForm();
  setupCategoryFilters();
  await loadProductsFromApi();
  setupAddToCartButtons();
});

async function loadProductsFromApi() {
  const container = document.getElementById("products-container");
  if (!container) return;

  try {
    const response = await fetch(FERTILIZER_API);
    if (!response.ok) {
      throw new Error(`Failed with status ${response.status}`);
    }

    const fertilizers = await response.json();
    if (!Array.isArray(fertilizers) || fertilizers.length === 0) {
      return;
    }

    allProducts = fertilizers.map(item => ({
      id: item.fertilizer_id,
      name: item.name,
      description: item.description || "Quality fertilizer for healthier crop growth.",
      price: item.price,
      stock: item.stock,
      image: item.image_url || FALLBACK_IMAGE,
      category: normalizeCategory(item.category, item.name, item.description)
    }));

    applyFilters();
  } catch (error) {
    console.error("Unable to load products from API:", error);
  }
}

function renderProducts(products) {
  const container = document.getElementById("products-container");
  if (!container) return;

  if (!Array.isArray(products) || products.length === 0) {
    container.innerHTML = `
      <div class="col-12 text-center py-5 text-muted">
        No products found. Please import products into database.
      </div>
    `;
    return;
  }

  container.innerHTML = products.map(product => `
    <div class="col">
      <div class="card h-100 shadow-sm">
        <img src="${product.image}" class="card-img-top" alt="${escapeHtml(product.name)}">
        <div class="card-body">
          <div class="category-badge">${escapeHtml(product.category)}</div>
          <h5 class="card-title">${escapeHtml(product.name)}</h5>
          <p class="card-text">${escapeHtml(product.description)}</p>
          <p><strong>Price: Rs ${Number(product.price || 0).toLocaleString("en-IN")}</strong></p>
          <button class="btn btn-success add-cart-btn w-100"
                  data-id="${product.id}"
                  data-name="${escapeHtml(product.name)}"
                  data-price="${product.price}"
                  data-img="${product.image}">
            Add to Cart
          </button>
        </div>
      </div>
    </div>
  `).join("");

  setupAddToCartButtons();
}

function setupSearchForm() {
  const form = document.getElementById("searchForm");
  const box = document.getElementById("searchBox");
  if (!form || !box) return;

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    searchQuery = box.value.trim().toLowerCase();
    applyFilters();
  });
}

function setupCategoryFilters() {
  const filterButtons = document.querySelectorAll(".filter-chip");
  filterButtons.forEach(button => {
    button.addEventListener("click", function () {
      activeCategory = this.dataset.category || "all";
      filterButtons.forEach(btn => btn.classList.remove("active"));
      this.classList.add("active");
      applyFilters();
    });
  });
}

function applyFilters() {
  if (!Array.isArray(allProducts) || allProducts.length === 0) {
    renderProducts([]);
    return;
  }

  const filtered = allProducts.filter(product => {
    const categoryMatch = activeCategory === "all" || (product.category || "").toLowerCase() === activeCategory;
    const name = (product.name || "").toLowerCase();
    const description = (product.description || "").toLowerCase();
    const textMatch = !searchQuery || name.includes(searchQuery) || description.includes(searchQuery);
    return categoryMatch && textMatch;
  });

  renderProducts(filtered);
}

function setupAddToCartButtons() {
  document.querySelectorAll(".add-cart-btn").forEach(btn => {
    const fresh = btn.cloneNode(true);
    btn.parentNode.replaceChild(fresh, btn);
    fresh.addEventListener("click", function () {
      handleAddToCart(this);
    });
  });
}

function handleAddToCart(button) {
  const productId = button.dataset.id;
  const productName = button.dataset.name;
  const productPrice = button.dataset.price;
  const productImg = button.dataset.img;

  let cart = JSON.parse(localStorage.getItem("cart")) || [];
  const existing = cart.find(item => item.name === productName);

  if (existing) {
    existing.qty = (existing.qty || 1) + 1;
  } else {
    cart.push({ id: productId, name: productName, price: productPrice, img: productImg, qty: 1 });
  }

  localStorage.setItem("cart", JSON.stringify(cart));
  updateCartCount();

  const original = button.textContent;
  button.textContent = "Added";
  button.disabled = true;
  button.classList.replace("btn-success", "btn-secondary");
  setTimeout(() => {
    button.textContent = original;
    button.disabled = false;
    button.classList.replace("btn-secondary", "btn-success");
  }, 1200);
}

function updateCartCount() {
  const cart = JSON.parse(localStorage.getItem("cart")) || [];
  const total = cart.reduce((sum, item) => sum + (item.qty || item.quantity || 1), 0);
  const el = document.getElementById("cart-count");
  if (el) el.textContent = total;
}

function escapeHtml(value) {
  return String(value || "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function normalizeCategory(rawCategory, name, description) {
  const source = `${rawCategory || ""} ${name || ""} ${description || ""}`.toLowerCase();
  if (source.includes("organic")) {
    return "Organic";
  }
  if (
    source.includes("chemical") ||
    source.includes("urea") ||
    source.includes("dap") ||
    source.includes("potash") ||
    source.includes("npk")
  ) {
    return "Chemical";
  }
  return "Other";
}
