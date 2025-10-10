

document.addEventListener("DOMContentLoaded", () => {

 
  // Add to Cart functionality
  const cartButtons = document.querySelectorAll('.btn-success');
  cartButtons.forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      const card = btn.closest('.card');
      const productName = card.querySelector('.card-title').textContent;
      const productPrice = card.querySelector('p strong').textContent;


      alert(` ${productName} added to cart!`);
    });
  });

  // Optional: Handle search
  const searchForm = document.querySelector('form[role="search"]');
  searchForm.addEventListener('submit', (e) => {
    e.preventDefault();
    const query = searchForm.querySelector('input[type="search"]').value.trim();
    if (!query) {
      alert(" Please enter a search term.");
    } else {
      alert(` Searching for "${query}"...`);
      
    }
  });
});
