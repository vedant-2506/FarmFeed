import requests
from bs4 import BeautifulSoup

url = "https://www.bighaat.com/hi/collections/insecticides"
response = requests.get(url)
soup = BeautifulSoup(response.text, "html.parser")

# Product container selector (correct)
products = soup.select("div.relative.group.cursor-pointer")

print(f"Products found: {len(products)}")

for product in products:
    # NAME
    name_tag = product.select_one("p.font-semibold")
    name = name_tag.text.strip() if name_tag else "No Name"

    # PRICE
    price_tag = product.select_one("span.text-sm.font-medium")
    price = price_tag.text.strip() if price_tag else "No Price"

    # IMAGE
    img_tag = product.select_one("img")
    image = img_tag["src"] if img_tag else "No Image"

    print("Name:", name)
    print("Price:", price)
    print("Image:", image)
    print("--------------------------------")
