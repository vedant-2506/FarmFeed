const API_BASE_URL = window.API_BASE_URL || window.location.origin;
const PRODUCTS_API = `${API_BASE_URL}/api/products`;
const FALLBACK_IMAGE = "https://images.unsplash.com/photo-1592982537447-6f2a6a0b2d8f?w=800&q=80";
const CACHE_KEY = "farmfeed_products_cache_v3"; // Bumped version to override old cache
const CACHE_TTL_MS = 2 * 60 * 1000; // 2 minutes for quicker testing

const e = React.createElement;

// Map raw primary_category to display name
function mapPrimaryCategory(raw) {
  if (!raw) return "Other";
  const normalized = raw.toLowerCase().replace(/_/g, " ");
  return normalized.split(" ").map(w => w.charAt(0).toUpperCase() + w.slice(1)).join(" ");
}

// Detect Organic vs Chemical based on product info
function detectSubcategory(name, description, subcategory) {
  const source = `${name || ""} ${description || ""} ${subcategory || ""}`.toLowerCase();
  if (source.includes("organic") || source.includes("natural") || source.includes("bio")) {
    return "Organic";
  }
  if (
    source.includes("chemical") ||
    source.includes("urea") ||
    source.includes("dap") ||
    source.includes("potash") ||
    source.includes("npk") ||
    source.includes("granule")
  ) {
    return "Chemical";
  }
  return "Other";
}

function normalizeProducts(items) {
  if (!Array.isArray(items)) {
    console.error("items is not an array:", items);
    return [];
  }

  console.log("Raw API response - Total items:", items.length);
  if (items.length > 0) {
    console.log("First item raw:", JSON.stringify(items[0]));
    console.log("First item keys:", Object.keys(items[0]));
  }

  return items
    .filter((item) => item && (item.name || item.product_name))
    .map((item, index) => {
      const name = item.name || item.product_name || "Unnamed";
      const description = item.description || item.description_clean || item.detailed_description_10_sentences || item.detailedDescription || "";
      const primaryCat = mapPrimaryCategory(item.primary_category || item.category);
      const subcat = detectSubcategory(name, description, item.subcategory);

      const productId = item.fertilizer_id || item.id || index + 1;
      
      if (index < 3) {
        console.log(`Product ${index}: fertilizer_id=${item.fertilizer_id}, id=${item.id}, finalId=${productId}`);
      }

      return {
        id: productId,
        name: name,
        description: description,
        price: Number(item.price || item.price_inr || 0),
        stock: Number(item.stock || 0),
        image: item.imageLink || item.image_url || FALLBACK_IMAGE,
        primaryCategory: primaryCat,
        subcategory: subcat,
        rating: item.rating || "4.5"
      };
    });
}

function updateCartCount() {
  const farmerId = localStorage.getItem("farmer_id");
  if (!farmerId) {
    const badge = document.getElementById("cart-count");
    if (badge) badge.textContent = "0";
    return;
  }

  fetch(`${API_BASE_URL}/api/cart/farmer/${farmerId}/count`)
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        const badge = document.getElementById("cart-count");
        if (badge) badge.textContent = String(data.itemCount !== undefined ? data.itemCount : (data.count || 0));
      }
    })
    .catch(e => console.error("error fetching cart count:", e));
}

