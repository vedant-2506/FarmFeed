const API_BASE_URL = window.API_BASE_URL || window.location.origin;
const FERTILIZER_API = `${API_BASE_URL}/api/fertilizers`;
const FALLBACK_IMAGE = "https://images.unsplash.com/photo-1592982537447-6f2a6a0b2d8f?w=800&q=80";
const CACHE_KEY = "farmfeed_products_cache_v2";
const CACHE_TTL_MS = 5 * 60 * 1000;

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
  if (!Array.isArray(items)) return [];

  return items
    .filter((item) => item && (item.name || item.product_name))
    .map((item, index) => {
      const name = item.name || item.product_name || "Unnamed";
      const description = item.description || item.description_clean || item.detailed_description_10_sentences || "";
      const primaryCat = mapPrimaryCategory(item.primary_category);
      const subcat = detectSubcategory(name, description, item.subcategory);

      return {
        id: item.fertilizer_id || item.id || index + 1,
        name: name,
        description: description,
        price: Number(item.price || item.price_inr || 0),
        stock: Number(item.stock || 0),
        image: item.image_url || item.image_link || FALLBACK_IMAGE,
        primaryCategory: primaryCat,
        subcategory: subcat,
        rating: item.rating || "4.5"
      };
    });
}

function updateCartCount() {
  const cart = JSON.parse(localStorage.getItem("cart") || "[]");
  const total = cart.reduce((sum, item) => sum + (item.qty || item.quantity || 1), 0);
  const badge = document.getElementById("cart-count");
  if (badge) badge.textContent = String(total);
}

function addToCart(product) {
  const cart = JSON.parse(localStorage.getItem("cart") || "[]");
  const existing = cart.find((item) => item.id === product.id);

  if (existing) {
    existing.qty = (existing.qty || 1) + 1;
  } else {
    cart.push({
      id: product.id,
      name: product.name,
      price: product.price,
      img: product.image,
      qty: 1
    });
  }

  localStorage.setItem("cart", JSON.stringify(cart));
  updateCartCount();
  alert(`${product.name} added to cart!`);
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
    const onSearchInput = () => {
      setSearchQuery((searchBox ? searchBox.value : "").trim().toLowerCase());
    };

    if (searchBox) searchBox.addEventListener("input", onSearchInput);

    const controller = new AbortController();

    fetch(FERTILIZER_API, { signal: controller.signal })
      .then((response) => {
        if (!response.ok) throw new Error(`Failed with status ${response.status}`);
        return response.json();
      })
      .then((data) => {
        const normalized = normalizeProducts(data);
        setProducts(normalized);
        setError("");
        saveCache(data);
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
    return products.filter((product) => {
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

      if (!searchQuery) return true;

      const name = (product.name || "").toLowerCase();
      const description = (product.description || "").toLowerCase();
      return name.includes(searchQuery) || description.includes(searchQuery);
    });
  }, [products, activePrimaryCategory, searchQuery]);

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
                    addToCart(product);
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
                addToCart(selectedProduct);
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
    DetailModal,

    // Primary category filter bar (below navbar)
    e(
      "div",
      { 
        className: "mb-4",
        style: {
          display: "flex",
          gap: "10px",
          flexWrap: "wrap",
          paddingBottom: "12px",
          borderBottom: "1px solid #e0e0e0"
        }
      },
      primaryCategories.map((cat) =>
        e(
          "button",
          {
            key: `cat-${cat}`,
            className: `btn btn-sm ${activePrimaryCategory === cat.toLowerCase() ? "btn-success" : "btn-outline-success"}`,
            style: {
              padding: "8px 16px",
              fontSize: "13px",
              fontWeight: "600",
              borderRadius: "20px"
            },
            onClick: () => {
              setActivePrimaryCategory(cat === "All" ? "all" : cat);
            }
          },
          cat
        )
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
