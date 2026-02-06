document.addEventListener("DOMContentLoaded", () => {
  const cartButtons = document.querySelectorAll(".add-cart-btn");
  const cartCount = document.getElementById("cart-count");

  // Initialize cart from localStorage
  let cart = JSON.parse(localStorage.getItem("cart")) || [];
  updateCartCount();

  // Add to cart functionality
  cartButtons.forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();

      const card = btn.closest(".card");
      const name = card.querySelector(".card-title").textContent;
      const price = card.querySelector("p strong").textContent;
      const img = card.querySelector("img").src;

      const product = { name, price, img };

      // Add product to cart array
      cart.push(product);
      localStorage.setItem("cart", JSON.stringify(cart));

      updateCartCount();

      alert(`${name} added to cart!`);
    });
  });

  // Update cart count display
  function updateCartCount() {
    cartCount.textContent = cart.length;
  }

  // Search functionality
  const searchForm = document.querySelector('form[role="search"]');
  searchForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const query = searchForm.querySelector("input[type='search']").value.trim();

    if (!query) {
      alert("Please enter a search term.");
    } else {
      alert(`Searching for "${query}"...`);
    }
  });
});

// Select all Add to Cart buttons
const addToCartButtons = document.querySelectorAll('.add-to-cart');
const cartCountElement = document.getElementById('cart-count');

let cartCount = 0;

// When user clicks "Add to Cart"
addToCartButtons.forEach(button => {
  button.addEventListener('click', () => {
    cartCount++;
    cartCountElement.textContent = cartCount;

    // Optional: show feedback
    button.textContent = "Added ✓";
    button.disabled = true;
    button.classList.remove('btn-success');
    button.classList.add('btn-secondary');

    // Revert after 2 seconds
    setTimeout(() => {
      button.textContent = "Add to Cart";
      button.disabled = false;
      button.classList.remove('btn-secondary');
      button.classList.add('btn-success');
    }, 2000);
  });
});

document.addEventListener("DOMContentLoaded", () => {
  const cartButtons = document.querySelectorAll(".add-cart-btn");
  const cartCount = document.getElementById("cart-count");

  // Load existing cart from localStorage
  let cart = JSON.parse(localStorage.getItem("cart")) || [];
  updateCartCount();

  // Add to cart event
  cartButtons.forEach((btn) => {
    btn.addEventListener("click", (e) => {
      e.preventDefault();

      const card = btn.closest(".card");
      const name = card.querySelector(".card-title").textContent;
      const price = card.querySelector("p strong").textContent;
      const img = card.querySelector("img").src;

      // Create product object
      const product = { name, price, img };

      // Add product to cart array
      cart.push(product);
      localStorage.setItem("cart", JSON.stringify(cart));

      // Update count
      updateCartCount();

      // Feedback
      btn.textContent = "Added ✓";
      btn.disabled = true;
      btn.classList.remove("btn-success");
      btn.classList.add("btn-secondary");

      // Reset button text after 2 seconds
      setTimeout(() => {
        btn.textContent = "Add to Cart";
        btn.disabled = false;
        btn.classList.remove("btn-secondary");
        btn.classList.add("btn-success");
      }, 2000);
    });
  });

  // Function to update cart count
  function updateCartCount() {
    cartCount.textContent = cart.length;
  }

  // Search functionality
  const searchForm = document.querySelector('form[role="search"]');
  if (searchForm) {
    searchForm.addEventListener("submit", (e) => {
      e.preventDefault();

      const query = searchForm.querySelector("input[type='search']").value.trim();

      if (!query) {
        alert("Please enter a search term.");
      } else {
        alert(`Searching for "${query}"...`);
      }
    });
  }
});