import requests
from bs4 import BeautifulSoup

url = "https://www.bighaat.com/hi/collections/insecticides"   # replace with real URL
response = requests.get(url)
soup = BeautifulSoup(response.text, "html.parser")

products = soup.select(".w-1/2 sm:w-1/3 lg:w-1/4 xl:w-1/4 px-2 my-2 md:mb-2")       # change selector based on website

for product in products:
    name = product.select_one(".line-clamp-2 text-sm font-medium text-black text-left  text-sm").text.strip()
    price = product.select_one(".font-normal text-black text-base").text.strip()
    image = product.select_one("img")[""]
    
    print("Name:", name)
    print("Price:", price)
    print("Image:", image)
    print("--------------------------------")
