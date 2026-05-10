const API_BASE_URL = window.API_BASE_URL || window.location.origin;
const PRODUCTS_API = `${API_BASE_URL}/api/products`;
const FALLBACK_IMAGE = "https://images.unsplash.com/photo-1592982537447-6f2a6a0b2d8f?w=800&q=80";

let allProducts = [];
let activeCategory = "all";
let searchQuery = "";

document.addEventListener("DOMContentLoaded", async function () {
  updateCartCount();
  setupSearchForm();
  setupCategoryFilters();
  await loadProductsFromApi();
});

// ─── API LOAD ────────────────────────────────────────────────────────────────

async function loadProductsFromApi() {
  const container = document.getElementById("products-container");
  if (!container) return;

  try {
    const response = await fetch(PRODUCTS_API);
    if (!response.ok) throw new Error(`Failed with status ${response.status}`);

    const responseBody = await response.json();

    // Handle both array response and { success, data: [] } envelope
    const fertilizers = Array.isArray(responseBody)
      ? responseBody
      : Array.isArray(responseBody.data)
        ? responseBody.data
        : [];

    if (fertilizers.length === 0) {
      console.warn("No products found from API");
      renderProducts([]);
      return;
    }

    // DEBUG: log exact keys returned by backend
    console.log("Raw API field names:", Object.keys(fertilizers[0]));
    console.log("Sample product:", fertilizers[0]);

    allProducts = fertilizers.map(item => ({
      // FIX: SQL feed uses product_id/product_name; JPA entity uses id/name — handle both
      id:          item.product_id   || item.id,
      name:        item.product_name || item.name,
      description: item.description_clean || item.description || item.detailedDescription || "Quality product for healthier crop growth.",
      price:       item.price_inr    || item.price  || 0,
      stock:       item.stock        || 0,
      rating:      item.rating       || 0,
      totalReviews: item.total_reviews || item.totalReviews || 0,
      // FIX: SQL feed uses image_link; JPA entity uses imageLink
      image:       item.image_link   || item.imageLink || FALLBACK_IMAGE,
      vendorId:    item.vendor_id    || item.vendorId  || 1,
      // FIX: SQL feed uses primary_category; JPA uses category
      category:    normalizeCategory(
                     item.primary_category || item.category,
                     item.product_name     || item.name,
                     item.description_clean || item.description
                   )
    }));

    console.log("Processed products:", allProducts.length, allProducts[0]);
    applyFilters();
  } catch (error) {
    console.error("Unable to load products from API:", error);
    const container = document.getElementById("products-container");
    if (container) {
      container.innerHTML = `
        <div class="col-12 text-center py-5 text-muted">
          <p>Unable to load products. Please refresh the page.</p>
          <small>${error.message}</small>
        </div>`;
    }
  }
}

// ─── RENDER ──────────────────────────────────────────────────────────────────

function renderProducts(products) {
  const container = document.getElementById("products-container");
  if (!container) return;

  if (!Array.isArray(products) || products.length === 0) {
    container.innerHTML = `
      <div class="col-12 text-center py-5 text-muted">
        No products found. Please import products into database.
      </div>`;
    return;
  }

  container.innerHTML = products.map(product => {
    const inStock  = parseInt(product.stock) > 0;
    const stars    = renderStars(product.rating);

    return `
    <div class="col">
      <div class="card h-100 shadow-sm">
        <img
          src="${escapeHtml(product.image)}"
          class="card-img-top"
          alt="${escapeHtml(product.name)}"
          onerror="this.src='${FALLBACK_IMAGE}'"
          style="height:200px; object-fit:cover;"
        >
        <div class="card-body d-flex flex-column">
          <div class="category-badge mb-1">${escapeHtml(product.category)}</div>
          <h5 class="card-title">${escapeHtml(product.name)}</h5>
          <p class="card-text flex-grow-1">${escapeHtml(truncate(product.description, 100))}</p>
          <div class="mb-1" title="${product.rating} / 5">
            ${stars}
            <small class="text-muted">(${product.totalReviews})</small>
          </div>
          <p class="mb-2"><strong>₹${Number(product.price || 0).toLocaleString("en-IN")}</strong></p>
          <p class="mb-2 ${inStock ? 'text-success' : 'text-danger'} small">
            ${inStock ? `In Stock (${product.stock})` : 'Out of Stock'}
          </p>
          <button
            class="btn ${inStock ? 'btn-success add-cart-btn' : 'btn-secondary'} w-100"
            data-id="${escapeHtml(String(product.id))}"
            data-name="${escapeHtml(product.name)}"
            data-price="${product.price}"
            data-img="${escapeHtml(product.image)}"
            data-stock="${product.stock}"
            data-vendor-id="${product.vendorId}"
            ${!inStock ? 'disabled' : ''}
          >
            ${inStock ? 'Add to Cart' : 'Out of Stock'}
          </button>
        </div>
      </div>
    </div>`;
  }).join("");

  setupAddToCartButtons();
}

// ─── SEARCH & FILTER ─────────────────────────────────────────────────────────

function setupSearchForm() {
  const form = document.getElementById("searchForm");
  const box  = document.getElementById("searchBox");
  if (!form || !box) return;

  form.addEventListener("submit", function (e) {
    e.preventDefault();
    searchQuery = box.value.trim().toLowerCase();
    applyFilters();
  });

  // Live search on input
  box.addEventListener("input", function () {
    searchQuery = this.value.trim().toLowerCase();
    applyFilters();
  });
}

