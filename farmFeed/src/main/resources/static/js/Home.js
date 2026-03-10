


// ============================================================
// farmFeed - Home.js (COMPLETE WORKING VERSION)
// ============================================================
// This file handles:
//   1. Search functionality (calls backend API)
//   2. Add to cart functionality
//   3. Cart count badge updates
//   4. Dynamic product rendering
// ============================================================

// ===========================================================
// CONFIGURATION
// ===========================================================
const API_BASE_URL = "http://localhost:9090/api/fertilizers";

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

// ===========================================================
// INITIALIZE WHEN PAGE LOADS
// ===========================================================
document.addEventListener("DOMContentLoaded", function() {
  console.log("🌱 FarmFeed Initialized");
  console.log("📡 API URL:", API_BASE_URL);
  
  // Update cart count from localStorage
  updateCartCount();
  
  // Setup search form handler
  setupSearchForm();
  
  // Setup all "Add to Cart" buttons
  setupAddToCartButtons();
  
  // Load all products on page load
  loadAllProducts();
  
  console.log("✅ All event listeners attached");
});

// ===========================================================
// SEARCH FUNCTIONALITY
// ===========================================================

/**
 * Setup the search form to prevent default submit
 * and call our custom search function
 */
function setupSearchForm() {
  const searchForm = document.getElementById("searchForm");
  const searchBox = document.getElementById("searchBox");
  
  if (!searchForm) {
    console.error("❌ Search form not found!");
    return;
  }
  
  // When user submits the search form
  searchForm.addEventListener("submit", function(event) {
    // CRITICAL: Prevent the form from submitting normally
    // This stops the page from refreshing or redirecting
    event.preventDefault();
    
    const query = searchBox.value.trim();
    
    console.log("🔍 Search triggered, query:", query);
    
    if (!query) {
      // If search box is empty, show all products
      loadAllProducts();
      return;
    }
    
    // Call the backend search API
    searchProducts(query);
  });
  
  console.log("✅ Search form listener attached");
}

/**
 * Search products by calling the backend API
 * GET /api/fertilizers/search?name={query}
 */
function searchProducts(query) {
  const searchURL = `${API_BASE_URL}/search?name=${encodeURIComponent(query)}`;
  
  console.log("📡 Calling API:", searchURL);
  
  // Show loading state
  showLoadingState();
  
  // Call the backend
  fetch(searchURL)
    .then(response => {
      console.log("📥 Response status:", response.status);
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }
      
      return response.json();
    })
    .then(products => {
      console.log("✅ Search results:", products);
      console.log(`Found ${products.length} products`);
      
      // Render the filtered products
      renderProducts(products);
    })
    .catch(error => {
      console.error(" Search failed:", error);
      showErrorMessage(`Search failed: ${error.message}. Is the backend running?`);
    });
}

/**
 * Load all products from backend
 * GET /api/fertilizers
 */
function loadAllProducts() {
  console.log(" Loading all products...");
  
  showLoadingState();
  
  fetch(API_BASE_URL)
    .then(response => {
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      return response.json();
    })
    .then(products => {
      console.log("✅ Loaded all products:", products.length);
      renderProducts(products);
      
      // Clear search box
      const searchBox = document.getElementById("searchBox");
      if (searchBox) searchBox.value = "";
    })
    .catch(error => {
      console.error("❌ Load failed:", error);
      showErrorMessage("Failed to load products. Is the backend running?");
    });
}

// ===========================================================
// RENDER PRODUCTS
// ===========================================================

/**
 * Render products to the page
 * This replaces the static HTML cards with dynamic ones from the API
 */
function renderProducts(products) {
  const container = document.getElementById("products-container");
  
  if (!container) {
    console.error("❌ Products container not found!");
    return;
  }
  
  // If no products found
  if (!products || products.length === 0) {
    container.innerHTML = `
      <div class="col-12 text-center my-5">
        <div class="alert alert-info">
          <i class="bi bi-info-circle fs-3"></i>
          <h4 class="mt-3">No products found</h4>
          <p class="text-muted">Try a different search term</p>
          <button class="btn btn-success" onclick="loadAllProducts()">
            Show All Products
          </button>
        </div>
      </div>
    `;
    return;
  }
  
  // Build HTML for all products
  let html = "";
  
  products.forEach(product => {
    const img = PRODUCT_IMAGES[product.name] || PRODUCT_IMAGES["Urea Fertilizer"];
    const name = product.name;
    const price = product.price;
    const description = product.description || "";
    const stock = product.stock || 0;
    
    html += `
      <div class="col">
        <div class="card h-100 shadow-sm">
          <img src="${img}" class="card-img-top" alt="${name}">
          <div class="card-body">
            <h5 class="card-title">${name}</h5>
            <p class="card-text">${description}</p>
            <p><strong>Price: ₹${price}/50kg bag</strong></p>
            <p class="text-muted small">Stock: ${stock} bags</p>
            <button class="btn btn-success add-cart-btn w-100"
                    data-id="${product.fertilizer_id}"
                    data-name="${name}"
                    data-price="${price}"
                    data-img="${img}"
                    ${stock === 0 ? 'disabled' : ''}>
              ${stock === 0 ? 'Out of Stock' : 'Add to Cart'}
            </button>
          </div>
        </div>
      </div>
    `;
  });
  
  // Update the page
  container.innerHTML = html;
  
  // Re-attach event listeners to new buttons
  setupAddToCartButtons();
  
  console.log(`✅ Rendered ${products.length} products`);
}

