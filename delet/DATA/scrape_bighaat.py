import sys
sys.stdout.reconfigure(encoding="utf-8")   # Fix printing issues on Windows

import requests
from bs4 import BeautifulSoup
import pandas as pd
import os

# Target URL (Insecticides page)
BASE_URL = "https://www.bighaat.com/collections/insecticides"

# Fake browser header so site doesn't block you
headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                  "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}

# Fetch the page
response = requests.get(BASE_URL, headers=headers)
print("Status Code:", response.status_code)

# Parse HTML
soup = BeautifulSoup(response.text, "lxml")

# Find all product cards
cards = soup.find_all("div", class_="relative flex flex-col gap-2")  # matches BigHaat structure

data = []

for card in cards:
    # Product Name
    name_tag = card.find("h3")
    name = name_tag.get_text(strip=True) if name_tag else "N/A"

    # Price
    price_tag = card.find("div", class_="text-base font-semibold")
    price = price_tag.get_text(strip=True) if price_tag else "N/A"

    # Image
    img_tag = card.find("img")
    img = img_tag.get("src") if img_tag else "N/A"
    if img and img.startswith("/"):
        img = "https://www.bighaat.com" + img

    data.append({
        "Product Name": name,
        "Price": price,
        "Image URL": img
    })

# Save results
os.makedirs("output", exist_ok=True)
df = pd.DataFrame(data)
df.to_csv("output/bighaat_products.csv", index=False, encoding="utf-8-sig")

print(f"✅ Scraped {len(data)} products and saved to output/bighaat_products.csv")

