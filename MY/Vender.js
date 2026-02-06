const products = [
  {
    name: "Urea Fertilizer",
    price: "₹600 / 50kg",
    image: "https://imgs.search.brave.com/8Ues7vOho-SowZyusrF0nz05eITiqBf8LFszypFswE8/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9zLmFs/aWNkbi5jb20vQHNj/MDQva2YvSFRCMUhz/a2VYekR1SzFSalNz/emRxNnhHTHBYYW8u/anBnXzMwMHgzMDAu/anBn"
  },
  {
    name: "Organic Compost",
    price: "₹400 / 25kg",
    image: "https://imgs.search.brave.com/Yk9Tvvcc0Dc3qKU9m6aeIEBYK6dsu3P5YelTKae9220/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tLm1l/ZGlhLWFtYXpvbi5j/b20vaW1hZ2VzL0kv/NzFuVGQwZmgwTkwu/anBn"
  }
];

const productList = document.getElementById("productList");

products.forEach(p => {
  productList.innerHTML += `
    <div class="col-md-4">
      <div class="card product-card shadow-sm">
        <img src="${p.image}" class="card-img-top">
        <div class="card-body">
          <h5>${p.name}</h5>
          <p><strong>${p.price}</strong></p>
          <div class="d-flex justify-content-between">
            <button class="btn btn-outline-success btn-sm">Edit</button>
            <button class="btn btn-outline-danger btn-sm">Delete</button>
          </div>
        </div>
      </div>
    </div>
  `;
});

document.getElementById("productCount").innerText = products.length;
