import requests
import title 
from fake_useragent importUseAgent
url= "https://www.bighaat.com/hi/collections/insecticides"

session = requests.Session()

header ={
    
}
r = requests.get(url)
# print(r.text)

with open ("file.html","w") as f:
    f.write(r.text) 