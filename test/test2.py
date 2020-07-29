import requests

r = requests.get("http://192.168.0.101:5000/uploads/123.JPG")
print(r.text)