import requests

r = requests.post("http://192.168.0.101:5000/api/sent_moment", data={'name': 'aaa', 'content': 'qqew', 'type': 'iamge'},
                  files={'file': open("444.jpg", 'rb')})
print(r.text)
