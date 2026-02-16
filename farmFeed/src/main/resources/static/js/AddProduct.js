document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("vendorForm");

  form.addEventListener("submit", function(e){
    e.preventDefault();
    alert("Fertilizer added successfully!\nWaiting for admin approval.");
    form.reset();
  });
});
