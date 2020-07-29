import requests
from datetime import datetime
import time

r = requests.post("http://192.168.0.101:5000/api/post_moment", data={'name': 'lbw', 'content': 'ooo', 'type': 'image'},
                  files={'file': open("666.jpg", 'rb')})
print(r.text)