function addToCart(product, showLoginModal) {
  const farmerId = localStorage.getItem("farmer_id");
  const shopId = localStorage.getItem("shop_id");
  
  if (!farmerId) {
    showLoginModal();
    return;
  }

  // Validate product has an ID
  if (!product || !product.id) {
    console.error("Product object with missing id:", product);
    alert("Error: Product ID is missing");
    return;
  }

  const vendorId = shopId || 1;
  
  const parsedFarmerId = parseInt(farmerId);
  const parsedVendorId = parseInt(vendorId);

  // Validate all values are valid numbers
  if (isNaN(parsedFarmerId) || isNaN(parsedVendorId)) {
    console.error("Invalid IDs - product.id:", product.id, "farmerId:", parsedFarmerId, "vendorId:", parsedVendorId);
    alert("Error: Invalid ID values");
    return;
  }

  const payload = {
    farmerId: parsedFarmerId,
    productId: product.id,
    vendorId: parsedVendorId,
    quantity: 1
  };

  console.log("Product full object:", product);
  console.log("Payload being sent:", JSON.stringify(payload));
  
  fetch(`${API_BASE_URL}/api/cart/add`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload)
  })
  .then(res => {
    console.log("Response status:", res.status);
    return res.json().then(data => ({ status: res.status, data }));
  })
  .then(({ status, data }) => {
    console.log("Response data:", data);
    if (data.success) {
      updateCartCount();
      alert(`${product.name} added`);
    } else {
      const errorMsg = data.error || "error adding item";
      console.error("Backend error:", errorMsg);
      alert(errorMsg);
    }
  })
  .catch(e => {
    alert("error. try again");
    console.error(e);
  });
}

function saveCache(products) {
  try {
    const payload = {
      timestamp: Date.now(),
      products
    };
    sessionStorage.setItem(CACHE_KEY, JSON.stringify(payload));
  } catch (error) {
    console.warn("Unable to save product cache:", error);
  }
}

function readCache() {
  try {
    const raw = sessionStorage.getItem(CACHE_KEY);
    if (!raw) return null;

    const parsed = JSON.parse(raw);
    if (!parsed || !Array.isArray(parsed.products) || !parsed.timestamp) return null;

    if (Date.now() - parsed.timestamp > CACHE_TTL_MS) return null;

    return parsed.products;
  } catch (error) {
    console.warn("Unable to read product cache:", error);
    return null;
  }
}

