const BASE_URL = window.API_BASE_URL || window.location.origin;

document.addEventListener("DOMContentLoaded", () => {
  const shopId = localStorage.getItem("shop_id");
  const shopName = localStorage.getItem("shop_name");
  
  if (!shopId || !shopName) {
    alert("login as vendor first");
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

  loadVendorInfo();
  loadInventory();
  loadStats();
});

async function loadInventory() {
  const shopId = localStorage.getItem("shop_id");
  const tableBody = document.getElementById("inventoryTableBody");
  if (!tableBody) return;

  try {
    const response = await fetch(`${BASE_URL}/api/products/vendor/${shopId}`);
    const products = await response.json();

    if (Array.isArray(products) && products.length > 0) {
      tableBody.innerHTML = "";
      let availableCount = 0;
      let outOfStockCount = 0;

      products.forEach(product => {
        const stock = product.quantity_in_stock ?? product.stock ?? product.stock_quantity ?? 0;
        const isOutOfStock = stock <= 0;
        if (isOutOfStock) outOfStockCount++; else availableCount++;

        const row = document.createElement("tr");
        const statusBadge = isOutOfStock 
          ? '<span class="badge bg-danger">Out of Stock</span>' 
          : '<span class="badge bg-success">Available</span>';
        
        let imageHtml = "";
        if (product.image_url || product.image) {
          imageHtml = `<img src="${product.image_url || product.image}" class="rounded" style="width: 50px; height: 50px; object-fit: cover;">`;
        } else {
          imageHtml = `<div class="rounded bg-light d-flex align-items-center justify-content-center" style="width: 50px; height: 50px;"><i class="bi bi-image text-muted"></i></div>`;
        }

        row.innerHTML = `
          <td>${imageHtml}</td>
          <td><strong>${product.name}</strong></td>
          <td><span class="badge bg-light text-dark border">${product.category}</span></td>
          <td>₹${product.price.toLocaleString("en-IN")}</td>
          <td>${stock} units</td>
          <td>${statusBadge}</td>
          <td class="text-center">
            <div class="btn-group">
              <button class="btn btn-sm btn-outline-primary" onclick="editProduct(${product.inventory_id || product.fertilizer_id || product.id})"><i class="bi bi-pencil"></i></button>
              <button class="btn btn-sm btn-outline-danger" onclick="deleteProduct(${product.inventory_id || product.fertilizer_id || product.id})"><i class="bi bi-trash"></i></button>
            </div>
          </td>
        `;
        tableBody.appendChild(row);
      });

      document.getElementById("totalProducts").textContent = products.length;
      document.getElementById("availableProducts").textContent = availableCount;
      document.getElementById("outOfStockProducts").textContent = outOfStockCount;
    } else {
      tableBody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-muted">No products found in your inventory.</td></tr>';
    }
  } catch (error) {
    console.error("Error loading inventory:", error);
    tableBody.innerHTML = '<tr><td colspan="7" class="text-center py-4 text-danger">Error loading inventory. Please try again.</td></tr>';
  }
}

async function loadStats() {
  const shopId = localStorage.getItem("shop_id");
  try {
    const response = await fetch(`${BASE_URL}/api/orders/vendor/${shopId}/count`);
    const data = await response.json();
    if (data.success) {
      document.getElementById("totalOrders").textContent = data.orderCount || 0;
    }
  } catch (error) {
    console.error("Error loading stats:", error);
  }
}

function loadVendorInfo() {
  const vendorName = localStorage.getItem("user_name") || "Vendor";
  const bannerName = document.getElementById("bannerVendorName");
  if (bannerName) bannerName.textContent = vendorName;
}

function editProduct(id) {
  alert("Edit functionality coming soon for ID: " + id);
}

async function deleteProduct(id) {
  if (confirm("Are you sure you want to delete this product?")) {
    try {
      const response = await fetch(`${BASE_URL}/api/products/${id}`, { method: "DELETE" });
      if (response.ok) {
        alert("Product deleted successfully");
        loadInventory();
      }
    } catch (error) {
      alert("Error deleting product");
    }
  }
}

function logout() {
  localStorage.removeItem("vendorName");
  localStorage.removeItem("vendorEmail");
  localStorage.removeItem("vendorToken");
  window.location.href = "Login.html";
}