function setupCategoryFilters() {
  const filterButtons = document.querySelectorAll(".filter-chip");
  filterButtons.forEach(button => {
    button.addEventListener("click", function () {
      activeCategory = (this.dataset.category || "all").toLowerCase();
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
    const categoryMatch =
      activeCategory === "all" ||
      (product.category || "").toLowerCase() === activeCategory;

    const name        = (product.name        || "").toLowerCase();
    const description = (product.description || "").toLowerCase();
    const textMatch   = !searchQuery || name.includes(searchQuery) || description.includes(searchQuery);

    return categoryMatch && textMatch;
  });

  renderProducts(filtered);
}

// ─── CART ────────────────────────────────────────────────────────────────────

function setupAddToCartButtons() {
  document.querySelectorAll(".add-cart-btn").forEach(btn => {
    // Clone to remove any old listeners
    const fresh = btn.cloneNode(true);
    btn.parentNode.replaceChild(fresh, btn);
    fresh.addEventListener("click", function () {
      handleAddToCart(this);
    });
  });
}

function handleAddToCart(button) {
  const farmerId = localStorage.getItem("farmer_id");
  const userType = localStorage.getItem("user_type");

  if (!farmerId || userType !== "farmer") {
    alert("Please login as a farmer to add items to cart");
    window.location.href = "Login.html";
    return;
  }

  // FIX: stock check before hitting API
  if (parseInt(button.dataset.stock) <= 0) {
    alert("This product is out of stock");
    return;
  }

  const productId = button.dataset.id;           // String e.g. "bighaat_1" — DO NOT parseInt
  const vendorId  = button.dataset.vendorId || 1; // FIX: from product data, not hardcoded

  const payload = {
    farmerId:  parseInt(farmerId),
    productId: productId,                         // FIX: keep as String, matches varchar(255) in DB
    vendorId:  parseInt(vendorId),
    quantity:  1
  };

  console.log("Adding to cart:", payload);

  // Optimistic UI
  button.textContent = "Adding...";
  button.disabled = true;

  fetch(`${API_BASE_URL}/api/cart/add`, {
    method:  "POST",
    headers: { "Content-Type": "application/json" },
    body:    JSON.stringify(payload)
  })
  .then(res => res.json())
  .then(data => {
    console.log("Cart response:", data);
    if (data.success) {
      updateCartCount();
      button.textContent = "✓ Added";
      button.classList.replace("btn-success", "btn-secondary");
      setTimeout(() => {
        button.textContent = "Add to Cart";
        button.disabled = false;
        button.classList.replace("btn-secondary", "btn-success");
      }, 1200);
    } else {
      button.textContent = "Add to Cart";
      button.disabled = false;
      alert("Error: " + (data.error || "Failed to add to cart"));
      console.error("Cart error response:", data);
    }
  })
  .catch(e => {
    button.textContent = "Add to Cart";
    button.disabled = false;
    alert("Connection error. Please try again.");
    console.error("Cart fetch error:", e);
  });
}

function updateCartCount() {
  const farmerId = localStorage.getItem("farmer_id");
  const vendorId = localStorage.getItem("shop_id");
  const el       = document.getElementById("cart-count");

  if (farmerId && !vendorId) {
    fetch(`${API_BASE_URL}/api/cart/farmer/${farmerId}/count`)
      .then(res => res.json())
      .then(data => {
        if (el) el.textContent = data.itemCount !== undefined ? data.itemCount : (data.count || 0);
      })
      .catch(() => {
        // Fallback to localStorage
        const cart = JSON.parse(localStorage.getItem("cart")) || [];
        if (el) el.textContent = cart.length;
      });
  } else {
    const cart  = JSON.parse(localStorage.getItem("cart")) || [];
    const total = cart.reduce((sum, item) => sum + (item.qty || item.quantity || 1), 0);
    if (el) el.textContent = total;
  }
}

// ─── HELPERS ─────────────────────────────────────────────────────────────────

function escapeHtml(value) {
  return String(value || "")
    .replaceAll("&",  "&amp;")
    .replaceAll("<",  "&lt;")
    .replaceAll(">",  "&gt;")
    .replaceAll('"',  "&quot;")
    .replaceAll("'",  "&#39;");
}

function truncate(str, maxLen) {
  if (!str) return "";
  return str.length > maxLen ? str.substring(0, maxLen) + "…" : str;
}

function renderStars(rating) {
  const r     = Math.round(parseFloat(rating) || 0);
  const total = 5;
  let stars   = "";
  for (let i = 1; i <= total; i++) {
    stars += `<span style="color:${i <= r ? '#f5a623' : '#ccc'}">★</span>`;
  }
  return stars;
}

// FIX: normalizeCategory now handles DB category values (seed, fertilizer, etc.)
// and falls back to the raw DB value instead of always returning "Other"
function normalizeCategory(rawCategory, name, description) {
  const raw    = (rawCategory || "").toLowerCase().trim();
  const source = `${raw} ${(name || "").toLowerCase()} ${(description || "").toLowerCase()}`;

  if (raw === "seed" || source.includes("seed"))                          return "Seed";
  if (raw === "fertilizer" || source.includes("fertilizer"))             return "Fertilizer";
  if (raw === "pesticide" || source.includes("pesticide") ||
      source.includes("insecticide") || source.includes("herbicide"))    return "Pesticide";
  if (source.includes("organic"))                                        return "Organic";
  if (source.includes("chemical") || source.includes("urea") ||
      source.includes("dap")      || source.includes("potash") ||
      source.includes("npk"))                                            return "Chemical";
  if (source.includes("tool") || source.includes("equipment"))          return "Equipment";

  // FIX: use raw DB category value with proper casing instead of "Other"
  if (rawCategory) return rawCategory.charAt(0).toUpperCase() + rawCategory.slice(1).toLowerCase();
  return "Other";
}
