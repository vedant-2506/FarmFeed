// ============================================================
// farmFeed - Home.js (COMPLETE WORKING VERSION)
// ============================================================

// ===========================================================
// CONFIGURATION
// ===========================================================
const API_BASE_URL = "https://farmfeed-backend.onrender.com/api/fertilizers";

// Product image mapping (since images aren't stored in DB)
const PRODUCT_IMAGES = {
  "Urea Fertilizer": "https://imgs.search.brave.com/8Ues7vOho-SowZyusrF0nz05eITiqBf8LFszypFswE8/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9zLmFs/aWNkbi5jb20vQHNj/MDQva2YvSFRCMUhz/a2VYekR1SzFSalNz/emRxNnhHTHBYYW8u/anBnXzMwMHgzMDAu/anBn",
  "DAP Fertilizer": "https://imgs.search.brave.com/ylsWAPAwNr2SATw0YlPy3TqLN2j9UT2CsOpLDTsDBJ8/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90aWlt/Zy50aXN0YXRpYy5j/b20vZnAvMS8wMDUv/MTU5L251dHJpZW50/LWZlcnRpbGl6ZXIt/c3VscGhhdGUtcG90/YXNoLTg3MC5qcGc",
  "Potash Fertilizer": "https://imgs.search.brave.com/FaGpCKkEVWPf5dqhNfHV1OVjJ3I71NXUBAYPnftsH4A/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly80Lmlt/aW1nLmNvbS9kYXRh/NC9DSC9YTi9NWS0y/NjA2Nzc3Mi9wb3Rh/c2gtZmVydGlsaXpl/ci0xMDAweDEwMDAu/cG5n",
  "NPK Fertilizer": "https://imgs.search.brave.com/v5Dp7ubNl2kRE6HWAzA1h9PWqS3_fB6ChmN7H6CDR70/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly81Lmlt/aW1nLmNvbS9kYXRh/NS9TRUxMRVIvRGVm/YXVsdC8yMDI1Lzkv/NTQyMjY5NTY1L0tJ/L0FFL0lMLzE5MzQ0/NTU4OC9ucGstZmVy/dGlsaXplci0wMC0w/MC01MC0yNTB4MjUw/LmpwZw",
  "Organic Compost": "https://imgs.search.brave.com/Yk9Tvvcc0Dc3qKU9m6aeIEBYK6dsu3P5YelTKae9220/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NzFuVGQwZmgwTkwu/anBn",
  "Bio Fertilizer": "https://imgs.search.brave.com/lffUPMk8NpDbFlx3xJuLURnpIAVvS1ylcLvJOSpYoQk/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9pbWFn/ZS5tYWRlLWluLWNo/aW5hLmNvbS8yMDNm/MGowMFlla2JGYXNK/a3Jvai9CT1BQLUJh/Zy1mb3ItQmlvLUZl/cnRpbGl6ZXItT3Jn/YW5pYy1GZXJ0aWxp/emVyLUdyYW51bGF0/b3ItNTBrZy1QUC1C/YWdzLVRyYW5zcGFy/ZW50LVBQLVdvdmVu/LUJhZy53ZWJw",
  "Superphosphate": "https://imgs.search.brave.com/QFFODfN0lR2sjbbF16-8NO3nSfhsa-B05OQxtZyGbLs/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/ZG9teW93bi5jb20v/aW1hZ2VzL3RodW1i/bmFpbHMvMzgxMS8z/ODExLmpwZy50aHVt/Yl80NTB4NDUwLmpw/Zw",
  "Vermicompost": "https://imgs.search.brave.com/ACpiqQcq1GSCooWDNOvI2Les1Hz3CZpZ9rkVFZ7oQoE/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly81Lmlt/aW1nLmNvbS9kYXRh/NS9aRC9MRy9TSC9T/RUxMRVItMzE3MDA1/Ny8xMC1rZy1zYWph/Zy1vcmdhbmljLXZl/cm1pY29tcG9zdC0y/NTB4MjUwLmpwZw",
  "Zinc Sulphate": "https://imgs.search.brave.com/wUrYIKAfxOxzNkQ8256rK_FMRFS0jtRxFUAVDZ2Q-x8/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly93d3cu/d2ludGVyc3VuY2hl/bWljYWwuY29tL2Nk/bi9zaG9wL2ZpbGVz/LzAxLTAxMi02X2xh/cmdlLmpwZz92PTE3/NTQ1MTczNTg",
  "Ammonium Sulphate": "https://5.imimg.com/data5/SELLER/Default/2022/5/ZH/YP/FA/150381740/ammonium-sulphate-fertilizer.jpg"
};

