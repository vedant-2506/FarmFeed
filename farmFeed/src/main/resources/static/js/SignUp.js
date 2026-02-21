const farmerBtn = document.getElementById("farmerBtn");
const vendorBtn = document.getElementById("vendorBtn");

const farmerForm = document.getElementById("farmerForm");
const vendorForm = document.getElementById("vendorForm");

const title = document.getElementById("title");

farmerBtn.addEventListener("click", function(){
    farmerForm.style.display = "block";
    vendorForm.style.display = "none";

    farmerBtn.classList.add("active");
    vendorBtn.classList.remove("active");

    title.innerText = "Sign Up - Farmer";
});

vendorBtn.addEventListener("click", function(){
    farmerForm.style.display = "none";
    vendorForm.style.display = "block";

    vendorBtn.classList.add("active");
    farmerBtn.classList.remove("active");

    title.innerText = "Sign Up - Vendor";
});
