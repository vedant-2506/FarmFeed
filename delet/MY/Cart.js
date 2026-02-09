document.addEventListener("DOMContentLoaded", () => {
  const cartContainer = document.getElementById("cart-items");
  const clearCartBtn = document.getElementById("clear-cart");

  let cart = JSON.parse(localStorage.getItem("cart")) || [];

  displayCart();

  // Display cart items
  function displayCart() {
    cartContainer.innerHTML = "";

    if (cart.length === 0) {
      cartContainer.innerHTML =
        "<h5 class='text-center text-muted'>Your cart is empty!</h5>";
      return;
    }

    cart.forEach((item, index) => {
      const col = document.createElement("div");
      col.classList.add("col-md-4");

      col.innerHTML = `
        <div class="card shadow-sm h-100">
          <img src="${item.img}" class="card-img-top" alt="${item.name}">
          <div class="card-body text-center">
            <h5 class="card-title">${item.name}</h5>
            <p class="card-text mb-2"><strong>${item.price}</strong></p>
            <button class="btn btn-danger btn-sm remove-btn" data-index="${index}">
              Remove
            </button>
          </div>
        </div>
      `;
      cartContainer.appendChild(col);
    });

    // Remove product button
    document.querySelectorAll(".remove-btn").forEach((btn) => {
      btn.addEventListener("click", (e) => {
        const index = e.target.dataset.index;
        cart.splice(index, 1);
        localStorage.setItem("cart", JSON.stringify(cart));
        displayCart();
      });
    });
  }

  // Clear all items
  clearCartBtn.addEventListener("click", () => {
    if (confirm("Are you sure you want to clear the cart?")) {
      localStorage.removeItem("cart");
      cart = [];
      displayCart();
    }
  });
});
