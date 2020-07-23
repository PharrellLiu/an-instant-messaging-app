#import requests
#import mysql.connector


'''class MyDatabase:
    conn = None
    cursor = None

    def __init__(self):
        self.connect()
        return

    def connect(self):
        self.conn = mysql.connector.connect(
            host="localhost",
            port=3306,
            user="dbuser",
            password="password",
            database="iems5722",
        )
        self.cursor = self.conn.cursor(dictionary=True)
        return'''


'''mydb = MyDatabase()
query = 'INSERT INTO chatroom_messages (chatroom,name, message,message_time) VALUES (%s, %s,%s,default);'
params = ("room1", "root", "password")
mydb.cursor.execute(query, params)
query = "SELECT @@IDENTITY;"
mydb.cursor.execute(query)
result = mydb.cursor.fetchall()
mydb.conn.commit()




print(type(result[0]['@@IDENTITY']))'''
'''r = requests.post('http://192.168.0.100:5000/api/post_private_chat_message',
                  data={"receivename": "root", "sendname": "root", "message": "tessss"})
print(r.text)'''

q = '["lbw"]'
q = q[2:]
q = q[:-2]
r = q.split('","')
print(r)
