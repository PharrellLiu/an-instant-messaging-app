import requests

r = requests.post('http://192.168.0.100:5000/api/login',
                  data={'name': 'lbw', 'password': 'lbwnbb'})
print(r.text)