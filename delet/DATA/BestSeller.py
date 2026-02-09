# scrape_requests.py
import sys
sys.stdout.reconfigure(encoding="utf-8")   # fix printing unicode on Windows

import requests
from bs4 import BeautifulSoup
import pandas as pd
import time
import os

BASE_URL = "https://www.bighaat.com/collections/insecticides?utm_source=WSF&utm_medium=CATEGORY+TILE&utm_campaign=Insecticides+CT+%2804-11-25%29"  # change to real list URL

headers = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                  "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
}

resp = requests.get(BASE_URL, headers=headers, timeout=20)
print("Status:", resp.status_code)
resp.raise_for_status()  # stop if not 200

soup = BeautifulSoup(resp.text, "html.parser")  # parse HTML

# Example: find product cards (update the selector by inspecting page)
cards = soup.select("div.product-card, div.product")  # try a couple variants
data = []
for card in cards:
    # try to extract fields; update selectors for the target site
    name_el = card.select_one("h5, line-clamp-2 text-sm font-medium text-black text-left  text-sm")
    price_el = card.select_one("p ,font-normal text-black text-base")
    desc_el = card.select_one(".description, .product-desc")
    img_el  = card.select_one("img")

    name = name_el.get_text(strip=True) if name_el else ""
    price = price_el.get_text(strip=True) if price_el else ""
    desc = desc_el.get_text(strip=True) if desc_el else ""
    img = img_el["src"] if img_el and img_el.has_attr("src") else (img_el["data-src"] if img_el and img_el.has_attr("data-src") else "")

    # normalize relative image URLs
    if img and img.startswith("//"):
        img = "https:" + img
    elif img and img.startswith("/"):
        img = "https://www.bighaat.com" + img

    link_el = card.select_one("a")
    link = link_el["href"] if link_el and link_el.has_attr("href") else ""
    if link and link.startswith("/"):
        link = "https://www.bighaat.com" + link

    data.append({"name": name, "price": price, "description": desc, "image": img, "link": link})

# Save
df = pd.DataFrame(data)
os.makedirs("output", exist_ok=True)
df.to_csv("output/fertilizers_requests.csv", index=False, encoding="utf-8-sig")
print("Saved", len(df), "items to output/fertilizers_requests.csv")
