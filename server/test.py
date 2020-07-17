import requests

r = requests.post('http://127.0.0.1:5000/api/login',
                  data={'name': 'lbwn22m', 'password': 'lbnbb'})
print(r.text)