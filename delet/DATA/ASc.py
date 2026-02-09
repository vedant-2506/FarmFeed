import pandas as pd 
import requests
from bs4 import BeautifulSoup

url =" https://www.bighaat.com/"

r = requests.get(url)
print(r)

Soup = BeautifulSoup(r.text , "lxml")
print(Soup) 