function HomeCatalogApp() {
  const [products, setProducts] = React.useState([]);
  const [activePrimaryCategory, setActivePrimaryCategory] = React.useState("all");
  const [searchQuery, setSearchQuery] = React.useState("");
  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState("");
  const [selectedProduct, setSelectedProduct] = React.useState(null);
  const [isDetailOpen, setIsDetailOpen] = React.useState(false);
  const [isLoginModalOpen, setIsLoginModalOpen] = React.useState(false);

  // New Filters
  const [activeSubcategory, setActiveSubcategory] = React.useState("all"); // all, Organic, Chemical
  const [showOnlyInStock, setShowOnlyInStock] = React.useState(false);
  const [priceSort, setPriceSort] = React.useState("none"); // none, low-to-high, high-to-low
  const [minRating, setMinRating] = React.useState(0);
  // priceRange options: "all", "under500", "500-1000", "over1000"
  const [priceRange, setPriceRange] = React.useState("all");

  // Helper to reliably close the Bootstrap dropdown upon selection
  const handlePriceSelect = (sort, range) => {
    setPriceSort(sort);
    setPriceRange(range);
    
    // Use Bootstrap's API to close the dropdown cleanly without scrolling
    const dropdownToggleEl = document.getElementById('priceDropdownBtn');
    if (dropdownToggleEl && window.bootstrap) {
      const bsDropdown = window.bootstrap.Dropdown.getInstance(dropdownToggleEl);
      if (bsDropdown) bsDropdown.hide();
    } else {
      // Fallback
      document.body.click();
    }
  };

  // Get unique primary categories (All, Seed, Crop Production, Crop Nutrition, Other)
  const primaryCategories = React.useMemo(() => {
    const cats = new Set(products.map(p => p.primaryCategory).filter(p => p !== "Other"));
    const baseCategories = ["All", "Seed", "Crop Production", "Crop Nutrition"];
    const existingByName = baseCategories.filter(cat => {
      if (cat === "All") return true;
      return Array.from(cats).includes(cat);
    });
    // Add "Other" if there are products with unknown/other categories
    const hasOther = products.some(p => !baseCategories.slice(1).includes(p.primaryCategory));
    return hasOther ? [...existingByName, "Other"] : existingByName;
  }, [products]);

  React.useEffect(() => {
    updateCartCount();

    const cached = readCache();
    if (cached) {
      setProducts(normalizeProducts(cached));
      setLoading(false);
    }

    const searchBox = document.getElementById("searchBox");
    const searchForm = document.getElementById("searchForm");
    
    const onSearchInput = () => {
      setSearchQuery((searchBox ? searchBox.value : "").trim().toLowerCase());
    };

    const onSearchSubmit = (e) => {
      e.preventDefault();
      onSearchInput();
    };

    if (searchBox) searchBox.addEventListener("input", onSearchInput);
    if (searchForm) searchForm.addEventListener("submit", onSearchSubmit);

    const controller = new AbortController();

    fetch(PRODUCTS_API, { signal: controller.signal })
      .then((response) => {
        if (!response.ok) throw new Error(`Failed with status ${response.status}`);
        return response.json();
      })
      .then((data) => {
        const items = Array.isArray(data) ? data : (data.data || []);
        const normalized = normalizeProducts(items);
        setProducts(normalized);
        setError("");
        saveCache(items);
      })
      .catch((fetchError) => {
        if (fetchError.name !== "AbortError") {
          console.error("Unable to load products:", fetchError);
          if (!cached) {
            setError("Could not load products from database. Please try again.");
          }
        }
      })
      .finally(() => setLoading(false));

    return () => {
      controller.abort();
      if (searchBox) searchBox.removeEventListener("input", onSearchInput);
    };
  }, []);

  const filteredProducts = React.useMemo(() => {
    let result = products.filter((product) => {
      // Handle "Other" category - products not in the main categories
      let primaryOk = false;
      if (activePrimaryCategory === "all") {
        primaryOk = true;
      } else if (activePrimaryCategory === "Other") {
        const knownCategories = ["Seed", "Crop Production", "Crop Nutrition"];
        primaryOk = !knownCategories.includes(product.primaryCategory);
      } else {
        primaryOk = product.primaryCategory === activePrimaryCategory;
      }

      if (!primaryOk) return false;

      // Subcategory filter (Organic/Chemical)
      if (activeSubcategory !== "all" && product.subcategory !== activeSubcategory) {
        return false;
      }

      // Stock filter
      if (showOnlyInStock && product.stock <= 0) {
        return false;
      }

      // Rating filter
      if (minRating > 0 && parseFloat(product.rating) < minRating) {
        return false;
      }

      // Price Range filter
      if (priceRange !== "all") {
        const price = product.price;
        if (priceRange === "under500" && price >= 500) return false;
        if (priceRange === "500-1000" && (price < 500 || price > 1000)) return false;
        if (priceRange === "over1000" && price <= 1000) return false;
      }

      if (!searchQuery) return true;

      const name = (product.name || "").toLowerCase();
      const description = (product.description || "").toLowerCase();
      return name.includes(searchQuery) || description.includes(searchQuery);
    });

    // Sorting
    if (priceSort === "low-to-high") {
      result.sort((a, b) => a.price - b.price);
    } else if (priceSort === "high-to-low") {
      result.sort((a, b) => b.price - a.price);
    } else if (priceSort === "top-rated") {
      result.sort((a, b) => parseFloat(b.rating) - parseFloat(a.rating));
    }

    return result;
  }, [products, activePrimaryCategory, activeSubcategory, showOnlyInStock, priceSort, minRating, priceRange, searchQuery]);

  const content = [];

  if (loading) {
    content.push(
      e(
        "div",
        { className: "col-12 text-center py-5 text-muted", key: "loading" },
        "⏳ Loading products..."
      )
    );
  }

  if (!loading && error) {
    content.push(
      e(
        "div",
        { className: "col-12 text-center py-5 text-danger", key: "error" },
        error
      )
    );
  }

  if (!loading && !error && filteredProducts.length === 0) {
    content.push(
      e(
        "div",
        { className: "col-12 text-center py-5 text-muted", key: "empty" },
        "No products found. Try another filter or search."
      )
    );
  }

  if (!loading && !error && filteredProducts.length > 0) {
    filteredProducts.forEach((product) => {
      content.push(
        e(
          "div",
          { 
            className: "col-12 col-sm-6 col-md-4 col-lg-3",
            key: `p-${product.id}`,
            style: { marginBottom: "24px" }
          },
          e(
            "div",
            {
              className: "product-card",
              onClick: () => {
                setSelectedProduct(product);
                setIsDetailOpen(true);
              },
              style: { cursor: "pointer" }
            },
            // Image
            e(
              "div",
              { className: "product-card-image" },
              e("img", {
                src: product.image,
                alt: product.name,
                loading: "lazy"
              })
            ),
            // Body
            e(
              "div",
              { className: "product-card-body" },
              // Category badges
              e(
                "div",
                { className: "product-card-badges" },
                e("span", {
                  className: "badge bg-primary",
                }, product.primaryCategory),
                e("span", {
                  className: "badge bg-info",
                }, product.subcategory)
              ),
              // Title
              e("h6", {
                className: "product-card-title",
              }, product.name),
              // Rating
              e("div", {
                className: "product-card-rating",
              }, `⭐ ${product.rating}`),
              // Description
              e("p", {
                className: "product-card-description",
              }, product.description),
              // Price
              e("div", {
                className: "product-card-price",
              }, `₹${Number(product.price).toLocaleString("en-IN")}`),
              // Stock
              e("div", {
                className: "product-card-stock",
              }, `Stock: ${product.stock} units`),
              // Button
              e(
                "button",
                {
                  className: "product-card-button",
                  onClick: (event) => {
                    event.stopPropagation();
                    addToCart(product, () => setIsLoginModalOpen(true));
                  }
                },
                "Add to Cart"
              )
            )
          )
        )
      );
    });
  }

  // Login prompt modal
  const LoginPromptModal = isLoginModalOpen && e(
    "div",
    {
      className: "modal d-block",
      style: { 
        backgroundColor: "rgba(0,0,0,0.5)", 
        display: "flex", 
        justifyContent: "center", 
        alignItems: "center",
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        zIndex: 9999
      },
      onClick: () => setIsLoginModalOpen(false)
    },
    e(
      "div",
      {
        className: "modal-dialog",
        style: { maxWidth: "400px" },
        onClick: (event) => event.stopPropagation()
      },
      e(
        "div",
        { className: "modal-content" },
        e(
          "div",
          { className: "modal-header bg-warning text-dark" },
          e("h5", { className: "modal-title" }, "Login Required"),
          e(
            "button",
            { 
              type: "button", 
              className: "btn-close", 
              onClick: () => setIsLoginModalOpen(false)
            }
          )
        ),
        e(
          "div",
          { className: "modal-body" },
          e(
            "p",
            { className: "text-center text-dark fw-bold mb-3", style: { fontSize: "16px" } },
            "login first to add items"
          ),
          e(
            "p",
            { className: "text-center text-muted small" },
            "click login button in top right corner"
          )
        ),
        e(
          "div",
          { className: "modal-footer" },
          e(
            "button",
            { type: "button", className: "btn btn-success w-100", onClick: () => setIsLoginModalOpen(false) },
            "OK, Got it!"
          )
        )
      )
    )
  );

  // Product detail modal
  const DetailModal = isDetailOpen && selectedProduct && e(
    "div",
    {
      className: "modal d-block",
      style: { 
        backgroundColor: "rgba(0,0,0,0.5)", 
        display: "flex", 
        justifyContent: "center", 
        alignItems: "center",
        position: "fixed",
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        zIndex: 9999
      },
      onClick: () => setIsDetailOpen(false)
    },
    e(
      "div",
      {
        className: "modal-dialog modal-lg",
        style: { maxWidth: "600px", marginTop: "50px" },
        onClick: (event) => event.stopPropagation()
      },
      e(
        "div",
        { className: "modal-content" },
        e(
          "div",
          { className: "modal-header bg-success text-white" },
          e("h5", { className: "modal-title" }, selectedProduct.name),
          e(
            "button",
            { 
              type: "button", 
              className: "btn-close btn-close-white", 
              onClick: () => setIsDetailOpen(false)
            }
          )
        ),
        e(
          "div",
          { className: "modal-body" },
          e(
            "div",
            { className: "row" },
            e(
              "div",
              { className: "col-md-5 mb-3" },
              e("img", {
                src: selectedProduct.image,
                alt: selectedProduct.name,
                className: "img-fluid rounded",
                style: { width: "100%", height: "300px", objectFit: "cover" }
              })
            ),
            e(
              "div",
              { className: "col-md-7" },
              e(
                "div",
                { className: "mb-3" },
                e("span", { className: "badge bg-primary me-2" }, selectedProduct.primaryCategory),
                e("span", { className: "badge bg-info" }, selectedProduct.subcategory)
              ),
              e(
                "div",
                { className: "mb-3" },
                e("h6", { className: "text-muted" }, `Rating: ${selectedProduct.rating} ⭐`)
              ),
              e(
                "div",
                { className: "mb-3" },
                e("h4", { className: "text-success" }, `₹${Number(selectedProduct.price).toLocaleString("en-IN")}`)
              ),
              e(
                "div",
                { className: "mb-3" },
                e("p", { className: "text-muted small" }, `Stock: ${selectedProduct.stock} units available`)
              ),
              e(
                "div",
                { className: "mb-4" },
                e("h6", { className: "fw-bold mb-2" }, "Description:"),
                e("p", { className: "text-muted small" }, selectedProduct.description)
              )
            )
          )
        ),
        e(
          "div",
          { className: "modal-footer" },
          e(
            "button",
            { type: "button", className: "btn btn-secondary", onClick: () => setIsDetailOpen(false) },
            "Close"
          ),
          e(
            "button",
            {
              type: "button",
              className: "btn btn-success",
              onClick: () => {
                addToCart(selectedProduct, () => setIsLoginModalOpen(true));
                setIsDetailOpen(false);
              }
            },
            "Add to Cart & Order"
          )
        )
      )
    )
  );

  return e(
    "div",
    { 
      className: "container-fluid",
      style: { padding: "20px", backgroundColor: "#ffffff", minHeight: "100vh" }
    },
    LoginPromptModal,
    DetailModal,

    // Unified Filter Bar
    e(
      "div",
      { 
        className: "mb-4 d-flex align-items-center flex-wrap gap-2",
        style: {
          padding: "15px 0",
          borderBottom: "1px solid #f0f0f0"
        }
      },
      // Categories (All, Seed, etc.)
      primaryCategories.map((cat) =>
        e(
          "button",
          {
            key: `cat-${cat}`,
            className: `btn btn-sm ${activePrimaryCategory === (cat === "All" ? "all" : cat) ? "btn-success" : "btn-outline-success"}`,
            style: { padding: "6px 14px", fontSize: "12px", fontWeight: "600", borderRadius: "20px" },
            onClick: () => setActivePrimaryCategory(cat === "All" ? "all" : cat)
          },
          cat
        )
      ),

      // Vertical Divider
      e("div", { style: { width: "1px", height: "24px", backgroundColor: "#ddd", margin: "0 10px" } }),

      // Organic / Chemical Filter
      ["Organic", "Chemical"].map(type => 
        e("button", {
          key: type,
          className: `btn btn-sm ${activeSubcategory === type ? "btn-success" : "btn-outline-success"}`,
          style: { padding: "6px 14px", fontSize: "12px", fontWeight: "600", borderRadius: "20px" },
          onClick: () => setActiveSubcategory(activeSubcategory === type ? "all" : type)
        }, type)
      ),

      // Unified Price & Sort Dropdown
      e(
        "div",
        { className: "dropdown" },
        e(
          "button",
          {
            id: "priceDropdownBtn",
            className: `btn btn-sm ${(priceRange !== "all" || priceSort !== "none") ? "btn-success" : "btn-outline-success"} dropdown-toggle`,
            style: { padding: "6px 14px", fontSize: "12px", fontWeight: "600", borderRadius: "20px" },
            "data-bs-toggle": "dropdown",
            "aria-expanded": "false"
          },
          e("i", { className: "bi bi-currency-rupee me-1" }),
          (priceRange === "all" && priceSort === "none") ? "Price" : 
          (priceSort === "low-to-high" ? "Price: Low to High" : 
           priceSort === "high-to-low" ? "Price: High to Low" : 
           priceSort === "top-rated" ? "Top Rated" :
           priceRange === "under500" ? "Price: Under ₹500" : 
           priceRange === "500-1000" ? "Price: ₹500 - ₹1000" : "Price: Over ₹1000")
        ),
        e(
          "ul",
          { className: "dropdown-menu shadow-sm" },
          // Sorting Options
          e("li", {}, e("h6", { className: "dropdown-header" }, "Sort By")),
          e("li", {}, e("button", { className: "dropdown-item", type: "button", onClick: () => handlePriceSelect("low-to-high", "all") }, "Price: Low to High")),
          e("li", {}, e("button", { className: "dropdown-item", type: "button", onClick: () => handlePriceSelect("high-to-low", "all") }, "Price: High to Low")),
          e("li", {}, e("button", { className: "dropdown-item", type: "button", onClick: () => handlePriceSelect("top-rated", "all") }, "Top Rated First")),
          e("li", {}, e("hr", { className: "dropdown-divider" })),
          // Filter Options
          e("li", {}, e("h6", { className: "dropdown-header" }, "Filter By Range")),
          e("li", {}, e("button", { className: "dropdown-item", type: "button", onClick: () => handlePriceSelect("none", "all") }, "All Prices")),
          e("li", {}, e("button", { className: "dropdown-item", type: "button", onClick: () => handlePriceSelect("none", "under500") }, "Under ₹500")),
          e("li", {}, e("button", { className: "dropdown-item", type: "button", onClick: () => handlePriceSelect("none", "500-1000") }, "₹500 - ₹1000")),
          e("li", {}, e("button", { className: "dropdown-item", type: "button", onClick: () => handlePriceSelect("none", "over1000") }, "Over ₹1000"))
        )
      ),

      // Stock Filter (Active/All)
      e(
        "button",
        {
          className: `btn btn-sm ${showOnlyInStock ? "btn-success" : "btn-outline-success"}`,
          style: { padding: "6px 14px", fontSize: "12px", fontWeight: "600", borderRadius: "20px" },
          onClick: () => setShowOnlyInStock(!showOnlyInStock)
        },
        e("i", { className: `bi ${showOnlyInStock ? "bi-check-circle-fill" : "bi-circle"} me-1` }),
        "In Stock"
      ),

      // Clear button (if any filter is active)
      (activePrimaryCategory !== "all" || activeSubcategory !== "all" || showOnlyInStock || priceSort !== "none" || minRating !== 0 || priceRange !== "all") && e(
        "button",
        {
          className: "btn btn-sm btn-link text-danger text-decoration-none p-0 ms-auto",
          onClick: () => {
            setActivePrimaryCategory("all");
            setActiveSubcategory("all");
            setShowOnlyInStock(false);
            setPriceSort("none");
            setMinRating(0);
            setPriceRange("all");
          }
        },
        "Clear All"
      )
    ),

    // Product count
    e(
      "div",
      { 
        className: "mb-3 text-muted",
        style: { fontSize: "13px" }
      },
      `Showing ${filteredProducts.length} product(s) out of ${products.length}`
    ),

    // Products grid with proper spacing
    e("div", { 
      className: "row",
      style: {
        marginLeft: "-12px",
        marginRight: "-12px"
      }
    }, content)
  );
}

document.addEventListener("DOMContentLoaded", function () {
  const rootElement = document.getElementById("home-react-root");
  if (!rootElement) return;

  const root = ReactDOM.createRoot(rootElement);
  root.render(e(HomeCatalogApp));
});