document.addEventListener("DOMContentLoaded", function() {
  updateCartCount();
  setupSearchForm();
  setupAddToCartButtons();
  loadAllProducts();
});

function setupSearchForm() {
  const searchForm = document.getElementById("searchForm");
  const searchBox = document.getElementById("searchBox");
  if (!searchForm) return;
  searchForm.addEventListener("submit", function(event) {
    event.preventDefault();
    const query = searchBox.value.trim();
    if (!query) { loadAllProducts(); return; }
    searchProducts(query);
  });
}

function searchProducts(query) {
  showLoadingState();
  fetch(`${API_BASE_URL}/search?name=${encodeURIComponent(query)}`)
    .then(r => { if (!r.ok) throw new Error(`HTTP ${r.status}`); return r.json(); })
    .then(products => renderProducts(products))
    .catch(error => showErrorMessage(`Search failed: ${error.message}`));
}

function loadAllProducts() {
  showLoadingState();
  fetch(API_BASE_URL)
    .then(r => { if (!r.ok) throw new Error(`HTTP ${r.status}`); return r.json(); })
    .then(products => {
      renderProducts(products);
      const searchBox = document.getElementById("searchBox");
      if (searchBox) searchBox.value = "";
    })
    .catch(error => showErrorMessage("Failed to load products. Backend may be sleeping (free tier). Please wait 30 seconds and refresh."));
}

function renderProducts(products) {
  const container = document.getElementById("products-container");
  if (!container) return;

  if (!products || products.length === 0) {
    container.innerHTML = `
      <div class="col-12 text-center my-5">
        <div class="alert alert-info">
          <h4>No products found</h4>
          <button class="btn btn-success" onclick="loadAllProducts()">Show All Products</button>
        </div>
      </div>`;
    return;
  }

  let html = "";
  products.forEach(product => {
    const img = PRODUCT_IMAGES[product.name] || PRODUCT_IMAGES["Urea Fertilizer"];
    const stock = product.stock || 0;
    html += `
      <div class="col">
        <div class="card h-100 shadow-sm">
          <img src="${img}" class="card-img-top" alt="${product.name}">
          <div class="card-body">
            <h5 class="card-title">${product.name}</h5>
            <p class="card-text">${product.description || ""}</p>
            <p><strong>Price: ₹${product.price}/50kg bag</strong></p>
            <p class="text-muted small">Stock: ${stock} bags</p>
            <button class="btn btn-success add-cart-btn w-100"
                    data-id="${product.fertilizer_id}"
                    data-name="${product.name}"
                    data-price="${product.price}"
                    data-img="${img}"
                    ${stock === 0 ? 'disabled' : ''}>
              ${stock === 0 ? 'Out of Stock' : 'Add to Cart'}
            </button>
          </div>
        </div>
      </div>`;
  });

  container.innerHTML = html;
  setupAddToCartButtons();
}

function showLoadingState() {
  const container = document.getElementById("products-container");
  if (container) container.innerHTML = `
    <div class="col-12 text-center my-5">
      <div class="spinner-border text-success" role="status"></div>
      <p class="mt-3 text-muted">Loading products... (first load may take 30s on free tier)</p>
    </div>`;
}

function showErrorMessage(message) {
  const container = document.getElementById("products-container");
  if (container) container.innerHTML = `
    <div class="col-12 text-center my-5">
      <div class="alert alert-danger">
        <h4>Error</h4><p>${message}</p>
        <button class="btn btn-success" onclick="loadAllProducts()">Try Again</button>
      </div>
    </div>`;
}

function setupAddToCartButtons() {
  document.querySelectorAll(".add-cart-btn").forEach(button => {
    const newButton = button.cloneNode(true);
    button.parentNode.replaceChild(newButton, button);
    newButton.addEventListener("click", function() { handleAddToCart(this); });
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
    existing.quantity = (existing.quantity || 1) + 1;
  } else {
    cart.push({ id: productId, name: productName, price: productPrice, img: productImg, quantity: 1 });
  }

  localStorage.setItem("cart", JSON.stringify(cart));
  updateCartCount();

  const original = button.textContent;
  button.textContent = "Added ✓";
  button.disabled = true;
  button.classList.replace("btn-success", "btn-secondary");
  setTimeout(() => {
    button.textContent = original;
    button.disabled = false;
    button.classList.replace("btn-secondary", "btn-success");
  }, 2000);
}

function updateCartCount() {
  const cart = JSON.parse(localStorage.getItem("cart")) || [];
  const total = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);
  const el = document.getElementById("cart-count");
  if (el) el.textContent = total;
}
