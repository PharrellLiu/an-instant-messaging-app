import requests

r = requests.get('http://192.168.0.102:5000/api/get_private_chat_messages?name1=lbw&name2=root&page=1')
print(r.text)
