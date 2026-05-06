let vendorId = localStorage.getItem('shop_id');
let currentFertilizer = null;

document.addEventListener("DOMContentLoaded", () => {
  console.log("AddProduct.js loading. vendorId:", vendorId);
  
  if (!vendorId) {
    alert("login as vendor");
    window.location.href = "Login.html";
    return;
  }

  setupEventListeners();
  loadInventory();
  loadAllFertilizers();
});

function setupEventListeners() {
  const searchInput = document.getElementById("searchFertilizer");
  const suggestionsDiv = document.getElementById("autocompleteSuggestions");
  const btnAddToInventory = document.getElementById("btnAddToInventory");
  const tabInventory = document.getElementById("tabInventory");
  const tabBrowse = document.getElementById("tabBrowse");

  // Search fertilizer with autocomplete
  searchInput.addEventListener("input", async (e) => {
    const query = e.target.value.trim();
    if (query.length < 2) {
      suggestionsDiv.innerHTML = "";
      suggestionsDiv.classList.remove("show");
      return;
    }

    try {
      const response = await fetch(`${window.API_BASE_URL}/api/fertilizers/search?name=${encodeURIComponent(query)}`);
      const data = await response.json();
      
      suggestionsDiv.innerHTML = "";
      data.forEach(fert => {
        const item = document.createElement("div");
        item.className = "autocomplete-item";
        item.innerHTML = `
          <div><strong>${fert.name}</strong> - Rs${fert.price}</div>
          <small style="color: #999;">${fert.description || ''}</small>
        `;
        item.addEventListener("click", () => selectFertilizer(fert));
        suggestionsDiv.appendChild(item);
      });
      
      if (data.length > 0) {
        suggestionsDiv.classList.add("show");
      }
    } catch (error) {
      console.error("Error searching fertilizers:", error);
      alert("can't load suggestions");
    }
  });

  // Add to inventory button
  btnAddToInventory.addEventListener("click", async () => {
    if (!currentFertilizer) {
      alert("pick a fertilizer first");
      return;
    }

    const vendorPrice = parseFloat(document.getElementById("vendorPrice").value);
    const quantity = parseInt(document.getElementById("addQuantity").value);

    if (!vendorPrice || vendorPrice <= 0) {
      alert("enter valid price");
      return;
    }

    if (!quantity || quantity <= 0) {
      alert("enter valid quantity");
      return;
    }

    await addToInventory(currentFertilizer.id, vendorPrice, quantity);
  });

  // Tab switching
  tabInventory.addEventListener("click", () => {
    document.getElementById("inventorySection").style.display = "block";
    document.getElementById("browseSection").style.display = "none";
    tabInventory.classList.add("active");
    tabBrowse.classList.remove("active");
  });

  tabBrowse.addEventListener("click", () => {
    document.getElementById("inventorySection").style.display = "none";
    document.getElementById("browseSection").style.display = "block";
    tabBrowse.classList.add("active");
    tabInventory.classList.remove("active");
  });
}

function selectFertilizer(fertilizer) {
  currentFertilizer = fertilizer;
  document.getElementById("selectedCard").style.display = "block";
  document.getElementById("fertName").textContent = fertilizer.name;
  document.getElementById("fertBasePrice").textContent = fertilizer.price;
  document.getElementById("fertDesc").textContent = fertilizer.description || "";
  
  if (fertilizer.image_url) {
    document.getElementById("fertImage").src = fertilizer.image_url;
  }

  document.getElementById("vendorPrice").value = fertilizer.price;
  document.getElementById("searchFertilizer").value = fertilizer.name;
  document.getElementById("autocompleteSuggestions").classList.remove("show");
}