// ===========================================================
// LOADING & ERROR STATES
// ===========================================================

function showLoadingState() {
  const container = document.getElementById("products-container");
  if (container) {
    container.innerHTML = `
      <div class="col-12 text-center my-5">
        <div class="spinner-border text-success" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-3 text-muted">Loading products...</p>
      </div>
    `;
  }
}

function showErrorMessage(message) {
  const container = document.getElementById("products-container");
  if (container) {
    container.innerHTML = `
      <div class="col-12 text-center my-5">
        <div class="alert alert-danger">
          <i class="bi bi-exclamation-triangle fs-3"></i>
          <h4 class="mt-3">Error</h4>
          <p>${message}</p>
          <button class="btn btn-success" onclick="loadAllProducts()">
            Try Again
          </button>
        </div>
      </div>
    `;
  }
}

// ===========================================================
// ADD TO CART FUNCTIONALITY
// ===========================================================

/**
 * Setup event listeners for all "Add to Cart" buttons
 * Works for both static HTML buttons and dynamically rendered ones
 */
function setupAddToCartButtons() {
  const buttons = document.querySelectorAll(".add-cart-btn");
  
  console.log(`🛒 Found ${buttons.length} cart buttons`);
  
  buttons.forEach(button => {
    // Remove old listener (if any) by cloning
    const newButton = button.cloneNode(true);
    button.parentNode.replaceChild(newButton, button);
    
    // Add fresh listener
    newButton.addEventListener("click", function() {
      handleAddToCart(this);
    });
  });
}

/**
 * Handle add to cart button click
 */
function handleAddToCart(button) {
  // Get product info from button's data attributes or from the card
  const card = button.closest(".card");
  
  let productId, productName, productPrice, productImg;
  
  // Try to get from data attributes first (for dynamic products)
  if (button.dataset.id) {
    productId = button.dataset.id;
    productName = button.dataset.name;
    productPrice = button.dataset.price;
    productImg = button.dataset.img;
  } else {
    // Fallback: get from card HTML (for static products)
    productName = card.querySelector(".card-title").textContent.trim();
    const priceText = card.querySelector("p strong").textContent;
    productPrice = priceText.replace(/[^0-9]/g, ""); // Extract numbers only
    productImg = card.querySelector("img").src;
    productId = Date.now(); // Generate temporary ID
  }
  
  console.log("🛒 Adding to cart:", productName);
  
  // Get current cart from localStorage
  let cart = JSON.parse(localStorage.getItem("cart")) || [];
  
  // Check if product already in cart
  const existingProduct = cart.find(item => item.name === productName);
  
  if (existingProduct) {
    // Increase quantity
    existingProduct.quantity = (existingProduct.quantity || 1) + 1;
    console.log(`📦 Updated quantity: ${productName} x${existingProduct.quantity}`);
  } else {
    // Add new product
    cart.push({
      id: productId,
      name: productName,
      price: productPrice,
      img: productImg,
      quantity: 1
    });
    console.log(`✅ Added new product: ${productName}`);
  }
  
  // Save to localStorage
  localStorage.setItem("cart", JSON.stringify(cart));
  
  // Update cart count badge
  updateCartCount();
  
  // Visual feedback
  showAddToCartFeedback(button, productName);
}

/**
 * Show visual feedback when product is added to cart
 */
function showAddToCartFeedback(button, productName) {
  // Change button text temporarily
  const originalText = button.textContent;
  button.textContent = "Added ✓";
  button.disabled = true;
  button.classList.remove("btn-success");
  button.classList.add("btn-secondary");
  
  // Show alert
  alert(`✅ ${productName} added to cart!`);
  
  // Revert button after 2 seconds
  setTimeout(() => {
    button.textContent = originalText;
    button.disabled = false;
    button.classList.remove("btn-secondary");
    button.classList.add("btn-success");
  }, 2000);
}

/**
 * Update the cart count badge in the navbar
 */
function updateCartCount() {
  const cart = JSON.parse(localStorage.getItem("cart")) || [];
  const totalItems = cart.reduce((sum, item) => sum + (item.quantity || 1), 0);
  
  const cartCountElement = document.getElementById("cart-count");
  if (cartCountElement) {
    cartCountElement.textContent = totalItems;
  }
  
  console.log(`🛒 Cart count updated: ${totalItems}`);
}

// ===========================================================
// UTILITY FUNCTIONS
// ===========================================================

/**
 * Get product image URL by name
 */
function getProductImage(name) {
  return PRODUCT_IMAGES[name] || PRODUCT_IMAGES["Urea Fertilizer"];
}

// ===========================================================
console.log("✅ Home.js loaded successfully")