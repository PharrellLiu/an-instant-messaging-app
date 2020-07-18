import requests

r = requests.get('http://192.168.0.103:5000/api/get_fri_list')
print(r.text)