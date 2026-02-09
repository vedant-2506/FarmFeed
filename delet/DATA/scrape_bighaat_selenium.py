import sys
sys.stdout.reconfigure(encoding="utf-8")

from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from webdriver_manager.chrome import ChromeDriverManager
from selenium.webdriver.common.by import By
import pandas as pd
import time
import os

# Setup Chrome
options = webdriver.ChromeOptions()
options.add_argument("--headless")  # run without opening window
options.add_argument("--no-sandbox")
options.add_argument("--disable-dev-shm-usage")

# Start browser
driver = webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=options)

url = "https://www.bighaat.com/collections/insecticides?utm_source=WSF&utm_medium=CATEGORY+TILE&utm_campaign=Insecticides+CT+%2804-11-25%29"
driver.get(url)
time.sleep(10)  # wait for JS to load

# Find all product cards
cards = driver.find_elements(By.CSS_SELECTOR, "div.relative.flex.flex-col.gap-2")

data = []
for card in cards:
    try:
        name = card.find_element(By.TAG_NAME, "h3").text.strip()
    except:
        name = "N/A"

    try:
        price = card.find_element(By.CSS_SELECTOR, "div.text-base.font-semibold").text.strip()
    except:
        price = "N/A"

    try:
        img = card.find_element(By.TAG_NAME, "img").get_attribute("src")
    except:
        img = "N/A"

    data.append({
        "Product Name": name,
        "Price": price,
        "Image URL": img
    })

# Save data
os.makedirs("output", exist_ok=True)
df = pd.DataFrame(data)
df.to_csv("output/bighaat_products.csv", index=False, encoding="utf-8-sig")

print(f"✅ Scraped {len(data)} products and saved to output/bighaat_products.csv")

driver.quit()