async function addToInventory(fertilizerId, vendorPrice, quantity) {
  try {
    console.log("Adding to inventory:", { vendorId, fertilizerId, vendorPrice, quantity });
    
    if (!vendorId) {
      alert("vendor not found. login again");
      window.location.href = "Login.html";
      return;
    }

    const payload = {
      vendorId: parseInt(vendorId),
      fertilizerId: parseInt(fertilizerId),
      vendorPrice: parseFloat(vendorPrice),
      quantity: parseInt(quantity)
    };
    
    console.log("Request payload:", payload);

    const response = await fetch(`${window.API_BASE_URL}/api/vendor-inventory/add`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(payload)
    });

    const result = await response.json();
    console.log("Response:", result, "Status:", response.status);

    if (response.ok && result.success) {
      alert("Fertilizer added to your inventory successfully!");
      document.getElementById("selectedCard").style.display = "none";
      document.getElementById("searchFertilizer").value = "";
      document.getElementById("vendorPrice").value = "";
      document.getElementById("addQuantity").value = "10";
      currentFertilizer = null;
      loadInventory();
    } else {
      alert("Error: " + (result.message || result.error || "Failed to add to inventory"));
    }
  } catch (error) {
    console.error("Error adding to inventory:", error);
    alert("Error adding to inventory: " + error.message);
  }
}

async function loadInventory() {
  try {
    const response = await fetch(`${window.API_BASE_URL}/api/vendor-inventory/${vendorId}`);
    const result = await response.json();

    const inventoryList = document.getElementById("inventoryList");
    
    if (result.success && result.data && result.data.length > 0) {
      inventoryList.innerHTML = result.data.map(item => `
        <div class="inventory-item">
          <div class="inventory-header">
            <div>
              <div class="inventory-name">${item.name}</div>
              <small style="color: #666;">Base Price: Rs${item.base_price} | Your Price: Rs${item.vendor_price}</small>
            </div>
            <div>
              <span class="badge bg-info">Qty: ${item.quantity}</span>
            </div>
          </div>
          <div class="inventory-actions">
            <button class="btn btn-sm btn-warning" onclick="editInventoryItem(${item.id})">
              <i class="bi bi-pencil"></i> Edit
            </button>
            <button class="btn btn-sm btn-danger" onclick="deleteInventoryItem(${item.id})">
              <i class="bi bi-trash"></i> Delete
            </button>
          </div>
        </div>
      `).join("");
    } else {
      inventoryList.innerHTML = `
        <div class="empty-state">
          <i class="bi bi-inbox"></i>
          <p>Your inventory is empty. Start by adding fertilizers!</p>
        </div>
      `;
    }
  } catch (error) {
    console.error("Error loading inventory:", error);
    document.getElementById("inventoryList").innerHTML = '<p class="text-danger">Error loading inventory</p>';
  }
}

async function loadAllFertilizers() {
  try {
    const response = await fetch(`${window.API_BASE_URL}/api/fertilizers`);
    const data = await response.json();

    const browseList = document.getElementById("browseList");
    if (Array.isArray(data) && data.length > 0) {
      browseList.innerHTML = data.map(fert => `
        <div class="fertilizer-card">
          <div class="row align-items-center">
            <div class="col-md-6">
              <div class="fert-name">${fert.name}</div>
              <div class="fert-price">Rs${fert.price}</div>
              <small style="color: #999;">${fert.description || ''}</small>
            </div>
            <div class="col-md-6">
              <button class="btn btn-success" onclick="selectAndAdd({id: ${fert.id}, name: '${fert.name}', price: ${fert.price}, description: '${fert.description || ''}', image_url: '${fert.image_url || ''}'})">
                <i class="bi bi-plus-circle"></i> Add to My Inventory
              </button>
            </div>
          </div>
        </div>
      `).join("");
    } else {
      browseList.innerHTML = '<p>No fertilizers available</p>';
    }
  } catch (error) {
    console.error("Error loading fertilizers:", error);
    document.getElementById("browseList").innerHTML = '<p class="text-danger">Error loading fertilizers</p>';
  }
}

function selectAndAdd(fertilizer) {
  selectFertilizer(fertilizer);
  document.getElementById("tabInventory").click();
}

async function editInventoryItem(itemId) {
  const newPrice = prompt("Enter new selling price:");
  if (newPrice && !isNaN(newPrice)) {
    // TODO: Implement update in backend
    alert("Update functionality will be added soon");
  }
}

async function deleteInventoryItem(itemId) {
  if (confirm("Are you sure you want to remove this item from your inventory?")) {
    // TODO: Implement delete in backend
    alert("Delete functionality will be added soon");
  }
}
